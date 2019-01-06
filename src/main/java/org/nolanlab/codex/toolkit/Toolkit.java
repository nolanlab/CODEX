/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.toolkit;

import clustering.Cluster;
import clustering.ClusterMember;
import clustering.Datapoint;
import org.nolanlab.codex.upload.Experiment;
import dataIO.DatasetStub;
import util.DefaultEntry;
import util.IO;
import util.logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 *
 * @author Nikolay Samusik
 */
public class Toolkit {

    public static HashMap<String, Object> lkp = new HashMap<>();

    private static class JoinEntry {

        public final String file;
        public final int idx;
        public final double entropy;

        public JoinEntry(String file, int idx, double entropy) {
            this.file = file;
            this.idx = idx;
            this.entropy = entropy;
        }

        public String getFile() {
            return file;
        }
        public int getIdx() {
            return idx;
        }
        public double getEntropy() {
            return entropy;
        }
    }

    private static void loadEntropyMap() throws IOException {
        File f = new File(lkp.get("entropyFile").toString());
        DatasetStub ds = DatasetStub.createFromTXT(f);
        List<JoinEntry> lst = new ArrayList<>();
        for (int i = 0; i < ds.getRowCount(); i++) {
            String name = ds.getRowName(i);
            double entropy = ds.getRow(i)[3];
            int idx = (int) ds.getRow(i)[4];
            lst.add(new JoinEntry(name, idx, entropy));
        }
        final HashMap<String, double[]> hm = new HashMap<>();
        lst.stream().collect(Collectors.groupingBy(JoinEntry::getFile)).forEach((file, list) -> {
            int size = list.stream().max((a, b) -> a.idx - b.idx).get().getIdx();
            double[] arr = new double[size + 1];
            list.forEach((s) -> {
                arr[s.idx] = s.entropy;
            });
            hm.put(file, arr);
        });
        lkp.put("entropyFile", hm);
    }

    public static Object lookup(String s) {
        if (!lkp.containsKey(s)) {
            log("Error: object '" + s + "' is not in the lookup");
        }
        return lkp.get(s);
    }

    public static void lookup(String s, Object obj) {
        if (lkp.containsKey(s)) {
            log("Warning: object '" + s + "' is already in the lookup and will be overwritten");
        }
        lkp.put(s, obj);
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void computeCellTypeCooccurenceCorrelation() throws IOException {

    }

    public static void loadExperiment() throws FileNotFoundException {
        File f = new File("Experiment.json");
        if (!f.exists()) {
            throw new IllegalStateException("Error: Experiment.json could not be found.");
        }

        Experiment exp = Experiment.loadFromJSON(f);
        lookup("experiment", exp);
    }

    public static Entry<ClusterMember[], ClusterMember[][]> computeGraph(File fcs) throws IOException, SQLException {

        Experiment exp = (Experiment) lookup("experiment");

        int regionWidthInTiles = exp.region_width;
        int regionHeightInTiles = exp.region_height;
        int tileWidth = exp.tile_width;
        int tileHeight = exp.tile_height;

        Cluster[] cl = createClusters(fcs);

        ClusterMember[] cm = Arrays.asList(cl).stream().flatMap(e -> Arrays.asList(e.getClusterMembers()).stream()).sorted((a, b) -> a.getDatapoint().getID() - b.getDatapoint().getID()).toArray(ClusterMember[]::new);

        Entry<ClusterMember[], ClusterMember[][]> graph = new DefaultEntry<>(cm, DelaunayGraph.findDelaunayNeighbors(cm, regionWidthInTiles * tileWidth, regionHeightInTiles * tileHeight));
        return graph;
    }

    public static void loadClusterNames() throws IOException {
        File clusterIDNameKey = new File(lookup("clusterIDFile").toString());
        if (!clusterIDNameKey.exists()) {
            throw new IllegalStateException(lookup("clusterIDFile").toString() + "  file could not be found in the working directory");
        }
        ArrayList<String> al = IO.getListOfStringsFromStream(new FileInputStream(clusterIDNameKey));
        lookup("clusterIDtoName", al.stream().filter(c -> c.split("[\t,]").length > 1).
        collect(Collectors.toMap(c -> Integer.parseInt(c.split("[\t,]")[0]), c -> c.split("[\t,]")[1])));
    }

    public static Entry<BufferedImage,BufferedImage> renderGraph(Entry<ClusterMember[], ClusterMember[][]> graph, String sourceFileName) throws IOException {

        Experiment exp = (Experiment) lookup("experiment");
        int regionWidthInTiles = exp.region_width;
        int regionHeightInTiles = exp.region_height;
        int tileWidth = exp.tile_width;
        int tileHeight = exp.tile_height;
        Map<Integer, String> cidMap = (Map<Integer, String>) lookup("clusterIDtoName");

        String file = sourceFileName.split("\\.")[0];
        Entry<BufferedImage,BufferedImage> bi = DelaunayGraph.renderRegion(file, cidMap, graph, regionWidthInTiles, tileWidth, regionHeightInTiles, tileHeight);
        return bi;
    }

    //returns Entry<Integer, Datapoint>[] clusArray and creates a String[] clusterNames on the lookup with remapped cluster names
    public static Cluster[] createClusters(File fcs) {
        Experiment exp = (Experiment) lookup("experiment");
        int regionHeightInTiles = exp.region_height;
        int regionWidthInTiles = exp.region_width;
        int tileWidth = exp.tile_width;
        int tileHeight = exp.tile_height;
        DatasetStub ds = DatasetStub.createFromFCS(fcs);
        String[] colNames = ds.getShortColumnNames();
        int xCol = -1;
        int yCol = -1;
        int tileCol = -1;
        int clusterCol = -1;

        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i].equals("X.X")) {
                xCol = i;
            }

            if (colNames[i].equals("Y.Y")) {
                yCol = i;
            }

            if (colNames[i].equals("tile_nr.tile_nr")) {
                tileCol = i;
            }

            if (colNames[i].toLowerCase().contains("cluster")) {
                clusterCol = i;
            }
        }

        try {
            if (xCol == -1) {
                throw new IllegalStateException("x column could not be identified");
            }
            if (yCol == -1) {
                throw new IllegalStateException("y column could not be identified");
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

        Datapoint[] dp = new Datapoint[(int) ds.getRowCount()];

        Map<Integer, String> cidMap = (Map<Integer, String>) lookup("clusterIDtoName");

        cidMap.values().stream().distinct();

        HashMap<String, Integer> remappedClusterID = new HashMap<>();

        int c = 0;
        for (Entry<Integer, String> cid : cidMap.entrySet().stream().sorted((a, b) -> a.getKey() - b.getKey()).collect(Collectors.toList())) {
            if (!remappedClusterID.containsKey(cid.getValue())) {
                remappedClusterID.put(cid.getValue(), c++);
            };
        }

        remappedClusterID.put("null", c);

        int minTileIndex = Integer.MAX_VALUE;

        for (int i = 0; i < ds.getRowCount(); i++) {
            double[] vec = ds.getRow(i);
            if (vec == null) {
                logger.print("Skipping row#" + ds.getSkippedRows()[ds.getSkippedRows().length - 1]);
                continue;
            }
            int tile = (int) (vec[tileCol]);
            minTileIndex = Math.min(minTileIndex, tile);
        }

        int regSize = (regionHeightInTiles * regionWidthInTiles);

        int probableRegIDX = minTileIndex / regSize;

        minTileIndex = 1 + probableRegIDX * regSize;

        int prevTile = -1;
        double[] nullvec = new double[0];

        for (int i = 0; i < ds.getRowCount(); i++) {
            double[] vec = ds.getRow(i);
            if (vec == null) {
                logger.print("Skipping row#" + ds.getSkippedRows()[ds.getSkippedRows().length - 1]);
                continue;
            }
            //Tile is one-based
            int tile = (int) (vec[tileCol] - minTileIndex);
            int tileX = tile / regionHeightInTiles;
            int tileY = tile % regionHeightInTiles;

            if (tile != prevTile) {
                prevTile = tile;
                logger.print("tile=" + vec[tileCol] + ", X=" + tileX + ", Y=" + tileY);
            }

            int cid = (int) vec[clusterCol];

            String clusterName = cidMap.get(cid);
            if (clusterName == null) {
                clusterName = "null";
            }

            Datapoint d = new Datapoint(clusterName, new double[]{vec[xCol] + ((tileX) * tileWidth), vec[yCol] + ((tileY) * tileHeight)}, nullvec, i, ds.getFileName(), i);
            dp[i] = d;
        }

        String[] clusterNames = remappedClusterID.entrySet().stream().sorted((a, b) -> a.getValue() - b.getValue()).map(a -> a.getKey()).toArray(String[]::new);

        Cluster[] cl = new Cluster[remappedClusterID.size()];

        for (int i = 0; i < cl.length; i++) {
            final int cid = i;
            Datapoint[] cldp = Arrays.asList(dp).stream().filter(e -> remappedClusterID.get(e.getName()) == cid).toArray(Datapoint[]::new);
            cl[i] = new Cluster(cldp, nullvec, nullvec, clusterNames[i]);
            cl[i].setID(i);
        }

        logger.print("Imported #" + dp.length + " out of #" + ds.getRowCount());
        return cl;

    }

    private static String getFeatName(File fcs) {
        return "Featurized" + fcs.getName().substring(0, fcs.getName().length() - 4) + ".csv";
    }

    public static void saveGraph(Entry<ClusterMember[], ClusterMember[][]> graph, String sourceFileName) throws IOException {
        DelaunayGraph.saveDelaunayGraph(sourceFileName, graph);
    }

    public static void main(String[] args) throws Exception {
        //testPointScan();

        if(args.length < 1){
            System.out.println("USAGE:\n java -jar CODEXToolkit.jar <path-to-dir>\n<path-to-dir> is a path to directory " +
                    "that contains clustered FCS files (there must be one column with a name containing the word 'cluster') " +
                    "and a ClusterIDtoName.txt file that is a two-column tab delimited file that maps integer clusterIDs from the FCS column to" +
                    "names of cell types. Unnamed clusters are omitted from the analysis while multiple clusters with the same name are considered" +
                    "to be one cell type");
            System.exit(1);
        }

        String baseDirStr = args[0];
        File baseDir = new File(baseDirStr);

        if(!baseDir.exists()){
            throw new IllegalArgumentException("the specified base directory does not exist: "+ baseDirStr);
        }

        boolean recompute = true;

        List<Matrix> ctrlMtxOR = new ArrayList<>();

        List<Matrix> assayMtxOR = new ArrayList<>();

        List<Matrix> ctrlMtxCount = new ArrayList<>();

        List<Matrix> assayMtxCount = new ArrayList<>();

        for (File fcs : (baseDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".fcs");
            }
        }))) {
            String file = "out" + File.separator +fcs.getName().split("\\.")[0];
            new File(file+".fcs").mkdirs();
            new File(file+".fcs").delete();
            
            File mtxFile = new File(file + "_CellTypeSpatialInteraction_OddsRatioMtx.txt");
            
                Matrix mtx;
                //BufferedWriter bwAnn = new BufferedWriter(new FileWriter("cellTypeAnnotation.txt"));
                lookup("clusterIDFile", "clusterIDtoName.txt");
                loadClusterNames();
                loadExperiment();
                Entry<ClusterMember[], ClusterMember[][]> graph = computeGraph(fcs);
                mtx = CellTypeCooccurence.getLogsOddsRatioMtx(graph, fcs.getName().split("\\.")[0]);
                saveGraph(graph, file);
                Entry<BufferedImage,BufferedImage> bi = renderGraph(graph, fcs.getName());
                ImageIO.write(bi.getKey(), "PNG", new File(file + "_VoronoiDiagram.png"));
                ImageIO.write(bi.getValue(), "PNG", new File(file + "_VoronoiDiagram_Legend.png"));

                mtx.writeToFile(new File(file + "_CellTypeSpatialInteraction_OddsRatioMtx.txt"));

                mtx = CellTypeCooccurence.getInteractionCountMtx(graph, fcs.getName().split("\\.")[0]);
                mtx.writeToFile(new File(file + "_CellTypeSpatialInteraction_InteractionCountMtx.txt"));

            Matrix cntMtx = new Matrix(new File(file + "_CellTypeSpatialInteraction_InteractionCountMtx.txt"));

            if (cntMtx.getName().toLowerCase().contains("balbc")) {
                ctrlMtxCount.add(cntMtx);
                assayMtxCount.add(cntMtx);
            } else {
                assayMtxCount.add(cntMtx);
            }

            /*mtx = CellTypeCooccurence.getPvalueMtx(graph, fcs.getName().split("\\.")[0]);
                mtx.writeToFile(new File(file + "_CellTypeSpatialInteraction_InteractionPValueMtx.txt"));*/
            //}
        }

        for (Matrix m : assayMtxOR) {
            Matrix mtx = CellTypeCooccurence.getZScoreMtx(ctrlMtxOR, m);
            mtx.writeToFile(new File(m.getName() + "_CellTypeSpatialInteraction_LogOddsRatioMtx_ZScore.txt"));
        }

        for (Matrix m : assayMtxCount) {
            Matrix mtx = CellTypeCooccurence.getZScoreMtx(ctrlMtxCount, m);
            mtx.writeToFile(new File(m.getName() + "_CellTypeSpatialInteraction_InteractionCountMtx_ZScore.txt"));

            Entry<Matrix, Image> entry = Strelochki.renderInteractionMatrixCounts(m, 0);

            //ImageIO.write((RenderedImage) entry.getValue(), "PNG", new File(entry.getKey().getName() + "_CellTypeSpatialInteractionCounts_Graph.png"));
        }

        /*
        Entry<Matrix, Image>[] img = Strelochki.renderInteractionMatrix(ctrlMtx, assayMtx, 1.0);

        for (Entry<Matrix, Image> entry : img) {
            ImageIO.write((RenderedImage) entry.getValue(), "PNG", new File(entry.getKey().getName() + "_CellTypeSpatialInteraction_Graph.png"));
        }*/
        //fcs.getName().split("\\.")[0] + "_CellTypeSpatialInteraction_Graph"
        //ImageIO.write((RenderedImage) img, "PNG", new File(fcs.getName().split("\\.")[0] + "_CellTypeSpatialInteraction_Graph.png"));
        System.exit(0);
    }
}
