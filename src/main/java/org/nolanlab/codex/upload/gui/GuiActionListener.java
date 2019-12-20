package org.nolanlab.codex.upload.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.logger;

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
                            logger.showException(e);
                            System.out.println(e.getMessage());
                        }
                    } else {
                        GuiWorkers guessWorker = new GuiWorkers(gui);
                        String err = guessWorker.parseExperimentFolderForFields(jfc.getSelectedFile());
                        if (err.length() > 0) {
                            JOptionPane.showMessageDialog(gui.getMainPanel(), err);
                            throw new IllegalStateException(err);
                        }
                    }

                    gui.getInputPathField().setText(jfc.getSelectedFile().getAbsolutePath());

                    //Include the name of the experiment to be set as folder name
                    gui.getNameField().setText(jfc.getSelectedFile().getName());

                    fireStateChanged();
                }
            } else if(event.getSource() == gui.getOutputPathBrowseButton()) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setDialogTitle("Select an output processed directory for your experiment");
                if (jfc.showOpenDialog(gui.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
                    gui.getOutputDirField().setText(jfc.getSelectedFile().getAbsolutePath());
                    fireStateChanged();
                }
                if (!StringUtils.isBlank(gui.getOutputDirField().getText())) {
                    gui.getOpenOutputButton().setEnabled(true);
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
            } else if(event.getSource() == gui.getStartButton()) {
                GuiWorkers startWorker = new GuiWorkers(gui);
                startWorker.startActionPerformed();
            }
        } catch (Exception e) {
            guiHelper.log(ExceptionUtils.getStackTrace(e));
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

    private void fireStateChanged() {
        PropertyChangeListener[] chl = gui.getMainPanel().getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(this, "dir", "...", gui.getInputPathField().getText()));
        }
    }
}
