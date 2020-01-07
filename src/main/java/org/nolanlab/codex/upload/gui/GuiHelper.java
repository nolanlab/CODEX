package org.nolanlab.codex.upload.gui;

import ij.IJ;
import ij.ImagePlus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.MicroscopeTypeEnum;
import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.TextAreaOutputStream;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

/**
 *
 * @author Vishal
 */

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
                    JOptionPane.showMessageDialog(null, "Logs file was not found or has not been created yet.");
                    log(ExceptionUtils.getStackTrace(e));
                }
            } else {
                log("Could not find a Windows text editor!");
            }
        } else if (SystemUtils.IS_OS_MAC) {
            try {
                new ProcessBuilder("open", "\"" + textPath + "\"").start();
            } catch (IOException e) {
                log(ExceptionUtils.getStackTrace(e));
            }
        } else {
            log("Unsupported operating system!");
        }
    }

    public void openFolder(String textPath) {
        if(SystemUtils.IS_OS_WINDOWS) {
            try {
                Runtime.getRuntime().exec("explorer.exe " + textPath);
            } catch (IOException e) {
                log(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void replaceTileOverlapInExp(File dir, Experiment exp) {
        if(dir != null) {
            for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File[] cycFiles = cyc.listFiles(tif->tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif"));
                    ImagePlus imp = IJ.openImage(cycFiles[0].getAbsolutePath());
                    exp.tile_overlap_X  = (int)((double)(exp.tile_overlap_X *imp.getWidth()/100));
                    exp.tile_overlap_Y = (int)((double)(exp.tile_overlap_Y*imp.getHeight()/100));
                    break;
                }
            }
        }
    }

    public void copyFileFromSourceToDest(File source, File dest) {
        try {
            FileUtils.copyFileToDirectory(source, dest);
        } catch (IOException e) {
            log(ExceptionUtils.getStackTrace(e));
        }

    }

    public boolean isChannelNamesPresent(File dir) {
        File chNames = new File(dir + File.separator + "channelNames.txt");
        return chNames == null ? false : (!chNames.isDirectory() && chNames.exists());
    }

    public void waitAndPrint(Process proc) throws IOException {
        do {
            try {
                BufferedReader brOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String s = null;
                while ((s = brOut.readLine()) != null) {
                    log(s);
                }

                BufferedReader brErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                while ((s = brErr.readLine()) != null) {
                    log("ERROR>" + s);
                }

                Thread.sleep(100);

            } catch (InterruptedException e) {
                log("Process interrupted");
                return;
            }
        } while (proc.isAlive());
        log("Process done");
    }

    public void log(String s) {
        System.out.println(s);
    }

    public void logRouting(NewGUI gui) {
        File uploaderLogFile = new File(gui.getOutputDirField().getText() + File.separator + "uploader-console.log");
        gui.setTaOutputStream(new TextAreaOutputStream(gui.getLoggingTextArea(), "", uploaderLogFile));
        System.setOut(new PrintStream(gui.getTaOutputStream()));
        System.setErr(new PrintStream(gui.getTaOutputStream()));
    }

    public static void enableButtonsOnInputLoad(NewGUI gui, boolean enabled) {
        gui.getOpenInputButton().setEnabled(enabled);
        gui.getEditChannelNamesButton().setEnabled(enabled);
        gui.getEditExperimentJsonButton().setEnabled(enabled);
        gui.getEditExposureTimesButton().setEnabled(enabled);
        gui.getOutputPathBrowseButton().setEnabled(enabled);
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
        gui.getOpenOutputButton().setEnabled(enabled);
        gui.getOpenLogsButton().setEnabled(enabled);
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
