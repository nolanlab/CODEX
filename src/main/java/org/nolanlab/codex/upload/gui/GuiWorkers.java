package org.nolanlab.codex.upload.gui;

import ij.IJ;
import ij.ImagePlus;
import org.nolanlab.codex.MicroscopeTypeEnum;
import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.util;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public class GuiWorkers {
    private final NewGUI gui;
    private GuiHelper guiHelper = new GuiHelper();

    GuiWorkers(NewGUI gui) {
        this.gui = gui;
    }

    /*
   Method to load the values from the JSON file and set it to the Experiment property
    */
    public void load(Experiment exp, File dir) {
        System.out.println("Started logging with load...");
        gui.getNameField().setText(exp.name);
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
//        val11.setText(util.concat(exp.channel_names));
        gui.getWavelengthsField().setText(util.concat(exp.emission_wavelengths)); //OUT OF ORDER
        gui.getDriftReferenceChannelField().setValue(exp.drift_comp_channel);
        gui.getDriftReferenceCycleField().setValue(exp.driftCompReferenceCycle);
        gui.getBestFocusChannelField().setValue(exp.best_focus_channel);
        gui.getBestFocusCycleField().setValue(exp.bestFocusReferenceCycle);

        if(exp.cycle_upper_limit != exp.cycle_lower_limit) {
            gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit) + "-" + String.valueOf(exp.cycle_upper_limit));
        }
        else {
            gui.getCycleRangeField().setText(String.valueOf(exp.cycle_lower_limit));
        }

//        val14.setText(util.concat(exp.regIdx));
//        val15.setText(util.concat(exp.region_names));
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
        gui.getFocusingOffsetField().setValue(exp.focusing_offset);

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
////        processTiles.setText((exp.processTiles == null || exp.processTiles.length == 0) ? "1-" + exp.region_height * exp.region_width : String.join(";", exp.processTiles));
////        processRegions.setText((exp.processRegions == null || exp.processRegions.length == 0) ? String.join(";", Arrays.stream(exp.regIdx)
////                .mapToObj(String::valueOf)
////                .toArray(String[]::new)) : String.join(";", exp.processRegions));
//        processTiles.setText((exp.processTiles == null || exp.processTiles.length == 0) ? null : String.join(";", exp.processTiles));
//        processRegions.setText((exp.processRegions == null || exp.processRegions.length == 0) ? null : String.join(";", exp.processRegions));
    }
}
