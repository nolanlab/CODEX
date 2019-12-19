package org.nolanlab.codex.upload.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.Duplicator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.nolanlab.codex.Microscope;
import org.nolanlab.codex.MicroscopeFactory;
import org.nolanlab.codex.MicroscopeTypeEnum;
import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.ProcessingOptions;
import org.nolanlab.codex.upload.driffta.BestFocus;
import org.nolanlab.codex.upload.model.Metadata;
import org.nolanlab.codex.upload.util;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

public class GuiWorkers {
    private final NewGUI gui;
    private GuiHelper guiHelper = new GuiHelper();

    GuiWorkers(NewGUI gui) {
        this.gui = gui;
    }

    /*
   Method to load the values from the JSON file and set it to the Experiment property
    */
    public void loadFromJson(Experiment exp, File dir) {
        System.out.println("Started logging with load...");
        gui.getNameField().setText(exp.name);
        gui.getProjectNameField().setText(exp.projName);
//        val2.setText(exp.codex_instrument);

        if(exp.microscope instanceof MicroscopeTypeEnum) {
            gui.getMicroscopeTypeComboBox().setSelectedItem(exp.microscope);
        }
        else {
            if(exp.microscope == null) {
                guiHelper.guessMicroscope(dir, gui.getMicroscopeTypeComboBox());
            }
        }

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
        gui.getTileHeightField().setText(String.valueOf(exp.tile_height));
        gui.getTileWidthField().setText(String.valueOf(exp.tile_width));

        // Set imp fields
        gui.getNumCyclesField().setText(String.valueOf(exp.num_cycles));
        gui.getNumRegionsField().setText(String.valueOf(exp.region_names.length));
        gui.getNumChannelsField().setText(String.valueOf(exp.channel_names.length));

        //Calculate tile overlap
        if(dir != null) {
            File [] dirList = dir.listFiles();
            outer: for (File cyc : dirList) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File [] cycList = cyc.listFiles();
                    for(File file : cycList) {
                        if(!file.isDirectory() && (file.getName().endsWith(".tif")||file.getName().endsWith(".tiff"))){
                            ImagePlus imp = IJ.openImage(file.getAbsolutePath());
                            gui.getTileOverlapXField().setText(String.valueOf(exp.tile_overlap_X * 100/imp.getWidth()));
                            gui.getTileOverlapYField().setText(String.valueOf(exp.tile_overlap_Y * 100/imp.getHeight()));
                            break outer;
                        }
                    }
                }
            }
        }

        gui.gethAndEStainCheckBox().setSelected(exp.HandEstain);
        gui.getBackgroundSubtractionCheckBox().setSelected(exp.bgSub);
//        optionalBgSub.setSelectedItem(Boolean.toString(exp.bgSub) == null ? "No" : Boolean.toString(exp.bgSub).equalsIgnoreCase("true") ? "Yes" : "No");
//        optionalFragmentButton.setSelectedItem(Boolean.toString(exp.optionalFocusFragment) == null ? "No" : Boolean.toString(exp.optionalFocusFragment).equalsIgnoreCase("true") ? "Yes" : "No");
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

        GuiHelper.enableAll(gui,true);
    }

    public String parseExperimentFolderForFields(File dir) {

        StringBuilder err = new StringBuilder();

        int maxRegion = 0;
        int maxCycle = 0;

        boolean containsBcf = false;
        boolean hasHandE = false;

        for (File f : dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().toLowerCase().startsWith("cyc"))) {
            if (!containsBcf) {
                containsBcf = f.listFiles(pathname -> pathname.getName().endsWith(".bcf")).length > 0;
            }
            if (containsBcf) {
                gui.getMicroscopeTypeComboBox().setSelectedItem(MicroscopeTypeEnum.KEYENCE);
            }
            String[] s = f.getName().split("_");
            int cyc = Integer.parseInt(s[0].substring(3));
            int reg = Integer.parseInt(s[1].substring(3));

            maxRegion = Math.max(reg, maxRegion);
            maxCycle = Math.max(cyc, maxCycle);
        }

        int[][] occup_table = new int[maxCycle][maxRegion];

        for (File f : dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().toLowerCase().startsWith("cyc"))) {
            if (!containsBcf) {
                containsBcf = f.listFiles(pathname -> pathname.getName().endsWith(".bcf")).length > 0;
            }
            String[] s = f.getName().split("_");
            int cyc = Integer.parseInt(s[0].substring(3));
            int reg = Integer.parseInt(s[1].substring(3));
            occup_table[cyc - 1][reg - 1]++;
        }

        for (int cyc = 1; cyc <= occup_table.length; cyc++) {
            for (int reg = 1; reg <= occup_table[cyc - 1].length; reg++) {
                if (occup_table[cyc - 1][reg - 1] == 0) {
                    err.append("Missing data: cycle=").append(String.valueOf(cyc)).append(", region=").append(String.valueOf(reg)).append("\n");
                }
                if (occup_table[cyc - 1][reg - 1] > 1) {
                    err.append("Duplicate data: cycle=").append(String.valueOf(cyc)).append(", region=").append(String.valueOf(reg)).append(". Delete duplicate folders before proceeding\n");
                }
            }
        }

        File[] hef = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().startsWith("HandE"));

        hasHandE = (hef.length == maxRegion)&&hef.length> 0;

        if (!hasHandE && hef.length > 0) {
            err.append("The experiment has HandE folders, but their number is less than a number of regions");
        }

        if (hasHandE) {
            gui.gethAndEStainCheckBox().setSelected(true);
            maxCycle++;
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

        gui.getRegionNamesField().setText(regNames);
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

        return err.length() == 0 ? "" : ("Following errors were found in the experiment:\n" + err.toString());
    }

//    public void startActionPerformed() {
//        Thread th = new Thread(() -> {
//            try {
//                File dir = new File(gui.getInputPathField().getText());
//
//                if (dir == null || dir.getName().equals("...")) {
////                    log("Please select an experiment folder and try again!");
//                }
//
//                Experiment exp = Metadata.getExperiment(gui);
//                replaceTileOverlapInExp(dir, exp);
//
//                String experimentJS = exp.toJSON();
//
//                String microscopeType = exp != null && exp.microscope != null ? exp.microscope.toString() : "";
//                if (microscopeType == null || microscopeType.equals("")) {
//                    JOptionPane.showMessageDialog(null, "Microscope type is invalid");
//                }
//                Microscope microscope = MicroscopeFactory.getMicroscope(microscopeType);
//                //Included a feature to check if the product of region size X and Y is equal to the number of tiles
//                File expJSON = null;
//                if (microscope.isTilesAProductOfRegionXAndY(dir, experimentView)) {
//                    expJSON = new File(dir + File.separator + "Experiment.json");
//                    exp.saveToFile(expJSON);
//                } else {
//                    JOptionPane.showMessageDialog(null, "Check the values of Region Size X and Y and then try again!");
//                    return;
//                }
//
//                File poFile = new File(dir + File.separator + "processingOptions.json");
//
//                ProcessingOptions po = uploadOptionsView.getUploadOptions();
//                boolean doUpload = po.doUpload();
//                po.saveToFile(poFile);
//
//                //Copy Experiment.JSON to processed folder.
//                if (expJSON != null) {
//                    if (po.isExportImgSeq()) {
//                        copyFileFromSourceToDest(expJSON, new File(po.getTempDir() + File.separator + "tiles"));
//                    }
//                    copyFileFromSourceToDest(expJSON, po.getTempDir());
//                }
//
//                //Included a feature to check if the channelNames.txt file is present
//                if (!experimentView.isChannelNamesPresent(dir)) {
//                    JOptionPane.showMessageDialog(null, "channelNames.txt file is not present in the experiment folder. Please check and try again!");
//                    return;
//                }
//
//                log("Copying channelNames.txt file from experiment folder to processed folder location");
//
//                File source = new File(dir + File.separator + "channelNames.txt");
//                if (po.isExportImgSeq()) {
//                    copyFileFromSourceToDest(source, new File(po.getTempDir() + File.separator + "tiles"));
//                }
//                copyFileFromSourceToDest(source, po.getTempDir());
//
//                cmdStart.setEnabled(false);
//                cmdStop.setEnabled(true);
//
//                //Uploader upl = doUpload ? new Uploader(po.getDestinationUrl(), po.getNumThreads()) : null;
//
//            /*
//            if (doUpload) {
//                log("\nAuthorizing...");
//            }
//            final String token = doUpload ? upl.sendAuthRequest(po.getUsername(), po.getPassword()) : null;
//            if (doUpload) {
//                log("\nCreating new experiment...");
//            }
//            Uploader.FileShareAccess fsa = doUpload ? upl.sendExpCreateRequest(token, experimentJS) : null;
//            if (doUpload) {
//                log("\nStarting upload...");
//            }*/
//
//                log("Verifying names...");
//
//                for (File f : dir.listFiles(new FileFilter() {
//                    @Override
//                    public boolean accept(File file) {
//                        return file.isDirectory() && file.getName().startsWith("Cyc");
//                    }
//                })) {
//                    String name = f.getName();
//                    String[] s = name.split("_");
//                    if (s.length > 2) {
//                        f.renameTo(new File(dir + File.separator + s[0] + "_" + s[1]));
//                    }
//                }
//                File f = new File(".\\");
//
//                f.getAbsolutePath();
//
//                boolean chNamesUpl = true;
//
//                int totalCount = exp.region_names.length * exp.region_width * exp.region_height;
//
//                prg.setMaximum(totalCount);
//
//                int currCnt = 1;
//
//                Properties config = new Properties();
//                config.load(new FileInputStream(System.getProperty("user.home") + File.separator + "config.txt"));
//                String maxRAM = "";
//                if (config.toString().contains("maxRAM") && !StringUtils.isEmpty(config.get("maxRAM").toString())) {
//                    maxRAM = config.get("maxRAM").toString();
//                }
//                maxRAM = maxRAM.equals("") ? "48" : maxRAM;
//
//                if((exp.processTiles == null || exp.processTiles.length == 0) && (exp.processRegions == null || exp.processRegions.length == 0)) {
//                    for(int reg : exp.regIdx) {
//                        processTiles(exp, experimentView, po, allProcess, currCnt, maxRAM, 1, exp.region_height * exp.region_width, reg);
//                    }
//                }
//                else {
//                    int[] processRegs = Stream.of(exp.processRegions).mapToInt(Integer::parseInt).toArray();
//
//                    if (processRegs.length == exp.processTiles.length) {
//                        for (int i = 0; i < processRegs.length; i++) {
//                            if (exp.processTiles[i].contains(",")) {
//                                String[] tiles = exp.processTiles[i].split(",");
//                                for (int j = 0; j < tiles.length; j++) {
//                                    if (tiles[j].contains("-")) {
//                                        String[] tileRange = tiles[j].split("-");
//                                        int lowerTile = Integer.parseInt(tileRange[0]);
//                                        int upperTile = Integer.parseInt(tileRange[1]);
//                                        processTiles(exp, experimentView, po, allProcess, currCnt, maxRAM, lowerTile, upperTile, processRegs[i]);
//                                    } else {
//                                        int tile = Integer.parseInt(tiles[j]);
//                                        processTiles(exp, experimentView, po, allProcess, currCnt, maxRAM, tile, tile, processRegs[i]);
//                                    }
//                                }
//                            } else if (exp.processTiles[i].contains("-")) {
//                                String[] tileRange = exp.processTiles[i].split("-");
//                                int lowerTile = Integer.parseInt(tileRange[0]);
//                                int upperTile = Integer.parseInt(tileRange[1]);
//                                processTiles(exp, experimentView, po, allProcess, currCnt, maxRAM, lowerTile, upperTile, processRegs[i]);
//                            } else {
//                                int tile = Integer.parseInt(exp.processTiles[i]);
//                                processTiles(exp, experimentView, po, allProcess, currCnt, maxRAM, tile, tile, processRegs[i]);
//                            }
//                        }
//                    } else {
//                        log("The number of tiles and regions to be processed are not equal");
//                        throw new IllegalStateException("The number of tiles and regions to be processed are not equal... " +
//                                "processTiles length not equal to processRegions length");
//                    }
//                }
//
//                log("Checking if bestFocus folder is present...");
//
//                String bfDirStr = "";
//                if(po.isExportImgSeq()) {
//                    bfDirStr = po.getTempDir() + File.separator + "tiles" + File.separator + "bestFocus";
//                } else {
//                    bfDirStr = po.getTempDir() + File.separator + "bestFocus";
//                }
//
//                File bf = new File(bfDirStr);
//                if(!bf.exists()) {
//                    log("Best focus folder is not present. Running it for all the tiffs inside the processed folder.");
//                    File processed = new File(po.getTempDir().getPath());
//                    String bestFocus = bfDirStr;
//                    File mkBestFocus = new File(bestFocus);
//                    mkBestFocus.mkdirs();
//                    if(processed.isDirectory()) {
//                        File[] procTiff = processed.listFiles(fName -> (fName.getName().endsWith(".tiff") || fName.getName().endsWith(".tif")));
//                        for(File aTif : procTiff) {
//                            ImagePlus p = IJ.openImage(aTif.getPath());
//                            int[] bestFocusPlanes = new int[p.getNFrames()];
//                            Duplicator dup = new Duplicator();
//                            ImagePlus rp = dup.run(p, exp.best_focus_channel, exp.best_focus_channel, 1, p.getNSlices(), exp.bestFocusReferenceCycle-exp.cycle_lower_limit+1,  exp.bestFocusReferenceCycle-exp.cycle_lower_limit+1);
//                            int refZ = Math.max(1, BestFocus.findBestFocusStackFromSingleTimepoint(rp, 1, exp.optionalFocusFragment));
//                            //Add offset here
//                            refZ = refZ + exp.focusing_offset;
//                            Arrays.fill(bestFocusPlanes, refZ);
//
//                            ImagePlus focused = BestFocus.createBestFocusStackFromHyperstack(p, bestFocusPlanes);
//                            log("Saving the focused tiff " + aTif.getName()+ "where Z: " +bestFocusPlanes[0]);
//                            FileSaver fs = new FileSaver(focused);
//                            fs.saveAsTiff(bestFocus + File.separator + Experiment.getDestStackFileNameWithZIndexForTif(exp.tiling_mode, aTif.getName(), bestFocusPlanes[0]));
//                        }
//                    }
//                }
//
//                log("Creating montages");
//                String mkMonIn = null;
//                if(po.isExportImgSeq()) {
//                    log("Image sequence folder structure recognized...");
//                    mkMonIn = po.getTempDir() + File.separator + "tiles" + File.separator + "bestFocus";
//                } else {
//                    mkMonIn = po.getTempDir() + File.separator + "bestFocus";
//                }
//                if(SystemUtils.IS_OS_WINDOWS) {
//                    ProcessBuilder pb = new ProcessBuilder("cmd", "/C start /B /belownormal java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \".\\*\" org.nolanlab.codex.upload.driffta.MakeMontage \"" + mkMonIn + "\" 2");
//                    log("Starting process: " + pb.command().toString());
//                    pb.redirectErrorStream(true);
//                    Process proc = pb.start();
//                    allProcess.add(proc);
//                    waitAndPrint(proc);
//                }
//
//                else if(SystemUtils.IS_OS_LINUX) {
//                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \"./*\" org.nolanlab.codex.upload.driffta.MakeMontage \"" + po.getTempDir() + File.separator + "bestFocus\" 2");
//                    log("Starting process: " + pb.command().toString());
//                    pb.redirectErrorStream(true);
//                    Process proc = pb.start();
//                    allProcess.add(proc);
//                    waitAndPrint(proc);
//                }
//
//            } catch (Exception e) {
//                System.out.println(new Error(e));
//            }
//        });
//        th.start();
//        return th;
//    }
}
