package org.nolanlab.codex.upload.model;

import org.apache.commons.lang3.StringUtils;
import org.nolanlab.codex.Microscope;
import org.nolanlab.codex.MicroscopeFactory;
import org.nolanlab.codex.MicroscopeTypeEnum;
import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.ProcessingOptions;
import org.nolanlab.codex.upload.gui.NewGUI;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Vishal
 */

public class Metadata {
    public static Experiment getExperiment(NewGUI gui) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = sdf.format(date);

        String[] regIds = gui.getRegionNamesField().getText().split(";");
        int[] reg = new int[regIds.length];
        for (int i = 0; i < reg.length; i++) {
            reg[i] = i + 1;
        }

        String[] wavelenS = gui.getWavelengthsField().getText().split(";");
        int[] wavelen = new int[wavelenS.length];
        for (int i = 0; i < wavelen.length; i++) {
            wavelen[i] = Integer.parseInt(wavelenS[i]);
        }

        if (gui.getInputPathField().getText().equals("...")) {
            throw new IllegalStateException("Folder not set");
        }

        if (gui.getNameField().getText().equals("<Experiment name>")) {
            throw new IllegalStateException("Experiment name not set");
        }

        File dir = new File(gui.getInputPathField().getText());

        File[] subdir = dir.listFiles(pathname -> pathname.isDirectory() && pathname.getName().startsWith("Cyc"));

        String projName = "p";

        if (gui.getMicroscopeTypeComboBox().getSelectedItem().toString().startsWith("Keyence")) {
            if (subdir.length == 0) {
                System.out.println("Directory does not contain a single folder starting with 'Cyc...'. Please try again!");
                throw new IllegalStateException("Directory" + dir.getAbsolutePath() + " does not contain a single folder starting with 'Cyc...'");
            }

            File[] bcf = subdir[0].listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".bcf");
                }
            });

            if (bcf.length == 0) {
                throw new IllegalStateException("Microscope is set to Keyence but there is no .bcf file in the directory ");
            }

            projName = bcf[0].getName().substring(0, bcf[0].getName().indexOf('.'));

        }

        String microscopeType = gui.getMicroscopeTypeComboBox().getSelectedItem() != null ? gui.getMicroscopeTypeComboBox().getSelectedItem().toString() : "";
        if(microscopeType == null || microscopeType.equals("")) {
            JOptionPane.showMessageDialog(null, "Microscope type is invalid");
        }

        Microscope microscope = MicroscopeFactory.getMicroscope(microscopeType);

        //New feature to support range for number of cycles
        String cyc = gui.getCycleRangeField().getText();
        int count = 0;
        for( int i=0; i<cyc.length(); i++ ) {
            if( cyc.charAt(i) == '-' ) {
                count++;
            }
        }
        int lowerCycLimit = 0;
        int upperCycLimit = 0;
        if(count == 0) {
            lowerCycLimit = StringUtils.isNumeric(gui.getCycleRangeField().getText()) ? Integer.parseInt(gui.getCycleRangeField().getText()) : Integer.MIN_VALUE;
            upperCycLimit = lowerCycLimit;
            if(lowerCycLimit == 0) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The number of cycles/range cannot be 0.");
                throw new IllegalStateException("The number of cycles/range cannot be 0.");
            }
            if(lowerCycLimit == Integer.MIN_VALUE) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The number of cycles is not a number. Please enter a number or range.");
                throw new IllegalStateException("The number of cycles is not a number. Please enter a number or range.");
            }
            if(Integer.parseInt(gui.getDriftReferenceCycleField().getText()) > lowerCycLimit) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "Drift compensation reference cycle is invalid.");
                throw new IllegalStateException("Drift compensation reference cycle is invalid.");
            }
            if(Integer.parseInt(gui.getBestFocusCycleField().getText()) > lowerCycLimit) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "Best focus cycle is invalid.");
                throw new IllegalStateException("Best focus cycle is invalid.");
            }
        }
        else if(count == 1) {
            String[] cycLimits = gui.getCycleRangeField().getText().split("-");
            if(cycLimits != null && cycLimits.length != 0) {
                lowerCycLimit = StringUtils.isNumeric(cycLimits[0]) ? Integer.parseInt(cycLimits[0]) : Integer.MIN_VALUE;
                upperCycLimit = StringUtils.isNumeric(cycLimits[1]) ? Integer.parseInt(cycLimits[1]) : Integer.MAX_VALUE;
            }
            if(lowerCycLimit > upperCycLimit) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The lower limit on the range of number of cycles cannot be greater than the upper limit.");
                throw new IllegalStateException("The lower limit on the range of number of cycles cannot be greater than the upper limit.");
            }
            if(lowerCycLimit == upperCycLimit) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The lower limit on the range of number of cycles cannot be equal to the upper limit.");
                throw new IllegalStateException("The lower limit on the range of number of cycles cannot be equal to the upper limit.");
            }
            if(lowerCycLimit == Integer.MIN_VALUE) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The lower limit on the range of number of cycles is not a number. Please enter a number.");
                throw new IllegalStateException("The lower limit on the range of number of cycles is not a number. Please enter a number.");
            }
            if(upperCycLimit == Integer.MAX_VALUE) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The upper limit on the range of number of cycles is not a number. Please enter a number.");
                throw new IllegalStateException("The upper limit on the range of number of cycles is not a number. Please enter a number.");
            }
            if(lowerCycLimit < 1) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "The lower limit on the range of number of cycles is invalid.");
                throw new IllegalStateException("The lower limit on the range of number of cycles is invalid.");
            }

            if(!gui.gethAndEStainCheckBox().isSelected()) {
                if (upperCycLimit > microscope.getMaxCycNumberFromFolder(dir)) {
                    JOptionPane.showMessageDialog(gui.getMainPanel(), "The upper limit on the range of number of cycles is invalid.");
                    throw new IllegalStateException("The upper limit on the range of number of cycles is invalid.");
                }
            }
            else {
                if (upperCycLimit > 1+microscope.getMaxCycNumberFromFolder(dir)) {
                    JOptionPane.showMessageDialog(gui.getMainPanel(), "The upper limit on the range of number of cycles is invalid for this experiment with H&E stain.");
                    throw new IllegalStateException("The upper limit on the range of number of cycles is invalid for this experiment with H&E stain.");
                }
            }
            if(Integer.parseInt(gui.getDriftReferenceCycleField().getText()) < lowerCycLimit || Integer.parseInt(gui.getDriftReferenceCycleField().getText()) > upperCycLimit) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "Drift compensation reference cycle is invalid.");
                throw new IllegalStateException("Drift compensation reference cycle is invalid.");
            }
            if(Integer.parseInt(gui.getBestFocusCycleField().getText()) < lowerCycLimit || Integer.parseInt(gui.getBestFocusCycleField().getText()) > upperCycLimit) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "Best focus cycle is invalid.");
                throw new IllegalStateException("Best focus cycle is invalid.");
            }
        }
        else {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please enter a valid number or range for number of cycles.");
            throw new IllegalStateException("Please enter a valid number or range for number cycles.");
        }

        if(StringUtils.isBlank(gui.getTileOverlapXField().getText()) || StringUtils.isBlank(gui.getTileOverlapYField().getText())) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please enter a valid percentage value for tile overlap.");
            throw new IllegalStateException("Please enter a valid percentage value for tile overlap.");
        }

        if(StringUtils.isBlank(gui.getProcessTilesField().getText())) {
            if(!StringUtils.isBlank(gui.getProcessRegionsField().getText())) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "Process regions must be blank because process tiles is blank.");
                throw new IllegalStateException("Process regions must be blank because process tiles is blank.");
            }
        }

        if(StringUtils.isBlank(gui.getProcessRegionsField().getText())) {
            if(!StringUtils.isBlank(gui.getProcessTilesField().getText())) {
                JOptionPane.showMessageDialog(gui.getMainPanel(), "Process tiles must be blank because process regions is blank.");
                throw new IllegalStateException("Process tiles must be blank because process regions is blank.");
            }
        }

        if(gui.getProcessTilesField().getText().split(";").length != gui.getProcessRegionsField().getText().split(";").length) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please make sure to have process tiles & regions mapped correctly.");
            throw new IllegalStateException("process tiles & process regions length are not equal");
        }

        return new Experiment(gui.getNameField().getText(),
                formattedDate,
                "CODEX-MPI",
                MicroscopeTypeEnum.getMicroscopeFromValue(gui.getMicroscopeTypeComboBox().getSelectedItem().toString()),
                gui.getDeconvolutionCheckBox().isSelected() ? "Microvolution" : "none",
                Integer.parseInt(gui.getDeconvolutionIterationsField().getText()),
                gui.getDeconvolutionModelComboBox().getSelectedItem().toString(),
                Integer.valueOf(gui.getMagnificationField().getText()),
                Double.parseDouble(gui.getApertureField().getText()),
                Double.parseDouble(gui.getXyResolutionField().getText()),
                Double.parseDouble(gui.getzPitchField().getText()),
                Integer.parseInt(gui.getNumPlanesField().getText()),
                (String) gui.getColorModeComboBox().getSelectedItem(),
                gui.getChannelNamesField().getText().split(";"),
                wavelen,
                Integer.parseInt(gui.getDriftReferenceChannelField().getText()),
                Integer.parseInt(gui.getDriftReferenceCycleField().getText()),
                Integer.parseInt(gui.getBestFocusCycleField().getText()),
                Integer.parseInt(gui.getBestFocusChannelField().getText()),
                lowerCycLimit,
                upperCycLimit,
                reg,
                gui.getRegionNamesField().getText().split(";"),
                "snake",
                Integer.parseInt(gui.getRegionWidthField().getText()),
                Integer.parseInt(gui.getRegionHeightField().getText()),
                Integer.parseInt(gui.getTileOverlapXField().getText()),
                Integer.parseInt(gui.getTileOverlapYField().getText()),
                gui.getObjectiveTypeComboBox().getSelectedItem().toString(),
                gui.gethAndEStainCheckBox().isSelected(),
                gui.getBackgroundSubtractionCheckBox().isSelected(),
                gui.getProjectNameField().getText(),
                gui.getOptionalFocusFragmentCheckBox().isSelected(),
                Integer.parseInt(gui.getFocusingOffsetField().getText()),
                StringUtils.isBlank(gui.getProcessTilesField().getText()) ? null : gui.getProcessTilesField().getText().split(";"),
                StringUtils.isBlank(gui.getProcessRegionsField().getText()) ? null : gui.getProcessRegionsField().getText().split(";")
        );
    }

    public static ProcessingOptions getProcessingOptions(NewGUI gui) {
        if (StringUtils.isBlank(gui.getOutputDirField().getText())) {
            System.out.println("Please specify the output folder and try again!");
            throw new IllegalStateException("Output directory not set");
        }
        return new ProcessingOptions(
                new File(gui.getOutputDirField().getText()),
                gui.getUseBleachMinimizingCropCheckBox().isSelected(),
                gui.getUseBlindDeconvolutionCheckBox().isSelected(),
                16,
                null,
               null,
               null,
                false,
                !gui.getImgSeqCheckBox().isSelected(),
                gui.getImgSeqCheckBox().isSelected()
        );
    }
}
