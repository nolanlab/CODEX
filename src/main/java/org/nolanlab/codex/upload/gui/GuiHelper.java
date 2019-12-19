package org.nolanlab.codex.upload.gui;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.MicroscopeTypeEnum;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GuiHelper {

    /*
    Set the microscope type
    */
    public void guessMicroscope(File dir, JComboBox microscopeTypeComboBox) {
        boolean flag = false;
        if(dir != null) {
            for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    microscopeTypeComboBox.setSelectedItem(MicroscopeTypeEnum.KEYENCE);
                    flag = true;
                    break;
                }
            }
            if(!flag) {
                microscopeTypeComboBox.setSelectedItem(MicroscopeTypeEnum.ZEISS);
            }
        }
    }

    public void openTextEditor(String textPath) {
        if (SystemUtils.IS_OS_WINDOWS) {
            String editorPath = "";
            String[] editorPaths = new String[]{
                    "C:\\Program Files\\Notepad++\\notepad++.exe",
                    "C:\\Program Files (x86)\\Notepad++\\notepad++.exe",
                    "C:\\Windows\\notepad.exe"};
            for (String p : editorPaths) {
                if (new File(p).exists()) {
                    editorPath = p;
                    break;
                }
            }
            if (!editorPath.isEmpty()) {
                try {
                    Runtime.getRuntime().exec(editorPath + " \"" + textPath + "\"");
                } catch (IOException e) {
//                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else {
//                log.error("Could not find a Windows text editor!");
            }
        } else if (SystemUtils.IS_OS_MAC) {
            try {
                new ProcessBuilder("open", "\"" + textPath + "\"").start();
            } catch (IOException e) {
//                log.error(ExceptionUtils.getStackTrace(e));
            }
        } else {
//            log.error("Unsupported operating system!");
        }
    }

    public void openFolder(String textPath) {
        if(SystemUtils.IS_OS_WINDOWS) {
            try {
                Runtime.getRuntime().exec("explorer.exe " + textPath);
            } catch (IOException e) {
                // log.error()
            }
        }
    }

    public static void enableAll(NewGUI gui, boolean enabled) {
        // Main panel
//        gui.getInputPathField().setEnabled(enabled);
//        gui.getOutputDirField().setEnabled(enabled);
        gui.getOutputPathBrowseButton().setEnabled(enabled);
//        gui.getNameField().setEnabled(enabled);
//        gui.getRegionWidthField().setEnabled(enabled);
//        gui.getRegionHeightField().setEnabled(enabled);
//        gui.getTileOverlapXField().setEnabled(enabled);
//        gui.getTileOverlapYField().setEnabled(enabled);
        gui.getOpenInputButton().setEnabled(enabled);
        gui.getEditChannelNamesButton().setEnabled(enabled);
        gui.getEditExperimentJsonButton().setEnabled(enabled);
        gui.getEditExposureTimesButton().setEnabled(enabled);
        gui.getStartButton().setEnabled(enabled);
        gui.getPreviewGenerateButton().setEnabled(enabled);

//        gui.getBackgroundSubtractionCheckBox().setEnabled(enabled);
//        gui.getDeconvolutionCheckBox().setEnabled(enabled);

        // Process panel
//        gui.getObjectiveTypeComboBox().setEnabled(enabled);
//        gui.getMagnificationField().setEnabled(enabled);
//        gui.getApertureField().setEnabled(enabled);
//        gui.getXyResolutionField().setEnabled(enabled);
//        gui.getzPitchField().setEnabled(enabled);
//        gui.getWavelengthsField().setEnabled(enabled);
//        gui.getNumRegionsField().setEnabled(enabled);
//        gui.getNumCyclesField().setEnabled(enabled);
//        gui.getNumPlanesField().setEnabled(enabled);
//        gui.getNumChannelsField().setEnabled(enabled);
//        gui.getTileWidthField().setEnabled(enabled);
//        gui.getTileHeightField().setEnabled(enabled);

//        gui.getReferenceCycleField().setEnabled(enabled);
//        gui.getReferenceChannelField().setEnabled(enabled);
//        gui.getDeconvolutionIterationsField().setEnabled(enabled);
//        gui.getDeconvolutionModelComboBox().setEnabled(enabled);
//        gui.getFocusingOffsetField().setEnabled(enabled);
//        gui.getUse3dCycleAlignmentCheckBox().setEnabled(enabled);
//        gui.getUseBlindDeconvolutionCheckBox().setEnabled(enabled);
//        gui.getUseBleachMinimizingCropCheckBox().setEnabled(false);
//        gui.getDiagnosticOutputCheckBox().setEnabled(enabled);

        // Advanced Panel
//        gui.getPreviewRegionField().setEnabled(enabled);
//        gui.getPreviewCycleField().setEnabled(enabled);
//        gui.getPreviewChannelField().setEnabled(enabled);
//        gui.getPreviewZPlaneField().setEnabled(enabled);
//        gui.getPreviewGenerateButton().setEnabled(enabled);
    }
}
