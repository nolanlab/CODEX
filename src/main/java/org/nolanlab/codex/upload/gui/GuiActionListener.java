package org.nolanlab.codex.upload.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.upload.Experiment;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 *
 * @author Vishal
 */

class GuiActionListener implements ActionListener {
    private final NewGUI gui;
    private GuiHelper guiHelper = new GuiHelper();

    GuiActionListener(NewGUI gui) {
        this.gui = gui;
    }
    public void actionPerformed(ActionEvent event) {
        try {
            if (event.getSource() == gui.getInputPathBrowseButton()) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setDialogTitle("Select an input directory for your experiment");
                if (jfc.showOpenDialog(gui.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
                    File expJS = new File(jfc.getSelectedFile().getAbsolutePath() + File.separator + "Experiment.json");
                    if (expJS.exists()) {
                        try {
                            GuiWorkers loadWorker = new GuiWorkers(gui);
                            loadWorker.loadFromJson(Experiment.loadFromJSON(expJS), jfc.getSelectedFile());
                        } catch (Exception e) {
                            guiHelper.log(ExceptionUtils.getStackTrace(e));
                        }
                    } else {
                        GuiWorkers guessWorker = new GuiWorkers(gui);
                        String err = guessWorker.parseExperimentFolderForFields(jfc.getSelectedFile());
                        if (err.length() > 0) {
                            JOptionPane.showMessageDialog(gui.getMainPanel(), err);
                            throw new IllegalStateException(err);
                        } else {
                            guiHelper.log("Fields were populated based on the input experiment folder. Please fill in any missing fields before processing...");
                            GuiHelper.enableButtonsOnInputLoad(gui, true);
                        }
                    }

                    gui.getInputPathField().setText(jfc.getSelectedFile().getAbsolutePath());

                    //Include the name of the experiment to be set as folder name
                    gui.getNameField().setText(jfc.getSelectedFile().getName());
//                    fireStateChanged();
                }
            } else if(event.getSource() == gui.getOutputPathBrowseButton()) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setDialogTitle("Select an output processed directory for your experiment");
                if (jfc.showOpenDialog(gui.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
                    gui.getOutputDirField().setText(jfc.getSelectedFile().getAbsolutePath());
//                    fireStateChanged();
                }
                if (!StringUtils.isBlank(gui.getOutputDirField().getText())) {
//                    gui.getOpenOutputButton().setEnabled(true);
                    GuiHelper.enableAll(gui, true);
                    guiHelper.log("Log file now available as uploader-console.log inside the output folder.");
                    guiHelper.logRouting(gui);
                }
            } else if(event.getSource() == gui.getOpenInputButton()) {
                openInputFolder();
            } else if(event.getSource() == gui.getEditChannelNamesButton()) {
                openChannelNames();
            } else if(event.getSource() == gui.getEditExperimentJsonButton()) {
                openExperimentJson();
            } else if(event.getSource() == gui.getEditExposureTimesButton()) {
                openExposureTimes();
            } else if(event.getSource() == gui.getOpenOutputButton()) {
                openOutputFolder();
            } else if(event.getSource() == gui.getOpenLogsButton()) {
                openLogs();
            } else if(event.getSource() == gui.getStartButton()) {
                GuiWorkers startWorker = new GuiWorkers(gui);
                startWorker.startActionPerformed();
            } else if(event.getSource() == gui.getStopButton()) {
                int selection = JOptionPane.showConfirmDialog(gui.getMainPanel(),
                        "Are you sure you want to stop the processing? This will close the application.", "Stop & close uploader", JOptionPane.YES_NO_OPTION);
                if (selection == JOptionPane.YES_OPTION) {
                    guiHelper.log("Uploader closed");
                    System.exit(0);
                }
            } else if(event.getSource() == gui.getPreviewGenerateButton()) {
                GuiWorkers previewWorker = new GuiWorkers(gui);
                previewWorker.previewActionPerformed();
            }
        } catch (Exception e) {
            guiHelper.log(ExceptionUtils.getStackTrace(e));
            gui.getProgressAnimation().setIndeterminate(false);
        }
    }

    private void openInputFolder() {
        guiHelper.openFolder(gui.getInputPathField().getText());
    }

    private void openChannelNames() {
        guiHelper.openTextEditor(gui.getInputPathField().getText() + File.separator + "channelNames.txt");
    }

    private void openExperimentJson() {
        guiHelper.openTextEditor(gui.getInputPathField().getText() + File.separator + "Experiment.json");
    }

    private void openExposureTimes() {
        guiHelper.openTextEditor(gui.getInputPathField().getText() + File.separator + "exposure_times.txt");
    }

    private void openOutputFolder() {
        guiHelper.openFolder(gui.getOutputDirField().getText());
    }

    private void openLogs() {
        guiHelper.openTextEditor(gui.getOutputDirField().getText()+ File.separator + "uploader-console.log");
    }

    private void fireStateChanged() {
        PropertyChangeListener[] chl = gui.getMainPanel().getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(this, "dir", "...", gui.getInputPathField().getText()));
        }
    }
}
