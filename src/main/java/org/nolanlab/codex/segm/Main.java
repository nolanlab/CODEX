package org.nolanlab.codex.segm;

import com.opencsv.CSVReader;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.io.FileSaver;
import ij.plugin.Duplicator;
import ij.plugin.FolderOpener;
import ij.plugin.HyperStackConverter;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.nolanlab.codex.upload.model.Experiment;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main {

    public static boolean printParams = false;
    public static final String revision = "CODEX-segm rev 6-JAN-2019";
    public static Properties params;
    public static String configName = null;
    public static String segOutLoc = null;

    public static void main(String[] args) throws Exception {

        File rootDir = null;
        File config = null;
        boolean showImage = false;
        boolean isImgSeqFolder = false;

        SegConfigParam segParam = new SegConfigParam();
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Invalid number of arguments");
            }
            if (args.length == 4) {
                rootDir = new File(args[0]);
                File tilesDir = new File(rootDir + File.separator + "tiles");
                if(tilesDir.exists()) {
                    System.out.println("Image sequence input folder directory structure recognized...");
                    isImgSeqFolder = true;
                    rootDir = new File(rootDir + File.separator + "tiles");
                }
                showImage = Boolean.parseBoolean(args[1]);
                printParams = Boolean.parseBoolean(args[2]);
                configName = args[3];
                segOutLoc = "segmented_" + configName;
            }
            if (args.length == 2) {
                //inputFolderDialog();
                //rootDir = new File(configField.getText());
                showImage = Boolean.parseBoolean(args[0]);
                printParams = Boolean.parseBoolean(args[1]);
                System.out.println("printParams = " + printParams);
            }

            if (rootDir != null) {
                if(!isImgSeqFolder) {
                    config = new File(rootDir + File.separator + "config.txt");
                } else {
                    File segOut = new File(rootDir.getParentFile() + File.separator + segOutLoc);
                    if(!segOut.exists()) {
                        segOut.mkdirs();
                    }
                    config = new File(segOut + File.separator + "config.txt");
                }
                if (!config.exists()) {
                    throw new IllegalArgumentException("Config file not found:\n" + config.getPath());
                }
            }
            params = new Properties();
            FileInputStream input = new FileInputStream(config);
            params.load(input);

            int radius = Integer.parseInt(params.getProperty("radius", "5").trim());
            boolean use_membrane = Boolean.parseBoolean(params.getProperty("use_membrane", "false").trim());
            double maxCutoff = Double.parseDouble(params.getProperty("maxCutoff", "0.99").trim());
            double minCutoff = Double.parseDouble(params.getProperty("minCutoff", "0.02").trim());
            double relativeCutoff = Double.parseDouble(params.getProperty("relativeCutoff", "0.05").trim());
            int nuclearStainChannel = Integer.parseInt(params.getProperty("nuclearStainChannel", "-1").trim());
            int nuclearStainCycle = Integer.parseInt(params.getProperty("nuclearStainCycle", "-1").trim());
            int membraneStainChannel = Integer.parseInt(params.getProperty("membraneStainChannel", "-1").trim());
            int membraneStainCycle = Integer.parseInt(params.getProperty("membraneStainCycle", "-1").trim());
            double inner_ring_size = Double.parseDouble(params.getProperty("inner_ring_size", "0.6").trim());
            boolean count_puncta = Boolean.parseBoolean(params.getProperty("count_puncta", "false").trim());
            boolean dont_inverse_memb = Boolean.parseBoolean(params.getProperty("dont_inverse_membrane", "false").trim());
            int concentricCircles = Integer.parseInt(params.getProperty("concentric_circle_featurization_steps", "0").trim());
            boolean delaunay_graph = Boolean.parseBoolean(params.getProperty("delaunay_graph", "true").trim());
            boolean single_plane_quant = Boolean.parseBoolean(params.getProperty("single_plane_quantification", "false").trim());

            boolean anisotropic_reg_growth = Boolean.parseBoolean(params.getProperty("anisotropic_region_growth", "true").trim());
            double size_cutoff_factor = Double.parseDouble(params.getProperty("cell_size_cutoff_factor", "1.0").trim());


            segParam.setRootDir(rootDir);
            segParam.setShowImage(showImage);
            segParam.setRadius(radius);
            segParam.setUse_membrane(use_membrane);
            segParam.setMaxCutoff(maxCutoff);
            segParam.setMinCutoff(minCutoff);
            segParam.setRelativeCutoff(relativeCutoff);
            segParam.setNuclearStainChannel(nuclearStainChannel);
            segParam.setNuclearStainCycle(nuclearStainCycle);
            segParam.setMembraneStainChannel(membraneStainChannel);
            segParam.setMembraneStainCycle(membraneStainCycle);
            segParam.setInner_ring_size(inner_ring_size);
            segParam.setCount_puncta(count_puncta);
            segParam.setDont_inverse_memb(dont_inverse_memb);
            segParam.setConcentricCircles(concentricCircles);
            segParam.setDelaunay_graph(delaunay_graph);
            segParam.setSizeCutoffFactor(size_cutoff_factor);
            segParam.setAnisotropicRegionGrowth(anisotropic_reg_growth);
            segParam.setSingle_plane_quant(single_plane_quant);

            System.out.printf("Using segmentation parameters:\n", new Object[0]);
            System.out.printf(params.toString().replace(',', '\n'), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Provided arguments as seen by the application:");
            for (int i = 0; i < args.length; ++i) {
                System.out.println("arg#" + i + "=" + args[i]);
            }
            System.out.println("Usage: java -jar codex.jar <directory-with-inFiles-and-config.txt> <showSegmentedImage[true,false]> <optional:quantifyMembraneIntensity[true,false]>");
            System.exit(0);
        }
        if (!rootDir.exists()) {
            throw new IllegalArgumentException("Error: Cannot find the input directoty");
        }

        File[] regFolder = rootDir.listFiles(r -> r.isDirectory() && r.getName().startsWith("reg0") && r.getName().contains("_X") && r.getName().contains("_Y"));
        if (regFolder != null && regFolder.length != 0) {
            File expJSON = new File(segParam.getRootDir().getParentFile() + File.separator + "Experiment.json");
            Experiment exp = Experiment.loadFromJSON(expJSON);
            for (int reg = 0; reg < regFolder.length; reg++) {
                if(segParam != null) {
                    FolderOpener fo = new FolderOpener();
                    fo.openAsVirtualStack(true);
                    ImagePlus imp = fo.openFolder(regFolder[reg].getPath());
                    ImagePlus hyp = HyperStackConverter.toHyperStack(imp, exp.channel_names.length, exp.num_z_planes, exp.num_cycles, "default", "Composite");
                    doSeg(regFolder[reg], hyp, segParam, true);
                }
            }
        }
        else {
            doSeg(rootDir, null, segParam, false);
        }
    }

    private static void doSeg(File f, ImagePlus imp, SegConfigParam segConfigParam, boolean imageSeq) throws Exception {
        int tile = 0;
        if(!imageSeq) {
            for (File currTiff : f.listFiles((dir, name) -> name.endsWith(".tiff") || name.endsWith(".tif"))) {
                segmentTiff(currTiff, imp, ++tile, segConfigParam, imageSeq);
            }
        }
        else {
            segmentTiff(null, imp, ++tile, segConfigParam, imageSeq);
        }
    }

    private static void segmentTiff(File currTiff, ImagePlus imp, int tile, SegConfigParam segConfigParam, boolean imageSeq) throws Exception {
        Duplicator dup = new Duplicator();
        int j;
        int i;
        if (!imageSeq) {
            System.out.print("\nprocessing file: " + currTiff.getName() + "\n");
        } else {
            System.out.print("\nprocessing image seq: " + imp.getTitle() + "\n");
        }
        if (currTiff != null && !imageSeq) {
            if (!currTiff.exists()) {
                throw new IllegalArgumentException("Error: Cannot find the input file:" + currTiff);
            }
            if (!imageSeq) {
                imp = IJ.openImage(currTiff.getAbsolutePath());
            }
            if (imp == null) {
                throw new IllegalStateException("Couldn't open the image file: " + currTiff);
            }
        }
        int[] readoutChannels = new int[imp.getNChannels()];
        for (int x = 0; x < imp.getNChannels(); x++) {
            readoutChannels[x] = x + 1;
        }
        imp.getNFrames();
        ImagePlus nucl = dup.run(imp, segConfigParam.getNuclearStainChannel(), segConfigParam.getNuclearStainChannel(), 1, imp.getNSlices(), segConfigParam.getNuclearStainCycle(), segConfigParam.getNuclearStainCycle());
        ImagePlus mult = nucl;
        if (Math.min(segConfigParam.getMembraneStainCycle(), segConfigParam.getMembraneStainChannel()) > 0) {
            System.out.println("Dividing by the membrane signal membrane");
            ImagePlus memb = dup.run(imp, segConfigParam.getMembraneStainChannel(), segConfigParam.getMembraneStainChannel(), 1, imp.getNSlices(), segConfigParam.getMembraneStainCycle(), segConfigParam.getMembraneStainCycle());

            for (int i2 = 1; i2 <= memb.getNSlices(); ++i2) {
                ImageProcessor ip = memb.getStack().getProcessor(i2);
                ip.add(1);
            }

            ImageCalculator ic = new ImageCalculator();
            mult =  ic.run("Divide stack float create", nucl, memb);

            for (int i2 = 1; i2 <= mult.getStack().getSize(); ++i2) {
                double ratio = nucl.getStack().getProcessor(i2).getStatistics().mean/mult.getStack().getProcessor(i2).getStatistics().mean;
                mult.getStack().getProcessor(i2).multiply(ratio);
            }
            memb = null;
        }
        nucl = null;

        System.gc();
        FFTFilter filter = new FFTFilter();
        filter.setup(10000.0, (double) segConfigParam.getRadius(), 0, 5.0, false, false, false);
        System.out.print("running FFT bandpass filter");
        filter.run(mult);

        boolean anisotropic_reg_growth = segConfigParam.isAnisotropicRegionGrowth();

        //GaussianBlur3D.blur(mult, radius, radius, radius);
        SegmentedObject[] cellsSegmentedObject = MaximaFinder3D.findCellsByIntensityGradient((ImagePlus) mult, segConfigParam.getRadius(), (double) segConfigParam.getMaxCutoff(), (double) segConfigParam.getMinCutoff(), (double) segConfigParam.getRelativeCutoff(), (boolean) segConfigParam.isShowImage(), segConfigParam.isSubtractInnerRing() ? 1.0 : segConfigParam.getInner_ring_size(), anisotropic_reg_growth);
        if (segConfigParam.isSubtractInnerRing()) {
            segConfigParam.setUse_membrane(false);
        }
        SegmentedObject[] innerRings = null;

        if (segConfigParam.isSubtractInnerRing()) {
            innerRings = MaximaFinder3D.findCellsByIntensityGradient((ImagePlus) mult, segConfigParam.getRadius(), (double) segConfigParam.getMaxCutoff(), (double) segConfigParam.getMinCutoff(), (double) segConfigParam.getInner_ring_size(), (boolean) segConfigParam.isShowImage(), 1.0, anisotropic_reg_growth);
        }

        if (segConfigParam.isShowImage()) {
            try {
                new ImageJ();
                mult.show();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //Filter out small sized regions and remove that row from the txt file

        double sizeCutoff = (segConfigParam.getSizeCutoffFactor())*((segConfigParam.getRadius() * segConfigParam.getRadius() * segConfigParam.getRadius()) * Math.PI * (4.0 / 3.0));
        System.out.println("Filtering small objects by size, cutoff = " + sizeCutoff + " init number of objects: "+cellsSegmentedObject.length);
        cellsSegmentedObject = Arrays.stream(cellsSegmentedObject).filter(c -> c.getPoints().length >= sizeCutoff).toArray(SegmentedObject[]::new);
        System.out.println("# of objects left after filtering = " + cellsSegmentedObject.length);
        if(cellsSegmentedObject.length<10){
            System.out.println("too few cell objects were found in this image. Try decreasing the cell_size_cutoff_factor, for instanece try setting it to 0.1 or 0.05");
        }

        BufferedImage[] bi2 = null;
        if(currTiff != null && !imageSeq) {
            //Apply overlay to the different Z stacks of the actual tif file based on different masks.
            bi2 = RegionImageWriter.writeRegionImage(cellsSegmentedObject, mult, currTiff.getName(), currTiff.getParentFile());
            ImagePlus copy = IJ.openImage(currTiff.getAbsolutePath());
            Overlay overlay = new Overlay();

            for (int z = 0; z < bi2.length; z++) {
                ImagePlus im2 = new ImagePlus("Image Slice: " + z, bi2[z]);
                ImageRoi imgRoi = new ImageRoi(0, 0, im2.getProcessor());
                imgRoi.setNonScalable(true);
                imgRoi.setZeroTransparent(true);
                imgRoi.setOpacity(1);
                imgRoi.setPosition(0, z + 1, 0);
                overlay.add(imgRoi);
            }
            copy.setOverlay(overlay);
            FileSaver fs = new FileSaver(copy);
            fs.saveAsTiff(currTiff.getAbsolutePath());
        }
        else {
            String regMaskName = imp.getTitle();
            File masksLoc = new File(segConfigParam.getRootDir().getParentFile() + File.separator + segOutLoc + File.separator + "masks" + File.separator + regMaskName);
            if(!masksLoc.exists()) {
                masksLoc.mkdirs();
            }
            bi2 = RegionImageWriter.writeRegionImage(cellsSegmentedObject, mult, imp.getTitle(), masksLoc);
        }

        File dir = (currTiff == null) ? segConfigParam.getRootDir() : currTiff.getParentFile();
        File bestFocusDir = new File(dir + File.separator + "bestFocus");

        if (!bestFocusDir.exists()) {
            System.out.println("Best focus folder cannot be found. Cannot apply overlays here." + bestFocusDir);
        } else {
            File[] bestFocusFiles = bestFocusDir.listFiles(t -> (t.getName().endsWith("tif") || t.getName().endsWith("tiff")));
            if (bestFocusFiles.length != 0) {
                try {
                    if(currTiff != null && !imageSeq) {
                        System.out.println("Applying mask/overlay for bestFocus file: " + FilenameUtils.removeExtension(currTiff.getName()));
                        applyBestFocusOverlay(bestFocusDir, currTiff, bi2, imageSeq);
                    }
                    else {
                        System.out.println("Applying mask/overlay for bestFocus file: " + imp.getTitle());
                        applyBestFocusOverlay(bestFocusDir, new File(segConfigParam.getRootDir()+File.separator+imp.getTitle()), bi2, imageSeq);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("No files present inside the best focus folder, so not applying the overlays here.");
            }
        }

        int numFrames = imp.getNFrames();
        String title = imp.getTitle();
        File fcsLoc = null;
        File compLoc = null;
        File uncompLoc = null;
        if(imageSeq) {
            fcsLoc = new File(segConfigParam.getRootDir().getParentFile() + File.separator + segOutLoc +
                    File.separator + "FCS");
            if (!fcsLoc.exists()) {
                fcsLoc.mkdirs();
            }
            compLoc = new File(segConfigParam.getRootDir().getParentFile() + File.separator + segOutLoc +
                    File.separator + "FCS" + File.separator + "compensated");
            if (!compLoc.exists()) {
                compLoc.mkdirs();
            }
            uncompLoc = new File(segConfigParam.getRootDir().getParentFile() + File.separator + segOutLoc +
                    File.separator + "FCS" + File.separator + "uncompensated");
            if (!uncompLoc.exists()) {
                uncompLoc.mkdirs();
            }
        }


        if (cellsSegmentedObject.length == 0) {
            System.out.println("Didn't find any cells here. exiting");

            BufferedWriter bwUncomp = null;
            BufferedWriter bwComp = null;

            if(currTiff != null && !imageSeq) {
                bwUncomp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Uncompensated.txt"));
                bwComp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Compensated.txt"));
            }
            else {
                bwUncomp = new BufferedWriter(new FileWriter(new File(uncompLoc + File.separator + title) + "_Expression_Uncompensated.txt"));
                bwComp = new BufferedWriter(new FileWriter(new File(compLoc + File.separator + title) + "_Expression_Compensated.txt"));
            }

            for (BufferedWriter bw : new BufferedWriter[]{bwUncomp, bwComp}) {
                bw.write("cell_id\ttile_nr\tX\tY\tZ\tsize");
                for (int i3 = 1; i3 <= numFrames; ++i3) {
                    for (int j2 = 0; j2 < readoutChannels.length; ++j2) {
                        bw.write("\tCyc_" + i3 + "_ch_" + readoutChannels[j2]);
                    }
                }
                for (int k = 0; k < segConfigParam.getConcentricCircles(); k++) {
                    for (int i3 = 1; i3 <= numFrames; ++i3) {
                        for (int j2 = 0; j2 < readoutChannels.length; ++j2) {
                            bw.write("\tCircle_" + k + "_Cyc_" + i3 + "_ch_" + readoutChannels[j2]);
                        }
                    }
                }
                bw.write("\n");
            }
            BufferedWriter bwGN = null;
            if(currTiff != null && !imageSeq) {
                bwGN = new BufferedWriter(new FileWriter(currTiff.getPath() + "_GabrielGraph.txt"));
            }
            else {
                bwGN = new BufferedWriter(new FileWriter(new File(fcsLoc + File.separator + title) + "_GabrielGraph.txt"));
            }
            bwGN.write("");
            bwGN.flush();
            bwGN.close();
            //continue;
        }

        int w = mult.getWidth();
        int h = mult.getHeight();
        int d = mult.getStackSize();
        mult = null;
        System.gc();
        System.out.println("Computing region intensities " + (segConfigParam.isUse_membrane() ? "by membrane" : "by whole cell"));
        double[][] regionIntensities = new double[cellsSegmentedObject.length][(imp.getNFrames() * readoutChannels.length)];
        ImagePlus mem = null;
        if (segConfigParam.isUse_membrane()) {
            mem = dup.run(imp, segConfigParam.getMembraneStainChannel(), segConfigParam.getMembraneStainChannel(), 1, imp.getNSlices(), segConfigParam.getMembraneStainCycle(), segConfigParam.getMembraneStainCycle());
        }
        for (int cycle = 1; cycle <= imp.getNFrames(); ++cycle) {
            for (int ch = 0; ch < readoutChannels.length; ++ch) {
                System.out.println("Cycle:" + cycle + ", channel" + readoutChannels[ch]);
                ImagePlus readout = dup.run(imp, readoutChannels[ch], readoutChannels[ch], 1, imp.getNSlices(), cycle, cycle);

                double[] intens = null;
                if (!segConfigParam.isCount_puncta()) {
                    intens = segConfigParam.isUse_membrane() ? Segmentation.computeMembraneIntensityOfRegions((ImageStack) readout.getImageStack(),
                            (ImageStack) mem.getImageStack(), (SegmentedObject[]) cellsSegmentedObject) :
                            Segmentation.computeMeanIntensityOfRegions((ImageStack) readout.getImageStack(), (SegmentedObject[]) cellsSegmentedObject, segConfigParam.isSingle_plane_quant());
                    if (segConfigParam.isSubtractInnerRing()) {
                        double[] innerRingIntens = Segmentation.computeMeanIntensityOfRegions((ImageStack) readout.getImageStack(), (SegmentedObject[]) innerRings , segConfigParam.isSingle_plane_quant());
                        for (int i4 = 0; i4 < intens.length; ++i4) {
                            intens[i4] -= innerRingIntens[i4];
                        }
                    }
                } else {
                    System.out.println("Counting puncta:");
                    intens = Segmentation.computePunctaCountOfRegions(readout, (SegmentedObject[]) cellsSegmentedObject, 100);
                }

                for (int i4 = 0; i4 < intens.length; ++i4) {
                    regionIntensities[i4][(cycle - 1) * readoutChannels.length + ch] = intens[i4];
                }
            }
        }

        ProfileAverager[][] pa = new ProfileAverager[cellsSegmentedObject.length][segConfigParam.getConcentricCircles()];

        System.out.println("Featurizing circles:" + segConfigParam.getConcentricCircles());
        for (int ci = 0; ci < segConfigParam.getConcentricCircles(); ci++) {
            System.out.println("Circle#" + (ci + 1) + ":" + Math.pow((segConfigParam.getRadius() * 2), 1 + (ci / 3.0)));
        }
        for (int r = 0; r < cellsSegmentedObject.length; ++r) {
            Point3D cent = cellsSegmentedObject[r].getCenter();
            for (int k = 0; k < cellsSegmentedObject.length; ++k) {
                double dist = Segmentation.dist(cent, cellsSegmentedObject[k].getCenter());
                for (int ci = 0; ci < segConfigParam.getConcentricCircles(); ci++) {
                    if (pa[r][ci] == null) {
                        pa[r][ci] = new ProfileAverager();
                    }
                    if (dist < Math.pow((segConfigParam.getRadius() * 2), 1 + (ci / 2.0))) {
                        pa[r][ci].addProfile(regionIntensities[k]);
                    }
                }
            }
        }

        double[][] featurizedVec = new double[cellsSegmentedObject.length][0];

        for (int r = 0; r < cellsSegmentedObject.length; ++r) {
            for (int ci = 0; ci < segConfigParam.getConcentricCircles(); ci++) {
                double[] avg = pa[r][ci].count > 0 ? pa[r][ci].getAverage() : new double[(imp.getNFrames() * readoutChannels.length)];
                featurizedVec[r] = MatrixOp.concat(featurizedVec[r], avg);
            }
        }

        imp = null;
        System.gc();
        ArrayList<Cell> cellsForTile = new ArrayList<>();

        for (i = 0; i < regionIntensities.length; ++i) {
            Cell c = new Cell(i + 1, cellsSegmentedObject[i], tile, regionIntensities[i], featurizedVec[i]);
            cellsForTile.add(c);
        }
        regionIntensities = new double[cellsForTile.size()][];
        for (i = 0; i < regionIntensities.length; ++i) {
            regionIntensities[i] = ((Cell) cellsForTile.get(i)).getExpressionVector();
        }
        Cell[] cellArr = cellsForTile.toArray(new Cell[cellsForTile.size()]);
        System.out.print("Building adj graph");
        double[][] adjN = Neighborhood.buildAdjacencyMatrix(cellArr, w, h, d, true , segConfigParam.isSingle_plane_quant());
        cellsForTile.clear();
        System.out.println("Compensating:");
        double[][] compRegionIntensities = Segmentation.compensatePositionalSpilloverOfExpressionMtx(cellsSegmentedObject, adjN, regionIntensities);
        for (int dt = 0; dt < compRegionIntensities.length; ++dt) {
            int id = dt + 1;
            Cell c = new Cell(id, cellsSegmentedObject[dt], tile, compRegionIntensities[dt], featurizedVec[dt]);
            cellsForTile.add(c);
        }
        Cell[] compCellArray = cellsForTile.toArray(new Cell[cellsForTile.size()]);
        adjN = null;
        System.gc();

        BufferedWriter bwUncomp = null;
        BufferedWriter bwComp = null;
        if(currTiff != null && !imageSeq) {
            bwUncomp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Uncompensated.txt"));
            bwComp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Compensated.txt"));
        }
        else {
            bwUncomp = new BufferedWriter(new FileWriter(new File(uncompLoc+File.separator+title) + "_Expression_Uncompensated.txt"));
            bwComp = new BufferedWriter(new FileWriter(new File(compLoc+File.separator+title) + "_Expression_Compensated.txt"));
        }
        for (BufferedWriter bw22 : new BufferedWriter[]{bwUncomp, bwComp}) {
            bw22.write("cell_id\ttile_nr\tX\tY\tZ\tsize");
            for (int i6 = 1; i6 <= numFrames; ++i6) {
                for (j = 0; j < readoutChannels.length; ++j) {
                    bw22.write("\tCyc_" + i6 + "_ch_" + readoutChannels[j]);
                }
            }
            for (int k = 0; k < segConfigParam.getConcentricCircles(); k++) {
                for (int i3 = 1; i3 <= numFrames; ++i3) {

                    for (int j2 = 0; j2 < readoutChannels.length; ++j2) {
                        bw22.write("\tCircle_" + k + "_Cyc_" + i3 + "_ch_" + readoutChannels[j2]);
                    }
                }
            }
            bw22.write("\n");
        }
        for (BufferedWriter bw : new BufferedWriter[]{bwUncomp, bwComp}) {
            Cell[] cells = bw == bwUncomp ? cellArr : compCellArray;
            j = cells.length;
            for (int k = 0; k < j; ++k) {
                Cell c = cells[k];
                bw.write("" + c.getId() + "\t");
                bw.write("" + c.getTile() + "\t");
                bw.write("" + c.getSegmentedObject().getCenter().x + "\t");
                bw.write("" + c.getSegmentedObject().getCenter().y + "\t");
                bw.write("" + c.getSegmentedObject().getCenter().z + "\t");
                bw.write("" + c.getSegmentedObject().getPoints().length + "\t");
                double[] ri = MatrixOp.concat(c.getExpressionVector(), c.getNeighFeaturizationVec());
                for (int ch = 0; ch < ri.length; ++ch) {
                    bw.write(String.valueOf(ri[ch]));
                    bw.write("\t");
                }

                bw.newLine();
            }
        }
        for (BufferedWriter bw22 : new BufferedWriter[]{bwUncomp, bwComp}) {
            bw22.flush();
            bw22.close();
        }
        if (segConfigParam.isDelaunay_graph()) {
            System.out.println("Computing Delaunay graph:");
            Collection<Cell>[] gn = Neighborhood.findDelaunayNeighbors(cellArr, (int) w, (int) h, (int) d);
            BufferedWriter bwGN = null;
            if(currTiff != null && !imageSeq) {
                bwGN = new BufferedWriter(new FileWriter(currTiff.getPath() + "_DelaunayGraph.txt"));
            }
            else {
                bwGN = new BufferedWriter(new FileWriter(fcsLoc + "_DelaunayGraph.txt"));
            }
            for (int i7 = 0; i7 < gn.length; ++i7) {
                for (Cell cell : gn[i7]) {
                    bwGN.write("" + cellArr[i7].getId() + "\t" + cell.getId() + "\n");
                    bwGN.write("" + cell.getId() + "\t" + cellArr[i7].getId() + "\n");
                }
            }
            bwGN.flush();
            bwGN.close();

            gn = null;
            System.gc();
        }
    }

    /**
     * Find the best focus folder and apply the overlay to the input tif file
     * @param bestFocusDir
     * @param overlays
     */
    private static void applyBestFocusOverlay(File bestFocusDir, File currTiff, BufferedImage [] overlays, boolean imgSeq) throws IllegalStateException {

        File[] lst = null;
        if(!imgSeq) {
            lst = bestFocusDir.listFiles(tif -> (FilenameUtils.removeExtension(tif.getName()).contains(FilenameUtils.removeExtension(currTiff.getName()))) & (tif.getName().endsWith(".tif") || tif.getName().endsWith(".tiff")));
        }
        else {
            lst = bestFocusDir.listFiles(tif -> (FilenameUtils.removeExtension(tif.getName()).contains(currTiff.getName())) & (tif.getName().endsWith(".tif") || tif.getName().endsWith(".tiff")));
        }
        if(lst.length != 1) {
            throw new IllegalStateException("Found more than one or less than one match for file:" + Arrays.toString(lst));
        }

        File bestFocusFile = lst[0];

        int tifLastZIndex = bestFocusFile.getName().lastIndexOf("Z");
        String tifZString = bestFocusFile.getName().substring(tifLastZIndex + 1, tifLastZIndex + 3);
        int tifZIndex = Integer.parseInt(tifZString);

        BufferedImage bi  = overlays[tifZIndex-1];

        ImagePlus impBf = IJ.openImage(bestFocusFile.getAbsolutePath());
        if(impBf.getOverlay() != null) {
            impBf.getOverlay().clear();
        }

        Overlay overlay = new Overlay();

        ImagePlus im2 = new ImagePlus(bestFocusFile.getName(), bi);
        ImageRoi imgRoi = new ImageRoi(0, 0, im2.getProcessor());
        imgRoi.setNonScalable(true);
        imgRoi.setZeroTransparent(true);
        imgRoi.setOpacity(1.0);
        //imgRoi.setPosition(0, zIndex, 0);
        overlay.add(imgRoi);
        impBf.setOverlay(overlay);

        FileSaver fs = new FileSaver(impBf);
        fs.saveAsTiff(bestFocusFile.getAbsolutePath());
    }

    private void compareWithHuman(ImageStack regionMap) throws IOException {
        File f = new File("C:\\Users\\Nikolay\\YandexDisk\\Working folder\\Manuscripts\\3DSegm\\Resubmission1");
        CSVReader csv = new CSVReader((Reader) new FileReader(f), '\t');
        List lst = csv.readAll();
        String[] header = (String[]) lst.get(0);
        lst.remove(0);
        Point3D[] userPoints = new Point3D[lst.size()];
        for (int i = 0; i < lst.size(); ++i) {
            String[] line = (String[]) lst.get(i);
            userPoints[i] = new Point3D(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]));
        }
        int[] regAssignments = new int[userPoints.length];
        for (int i2 = 0; i2 < userPoints.length; ++i2) {
            Point3D point3D = userPoints[i2];
        }
    }

    /*
    Preview segmentation for a tile of user's choice
    TO DO: Refactor code with doSegm() and runSegm() to avoid redundancy
     */
    public static void previewSegm(SegConfigParam segConfigParam, boolean imageSeq) throws Exception {
        File expJSON = null;
        Experiment exp = null;
        ImagePlus imp = null;
        if(imageSeq) {
            FolderOpener fo = new FolderOpener();
            fo.openAsVirtualStack(true);
            expJSON = new File(segConfigParam.getRootDir().getParentFile() + File.separator + "Experiment.json");
            exp = Experiment.loadFromJSON(expJSON);
            ImagePlus impImageSeq = fo.openFolder(segConfigParam.getRootDir().getPath());
            imp = HyperStackConverter.toHyperStack(impImageSeq, exp.channel_names.length, exp.num_z_planes, exp.num_cycles, "default", "Composite");
        }
        else {
            if (segConfigParam.getRootDir() != null && !segConfigParam.getRootDir().exists()) {
                throw new IllegalArgumentException("Error: Cannot find the region:" + segConfigParam.getRootDir().getName());
            }
            if (!imageSeq) {
                imp = IJ.openImage(segConfigParam.getRootDir().getAbsolutePath());
            }
            if (imp == null) {
                throw new IllegalStateException("Couldn't open the image file: " + segConfigParam.getRootDir().getName());
            }
        }
        Duplicator dup = new Duplicator();
        if (!imageSeq) {
            System.out.print("\nprocessing file: " + segConfigParam.getRootDir().getName() + "\n");
        } else {
            System.out.print("\nprocessing image seq: " + imp.getTitle() + "\n");
        }

        int[] readoutChannels = new int[imp.getNChannels()];
        for (int x = 0; x < imp.getNChannels(); x++) {
            readoutChannels[x] = x + 1;
        }
        imp.getNFrames();
        ImagePlus nucl = dup.run(imp, segConfigParam.getNuclearStainChannel(), segConfigParam.getNuclearStainChannel(), 1, imp.getNSlices(), segConfigParam.getNuclearStainCycle(), segConfigParam.getNuclearStainCycle());
        ImagePlus mult = nucl;
        if (Math.min(segConfigParam.getMembraneStainCycle(), segConfigParam.getMembraneStainChannel()) > 0) {
            System.out.println("Dividing by the membrane signal membrane");
            ImagePlus memb = dup.run(imp, segConfigParam.getMembraneStainChannel(), segConfigParam.getMembraneStainChannel(), 1, imp.getNSlices(), segConfigParam.getMembraneStainCycle(), segConfigParam.getMembraneStainCycle());

            for (int i2 = 1; i2 <= memb.getNSlices(); ++i2) {
                ImageProcessor ip = memb.getStack().getProcessor(i2);
                ip.add(1);
            }

            ImageCalculator ic = new ImageCalculator();
            mult =  ic.run("Divide stack float create", nucl, memb);

            for (int i2 = 1; i2 <= mult.getStack().getSize(); ++i2) {
                double ratio = nucl.getStack().getProcessor(i2).getStatistics().mean/mult.getStack().getProcessor(i2).getStatistics().mean;
                mult.getStack().getProcessor(i2).multiply(ratio);
            }
            memb = null;
        }
        nucl = null;

        System.gc();
        FFTFilter filter = new FFTFilter();
        filter.setup(10000.0, (double) segConfigParam.getRadius(), 0, 5.0, false, false, false);
        System.out.print("running FFT bandpass filter");
        filter.run(mult);

        boolean anisotropic_reg_growth = segConfigParam.isAnisotropicRegionGrowth();

        //GaussianBlur3D.blur(mult, radius, radius, radius);
        SegmentedObject[] cellsSegmentedObject = MaximaFinder3D.findCellsByIntensityGradient((ImagePlus) mult, segConfigParam.getRadius(), (double) segConfigParam.getMaxCutoff(), (double) segConfigParam.getMinCutoff(), (double) segConfigParam.getRelativeCutoff(), (boolean) segConfigParam.isShowImage(), segConfigParam.isSubtractInnerRing() ? 1.0 : segConfigParam.getInner_ring_size(), anisotropic_reg_growth);
        if (segConfigParam.isSubtractInnerRing()) {
            segConfigParam.setUse_membrane(false);
        }
        SegmentedObject[] innerRings = null;

        if (segConfigParam.isSubtractInnerRing()) {
            innerRings = MaximaFinder3D.findCellsByIntensityGradient((ImagePlus) mult, segConfigParam.getRadius(), (double) segConfigParam.getMaxCutoff(), (double) segConfigParam.getMinCutoff(), (double) segConfigParam.getInner_ring_size(), (boolean) segConfigParam.isShowImage(), 1.0, anisotropic_reg_growth);
        }

        if (segConfigParam.isShowImage()) {
            try {
                new ImageJ();
                mult.show();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //Filter out small sized regions and remove that row from the txt file
        double sizeCutoff = (segConfigParam.getSizeCutoffFactor())*((segConfigParam.getRadius() * segConfigParam.getRadius() * segConfigParam.getRadius()) * Math.PI * (4.0 / 3.0));
        System.out.println("Filtering small objects by size, cutoff = " + sizeCutoff + " init number of objects: "+cellsSegmentedObject.length);
        cellsSegmentedObject = Arrays.stream(cellsSegmentedObject).filter(c -> c.getPoints().length >= sizeCutoff).toArray(SegmentedObject[]::new);
        System.out.println("# of objects left after filtering = " + cellsSegmentedObject.length);
        if(cellsSegmentedObject.length<10){
            System.out.println("too few cell objects were found in this image. Try decreasing the cell_size_cutoff_factor, for instanece try setting it to 0.1 or 0.05");
        }

        BufferedImage[] bi2;
        if(!imageSeq) {
            //Apply overlay to the different Z stacks of the actual tif file based on different masks.
            bi2 = RegionImageWriter.writeRegionImage(cellsSegmentedObject, mult, segConfigParam.getRootDir().getName(),
                    segConfigParam.getRootDir().getParentFile());
            ImagePlus copy = IJ.openImage(segConfigParam.getRootDir().getAbsolutePath());
            Overlay overlay = new Overlay();

            for (int z = 0; z < bi2.length; z++) {
                ImagePlus im2 = new ImagePlus("Image Slice: " + z, bi2[z]);
                ImageRoi imgRoi = new ImageRoi(0, 0, im2.getProcessor());
                imgRoi.setNonScalable(true);
                imgRoi.setZeroTransparent(true);
                imgRoi.setOpacity(1);
                imgRoi.setPosition(0, z + 1, 0);
                overlay.add(imgRoi);
            }
            copy.setOverlay(overlay);
            //Delete masks png that was created
            File[] masksPng = segConfigParam.getRootDir().getParentFile().listFiles(t -> !t.isDirectory() && t.getName().toLowerCase().endsWith(".png"));
            for(int i=0; i<masksPng.length; i++) {
                if(masksPng[i].exists()) {
                    masksPng[i].delete();
                }
            }
            copy.show();
        }
        else {
            if (segConfigParam.getRootDir() != null) {
                String regMaskName = imp.getTitle();
                File masksLoc = new File(segConfigParam.getRootDir().getParentFile() + File.separator + "preview" +
                        File.separator + "masks" + File.separator + regMaskName);
                if (!masksLoc.exists()) {
                    masksLoc.mkdirs();
                }
                bi2 = RegionImageWriter.writeRegionImage(cellsSegmentedObject, mult, imp.getTitle(), masksLoc);

                //convert imp to hyp
                ImagePlus hyp = HyperStackConverter.toHyperStack(imp, imp.getNChannels(), exp.num_z_planes, exp.num_cycles);
                if (hyp.getOverlay() != null) {
                    hyp.getOverlay().clear();
                }

                Overlay overlay = new Overlay();

                for (int zIndex = 0; zIndex < bi2.length; zIndex++) {
                    ImagePlus im2 = new ImagePlus(hyp.getTitle(), bi2[zIndex]);
                    ImageRoi imgRoi = new ImageRoi(0, 0, im2.getProcessor());
                    imgRoi.setNonScalable(true);
                    imgRoi.setZeroTransparent(true);
                    imgRoi.setOpacity(1.0);
                    imgRoi.setPosition(0, zIndex + 1, 0);
                    overlay.add(imgRoi);
                }
                hyp.setOverlay(overlay);
                // Delete masks that were created
                if (masksLoc.exists()) {
                    FileUtils.deleteDirectory(masksLoc.getParentFile().getParentFile());
                }
                hyp.show();
            }
        }
    }
}
