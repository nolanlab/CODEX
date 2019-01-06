package org.nolanlab.codex.segm;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Nikolay
 */
public class Neighborhood {

    public static List<Point3D>[] findGabrielNeighbors(Point3D[] input) {
        int len = input.length;
        final List<Point3D>[] out = new List[len];
        for (int i = 0; i < len; i++) {
            out[i] = new ArrayList<>();
        }
        AtomicInteger ai = new AtomicInteger(-1);
        ThreadGroup tg = new ThreadGroup("GabrielThreads");
        Thread[] t = new Thread[Runtime.getRuntime().availableProcessors()];

        for (int tx = 0; tx < t.length; tx++) {
            t[tx] = new Thread(tg, () -> {
                Point3D[] tmpIn = Arrays.copyOf(input, input.length);

                do {
                    int i = ai.addAndGet(1);
                    if (i >= len) {
                        return;
                    }
                    if (i % 10 == 0) {
                        System.out.println("Gabriel graph " + i);
                    }
                    for (int j = i + 1; j < len; j++) {
                        final Point3D mid = getAverage(input[i], input[j]);
                        Arrays.sort(tmpIn, (Point3D o1, Point3D o2) -> (int) Math.signum(Segmentation.dist(o1, mid) - Segmentation.dist(o2, mid)));

                        boolean nei = (tmpIn[0].equals(input[i]) && tmpIn[1].equals(input[j])) || (tmpIn[1].equals(input[i]) && tmpIn[0].equals(input[j]));
                        if (nei) {
                            synchronized (out) {
                                out[i].add(input[j]);
                                out[j].add(input[i]);
                            }
                        }
                    }
                } while (true);
            });
            t[tx].start();
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
        return out;
    }

    public static double[][] buildAdjacencyMatrix(Cell[] input, int w, int h, int d, boolean normalize, boolean single_plane_quant) {
        int len = input.length;
        final double[][] adjMtx = new double[len][len];
        final int[][][] regionMap = new int[w][h][d];


        for (int i = 0; i < input.length; i++) {
            for (Point3D p : input[i].getSegmentedObject().getPoints()) {
                regionMap[p.x][p.y][p.z] = i + 1;
            }
        }

        AtomicInteger ai = new AtomicInteger(0);
        ThreadGroup tg = new ThreadGroup("AdjThreads");
        Thread[] t = new Thread[Runtime.getRuntime().availableProcessors()];

        for (int tx = 0; tx < t.length; tx++) {
            t[tx] = new Thread(tg, new Runnable() {
                @Override
                public void run() {
                    do {
                        int x = ai.addAndGet(1);
                        if (x >= w - 1) {
                            return;
                        }
                        if (x % 10 == 0) {
                            System.out.println("Adj graph " + x);
                        }
                        for (int y = 1; y < h - 1; y++) {
                            for (int z = 1; z < d - 1; z++) {
                                int currReg = regionMap[x][y][z];
                                if (currReg == 0) {
                                    continue;
                                }

                                if(single_plane_quant && z!=input[currReg-1].getSegmentedObject().getCenter().z){
                                    continue;
                                }


                                boolean border = false;
                                for (int[] offset : new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, -1, 0}, {0, 1, 0}}) {
                                    int otherReg = regionMap[x + offset[0]][y + offset[1]][z + offset[2]];
                                    if (currReg != otherReg) {
                                        if (otherReg != 0) {
                                            synchronized (adjMtx) {
                                                adjMtx[currReg - 1][otherReg - 1]++;
                                            }
                                        }
                                        border = true;
                                    }
                                }
                                if (border) {
                                    synchronized (adjMtx) {
                                        adjMtx[currReg - 1][currReg - 1]++;
                                    }
                                }
                            }
                        }
                    } while (true);
                }
            });
            t[tx].start();
        }

        do {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        } while (tg.activeCount() > 0);

        if (normalize) {
            for (int a = 0; a < len; a++) {
                for (int b = 0; b < len; b++) {
                    if (a == b) {
                        continue;
                    }
                    adjMtx[a][b] /= Math.max(adjMtx[a][a], 1);
                    adjMtx[a][b] /= 2.0;
                }
                adjMtx[a][a] = 1;
            }
            if (false) {
                for (int a = 0; a < len; a++) {
                    for (int b = 0; b < len; b++) {
                        double avg = (adjMtx[a][b] + adjMtx[b][a]) / 2;
                        adjMtx[a][b] = avg;
                        adjMtx[b][a] = avg;
                    }
                }
            }
        }

        return adjMtx;

    }

    public static Collection<Cell>[] findDelaunayNeighbors(final Cell[] input, int w, int h, int d) {

        ThreadGroup tg = new ThreadGroup("QSthreads");
        Thread[] t = new Thread[Runtime.getRuntime().availableProcessors()];

        AtomicInteger xGlobal = new AtomicInteger(-1);
        final int maxSearchXY = (w + h) / 20;
        final int maxSearchZ = Math.min(d, maxSearchXY);

        int maxDist = (int) Math.ceil(Math.sqrt(maxSearchXY * maxSearchXY + maxSearchXY * maxSearchXY + maxSearchZ * maxSearchZ));

        int[][][] distToNN = new int[w][h][d];

        int[][][] nnIDX = new int[w][h][d];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                for (int z = 0; z < d; z++) {
                    distToNN[x][y][z] = maxDist;
                    nnIDX[x][y][z] = -1;
                }
            }
        }

        for (int i = 0; i < t.length; i++) {
            t[i] = new Thread(tg, () -> {
                do {
                    int i1 = xGlobal.addAndGet(1);
                    if (i1 >= input.length) {
                        return;
                    }
                    if (i1 % 10 == 0) {
                        System.out.println("Delaunay Graph: " + i1);
                    }
                    Point3D currPoint = input[i1].getSegmentedObject().getCenter();
                    for (int x = -maxSearchXY; x < maxSearchXY; x++) {
                        int currX = currPoint.x + x;
                        if (currX < 0 || currX >= w) {
                            continue;
                        }
                        for (int y = -maxSearchXY; y < maxSearchXY; y++) {
                            int currY = currPoint.y + y;
                            if (currY < 0 || currY >= h) {
                                continue;
                            }
                            for (int z = -maxSearchZ; z < maxSearchZ; z++) {
                                int currZ = currPoint.z + z;
                                if (currZ < 0 || currZ >= d) {
                                    continue;
                                }
                                int currDist = (int) Math.ceil(Math.sqrt(x * x + y * y + z * z));
                                if (currDist < distToNN[currX][currY][currZ]) {
                                    distToNN[currX][currY][currZ] = currDist;
                                    nnIDX[currX][currY][currZ] = i1;
                                }
                            }
                        }
                    }
                } while (true);
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

        boolean[][] delanuayGraph = new boolean[input.length][input.length];

        for (int x = 1; x < w; x++) {
            for (int y = 1; y < h; y++) {
                for (int z = 1; z < d; z++) {
                    int currID = nnIDX[x][y][z];
                    if (currID >= 0) {
                        int otherID = -1;
                        if (nnIDX[x - 1][y][z] != currID) {
                            otherID = nnIDX[x - 1][y][z];
                        }
                        if (nnIDX[x][y - 1][z] != currID) {
                            otherID = nnIDX[x][y - 1][z];
                        }
                        if (nnIDX[x][y][z - 1] != currID) {
                            otherID = nnIDX[x][y][z - 1];
                        }
                        if (otherID >= 0) {
                            delanuayGraph[currID][otherID] = true;
                            delanuayGraph[otherID][currID] = true;
                        }
                    }
                }
            }
        }

        Collection<Cell>[] out = new Collection[input.length];

        for (int i = 0; i < out.length; i++) {
            out[i] = new ConcurrentLinkedQueue<>();
            for (int j = i + 1; j < out.length; j++) {
                if (i != j && delanuayGraph[i][j]) {
                    out[i].add(input[j]);
                }

            }
        }

        return out;

    }

    public static Collection<Cell>[] findGabrielNeighbors(Cell[] input) {
        int len = input.length;
        final Collection<Cell>[] out = new Collection[len];
        for (int i = 0; i < len; i++) {
            out[i] = new ConcurrentLinkedQueue<>();
        }
        AtomicInteger ai = new AtomicInteger(-1);
        ThreadGroup tg = new ThreadGroup("GabrielThreads");
        Thread[] t = new Thread[Runtime.getRuntime().availableProcessors()];

        for (int tx = 0; tx < t.length; tx++) {
            t[tx] = new Thread(tg, () -> {
                Cell[] tmpIn = Arrays.copyOf(input, input.length);

                do {
                    int i = ai.addAndGet(1);
                    if (i >= len) {
                        return;
                    }

                    if (i % 10 == 0) {
                        System.out.println("Gabriel graph " + i);
                    }
                    for (int j = i + 1; j < len; j++) {
                        final Point3D mid = getAverage(input[i].getSegmentedObject().getCenter(), input[j].getSegmentedObject().getCenter());
                        Arrays.sort(tmpIn, (Cell o1, Cell o2) -> (int) Math.signum(Segmentation.dist(o1.getSegmentedObject().getCenter(), mid) - Segmentation.dist(o2.getSegmentedObject().getCenter(), mid)));
                        boolean nei = (tmpIn[0].equals(input[i]) && tmpIn[1].equals(input[j])) || (tmpIn[1].equals(input[i]) && tmpIn[0].equals(input[j]));
                        if (nei) {

                            synchronized (out) {
                                out[i].add(input[j]);
                                out[j].add(input[i]);
                            }
                        }
                    }
                } while (true);
            });
            t[tx].start();
        }

        do {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        } while (tg.activeCount() > 0);
        return out;

    }

    public static Point3D getAverage(Point3D a, Point3D b) {
        return new Point3D((a.x + b.x) / 2, (a.y + b.y) / 2, (a.z + b.z) / 2, (a.intensity + b.intensity) / 2, new Color((a.color.getRGB() + b.color.getRGB()) / 2));
    }

}