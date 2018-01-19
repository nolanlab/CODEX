package com.akoya.codex.segm;

import com.opencsv.CSVReader;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.io.FileSaver;
import ij.plugin.Duplicator;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import org.apache.commons.io.FilenameUtils;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main {

    public static boolean printParams = false;
    public static final String revision = "CODEX-segm rev 18-JAN-2018";
    public static Properties params;

    public static void main(String[] args) throws IOException {

        File rootDir = null;
        File config = null;
        boolean use_membrane = false;
        double maxCutoff = 0.99;
        double minCutoff = 0.01;
        double relativeCutoff = 0.4;
        int nuclearStainChannel = -1;
        int nuclearStainCycle = -1;
        int membraneStainChannel = -1;
        int membraneStainCycle = -1;
        int radius = 2;
        boolean count_puncta = false;
        double inner_ring_size = 0.6;
        int[] readoutChannels = null;
        //this variable enables subtraction of the inner ring average
        boolean subtractInnerRing = false;
        //eto to
        boolean showImage = false;
        boolean dont_inverse_memb = false;
        boolean delaunay_graph = true;
        int concentricCircles = 0;
        try {
            if(args.length < 2 ){
                throw new IllegalArgumentException("Invalid number of arguments");
            }
            if (args.length == 3) {
                rootDir = new File(args[0]);
                showImage = Boolean.parseBoolean(args[1]);
                printParams = Boolean.parseBoolean(args[2]);
            }
            if (args.length == 2) {
                //inputFolderDialog();
                //rootDir = new File(configField.getText());
                showImage = Boolean.parseBoolean(args[0]);
                printParams = Boolean.parseBoolean(args[1]);
                System.out.println("printParams = " + printParams);
            }

            if(rootDir != null) {
                config = new File(rootDir + File.separator + "config.txt");
                if (!config.exists()) {
                    throw new IllegalArgumentException("Config file not found:\n" + config.getPath());
                }
            }
            params = new Properties();
            FileInputStream input = new FileInputStream(config);
            params.load(input);
            radius = Integer.parseInt(params.getProperty("radius", "5").trim());
            use_membrane = Boolean.parseBoolean(params.getProperty("use_membrane", "false").trim());
            maxCutoff = Double.parseDouble(params.getProperty("maxCutoff", "0.99").trim());
            minCutoff = Double.parseDouble(params.getProperty("minCutoff", "0.02").trim());
            relativeCutoff = Double.parseDouble(params.getProperty("relativeCutoff", "0.05").trim());

            nuclearStainChannel = Integer.parseInt(params.getProperty("nuclearStainChannel", "-1").trim());
            nuclearStainCycle = Integer.parseInt(params.getProperty("nuclearStainCycle", "-1").trim());
            membraneStainChannel = Integer.parseInt(params.getProperty("membraneStainChannel", "-1").trim());
            membraneStainCycle = Integer.parseInt(params.getProperty("membraneStainCycle", "-1").trim());
            inner_ring_size = Double.parseDouble(params.getProperty("inner_ring_size", "0.6").trim());
            count_puncta = Boolean.parseBoolean(params.getProperty("count_puncta", "false").trim());
            dont_inverse_memb = Boolean.parseBoolean(params.getProperty("dont_inverse_membrane", "false").trim());
            concentricCircles = Integer.parseInt(params.getProperty("concentric_circle_featurization_steps", "0").trim());
            delaunay_graph = Boolean.parseBoolean(params.getProperty("delaunay_graph", "true").trim());
            String[] s = params.getProperty("readoutChannels", "-1").trim().split(",");

            readoutChannels = new int[s.length];
            for (int i = 0; i < s.length; ++i) {
                readoutChannels[i] = Integer.parseInt(s[i]);
            }
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
        Duplicator dup = new Duplicator();
        int tile = 0;
        for (File currTiff : rootDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".tiff") || name.endsWith(".tif");
            }
        })) {
            int j;
            int i;
            ++tile;
            System.out.print("\nprocessing file: " + currTiff.getName() + "\n");
            if (!currTiff.exists()) {
                throw new IllegalArgumentException("Error: Cannot find the input file:" + currTiff);
            }
            ImagePlus imp = IJ.openImage(currTiff.getAbsolutePath());
            if (imp == null) {
                throw new IllegalStateException("Couldn't open the image file: " + currTiff);
            }
            imp.getNFrames();
            ImagePlus nucl = dup.run(imp, nuclearStainChannel, nuclearStainChannel, 1, imp.getNSlices(), nuclearStainCycle, nuclearStainCycle);
            ImagePlus mult = nucl;
            if (Math.min(membraneStainCycle, membraneStainChannel) > 0) {
                System.out.println("Multiplying by membrane");
                ImagePlus memb = dup.run(imp, membraneStainChannel, membraneStainChannel, 1, imp.getNSlices(), membraneStainCycle, membraneStainCycle);
                ImageStack membS = memb.getImageStack();
                if (dont_inverse_memb) {
                    System.out.println("Bypassing membrane inversion");
                } else {
                    for (int i2 = 1; i2 <= membS.getSize(); ++i2) {
                        ImageProcessor ip = membS.getProcessor(i2);
                        ip.invert();
                    }
                }
                ImageCalculator ic = new ImageCalculator();
                mult = dont_inverse_memb ? ic.run("Add stack float create", nucl, memb) : ic.run("Multiply stack float create", nucl, memb);
                memb = null;
            }
            nucl = null;

            System.gc();
            FFTFilter filter = new FFTFilter();
            filter.setup(10000.0, (double) radius, 0, 5.0, false, false, false);
            System.out.print("running FFT bandpass filter");
            filter.run(mult);

            //GaussianBlur3D.blur(mult, radius, radius, radius);
            SegmentedObject[] cellsSegmentedObject = MaximaFinder3D.findCellsByIntensityGradient((ImagePlus) mult, radius, (double) maxCutoff, (double) minCutoff, (double) relativeCutoff, (boolean) showImage, subtractInnerRing ? 1.0 : inner_ring_size);
            if (subtractInnerRing) {
                use_membrane = false;
            }
            SegmentedObject[] innerRings = null;

            if (subtractInnerRing) {
                innerRings = MaximaFinder3D.findCellsByIntensityGradient((ImagePlus) mult, radius, (double) maxCutoff, (double) minCutoff, (double) inner_ring_size, (boolean) showImage, 1.0);
            }

            if (showImage) {
                try {
                    new ImageJ();
                    mult.show();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            //Filter out small sized regions and remove that row from the txt file
            double sizeCutoff = (radius*radius*radius)*Math.PI*(4.0/3.0);
            cellsSegmentedObject = Arrays.stream(cellsSegmentedObject).filter(c -> c.getPoints().length >= sizeCutoff).toArray(SegmentedObject[]::new);

            //Apply overlay to the different Z stacks of the actual tif file based on different masks.
            BufferedImage[] bi2 = RegionImageWriter.writeRegionImage((SegmentedObject[]) cellsSegmentedObject, (ImagePlus) mult, (String) currTiff.getName(), (File) currTiff.getParentFile());
            ImagePlus copy = IJ.openImage(currTiff.getAbsolutePath());
            Overlay overlay = new Overlay();

            for(int z = 0; z < bi2.length; z++) {
                ImagePlus im2 = new ImagePlus("Image Slice: "+z, bi2[z]);
                ImageRoi imgRoi = new ImageRoi(0, 0, im2.getProcessor());
                imgRoi.setNonScalable(true);
                imgRoi.setZeroTransparent(true);
                imgRoi.setOpacity(1);
                imgRoi.setPosition(0,z+1,0);
                overlay.add(imgRoi);
            }
            copy.setOverlay(overlay);
            FileSaver fs = new FileSaver(copy);
            fs.saveAsTiff(currTiff.getAbsolutePath());

            System.out.println("Applying mask/overlay for bestFocus file: "+FilenameUtils.removeExtension(currTiff.getName()));
            applyBestFocusOverlay(currTiff , bi2);

            int numFrames = imp.getNFrames();
            if (cellsSegmentedObject.length == 0) {
                System.out.println("Didn't find any cells here. exiting");
                BufferedWriter bwUncomp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Uncompensated.txt"));
                BufferedWriter bwComp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Compensated.txt"));
                for (BufferedWriter bw : new BufferedWriter[]{bwUncomp, bwComp}) {
                    bw.write("cell_id\ttile_nr\tX\tY\tZ\tsize");
                    for (int i3 = 1; i3 <= numFrames; ++i3) {
                        for (int j2 = 0; j2 < readoutChannels.length; ++j2) {
                            bw.write("\tCyc_" + i3 + "_ch_" + readoutChannels[j2]);
                        }
                    }
                    for (int k = 0; k < concentricCircles; k++) {
                        for (int i3 = 1; i3 <= numFrames; ++i3) {
                            for (int j2 = 0; j2 < readoutChannels.length; ++j2) {
                                bw.write("\tCircle_" + k + "_Cyc_" + i3 + "_ch_" + readoutChannels[j2]);
                            }
                        }
                    }
                    bw.write("\n");
                }
                BufferedWriter bwGN = new BufferedWriter(new FileWriter(currTiff.getPath() + "_GabrielGraph.txt"));
                bwGN.write("");
                bwGN.flush();
                bwGN.close();
                continue;
            }

            int w = mult.getWidth();
            int h = mult.getHeight();
            int d = mult.getStackSize();
            mult = null;
            System.gc();
            System.out.println("Computing region intensities " + (use_membrane ? "by membrane" : "by whole cell"));
            double[][] regionIntensities = new double[cellsSegmentedObject.length][(imp.getNFrames() * readoutChannels.length)];
            ImagePlus mem = null;
            if(use_membrane) {
                mem = dup.run(imp, membraneStainChannel, membraneStainChannel, 1, imp.getNSlices(), membraneStainCycle, membraneStainCycle);
            }
            for (int cycle = 1; cycle <= imp.getNFrames(); ++cycle) {
                for (int ch = 0; ch < readoutChannels.length; ++ch) {
                    System.out.println("Cycle:" + cycle + ", channel" + readoutChannels[ch]);
                    ImagePlus readout = dup.run(imp, readoutChannels[ch], readoutChannels[ch], 1, imp.getNSlices(), cycle, cycle);

                    double[] intens = null;
                    if (!count_puncta) {
                        intens = use_membrane ? Segmentation.computeMembraneIntensityOfRegions((ImageStack) readout.getImageStack(), (ImageStack) mem.getImageStack(), (SegmentedObject[]) cellsSegmentedObject) : Segmentation.computeMeanIntensityOfRegions((ImageStack) readout.getImageStack(), (SegmentedObject[]) cellsSegmentedObject);
                        if (subtractInnerRing) {
                            double[] innerRingIntens = Segmentation.computeMeanIntensityOfRegions((ImageStack) readout.getImageStack(), (SegmentedObject[]) innerRings);
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

            ProfileAverager[][] pa = new ProfileAverager[cellsSegmentedObject.length][concentricCircles];

            System.out.println("Featurizing circles:" + concentricCircles);
            for (int ci = 0; ci < concentricCircles; ci++) {
                System.out.println("Circle#" + (ci + 1) + ":" + Math.pow((radius * 2), 1 + (ci / 3.0)));
            }
            for (int r = 0; r < cellsSegmentedObject.length; ++r) {
                Point3D cent = cellsSegmentedObject[r].getCenter();
                for (int k = 0; k < cellsSegmentedObject.length; ++k) {
                    double dist = Segmentation.dist(cent, cellsSegmentedObject[k].getCenter());
                    for (int ci = 0; ci < concentricCircles; ci++) {
                        if (pa[r][ci] == null) {
                            pa[r][ci] = new ProfileAverager();
                        }
                        if (dist < Math.pow((radius * 2), 1 + (ci / 2.0))) {
                            pa[r][ci].addProfile(regionIntensities[k]);
                        }
                    }
                }
            }

            double[][] featurizedVec = new double[cellsSegmentedObject.length][0];

            for (int r = 0; r < cellsSegmentedObject.length; ++r) {
                for (int ci = 0; ci < concentricCircles; ci++) {
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
            double[][] adjN = Neighborhood.buildAdjacencyMatrix((Cell[]) cellArr, (int) w, (int) h, (int) d, (boolean) true);
            cellsForTile.clear();
            System.out.println("Compensating:");
            double[][] compRegionIntensities = Segmentation.compensatePositionalSpilloverOfExpressionMtx((SegmentedObject[]) cellsSegmentedObject, (double[][]) adjN, (double[][]) regionIntensities);
            for (int dt = 0; dt < compRegionIntensities.length; ++dt) {
                int id = dt + 1;
                Cell c = new Cell(id, cellsSegmentedObject[dt], tile, compRegionIntensities[dt], featurizedVec[dt]);
                cellsForTile.add(c);
            }
            Cell[] compCellArray = cellsForTile.toArray(new Cell[cellsForTile.size()]);
            adjN = null;
            System.gc();
            BufferedWriter bwUncomp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Uncompensated.txt"));
            BufferedWriter bwComp = new BufferedWriter(new FileWriter(currTiff.getPath() + "_Expression_Compensated.txt"));
            for (BufferedWriter bw22 : new BufferedWriter[]{bwUncomp, bwComp}) {
                bw22.write("cell_id\ttile_nr\tX\tY\tZ\tsize");
                for (int i6 = 1; i6 <= numFrames; ++i6) {
                    for (j = 0; j < readoutChannels.length; ++j) {
                        bw22.write("\tCyc_" + i6 + "_ch_" + readoutChannels[j]);
                    }
                }
                for (int k = 0; k < concentricCircles; k++) {
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
            if (delaunay_graph) {
                System.out.println("Computing Delaunay graph:");
                Collection<Cell>[] gn = Neighborhood.findDelaunayNeighbors(cellArr, (int) w, (int) h, (int) d);
                BufferedWriter bwGN = new BufferedWriter(new FileWriter(currTiff.getPath() + "_DelaunayGraph.txt"));
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
    }

    /**
     * Find the best focus folder and apply the overlay to the input tif file
     * @param in
     * @param overlays
     */
    private static void applyBestFocusOverlay(File in, BufferedImage [] overlays) {

        File rootDir = (in == null)? null : in.getParentFile();
        File bestFocusDir = new File (rootDir + File.separator + "bestFocus");

        if(!bestFocusDir.exists()) {
            System.out.println("Best focus folder cannot be found: " + bestFocusDir);
            return;
        }

        File[] lst = bestFocusDir.listFiles(tif->(FilenameUtils.removeExtension(tif.getName()).contains(FilenameUtils.removeExtension(in.getName())))&(tif.getName().endsWith(".tif") || tif.getName().endsWith(".tiff")));

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
}
