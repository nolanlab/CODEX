package org.nolanlab.codex.upload.gui;

import ij.IJ;
import ij.ImagePlus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.upload.scope.MicroscopeTypeEnum;
import org.nolanlab.codex.upload.model.Experiment;
import com.microvolution.Licensing;
import javax.swing.*;
import java.io.*;

/**
 *
 * @author Vishal
 */

public class GuiHelper {

    /**
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

    /**
     Open text editor - either notepad++ or notepad
     */
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

    /**
     Open the folder directly in windows file explorer
     */
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
        gui.getOutputPathBrowseButton().setEnabled(enabled);
        gui.getOpenInputButton().setEnabled(enabled);
        gui.getOpenOutputButton().setEnabled(enabled);
        gui.getOpenLogsButton().setEnabled(enabled);
        gui.getEditChannelNamesButton().setEnabled(enabled);
        gui.getEditExperimentJsonButton().setEnabled(enabled);
        gui.getEditExposureTimesButton().setEnabled(enabled);
        gui.getStartButton().setEnabled(enabled);
        gui.getPreviewGenerateButton().setEnabled(enabled);
    }

    /**
     * Check for a valid microvolution.lic file, if exists by default set the deconvolution field to true, else false.
     * @param deconvolutionCheckbox
     */
    public void logMicrovolutionInfo(JCheckBox deconvolutionCheckbox) {
        boolean deconvolutionLicense = Licensing.GetInstance().HaveValidLicense("deconvolution");
        boolean multiGpuLicense = Licensing.GetInstance().HaveValidLicense("multi_gpu");
        if (deconvolutionLicense) {
            if (multiGpuLicense) {
                log("Found a valid multi-GPU Microvolution license. Enabling the deconvolution field by default." +
                        " You can modify this field if you have to.");
                deconvolutionCheckbox.setSelected(true);
            } else {
                log("Found a valid single-GPU Microvolution license. Enabling the deconvolution field by default." +
                        " You can modify this field if you have to.");
                deconvolutionCheckbox.setSelected(true);
            }
        } else {
            log("Could not find a valid Microvolution license. Hence, disabling the deconvolution field by default." +
                    " You can modify this field if you have to.");
            deconvolutionCheckbox.setSelected(false);
        }
    }

    /**
     * Mouseevent to open the filechooser option to specify config.txt TMP_SSD_DRIVE content.
     *
     * @param gui
     */
    public void configFieldDirMouseReleased(NewGUI gui) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.showOpenDialog(gui.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getSelectedFile() != null) {
                gui.getConfigField().setText(jfc.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
