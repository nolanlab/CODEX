/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.segm;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.ShapeRoi;
import ij.process.ImageStatistics;
import ij.process.LUT;
import ij.process.StackStatistics;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Nikolay
 */
public class MaximaFinder3D {

    /**
     *
     * @param in - denoised image stack to be segmented
     * @param radius - search radius for local maxima
     * @param maxCutoff - pixels above or equal to this intensity won't be
     * considered. intensity is relative to the dynamic range of the image
     * (2^bits_per_pixel). Possible range of values: [0,1] Default: 0.99
     * @param minCutoff - below above or equal to this intensity won't be
     * considered. intensity is relative to the dynamic range of the image
     * (2^bits_per_pixel). Possible range of values: [0,1]. Default: 0.1
     * @param relativeCutoff - Limits the size of the region by defining the
     * high-pass cutoff for the pixel intensity, relative to the intensity of
     * the local maximum. Default: 0.4
     * @param showImage - flag that controls whether the segmentation result
     * will be shown. Setting it to zero makes things faster
     * @return
     */
    public static SegmentedObject[] findCellsByIntensityGradient(final ImagePlus in, int radius, double maxCutoff, double minCutoff, double relativeCutoff, boolean showImage, final double nuclMaskCutoff, final boolean anisotropic_reg_growth) {
        final AtomicInteger xGlobal = new AtomicInteger(-1);
        final int w = in.getWidth();
        final int h = in.getHeight();
        final int d = in.getNSlices();

        final Point3D[][][] linkMatrix = new Point3D[w][h][d];
        final int[][][] maximaMatrix = new int[w][h][d];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Arrays.fill(maximaMatrix[i][j], -1);
            }
        }

        final ImageStack is = in.getImageStack();

        final ConcurrentLinkedQueue<Point3D> maximaLst = new ConcurrentLinkedQueue<>();
        //ImageStatistics stats = new StackStatistics(in);
        double range = 65535;

        double lo_pass_ths = minCutoff * range;
        double hi_pass_ths = maxCutoff * range;

        System.out.println("lo_pass_ths " + lo_pass_ths);
        System.out.println("hi_pass_ths " + hi_pass_ths);

        ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2);

        for (int xGlob = 0; xGlob < w; xGlob++) {
            final int i = xGlob;
            es.execute(new Runnable() {
                @Override
                public void run() {

                    if (i % 10 == 0) {
                        System.out.println("Quick-Shift: " + i);
                    }

                    for (int j = 0; j < h; j++) {
                        for (int k = 0; k < d; k++) {

                            if (linkMatrix[i][j][k] != null) {
                                continue;
                            }
                            double maxIntensity = is.getVoxel(i, j, k);

                            //int maxVal = (int) Math.pow(2, is.getBitDepth()) - 1;
                            if (maxIntensity >= hi_pass_ths) {
                                //System.out.println("skipping point too high "+i+","+j+","+k+"intens:"+maxIntensity);
                                continue;
                            }

                            for (int x = -1; x <= 1; x++) {
                                for (int y = -1; y <= 1; y++) {
                                    for (int z = -1; z <= 1; z++) {
                                        if (x == 0 && y == 0 && z == 0) {
                                            continue;
                                        }

                                        Point3D otherPoint = new Point3D(i + x, j + y, k + z);

                                        if (otherPoint.x >= 0 && otherPoint.x < w && otherPoint.y >= 0 && otherPoint.y < h && otherPoint.z >= 0 && otherPoint.z < d) {
                                            double currIntens = is.getVoxel(otherPoint.x, otherPoint.y, otherPoint.z);

                                            if (currIntens <= lo_pass_ths ) {
                                                continue;
                                            }

                                            if (currIntens > maxIntensity) {
                                                maxIntensity = currIntens;
                                                linkMatrix[i][j][k] = otherPoint;
                                            } else if (currIntens == maxIntensity) {
                                                if (x >= 0 && y >= 0 && z >= 0) {
                                                    linkMatrix[i][j][k] = otherPoint;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (linkMatrix[i][j][k] == null && maxIntensity > lo_pass_ths) {
                                maximaLst.add(new Point3D(i, j, k));
                            }
                        }
                    }
                }
            });
        }

        try {
            es.shutdown();
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        System.out.println("Found maxima: " + maximaLst.size());
        Point3D[] maxima = maximaLst.toArray(new Point3D[maximaLst.size()]);

        for (int i = 0; i < maxima.length; i++) {
            maximaMatrix[maxima[i].x][maxima[i].y][maxima[i].z] = i;
        }

        ArrayList<Point3D> filteredMaxima = new ArrayList<>();

        System.out.println("Merging:");
        for (Point3D m : maxima) {

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        Point3D otherPoint = new Point3D(m.x + x, m.y + y, m.z + z);

                        if (otherPoint.x >= w || otherPoint.y >= h || otherPoint.z >= d || otherPoint.x < 0 || otherPoint.y < 0 || otherPoint.z < 0) {
                            continue;
                        }

                        if (maximaMatrix[otherPoint.x][otherPoint.y][otherPoint.z] >= 0) {
                            if (is.getVoxel(otherPoint.x, otherPoint.y, otherPoint.z) > is.getVoxel(m.x, m.y, m.z)) {
                                linkMatrix[m.x][m.y][m.z] = otherPoint;
                            }
                        }
                    }
                }
            }
            if (linkMatrix[m.x][m.y][m.z] == null) {
                filteredMaxima.add(m);
            }
        }

        for (Point3D m : maxima) {
            maximaMatrix[m.x][m.y][m.z] = -1;
        }

        maxima = filteredMaxima.toArray(new Point3D[filteredMaxima.size()]);
        System.out.println("Filtered maxima: " + maxima.length);

        for (int i = 0; i < maxima.length; i++) {
            maximaMatrix[maxima[i].x][maxima[i].y][maxima[i].z] = i;
        }

        /*
        for (int i = 0; i < maxima.length; i++) {
            maximaMatrix[maxima[i].x][maxima[i].y][maxima[i].z] = i;
        }*/
        ConcurrentLinkedQueue<Point3D>[] regions = new ConcurrentLinkedQueue[maxima.length];
        for (int i = 0; i < maxima.length; i++) {
            regions[i] = new ConcurrentLinkedQueue<>();
        }

        es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2);
        for (int i = 0; i < w; i++) {
            final int x = i;
            es.execute(new Runnable() {
                @Override
                public void run() {

                    if (x % 10 == 0) {
                        System.out.println("Building regions: " + x);
                    }
                    for (int y = 0; y < h; y++) {
                        for (int z = 0; z < d; z++) {
                            Point3D nextPoint = new Point3D(x, y, z);
                            do {
                                if (linkMatrix[nextPoint.x][nextPoint.y][nextPoint.z] == null && maximaMatrix[nextPoint.x][nextPoint.y][nextPoint.z] >= 0) {
                                    int i = maximaMatrix[nextPoint.x][nextPoint.y][nextPoint.z];
                                    double normInt = is.getVoxel(x, y, z) / is.getVoxel(nextPoint.x, nextPoint.y, nextPoint.z);

                                    if (normInt > (anisotropic_reg_growth?1.0-((1.0-relativeCutoff)/Math.sqrt(1+Math.abs(nextPoint.z-z))):relativeCutoff)  && normInt < nuclMaskCutoff) {
                                        regions[i].add(new Point3D(x, y, z));
                                    } else {
                                        break;
                                    }
                                }
                                nextPoint = linkMatrix[nextPoint.x][nextPoint.y][nextPoint.z];
                            } while (nextPoint != null);
                        }
                    }

                }
            });
        }

        try {
            es.shutdown();
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        List<SegmentedObject> out = new ArrayList<>();
        Overlay ovl = new Overlay();

        assert (maxima.length == regions.length);

        //new Duplicator().run(in);
        /*
         for (int i = 0; i < maxima.length; i++) {
            ConcurrentLinkedQueue<Point3D> region = regions[i];
            double rand = Math.random()*Math.pow(65535,2);
             for (Point3D p : region) {
                 rend.getImageStack().setVoxel(p.x, p.y, p.z, rand);
             }
         }*/
        //rend.show();
        for (int i = 0; i < maxima.length; i++) {
            ConcurrentLinkedQueue<Point3D> region = regions[i];
            Point3D center = maxima[i];//new Point3D((int) cX,
            out.add(new SegmentedObject(center, region.toArray(new Point3D[region.size()])));
        }

        if (showImage) {
            System.out.println("Building image overlay...");
            in.setLut(LUT.createLutFromColor(Color.white));
            for (int i = 0; i < maxima.length; i++) {
                ConcurrentLinkedQueue<Point3D> region = regions[i];
                Point3D center = maxima[i];//new Point3D((int) cX, (int) cY, (int) cZ);

                PointRoi roi = new PointRoi(center.x, center.y);
                roi.setPosition(center.z + 1);
                ovl.add(roi);

                Color col = Color.white;//new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 127);
                double wid = 1; //+ Math.random()*2;
                for (int z = 0; z < in.getImageStackSize(); z++) {

                    final int currZ = z;

                    Point3D[] filteredReg = region.stream().filter((a) -> a.z == currZ).toArray((int value) -> new Point3D[value]);
                    if (filteredReg.length < 9) {
                        continue;
                    }
                    Point[] pt = new Point[filteredReg.length];
                    for (int j = 0; j < pt.length; j++) {
                        pt[j] = new Point(filteredReg[j].x, filteredReg[j].y);

                    }

                    Area a = new Area();

                    for (Point point : pt) {
                        a.add(new Area(new Rectangle(point.x, point.y, 1, 1)));
                    }
                    ShapeRoi sr = new ShapeRoi(a);
                    sr.setStrokeColor(col);
                    sr.setStrokeWidth(wid);
                    sr.setPosition(z + 1);
                    ovl.add(sr);
                }

            }

            in.setOverlay(ovl);
        }
        System.out.println("Filtered maxima: " + maxima.length);
        return out.toArray(new SegmentedObject[out.size()]);
    }

}
