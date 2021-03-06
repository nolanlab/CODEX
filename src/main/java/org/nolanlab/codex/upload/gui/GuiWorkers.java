package org.nolanlab.codex.upload.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.Duplicator;
import ij.plugin.StackCombiner;
import ij.process.StackProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.upload.scope.Microscope;
import org.nolanlab.codex.upload.scope.MicroscopeFactory;
import org.nolanlab.codex.upload.scope.MicroscopeTypeEnum;
import org.nolanlab.codex.upload.driffta.BestFocus;
import org.nolanlab.codex.upload.model.Experiment;
import org.nolanlab.codex.upload.model.Metadata;
import org.nolanlab.codex.upload.model.ProcessingOptions;
import org.nolanlab.codex.utils.util;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

/**
 *
 * @author Vishal
 */

public class GuiWorkers {
    private final NewGUI gui;
    private GuiHelper guiHelper = new GuiHelper();
    private WorkerHelper workerHelper = new WorkerHelper();

    GuiWorkers(NewGUI gui) {
        this.gui = gui;
    }

    /*
     Method to load the values from the JSON file and set it to the Experiment property
    */
    public void loadFromJson(Experiment exp, File dir) {
        guiHelper.log("Experiment.json was found. Will populate the fields based on the values present in json...");
        gui.getNameField().setText(exp.name);
        gui.getProjectNameField().setText(exp.projName);
//        val2.setText(exp.codex_instrument);

        if(exp.microscope instanceof MicroscopeTypeEnum) {
            gui.getMicroscopeTypeComboBox().setSelectedItem(exp.microscope.toString());
        }
        else {
            if(exp.microscope == null) {
                guiHelper.guessMicroscope(dir, gui.getMicroscopeTypeComboBox());
            }
        }

        gui.setTMA(exp.isTMA);
        gui.getDeconvolutionCheckBox().setSelected(exp.deconvolution.toLowerCase().equals("none") ? false : true);
        gui.getDeconvolutionIterationsField().setText(exp.deconvolutionIterations != 0 ? String.valueOf(exp.deconvolutionIterations) : "25");
        gui.getDeconvolutionModelComboBox().setSelectedItem(exp.deconvolutionModel != null ? exp.deconvolutionModel : "Vectorial");
        gui.getObjectiveTypeComboBox().setSelectedItem(exp.objectiveType);// out of order
        gui.getMagnificationField().setText(String.valueOf(exp.magnification));
        gui.getApertureField().setText(String.valueOf(exp.numerical_aperture));
        gui.getXyResolutionField().setText(String.valueOf(exp.per_pixel_XY_resolution));
        gui.getzPitchField().setText(String.valueOf(exp.z_pitch));
        gui.getNumPlanesField().setText(String.valueOf(exp.num_z_planes));
        gui.getColorModeComboBox().setSelectedItem(exp.channel_arrangement);
        gui.getChannelNamesField().setText(util.concat(exp.channel_names));
        gui.getWavelengthsField().setText(util.concat(exp.emission_wavelengths)); //OUT OF ORDER
        gui.getDriftReferenceChannelField().setText(String.valueOf(exp.drift_comp_channel));
        gui.getDriftReferenceCycleField().setText(String.valueOf(exp.driftCompReferenceCycle));
        gui.getBestFocusChannelField().setText(String.valueOf(exp.best_focus_channel));
        gui.getBestFocusCycleField().setText(String.valueOf(exp.bestFocusReferenceCycle));

        if(exp.cycle_upper_limit != exp.cycle_lower_limit) {
            gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit) + "-" + String.valueOf(exp.cycle_upper_limit));
        }
        else {
            gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit));
        }

//        val14.setText(util.concat(exp.regIdx));
        gui.getRegionNamesField().setText(util.concat(exp.region_names));
//        val16.setSelectedItem(exp.tiling_mode);


        gui.getRegionWidthField().setText(String.valueOf(exp.region_width));
        gui.getRegionHeightField().setText(String.valueOf(exp.region_height));
//        gui.getTileHeightField().setText(String.valueOf(exp.tile_height));
//        gui.getTileWidthField().setText(String.valueOf(exp.tile_width));

        // Set imp fields
        gui.getNumCyclesField().setText(String.valueOf(exp.num_cycles));
        if(!exp.isTMA) {
            gui.getNumRegionsField().setText(String.valueOf(exp.region_names.length));
        } else {
            gui.getNumRegionsField().setText(String.valueOf(exp.regIdx.length));
        }
        gui.getNumChannelsField().setText(String.valueOf(exp.channel_names.length));

        //Calculate tile overlap
        if(dir != null) {
            File [] dirList = dir.listFiles();
            outer: for (File cyc : dirList) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc") || cyc.getName().toLowerCase().startsWith("hande")) {
                    if(cyc.getName().toLowerCase().startsWith("hande")) {
                        gui.setOnlyHandE(true);
                    }
                    File [] cycList = cyc.listFiles();
                    if(!gui.isTMA()) {
                        for (File file : cycList) {
                            if (!file.isDirectory() && (file.getName().endsWith(".tif") || file.getName().endsWith(".tiff"))) {
                                ImagePlus imp = IJ.openImage(file.getAbsolutePath());
                                gui.getTileOverlapXField().setText(String.valueOf(exp.tile_overlap_X * 100 / imp.getWidth()));
                                gui.getTileOverlapYField().setText(String.valueOf(exp.tile_overlap_Y * 100 / imp.getHeight()));
                                break outer;
                            }
                        }
                    } else {
                        for (File xyFile : cycList) {
                            if (xyFile.isDirectory() && xyFile.getName().toLowerCase().contains("xy")) {
                                File[] tiffFiles = xyFile.listFiles(tif -> !tif.isDirectory() && (tif.getName().endsWith(".tif") || tif.getName().endsWith(".tiff")));
                                ImagePlus imp = IJ.openImage(tiffFiles[0].getAbsolutePath());
                                gui.getTileOverlapXField().setText(String.valueOf(exp.tile_overlap_X * 100 / imp.getWidth()));
                                gui.getTileOverlapYField().setText(String.valueOf(exp.tile_overlap_Y * 100 / imp.getHeight()));
                                break outer;
                            }
                        }

                    }
                }
            }
        }

        gui.gethAndEStainCheckBox().setSelected(exp.HandEstain);
        gui.getTmaCheckBox().setSelected(exp.isTMA);
        gui.getBackgroundSubtractionCheckBox().setSelected(exp.bgSub);
        gui.getOptionalFocusFragmentCheckBox().setSelected(exp.optionalFocusFragment);
        gui.getFocusingOffsetField().setText(String.valueOf(exp.focusing_offset));

        if(exp.HandEstain) {
            JCheckBox hAndE = gui.gethAndEStainCheckBox();
            ItemListener itl = itemEvent -> {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit) + "-" + String.valueOf(exp.cycle_upper_limit));
                } else {
                    gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit) + "-" + String.valueOf(exp.cycle_upper_limit - 1));
                }
            };
            hAndE.addItemListener(itl);
        }
        else {
            JCheckBox rb_handE_yes = gui.gethAndEStainCheckBox();
            ItemListener itl = itemEvent -> {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit) + "-" + String.valueOf(exp.cycle_upper_limit + 1));
                } else {
                    gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit) + "-" + String.valueOf(exp.cycle_upper_limit));
                }
            };
            rb_handE_yes.addItemListener(itl);
        }

        gui.getProcessRegionsField().setText((exp.processRegions == null || exp.processRegions.length == 0) ? null : String.join(";", exp.processRegions));
        gui.getProcessTilesField().setText((exp.processTiles == null || exp.processTiles.length == 0) ? null : String.join(";", exp.processTiles));

        // Load processing options
        File poFile = new File(dir + File.separator + "processingOptions.json");
        ProcessingOptions po = null;
        try {
            po = ProcessingOptions.load(poFile);
            gui.getUseBlindDeconvolutionCheckBox().setSelected(po.isUseBlindDeconvolution());
            gui.getUseBleachMinimizingCropCheckBox().setSelected(po.isUseBleachMinimizingCrop());
            if (!StringUtils.isBlank(po.getTempDir().getPath())) {
//                gui.getOpenOutputButton().setEnabled(true);
                GuiHelper.enableAll(gui,true);
            }
            gui.getOutputDirField().setText(po.getTempDir().getPath());
            guiHelper.logRouting(gui);
            gui.getImgSeqCheckBox().setSelected(po.isExportImgSeq());
        } catch (FileNotFoundException e) {
            guiHelper.log(ExceptionUtils.getStackTrace(e));
        }
    }

    /*
     Method to parse and guess the values from the input Experiment folder when experiment json is not present.
     */
    public String parseExperimentFolderForFields(File dir) {
        guiHelper.log("Experiment.json not found! Trying to parse the values for fields from the input folder...");

        StringBuilder err = new StringBuilder();

        int maxRegion = 0;
        int maxCycle = 0;

        boolean containsBcf = false;
        boolean hasHandE = false;

        File[] sub = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().toLowerCase().startsWith("cyc"));
        if(sub.length == 0) {
            sub = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().toLowerCase().startsWith("hande"));
            if(sub.length == 0) {
                guiHelper.log("Input folder neither contains the Cyc folders nor the HandE folder. Try again!");
                throw new IllegalStateException("Input folder neither contains the Cyc folders nor the HandE folder. Try again!");
            } else {
                gui.setOnlyHandE(true);
                guiHelper.log("Input experiment folder contains only the HandE folder. Thus, the number of channels is defaulted to 4.");
            }
        }
        for (File f : sub) {
            File[] xyFolders = f.listFiles(pathname -> pathname.isDirectory() && pathname.getName().toLowerCase().startsWith("xy"));
            if (xyFolders != null && xyFolders.length != 0) {
                gui.setTMA(true);
                guiHelper.log("Input folder structure is of TMA data.");
            }
            gui.getTmaCheckBox().setSelected(gui.isTMA());
            if (!gui.isTMA()) {
                if (!containsBcf) {
                    containsBcf = f.listFiles(pathname -> pathname.getName().endsWith(".bcf")).length > 0;
                }
            } else {
                if (!containsBcf) {
                    containsBcf = xyFolders[0].listFiles(pathname -> pathname.getName().endsWith(".bcf")).length > 0;
                }
            }
            if (containsBcf) {
                gui.getMicroscopeTypeComboBox().setSelectedItem(MicroscopeTypeEnum.KEYENCE);
            }
            break;
        }
        for (File f : sub) {
            String[] s = f.getName().split("_");
            int cyc = 0;
            if(gui.isOnlyHandE()) {
                cyc = 1;
            } else {
                cyc = Integer.parseInt(s[0].substring(3));
            }
            maxCycle = Math.max(cyc, maxCycle);
            if(!gui.isTMA()) {
                int reg = Integer.parseInt(s[1].substring(3));
                maxRegion = Math.max(reg, maxRegion);

            } else {
                File[] xy = f.listFiles(t -> t.isDirectory() && t.getName().toLowerCase().startsWith("xy"));
                maxRegion = xy.length;
            }
        }
        int[][] occup_table = new int[maxCycle][maxRegion];
        for (File f : sub) {
            if (!containsBcf && !gui.isTMA()) {
                containsBcf = f.listFiles(pathname -> pathname.getName().endsWith(".bcf")).length > 0;
            }
            String[] s = f.getName().split("_");
            int cyc = 0;
            if(gui.isOnlyHandE()) {
                cyc = 1;
            } else {
                cyc = Integer.parseInt(s[0].substring(3));
            }
            int reg = Integer.parseInt(s[1].substring(3));
            occup_table[cyc - 1][reg - 1]++;
        }

        if(!gui.isTMA()) {
            for (int cyc = 1; cyc <= occup_table.length; cyc++) {
                for (int reg = 1; reg <= occup_table[cyc - 1].length; reg++) {
                    if (occup_table[cyc - 1][reg - 1] == 0) {
                        err.append("Missing data: cycle=").append(cyc).append(", region=").append(reg).append("\n");
                    }
                    if (occup_table[cyc - 1][reg - 1] > 1) {
                        err.append("Duplicate data: cycle=").append(cyc).append(", region=").append(reg).append(". Delete duplicate folders before proceeding\n");
                    }
                }
            }
        }

        File[] hef = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().startsWith("HandE"));

        if(!gui.isTMA()) {
            hasHandE = (hef.length == maxRegion) && hef.length > 0;
            if (!hasHandE && hef.length > 0) {
                err.append("The experiment has HandE folders, but their number is less than a number of regions");
            }
        } else {
            hasHandE = hef.length > 0;
        }


        if (hasHandE) {
            gui.gethAndEStainCheckBox().setSelected(true);
            if(!gui.isOnlyHandE()) {
                maxCycle++;
            }
        }

        gui.gethAndEStainCheckBox().setSelected(hasHandE);

        //val13.setText(String.valueOf(maxCycle));
        gui.getCycleRangeField().setText("");

        String regTxt = "1";
        String regNames = "Region 1";

        for (int i = 2; i <= maxRegion; i++) {
            regTxt += ";" + i;
            regNames += ";Region " + i;
        }

        if(!gui.isTMA()) {
            gui.getRegionNamesField().setText(regNames);
        }
        gui.getNumRegionsField().setText(String.valueOf(maxRegion));
        gui.getNumCyclesField().setText(String.valueOf(maxCycle));
        //guessTiles(dir);

        guiHelper.guessMicroscope(dir, gui.getMicroscopeTypeComboBox());

        String microscopeType = gui.getMicroscopeTypeComboBox().getSelectedItem() != null ? gui.getMicroscopeTypeComboBox().getSelectedItem().toString() : "";
        if(microscopeType == null || microscopeType.equals("")) {
            JOptionPane.showMessageDialog(null, "Microscope type is invalid");
        }

        Microscope microscope = MicroscopeFactory.getMicroscope(microscopeType);
        microscope.guessZSlices(dir, gui);
        microscope.guessChannelNamesAndWavelength(dir, gui);
        microscope.guessCycleRange(dir, gui);
        if(!gui.isTMA()) {
            microscope.guessTileOverlap(gui);
        }
        if(gui.isTMA()) {
            microscope.guessWidthAndHeight(dir, gui);
        }

        return err.length() == 0 ? "" : ("Following errors were found in the experiment:\n" + err.toString());
    }

    /*
    Start button clicked
    */
    public Thread startActionPerformed() {
        Thread th = new Thread(() -> {
            try {
                File dir = new File(gui.getInputPathField().getText());
                ArrayList<Process> allProcess = new ArrayList<>();

                if (dir == null || dir.getName().equals("...")) {
                    guiHelper.log("Please select an experiment folder and try again!");
                }

                Experiment exp = Metadata.getExperiment(gui);
                guiHelper.replaceTileOverlapInExp(dir, exp, gui);

//                String experimentJS = exp.toJSON();

                String microscopeType = exp != null && exp.microscope != null ? exp.microscope.toString() : "";
                if (microscopeType == null || microscopeType.equals("")) {
                    JOptionPane.showMessageDialog(null, "Microscope type is invalid");
                }
                Microscope microscope = MicroscopeFactory.getMicroscope(microscopeType);
                //Included a feature to check if the product of region size X and Y is equal to the number of tiles
                File expJSON = null;
                if (microscope.isTilesAProductOfRegionXAndY(dir, gui)) {
                    expJSON = new File(dir + File.separator + "Experiment.json");
                    exp.saveToFile(expJSON);
                } else {
                    JOptionPane.showMessageDialog(null, "Check the values of Region Size X and Y and then try again!");
                    return;
                }

                File poFile = new File(dir + File.separator + "processingOptions.json");

                ProcessingOptions po = Metadata.getProcessingOptions(gui);
                po.saveToFile(poFile);

                //Copy Experiment.JSON to processed folder.
                if (expJSON != null) {
                    if (po.isExportImgSeq()) {
                        guiHelper.copyFileFromSourceToDest(expJSON, new File(po.getTempDir() + File.separator + "tiles"));
                    }
                    guiHelper.copyFileFromSourceToDest(expJSON, po.getTempDir());
                }

                //Included a feature to check if the channelNames.txt file is present
                if (!guiHelper.isChannelNamesPresent(dir)) {
                    JOptionPane.showMessageDialog(null, "channelNames.txt file is not present in the experiment folder. Please check and try again!");
                    return;
                }

                guiHelper.log("Copying channelNames.txt file from experiment folder to processed folder location");

                File source = new File(dir + File.separator + "channelNames.txt");
                if (po.isExportImgSeq()) {
                    guiHelper.copyFileFromSourceToDest(source, new File(po.getTempDir() + File.separator + "tiles"));
                }
                guiHelper.copyFileFromSourceToDest(source, po.getTempDir());

                gui.getStartButton().setEnabled(false);
                gui.getStopButton().setEnabled(true);

                gui.getProgressAnimation().setIndeterminate(true);
                guiHelper.log("Verifying names...");

                boolean selectiveProcessing = false;
                if(!StringUtils.isBlank(gui.getProcessRegionsField().getText()) &&
                        !StringUtils.isBlank(gui.getProcessTilesField().getText())) {
                    selectiveProcessing = true;
                    guiHelper.log("Selective processing of regions & tiles has been specified...");
                }

                for (File f : dir.listFiles(file -> file.isDirectory() && file.getName().startsWith("Cyc"))) {
                    String name = f.getName();
                    String[] s = name.split("_");
                    if (s.length > 2) {
                        f.renameTo(new File(dir + File.separator + s[0] + "_" + s[1]));
                    }
                }
                File f = new File(".\\");

                f.getAbsolutePath();

                int totalCount = 0;
                if(selectiveProcessing) {
                    for(int i = 0; i < exp.processTiles.length; i++) {
                        if (exp.processTiles[i].contains(".")) {
                            String[] tiles = exp.processTiles[i].split("\\.");
                            for (int j = 0; j < tiles.length; j++) {
                                if (tiles[j].contains("-")) {
                                    String[] tileRange = tiles[j].split("-");
                                    int lowerTile = Integer.parseInt(tileRange[0]);
                                    int upperTile = Integer.parseInt(tileRange[1]);
                                    totalCount += (upperTile - lowerTile + 1);
                                } else {
                                    totalCount += 1;
                                }
                            }
                        } else if (exp.processTiles[i].contains("-")) {
                            String[] tileRange = exp.processTiles[i].split("-");
                            int lowerTile = Integer.parseInt(tileRange[0]);
                            int upperTile = Integer.parseInt(tileRange[1]);
                            totalCount += (upperTile - lowerTile + 1);
                        } else {
                            totalCount += 1;
                        }
                    }
                } else {
                    totalCount = exp.regIdx.length * exp.region_width * exp.region_height;
                }

//                prg.setMaximum(totalCount);
                gui.getProgressBar().setMaximum(totalCount);

                int currCnt = 1;

                Properties config = new Properties();
                config.load(new FileInputStream(System.getProperty("user.home") + File.separator + "config.txt"));
                String maxRAM = "";
                if (config.toString().contains("maxRAM") && !StringUtils.isEmpty(config.get("maxRAM").toString())) {
                    maxRAM = config.get("maxRAM").toString();
                }
                maxRAM = maxRAM.equals("") ? "48" : maxRAM;

                if((exp.processTiles == null || exp.processTiles.length == 0) && (exp.processRegions == null || exp.processRegions.length == 0)) {
                    for(int reg : exp.regIdx) {
                        workerHelper.processTiles(exp, gui, po, allProcess, currCnt, maxRAM, 1, exp.region_height * exp.region_width, reg);
                        currCnt += exp.region_height * exp.region_width;
                    }
                }
                else {
                    int[] processRegs = Stream.of(exp.processRegions).mapToInt(Integer::parseInt).toArray();

                    if (processRegs.length == exp.processTiles.length) {
                        for (int i = 0; i < processRegs.length; i++) {
                            if (exp.processTiles[i].contains(".")) {
                                String[] tiles = exp.processTiles[i].split("\\.");
                                for (int j = 0; j < tiles.length; j++) {
                                    if (tiles[j].contains("-")) {
                                        String[] tileRange = tiles[j].split("-");
                                        int lowerTile = Integer.parseInt(tileRange[0]);
                                        int upperTile = Integer.parseInt(tileRange[1]);
                                        workerHelper.processTiles(exp, gui, po, allProcess, currCnt, maxRAM, lowerTile, upperTile, processRegs[i]);
                                        currCnt += upperTile - lowerTile + 1;
                                    } else {
                                        int tile = Integer.parseInt(tiles[j]);
                                        workerHelper.processTiles(exp, gui, po, allProcess, currCnt, maxRAM, tile, tile, processRegs[i]);
                                        currCnt++;
                                    }
                                }
                            } else if (exp.processTiles[i].contains("-")) {
                                String[] tileRange = exp.processTiles[i].split("-");
                                int lowerTile = Integer.parseInt(tileRange[0]);
                                int upperTile = Integer.parseInt(tileRange[1]);
                                workerHelper.processTiles(exp, gui, po, allProcess, currCnt, maxRAM, lowerTile, upperTile, processRegs[i]);
                                currCnt += upperTile - lowerTile + 1;
                            } else {
                                int tile = Integer.parseInt(exp.processTiles[i]);
                                workerHelper.processTiles(exp, gui, po, allProcess, currCnt, maxRAM, tile, tile, processRegs[i]);
                                currCnt++;
                            }
                        }
                    } else {
                        guiHelper.log("The number of tiles and regions to be processed are not equal");
                        throw new IllegalStateException("The number of tiles and regions to be processed are not equal... " +
                                "processTiles length not equal to processRegions length");
                    }
                }

                guiHelper.log("Checking if bestFocus folder is present...");

                String bfDirStr = "";
                if(po.isExportImgSeq()) {
                    bfDirStr = po.getTempDir() + File.separator + "tiles" + File.separator + "bestFocus";
                } else {
                    bfDirStr = po.getTempDir() + File.separator + "bestFocus";
                }

                File bf = new File(bfDirStr);
                if(!bf.exists()) {
                    guiHelper.log("Best focus folder is not present. Running it for all the tiffs inside the processed folder.");
                    File processed = new File(po.getTempDir().getPath());
                    String bestFocus = bfDirStr;
                    File mkBestFocus = new File(bestFocus);
                    mkBestFocus.mkdirs();
                    if(processed.isDirectory()) {
                        File[] procTiff = processed.listFiles(fName -> (fName.getName().endsWith(".tiff") || fName.getName().endsWith(".tif")));
                        for(File aTif : procTiff) {
                            ImagePlus p = IJ.openImage(aTif.getPath());
                            int[] bestFocusPlanes = new int[p.getNFrames()];
                            Duplicator dup = new Duplicator();
                            ImagePlus rp = dup.run(p, exp.best_focus_channel, exp.best_focus_channel, 1, p.getNSlices(), exp.bestFocusReferenceCycle-exp.cycle_lower_limit+1,  exp.bestFocusReferenceCycle-exp.cycle_lower_limit+1);
                            int refZ = Math.max(1, BestFocus.findBestFocusStackFromSingleTimepoint(rp, 1, exp.optionalFocusFragment));
                            //Add offset here
                            refZ = refZ + exp.focusing_offset;
                            Arrays.fill(bestFocusPlanes, refZ);

                            ImagePlus focused = BestFocus.createBestFocusStackFromHyperstack(p, bestFocusPlanes);
                            guiHelper.log("Saving the focused tiff " + aTif.getName()+ "where Z: " +bestFocusPlanes[0]);
                            FileSaver fs = new FileSaver(focused);
                            fs.saveAsTiff(bestFocus + File.separator + Experiment.getDestStackFileNameWithZIndexForTif(exp.tiling_mode, aTif.getName(), bestFocusPlanes[0]));
                        }
                    }
                }

                guiHelper.log("Creating montages");
                String mkMonIn = null;
                if(po.isExportImgSeq()) {
                    guiHelper.log("Image sequence folder structure recognized...");
                    mkMonIn = po.getTempDir() + File.separator + "tiles" + File.separator + "bestFocus";
                } else {
                    mkMonIn = po.getTempDir() + File.separator + "bestFocus";
                }
                if(SystemUtils.IS_OS_WINDOWS) {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/C start /B /belownormal java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \".\\*\" org.nolanlab.codex.upload.driffta.MakeMontage \"" + mkMonIn + "\" 2");
                    guiHelper.log("Starting process: " + pb.command().toString());
                    pb.redirectErrorStream(true);
                    Process proc = pb.start();
                    allProcess.add(proc);
                    guiHelper.waitAndPrint(proc);
                    gui.getProgressAnimation().setIndeterminate(false);
                }

                else if(SystemUtils.IS_OS_LINUX) {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \"./*\" org.nolanlab.codex.upload.driffta.MakeMontage \"" + po.getTempDir() + File.separator + "bestFocus\" 2");
                    guiHelper.log("Starting process: " + pb.command().toString());
                    pb.redirectErrorStream(true);
                    Process proc = pb.start();
                    allProcess.add(proc);
                    guiHelper.waitAndPrint(proc);
                }

            } catch (Exception e) {
                guiHelper.log(ExceptionUtils.getStackTrace(e));
            }
        });
        th.start();
        return th;
    }

    /*
    Preview button clicked
    */
    public Thread previewActionPerformed() {
        Thread th = new Thread(() -> {
            try {
                File dir = new File(gui.getInputPathField().getText());
                if(dir == null) {
                    JOptionPane.showMessageDialog(null, "Please select input experiment location and output location before running a preview to display pre-processed stitched image!");
                }
                else {
                    guiHelper.logRouting(gui);
                    guiHelper.log("Starting to create pre-processed stitched image for the selected cyc, reg, ch, z...");

                    Experiment exp = Metadata.getExperiment(gui);
                    guiHelper.replaceTileOverlapInExp(dir, exp, gui);

                    int cyc = Integer.parseInt(gui.getPreviewCycleField().getText());
                    int reg = Integer.parseInt(gui.getPreviewRegionField().getText());
                    int ch = Integer.parseInt(gui.getPreviewChannelField().getText());
                    int z = Integer.parseInt(gui.getPreviewZPlaneField().getText());

                    if (workerHelper.areEmptyFields(gui)) {
                        if(workerHelper.areValidFields(exp, cyc, reg, ch, z, gui)) {
                            String zSlice = "";
                            if (z > 0 && z < 10) {
                                zSlice = "00" + z;
                            } else {
                                zSlice = "0" + z;
                            }

                            StackCombiner stackCombiner = new StackCombiner();
                            String finalZSlice = zSlice;
                            File[] cycFolders;
                            File[] tifFiles;
                            if(!gui.isTMA()) {
                                cycFolders = dir.listFiles(cy -> cy.getName().toLowerCase().equals("cyc" + cyc + "_reg" + reg));
                                tifFiles = cycFolders[0].listFiles(t -> t.getName().toLowerCase().endsWith(".tif") && t.getName().toLowerCase().contains("_z" + finalZSlice)
                                        && t.getName().toLowerCase().contains("_ch" + ch));
                            } else {
                                String regStr = "";
                                if (reg > 0 && reg < 10) {
                                    regStr = "0" + reg;
                                }
                                String finalRegStr = regStr;
                                cycFolders = dir.listFiles(cy -> cy.getName().toLowerCase().equals("cyc" + cyc + "_reg1" ));
                                File[] xyFolders = cycFolders[0].listFiles(xy -> xy.getName().toLowerCase().contains("xy" + finalRegStr));
                                tifFiles = xyFolders[0].listFiles(t -> t.getName().toLowerCase().endsWith(".tif") && t.getName().toLowerCase().contains("_z" + finalZSlice)
                                        && t.getName().toLowerCase().contains("_ch" + ch));
                            }

                            int maxX = Integer.parseInt(gui.getRegionWidthField().getText());
                            int maxY = Integer.parseInt(gui.getRegionHeightField().getText());

                            ImageStack[][] grid = new ImageStack[maxX][maxY];
                            int[] coord;
                            for (int i = 0; i < tifFiles.length; i++) {
                                if(!gui.isTMA()) {
                                    coord = workerHelper.extractXYFromFile(tifFiles[i], exp, reg);
                                } else {
                                    coord = new int[] {1, 1};
                                }
                                ImagePlus tmp = IJ.openImage(tifFiles[i].getAbsolutePath());
                                tmp = new ImagePlus(tmp.getTitle(), tmp.getImageStack().crop((int) Math.floor(exp.tile_overlap_X / 2), (int) Math.floor(exp.tile_overlap_Y / 2), 0, tmp.getWidth() - (int) Math.ceil(exp.tile_overlap_X), tmp.getHeight() - (int) Math.ceil(exp.tile_overlap_Y), tmp.getStackSize()));
                                ImageStack is = tmp.getImageStack();
                                StackProcessor sp = new StackProcessor(is);
                                grid[coord[0] - 1][coord[1] - 1] = sp.resize(tmp.getWidth() / 2, tmp.getHeight() / 2);
                            }

                            for (int x = 0; x < grid.length; x++) {
                                for (int y = 0; y < grid[x].length; y++) {
                                    if (grid[x][y] == null) {
                                        throw new IllegalStateException("Tile does not exist. Please check the preview inputs and try again...");
                                    }
                                }
                            }
                            int snakeTileNumber = 1;
                            for(int y = 0; y < grid[0].length; y++) {
                                if(y % 2 == 0) {
                                    for (int x = 0; x < grid.length; x++) {
                                        ImagePlus imp = new ImagePlus("", grid[x][y]);
                                        IJ.run(imp, "Label...", "format=Text starting=0 interval=1 x=24 y=110 font=58 text=" + snakeTileNumber++);
                                    }
                                } else {
                                    for (int x = grid.length-1; x >= 0; x--) {
                                        ImagePlus imp = new ImagePlus("", grid[x][y]);
                                        IJ.run(imp, "Label...", "format=Text starting=0 interval=1 x=24 y=110 font=58 text=" + snakeTileNumber++);
                                    }
                                }
                            }

                            ImageStack[] horizStacks = new ImageStack[grid[0].length];

                            for (int y = 0; y < horizStacks.length; y++) {
                                horizStacks[y] = grid[0][y];
                                for (int x = 1; x < grid.length; x++) {
                                    horizStacks[y] = stackCombiner.combineHorizontally(horizStacks[y], grid[x][y]);
                                }
                            }

                            ImageStack out = horizStacks[0];

                            for (int i = 1; i < horizStacks.length; i++) {
                                if (horizStacks[i] != null) {
                                    out = stackCombiner.combineVertically(out, horizStacks[i]);
                                }
                            }

                            ImagePlus res = new ImagePlus("Pre-processed stitched for z: " + zSlice, out);
                            guiHelper.log("Successfully created the pre-processed stitched image for the selected cyc, reg, ch, z...");
                            res.show();
                        } else {
                            guiHelper.log("Some fields are incorrect.. Please retry...");
                        }
                    } else {
                        guiHelper.log("Some fields are incorrect.. Please retry...");
                    }
                }
            } catch (Exception e) {
                guiHelper.log(ExceptionUtils.getStackTrace(e));
            }
        });
        th.start();
        return th;
    }
}
