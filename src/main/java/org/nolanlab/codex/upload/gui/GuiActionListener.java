package org.nolanlab.codex.upload.gui;

import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

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
                            loadWorker.load(Experiment.loadFromJSON(expJS), jfc.getSelectedFile());
                        } catch (Exception e) {
                            logger.showException(e);
                            System.out.println(e.getMessage());
                        }
                    } else {
//                        String err = guessValues(jfc.getSelectedFile());
//                        if (err.length() > 0) {
//                            JOptionPane.showMessageDialog(this, err);
//                            throw new IllegalStateException(err);
//                        }
                    }

                    gui.getInputPathField().setText(jfc.getSelectedFile().getAbsolutePath());

                    //Include the name of the experiment to be set as folder name
                    gui.getNameField().setText(jfc.getSelectedFile().getName());

                    fireStateChanged();

                }
            } else if(event.getSource() == gui.getEditChannelNamesButton()) {
                openChannelNames();
            } else if(event.getSource() == gui.getEditExperimentJsonButton()) {
                openExperimentJson();
            } else if(event.getSource() == gui.getEditExposureTimesButton()) {
                openExposureTimes();
            }
        } catch (Exception e) {
//            log.debug(ExceptionUtils.getStackTrace(e));
        }
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

    private void fireStateChanged() {
        PropertyChangeListener[] chl = gui.getMainPanel().getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(this, "dir", "...", gui.getInputPathField().getText()));
        }
    }
}
