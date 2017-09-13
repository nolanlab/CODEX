/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.toolkit;

import clustering.Datapoint;
import clustering.Dataset;
import dataIO.DatasetStub;
import util.DefaultEntry;
import util.IO;
import util.MatrixOp;
import util.logger;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * @author Nikolay Samusik
 */
public class FeaturizeCellTypesKNN {

    private static BufferedWriter bwAnn;
    private static String[] sideColNames = new String[]{"tileX", "tileY", "tile_nr", "X", "Y", "Z", "ClusterID"};

    public static void main(String[] args) throws IOException {
        int a = 0;
        bwAnn = new BufferedWriter(new FileWriter("cellTypeAnnotation.txt"));
        File clusterIDNameKey = new File(args[a++]);
        int K = Integer.parseInt(args[a++]);
        int numCircles = Integer.parseInt(args[a++]);
        int regionWidthInTiles = Integer.parseInt(args[a++]);
        int tileWidth = Integer.parseInt(args[a++]);
        int tileHeight = Integer.parseInt(args[a++]);
        for (File fcs : (new File(".")).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".fcs");
            }
        })) {
            Dataset feat = computeFeatures(fcs, clusterIDNameKey, K, numCircles, regionWidthInTiles, tileWidth, tileHeight);
            Dataset filter = filterDataset(feat, "9639");
            if (feat != null) {
                filter.writeToFile(new File(getFeatName(fcs)), true, true);
            }
        }
    }

    private static String getFeatName(File fcs) {
        return "Featurized" + fcs.getName().substring(0, fcs.getName().length() - 4) + ".csv";
    }

    public static Map<Integer, String> getClusterNames(File clusterIDNameKey) throws IOException {
        ArrayList<String> al = IO.getListOfStringsFromStream(new FileInputStream(clusterIDNameKey));

        return al.stream().
                filter(c -> c.split("[\t,]").length > 1).
                collect(Collectors.toMap(c -> Integer.parseInt(c.split("[\t,]")[0]), c -> c.split("[\t,]")[1]));
    }

    public static Dataset computeFeatures(File clustering, File clusterIDNameKey, int K, int numCircles, int regionWidthInTiles, int tileWidth, int tileHeight) throws IOException {
        DatasetStub ds = DatasetStub.createFromFCS(clustering);
        String[] colNames = ds.getShortColumnNames();
        int xCol = -1;
        int yCol = -1;
        int zCol = -1;
        int tileCol = -1;
        int clusterCol = -1;

        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i].equals("X.X")) {
                xCol = i;
            }
            if (colNames[i].equals("Y.Y")) {
                yCol = i;
            }
            if (colNames[i].equals("Z.Z")) {
                zCol = i;
            }
            if (colNames[i].equals("tile_nr.tile_nr")) {
                tileCol = i;
            }
            if (colNames[i].contains("cluster X-shift")) {
                clusterCol = i;
            }
        }

        String fileName = getFeatName(clustering);
        try {
            if (xCol == -1) {
                throw new IllegalStateException("x column could not be identified");
            }
            if (yCol == -1) {
                throw new IllegalStateException("y column could not be identified");
            }
            if (zCol == -1) {
                throw new IllegalStateException("z column could not be identified");
            }
            if (tileCol == -1) {
                throw new IllegalStateException("tile_nr column could not be identified");
            }
            if (clusterCol == -1) {
                throw new IllegalStateException("ClusterID column could not be identified");
            }
        } catch (IllegalStateException e) {
            logger.print(Arrays.toString(colNames));
            throw e;
        }

        ArrayList<Datapoint> dp = new ArrayList<>();

        Map<Integer, String> cidMap = getClusterNames(clusterIDNameKey);

        Entry<Integer, String>[] s = cidMap.entrySet().toArray(new Entry[cidMap.size()]);

        HashMap<String, Integer> distinctClusterNameMap = new HashMap<>();

        for (Entry<Integer, String> entry : s) {
            if (!distinctClusterNameMap.containsKey(entry.getValue())) {
                distinctClusterNameMap.put(entry.getValue(), entry.getKey());
            }
        }

        String[] clusterNames = new String[distinctClusterNameMap.size()];
        Entry<String, Integer>[] s2 = distinctClusterNameMap.entrySet().toArray(new Entry[cidMap.size()]);
        HashMap<Integer, Integer> clusterIndices = new HashMap<>();

        for (int i = 0; i < clusterNames.length; i++) {
            clusterNames[i] = s2[i].getKey();
            clusterIndices.put(s2[i].getValue(), i);
        }

        int maxTileX = 0;
        int maxTileY = 0;

        HashMap<Integer, Double> hmCIDfreq = new HashMap<>();

        String fname = getFeatName(clustering);
        
        fname = fname.substring(0, fname.length()-4);

        int cnt =0;
        for (int i = 0; i < ds.getRowCount(); i++) {
            double[] vec = ds.getRow(i);
            if (vec == null) {
                logger.print("Skipping row#" + ds.getSkippedRows()[ds.getSkippedRows().length - 1]);
                continue;
            }
            //Tiles are zero-based
            int tile = (int) (vec[tileCol]-1);
            int tileY = tile / regionWidthInTiles;
            int tileX = tile % regionWidthInTiles;

            maxTileX = Math.max(maxTileX, tileX);
            maxTileY = Math.max(maxTileY, tileY);

            int cid = (int) vec[clusterCol];
            if (cidMap.containsKey(cid)) {
                int remappedCID = distinctClusterNameMap.get(cidMap.get(cid));
                if (!hmCIDfreq.containsKey(remappedCID)) {
                    hmCIDfreq.put(remappedCID, new Double(1));
                }
                hmCIDfreq.put(remappedCID, hmCIDfreq.get(remappedCID) + 1);

                Datapoint d = new Datapoint(String.valueOf(remappedCID), new double[]{vec[xCol] + (tileX * tileWidth), vec[yCol] + (tileY * tileHeight), vec[zCol]}, new double[]{tileX, tileY, tile, vec[xCol], vec[yCol], vec[zCol], cid}, remappedCID, ds.getFileName(), cnt++);
                bwAnn.write("Featurized " +d.getFullName() + "\t" + cidMap.get(cid) + "\n");
                dp.add(d);
            }
        }
        
        
        for (Integer i : hmCIDfreq.keySet()) {
            hmCIDfreq.put(i, hmCIDfreq.get(i) / dp.size());
        }

        ArrayList<Datapoint>[][] tileGrid = new ArrayList[maxTileX + 1][maxTileY + 1];

        for (Datapoint d : dp) {
            int x = (int) d.getSideVector()[0];
            int y = (int) d.getSideVector()[1];
            if (tileGrid[x][y] == null) {
                tileGrid[x][y] = new ArrayList<>();
            }
            tileGrid[x][y].add(d);
        }

        Datapoint[] adp = dp.toArray(new Datapoint[dp.size()]);

        int[] Karr = new int[numCircles];

        for (int i = 1; i <= Karr.length; i++) {
            Karr[i - 1] = K * (int) Math.pow(i, 3);
        }

        Datapoint[][] sortedDP = getSortedDp(adp, Karr[numCircles - 1], tileGrid);

        return createDataset(sortedDP, Karr, adp, clusterIndices, clusterNames, hmCIDfreq);
    }
    
    private static Dataset filterDataset(Dataset ds, String DPNameBeginsWith){
        ArrayList <Datapoint> al = new ArrayList<>();
        int k = 0;
        for (Datapoint d : ds.getDatapoints()) {
            if(d.getName().endsWith(DPNameBeginsWith)){
                d.setID(k++);
                al.add(d);
            }
        }
        return new Dataset(DPNameBeginsWith, al.toArray(new Datapoint[al.size()]), ds.getFeatureNames(), ds.getSideVarNames());
    }

    private static Dataset createDataset(Datapoint[][] sortedDP, int[] K, Datapoint[] dp, HashMap<Integer, Integer> CIDIndex, String[] clusterNames, HashMap<Integer, Double> hmCIDfreq) {
        Datapoint[] out = new Datapoint[dp.length];
        String[] colNames = new String[clusterNames.length * K.length];

        int t = 0;
        for (int k : K) {
            for (String cn : clusterNames) {
                colNames[t++] = "KNN" + k + "_freqOf_" + cn;
            }
        }

        for (int i = 0; i < dp.length; i++) {
            double[] neighVec = new double[0];
            for (int k : K) {
                neighVec = MatrixOp.concat(neighVec, avgNeighVec(sortedDP[i], k, CIDIndex, hmCIDfreq));
            }
            out[i] = new Datapoint(dp[i].getFullName(), neighVec, dp[i].getSideVector(), i);
        }

        return new Dataset("out", out, colNames, sideColNames);
    }

    private static double[] avgNeighVec(Datapoint[] sortedDP, int K, HashMap<Integer, Integer> CIDIndex, HashMap<Integer, Double> hmCIDfreq) {
        double[] vec = new double[CIDIndex.size()];
        double w = 1.0 / K;
        for (int i = 0; i < Math.min(K, sortedDP.length); i++) {
            int idx = CIDIndex.get(sortedDP[i].getID());
            double freq = hmCIDfreq.get(sortedDP[i].getID());
            vec[idx] += w/freq;
        }
        return vec;
    }

    public static Datapoint[][] getSortedDp(Datapoint[] dp, final int maxK, ArrayList<Datapoint>[][] tileGrid) {
        Datapoint[][] sortedDp = new Datapoint[dp.length][maxK];

        int cpu = Runtime.getRuntime().availableProcessors();
        Thread[] t = new Thread[cpu];

        logger.print("Computing " + maxK + "-NN graph of " + dp.length + "datapoints");

        final AtomicInteger ai = new AtomicInteger(-1);
        ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < t.length; i++) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    int d;
                    while ((d = ai.addAndGet(1)) < dp.length) {
                        if (d % 10 == 0) {
                            logger.print("Computing KNN graph: " + d);
                        }

                        ArrayList<DefaultEntry<Datapoint, Double>> arr = new ArrayList<>();
                        double[] vec = dp[d].getVector();
                        int x = (int) dp[d].getSideVector()[0];
                        int y = (int) dp[d].getSideVector()[1];

                        for (int ax = -1; ax <= 1; ax++) {
                            if (x + ax < 0 || x + ax >= tileGrid.length) {
                                continue;
                            }
                            for (int ay = -1; ay <= 1; ay++) {
                                if (y + ay < 0 || y + ay >= tileGrid[0].length) {
                                    continue;
                                }
                                if (tileGrid[x + ax][y + ay] == null) {
                                    continue;
                                }
                                for (Datapoint odp : tileGrid[x + ax][y + ay]) {
                                    arr.add(new DefaultEntry<>(odp, MatrixOp.getEuclideanDistance(vec, odp.getVector())));
                                }
                            }
                        }

                        Collections.sort(arr, (Entry<Datapoint, Double> o1, Entry<Datapoint, Double> o2) -> (int) Math.signum(o1.getValue() - o2.getValue()));
                        int lim = Math.min(maxK, arr.size());
                        sortedDp[d] = new Datapoint[lim];
                        for (int j = 0; j < lim; j++) {
                            sortedDp[d][j] = arr.get(j).getKey();
                        }
                    }
                }
            }
            );
        }
        es.shutdown();
        try {
            es.awaitTermination(1000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            logger.showException(e);
        }
        return sortedDp;
    }
    
    
      
       
    private static class Point3D {

    final int x, y, z;
  

    @Override
    public String toString() {
        return "{" + x + "," + y + "," + z + "}";
    }

    @Override
    public int hashCode() {
        return (String.valueOf(x) + String.valueOf(y) + String.valueOf(z)).hashCode();
    }

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Point3D) && ((Point3D) obj).x == this.x && ((Point3D) obj).y == this.y && ((Point3D) obj).z == this.z;
    }

}

}
