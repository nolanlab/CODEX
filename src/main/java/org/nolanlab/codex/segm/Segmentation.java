package org.nolanlab.codex.segm;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.MaximumFinder;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Nikolay
 */
public class Segmentation {

    public static SegmentedObject[] getRegionsMahDist(final ImageStack img, final SegmentedObject[] segmentedObjects, final double lbound, final double hbound) {

        final MahalonobisDistance[] mh = new MahalonobisDistance[segmentedObjects.length];
        final List<Point3D>[] out = new List[segmentedObjects.length];
        SegmentedObject[] ret = new SegmentedObject[segmentedObjects.length];

        final AtomicInteger xGlobal = new AtomicInteger(-1);
        ThreadGroup tg = new ThreadGroup("MDthreads");
        Thread[] t = new Thread[Runtime.getRuntime().availableProcessors()];

        for (int i = 0; i < segmentedObjects.length; i++) {
            mh[i] = new MahalonobisDistance(segmentedObjects[i]);
            out[i] = new LinkedList<Point3D>();
        }

        for (int i = 0; i < t.length; i++) {
            t[i] = new Thread(tg, new Runnable() {
                @Override
                public void run() {
                    do {
                        int x = xGlobal.addAndGet(1);
                        if (x >= img.getWidth()) {
                            return;
                        }
                        if (x % 10 == 0) {
                            System.out.println("Weighted Voronoi Tesselation: " + x);
                        }
                        for (int y = 0; y < img.getHeight(); y++) {
                            for (int z = 0; z < img.getHeight(); z++) {
                                int NID = -1;
                                double minDist = hbound;
                                double secondClosestDist = hbound;
                                for (int i = 0; i < mh.length; i++) {
                                    double dist = mh[i].distTo(new double[]{x, y, z});
                                    if (dist < minDist) {
                                        secondClosestDist = minDist;
                                        minDist = dist;
                                        NID = i;
                                    }
                                }
                                if (NID >= 0) {
                                    synchronized (out[NID]) {
                                        Point3D pt = new Point3D(x, y, z);
                                        if (minDist < lbound) {
                                            pt.color = Color.BLUE;
                                            pt.intensity = Math.exp(-minDist);
                                        }
                                        if ((secondClosestDist / minDist) < 1.1) {
                                            pt.color = Color.WHITE;
                                            pt.intensity = 1;
                                        }
                                        out[NID].add(pt);
                                    }
                                }
                            }
                        }
                    } while (true);
                }
            });
        }
        for (int i = 0; i < t.length; i++) {
            try {
                t[i].start();
            } catch (IllegalThreadStateException e) {
                System.out.println(e);
            }
        }
        do {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        } while (tg.activeCount() > 0);

        for (int i = 0; i < out.length; i++) {
            ret[i] = new SegmentedObject(segmentedObjects[i].getCenter(), out[i].toArray(new Point3D[out[i].size()]));
        }
        return ret;
    }

    public static double dist(Point3D a, Point3D b) { double dist = (a.x - b.x) * (a.x - b.x);
        dist += (a.y - b.y) * (a.y - b.y);
        dist += (a.z - b.z) * (a.z - b.z);
        return Math.sqrt(dist);
    }

    public static double[] computeMeanIntensityOfRegions(ImageStack img, SegmentedObject[] segmentedObjects, boolean central_plane_quant) {
        double[] out = new double[segmentedObjects.length];
        for (int i = 0; i < out.length; i++) {
            SegmentedObject r = segmentedObjects[i];
            double cnt = 0;
            for (Point3D p : r.getPoints()) {
                if (p.color.equals(Color.BLUE)) {
                    continue;
                }
                if(!central_plane_quant || p.z==r.getCenter().z) {
                    double vox = img.getVoxel(p.x, p.y, p.z);
                    out[i] += vox;
                    cnt++;
                }
            }
            out[i] /= cnt;
        }
        return out;
    }

    public static double[] computePunctaCountOfRegions(ImagePlus img, SegmentedObject[] segmentedObjects, int tolerance) {
        MaximumFinder mf = new MaximumFinder();

        final int[][][] maximaMatrix = new int[img.getWidth()][img.getHeight()][img.getNSlices()];

        for (int i = 0; i < segmentedObjects.length; i++) {
            for (Point3D p : segmentedObjects[i].getPoints()) {
                maximaMatrix[p.x][p.y][p.z] = i + 1;
            }
        }

        double[] out = new double[segmentedObjects.length];

        int cntPoints = 0;
        for (int z = 1; z <= img.getNSlices(); z++) {
            Polygon pg = mf.getMaxima(img.getImageStack().getProcessor(z), tolerance, true);
            PathIterator pi = pg.getPathIterator(null);
            double[] coord = new double[6];
            do {
                pi.next();
                pi.currentSegment(coord);
                if (coord.length >= 2) {
                    int idx = maximaMatrix[(int) coord[0]][(int) coord[1]][z - 1];

                    if (idx > 0) {
                        out[idx - 1]++;
                    }
                    cntPoints++;
                }

            } while (!pi.isDone());
        }

        System.out.println("found puncta:" + cntPoints);

        return out;
    }

    public static double[] computeMembraneIntensityOfRegions(ImageStack img, ImageStack membraneImg, SegmentedObject[] segmentedObjects) {
        double[] out = new double[segmentedObjects.length];
        for (int i = 0; i < out.length; i++) {
            SegmentedObject r = segmentedObjects[i];
            double cnt = 0;
            for (Point3D p : r.getPoints()) {
                if (p.color.equals(Color.BLUE)) {
                    continue;
                }
                double mem = img.getVoxel(p.x, p.y, p.z) + 1;
                out[i] += img.getVoxel(p.x, p.y, p.z) * mem;
                cnt += mem;
            }
            out[i] /= cnt;
        }
        return out;
    }

    public static double[][] compensatePositionalSpilloverOfExpressionMtx(SegmentedObject[] segmentedObjects, double[][] adjMatrix, double[][] expressionMatrix) {

        DenseDoubleMatrix2D exp = new DenseDoubleMatrix2D(expressionMatrix);
        DenseDoubleMatrix2D adj = new DenseDoubleMatrix2D(adjMatrix);
        //double[][] out = new double[expressionMatrix[0].length][];
        System.out.println("Solving matrix: " + adj.rows() + "x" + adj.columns());
        //System.out.println(adj.toString());
        // System.out.println("Det:"+Algebra.DEFAULT.det(adj));

        /*
        for (int i = 0; i < expT.length; i++) {
            logger.print("solving #"+i);
            DenseDoubleMatrix1D  currExp = new DenseDoubleMatrix1D(expT[i]);
            AbstractDoubleIterativeSolver solver = new DoubleGMRES(currExp);
            DoubleMatrix1D solution = currExp.copy();
            try{
                solver.solve(adj, currExp, solution);
            }catch(IterativeSolverDoubleNotConvergedException e){
               e.printStackTrace();
            }
        
            out[i] = solution.toArray();
        }*/
        DoubleMatrix2D out = new DenseDoubleMatrix2D(expressionMatrix);

        try {
            out = DenseDoubleAlgebra.DEFAULT.solve(adj, exp);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println("Using the source matrix");
        }

        //DoubleMatrix2D revExp = DenseDoubleAlgebra.DEFAULT.mult(adj, out);
        /*
        logger.print("expression mtx:");
        logger.print(exp);
        logger.print("reverse exp matrix");
        logger.print(revExp);
        logger.print("out");
        logger.print(out);
         */
        double [][] ret = out.toArray();

        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = Math.max(0, ret[i][j]);
            }
        }
        return ret;
        /*DoubleMatrix2D res = Algebra.DEFAULT.mult(Algebra.DEFAULT.transpose(exp), inv);
         return Algebra.DEFAULT.transpose(res).toArray();*/
    }

    public static double[] computeQuantileIntensityOfRegions(ImageStack img, SegmentedObject[] segmentedObjects, double quantile) {
        double[] out = new double[segmentedObjects.length];
        for (int i = 0; i < out.length; i++) {
            SegmentedObject r = segmentedObjects[i];
            double cnt = 0;
            LinkedList<Double> ll = new LinkedList<>();
            int k = 0;
            for (Point3D p : r.getPoints()) {
                if (p.color.equals(Color.BLUE)) {
                    continue;
                }
                double intens = img.getVoxel(p.x, p.y, p.z);
                if (intens > 0) {
                    ll.add(intens);
                }
            }
            Double[] ret = ll.toArray(new Double[ll.size()]);
            Arrays.sort(ret);
            out[i] = (ret.length > 2) ? ret[(int) (ret.length * quantile)] : 0;
        }
        return out;
    }

}
