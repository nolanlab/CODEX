/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.toolkit;

import clustering.Cluster;
import clustering.ClusterMember;
import clustering.Datapoint;
import util.DefaultEntry;
import util.IO;
import util.logger;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Nikolay Samusik
 */
public class DelaunayGraph {

    private static BufferedWriter bwAnn;
    private static String[] sideColNames = new String[]{"tileX", "tileY", "tile_nr", "X", "Y", "Z", "ClusterID"};
    private static final boolean drawGraph = true;
    private static HashMap<String, Color> colLookup;

    private static int legendWidth = 500;

    static {
        colLookup = new HashMap<>();
        colLookup.put("J", Color.GREEN.darker().darker().darker());
        colLookup.put("L", Color.YELLOW);
        colLookup.put("G", Color.PINK);
    }

    /*
    private static void testPointScan() throws IOException {
        BufferedImage img = new BufferedImage(1002, 1002, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) img.createGraphics();

        for (int j = 1; j < 500; j += 1) {
            for (Point2D p : drawcircle(500, 500, j)) {
                img.setRGB(p.x, p.y, 0xFFAA00);
            }
        }

        ImageIO.write(img, "PNG", new File("C:\\Users\\Nikolay Samusik\\Desktop\\test.png"));

    }*/
    public static Point2D getCenter(Polygon s) {
        int x = 0;
        int y = 0;

        int pointCount = s.npoints;
        for (int i = 0; i < pointCount; i++) {
            x += s.xpoints[i];
            y += s.ypoints[i];
        }

        x = x / pointCount;
        y = y / pointCount;

        return new Point2D(x, y);
    }

    private static HashMap<String, double[]> hmEntropy = null;

    public static void saveDelaunayGraph(String fileName, Entry<ClusterMember[], ClusterMember[][]> res) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName.split("\\.")[0] + "_DelaunayGraph.txt")));
        ClusterMember[][] graph = res.getValue();
        ClusterMember[] dp = res.getKey();
        if (graph != null) {
            for (int j = 0; j < graph.length; j++) {

                bw.write(String.valueOf(dp[j].getDatapoint().getIndexInFile()) + "\t");
                for (int k = 0; k < graph[j].length; k++) {
                    bw.write(String.valueOf(graph[j][k].getDatapoint().getIndexInFile()));
                    if (k < graph[j].length - 1) {
                        bw.write(",");
                    }

                }
                bw.write("\n");
            }
        }

        bw.flush();
        bw.close();
    }

    public static Entry<BufferedImage,BufferedImage> renderRegion(String fileName, Map<Integer, String> cidMap, Entry<ClusterMember[], ClusterMember[][]> res, int regionWidthInTiles, int tileWidth, int regionHeightInTiles, int tileHeight) throws IOException {

        
        ClusterMember[][] graph = res.getValue();
        ClusterMember[] dp = res.getKey();
        
        Cluster[] clusters = Arrays.asList(dp).stream().map(c->c.getCluster()).distinct().sorted((a,b)->a.getID()-b.getID()).toArray(Cluster[]::new);
        
        BufferedImage img = new BufferedImage(regionWidthInTiles * tileWidth, regionHeightInTiles * tileHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) img.createGraphics();
        Color lightGray = new Color(200, 200, 200);
        g2.setPaint(lightGray);

        g2.fillRect(0, 0, img.getWidth(), img.getHeight());

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(rh);

        g2.setPaint(Color.GRAY);

        //Never used
        double[] entropies = null;


        double maxEntropy = 0;
        double minEntropy = 0;

        if (hmEntropy != null) {
            entropies = hmEntropy.get(fileName);

            if (entropies == null) {
                throw new IllegalStateException("Entropies don't exist for file:" + fileName);

            }
            maxEntropy = minEntropy = entropies[0];

            for (double entropy : entropies) {
                maxEntropy = Math.max(maxEntropy, entropy);
                minEntropy = Math.min(minEntropy, entropy);
            }


        }



        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        if (graph != null) {
            for (int j = 0; j < graph.length; j++) {
                

                int idx = dp[j].getCluster().getID();

                float colVal = (idx) / (float) (clusters.length);

                //Never used
                if (entropies != null) {
                    colVal = (float) ((entropies[j] - minEntropy) / (maxEntropy - minEntropy));
                }

                if (idx < 0) {
                    continue;
                }

                String ch = new Character((char) (idx + 65)).toString();

                Color c = entropies == null ? colLookup.get(ch) : null;
                
                if (c == null) {
                    c = getColor(colVal);
                }
                
                if(idx == clusters.length-1){
                    c = lightGray;
                    ch = "";
                }

                double angle = 5 * Math.PI * (colVal);
                //LinearGradientPaint gp = new LinearGradientPaint(0.0f, 0.0f, (float) Math.sin(angle) * 2, (float) Math.cos(angle) * 2, new float[]{0.0f, 1.0f}, new Color[]{c, c.darker().darker()}, MultipleGradientPaint.CycleMethod.REFLECT);

                g2.setPaint(c);//gp);

                Polygon s = getVoronoiPolygon(res, j);
                g2.fill(s);
                g2.setStroke(new BasicStroke(2));

                g2.setPaint(lightGray);

                g2.draw(s);

                g2.setPaint(Color.BLACK);
                Point2D center = getCenter(s);
                //new Point2D((int) dp[j].getVector()[0], (int) dp[j].getVector()[1]);
                //g2.setPaint(new Color(0,0,0,128));
                g2.drawString(ch, center.x - 3, center.y + 5);
                //g2.fill(getShape(colVal, (float) dp[j].getVector()[0], (float) dp[j].getVector()[1], size));
            }
            if (drawGraph) {
                g2.setPaint(Color.black);
                for (int j = 0; j < graph.length; j++) {
                    for (int k = 0; k < graph[j].length; k++) {
                        g2.drawLine((int) graph[j][k].getDatapoint().getVector()[0], (int) graph[j][k].getDatapoint().getVector()[1], (int) dp[j].getDatapoint().getVector()[0], (int) dp[j].getDatapoint().getVector()[1]);
                    }
                }
            }
        }

        //Never happens


        //ImageIO.write(img, "PNG", new File(fileName + "_diagram.png"));
        BufferedImage legend = new BufferedImage(legendWidth,  clusters.length * 30, BufferedImage.TYPE_INT_RGB);
        
        
        g2 = (Graphics2D) legend.createGraphics();
        rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(rh);

        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, legend.getWidth(), legend.getHeight());
        
        for (int j = 0; j < clusters.length-1; j++) {
            float colVal = j / (float) clusters.length;

            String ch = new Character((char) (j + 65)).toString();
            Color c = colLookup.get(ch);

            if (c == null) {
                c = getColor(colVal);
            }

            double angle = 5 * Math.PI * (colVal);
            //LinearGradientPaint gp = new LinearGradientPaint(0.0f, 0.0f, (float) Math.sin(angle) * 2, (float) Math.cos(angle) * 2, new float[]{0.0f, 1.0f}, new Color[]{c, c.darker().darker()}, MultipleGradientPaint.CycleMethod.REFLECT);

            g2.setPaint(c);//gp);
            g2.fill(getShape(0, 20, (j * 30) + 10, 25));
            g2.setPaint(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(18.0f));
            g2.drawString(clusters[j] + ", " + clusters[j].getCaption(), 40, j * 30 + 15);

            g2.setFont(g2.getFont().deriveFont(13.0f));
            g2.drawString(ch, 15, j * 30 + 15);
        }

        //img.getGraphics().drawImage(legend, img.getWidth() - legend.getWidth(), 0, null);

        return new DefaultEntry<>(img,legend);
    }

    public static Polygon getVoronoiPolygon(Entry<ClusterMember[], ClusterMember[][]> res, int idx) {
        //This function creates a Voronoi polygon based on the convex hull
        Datapoint centerDP = res.getKey()[idx].getDatapoint();
        ClusterMember[] neighborDP = res.getValue()[idx];

        Point2D[] neigh2D = new Point2D[neighborDP.length];
        Point2D center = new Point2D((int) centerDP.getVector()[0], (int) centerDP.getVector()[1]);


        //Gets the list of neighbors of a given datapoint
        for (int i = 0; i < neighborDP.length; i++) {
            neigh2D[i] = new Point2D((int) neighborDP[i].getDatapoint().getVector()[0], (int) neighborDP[i].getDatapoint().getVector()[1]);
        }

        //Sorts them in polar order
        Arrays.sort(neigh2D, 0, neigh2D.length, center.polarOrder());


        //Array of points for which the convex hull will be computed
        ArrayList<Point2D> pts = new ArrayList<>();

        //Adds the given datapoint
        pts.add(center);


        //Adds the points that are half-way between the center and the Delaunay neighbor

        for (int i = 0; i < neigh2D.length; i++) {
            pts.add(new Point2D((int) ((center.x + neigh2D[i].x ) / 2), (int) ((center.y + neigh2D[i].y) / 2)));
        }


        for (int i = 0; i < neigh2D.length - 1; i++) {
            pts.add(new Point2D((int) ((center.x + neigh2D[i].x + neigh2D[i + 1].x) / 3), (int) ((center.y + neigh2D[i].y + neigh2D[i + 1].y) / 3)));
        }

        pts.add(new Point2D((int) ((center.x + neigh2D[neigh2D.length - 1].x + neigh2D[0].x) / 3), (int) ((center.y + neigh2D[neigh2D.length - 1].y + neigh2D[0].y) / 3)));

        //Convex hull of the list of points
        GrahamScan gs = new GrahamScan(pts.toArray(new Point2D[pts.size()]));

        Iterable<Point2D> h1 = gs.hull();

        ArrayList<Point2D> hull = new ArrayList<>();

        for (Point2D p : h1) {
            hull.add(p);
        }

        Polygon out = new Polygon();

        for (Point2D p : hull) {
            out.addPoint(p.x, p.y);
        }
        return out;

    }

    public static Color getColor(float colVal) {
        float val = colVal;
        val = val % 1.0f;
        val *= 0.9;

        Color c = new Color(Color.HSBtoRGB(val, 1, 1));
        return c;
    }

    public static Shape getShape(float val, float x, float y, float size) {

        val = (val % 3) / 3;

        if (val < 0.33) {
            return new Rectangle2D.Float(x - size / 2, y - size / 2, size, size);
        }

        if (val < 0.66) {
            return new Ellipse2D.Float(x - size / 2, y - size / 2, size, size);
        }

        Path2D triange = new Path2D.Float();

        triange.moveTo(x - (size / 2), y + (size / 3));
        triange.lineTo(x, y - ((2 * size) / 3));
        triange.lineTo(x + (size / 2), y + (size / 3));

        triange.closePath();
        return triange;
    }


    
    public static ClusterMember[][] findDelaunayNeighbors(final ClusterMember[] cm, int w, int h) {

        Thread[] t = new Thread[Runtime.getRuntime().availableProcessors()];

        final int maxSearchXY = (w + h) / 50;

        final int[][] nnIDX = new int[w][h];

        final int[][] nnDist = new int[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                nnIDX[x][y] = -1;
            }
        }

        Point2D[] pts = new Point2D[cm.length];

        for (int i = 0; i < pts.length; i++) {
            //Here we need to change the way we extract the X and Y coordinates - right now they are assumed to be under 0 and 1 indices of the vector
            pts[i] = new Point2D((int) cm[i].getDatapoint().getVector()[0], (int) cm[i].getDatapoint().getVector()[1]);
            nnIDX[pts[i].x][pts[i].y] = i;
            nnDist[pts[i].x][pts[i].y] = 0;
        }

        ConcurrentLinkedQueue<ClusterMember>[] delN = new ConcurrentLinkedQueue[cm.length];

        final boolean[] allDone = new boolean[cm.length];

        for (int i = 0; i < allDone.length; i++) {
            allDone[i] = false;
        }

        for (int i = 0; i < delN.length; i++) {
            delN[i] = new ConcurrentLinkedQueue<>();
        }

        AtomicBoolean allDoneCyc = new AtomicBoolean(false);

        final AtomicInteger countActive = new AtomicInteger(0);

        //Replace with a HashMap
        final boolean[][] adjM = new boolean[cm.length][cm.length];

        for (int j = 1; j < maxSearchXY && !allDoneCyc.get(); j++) {

            logger.print("Computing Delaunay Graph radius: " + j + " out of " + maxSearchXY + ", countActive: " + countActive.get());

            ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
            final int sr = j;
            allDoneCyc.set(true);
            final AtomicInteger ai = new AtomicInteger(-1);

            countActive.set(0);

            for (int i = 0; i < t.length; i++) {
                es.execute(new Runnable() {
                    public void run() {
                        int d;
                        while ((d = ai.incrementAndGet()) < cm.length) {

                            if (allDone[d]) {
                                continue;
                            }

                            countActive.incrementAndGet();

                            allDoneCyc.set(false);
                            int x = (int) cm[d].getDatapoint().getVector()[0];
                            int y = (int) cm[d].getDatapoint().getVector()[1];

                            allDone[d] = true;

                            for (Point2D p : drawcircle(x, y, sr)) {
                                if (p.x < 0 || p.y < 0 || p.x >= w || p.y >= h) {
                                    continue;
                                }
                                int nix = nnIDX[p.x][p.y];
                                if (nix == -1) {
                                    synchronized (nnIDX) {
                                        nnIDX[p.x][p.y] = d;
                                        nnDist[p.x][p.y] = sr;
                                    }
                                    allDone[d] = false;
                                } else if (d != nix && (nnDist[p.x][p.y] == sr || nnDist[p.x][p.y] == sr - 1)) {
                                    synchronized (adjM) {
                                        adjM[d][nnIDX[p.x][p.y]] = true;
                                        adjM[nnIDX[p.x][p.y]][d] = true;
                                    }
                                }
                            }
                        }
                    }
                });
            }
            es.shutdown();
            try {
                es.awaitTermination(1000, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.showException(e);
            }
        }

        logger.print("Collecting events");

        for (int x = 0; x < cm.length; x++) {
            for (int y = x + 1; y < cm.length; y++) {
                if (adjM[x][y]) {
                    delN[x].add(cm[y]);
                    delN[y].add(cm[x]);
                }
            }
        }

        ClusterMember[][] ret = new ClusterMember[cm.length][];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = delN[i].stream().toArray(ClusterMember[]::new);
        }

        return ret;
    }

    public static void verifyGraph(File dir) throws IOException {
        File[] f = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".txt") && pathname.getName().contains("DelaunayGraph");
            }
        });

        for (File file : f) {
            boolean[][] graph = parseFileIntoGraph(file);
            for (int i = 0; i < graph.length; i++) {
                for (int j = 0; j < graph.length; j++) {
                    if (graph[i][j] ^ graph[j][i]) {
                        System.out.println("Error in " + file.getName() + ", cells " + i + " and " + j);
                    }
                }
            }
        }
    }

    private static boolean[][] parseFileIntoGraph(File f) throws IOException {
        List<String> l = IO.getListOfStringsFromStream(new FileInputStream(f));

        boolean[][] mtx = new boolean[l.size()][l.size()];

        for (int i = 0; i < mtx.length; i++) {
            String[] idx = l.get(i).split("\t")[1].split(",");
            for (String s : idx) {
                int j = Integer.parseInt(s);
                mtx[i][j] = true;
            }
        }
        return mtx;
    }

    private static List<Point2D> drawcircle(int x0, int y0, int radius) {
        int x = radius;
        int y = 0;
        int err = 0;

        List<Point2D> out = new LinkedList<>();

        while (x >= y) {
            out.add(new Point2D(x0 + x, y0 + y));
            out.add(new Point2D(x0 + y, y0 + x));
            out.add(new Point2D(x0 - y, y0 + x));
            out.add(new Point2D(x0 - x, y0 + y));
            out.add(new Point2D(x0 - x, y0 - y));
            out.add(new Point2D(x0 - y, y0 - x));
            out.add(new Point2D(x0 + y, y0 - x));
            out.add(new Point2D(x0 + x, y0 - y));

            y += 1;
            err += 1 + 2 * y;
            if (2 * (err - x) + 1 > 0) {
                out.add(new Point2D(x0 + x, y0 + y));
                out.add(new Point2D(x0 + y, y0 + x));
                out.add(new Point2D(x0 - y, y0 + x));
                out.add(new Point2D(x0 - x, y0 + y));
                out.add(new Point2D(x0 - x, y0 - y));
                out.add(new Point2D(x0 - y, y0 - x));
                out.add(new Point2D(x0 + y, y0 - x));
                out.add(new Point2D(x0 + x, y0 - y));
                x -= 1;
                err += 1 - 2 * x;
            }
        }

        return out;

    }

}
