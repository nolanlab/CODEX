package com.akoya.codex.segm;

import com.akoya.codex.upload.logger;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 *
 * @author Vishal
 */

public class SegmMain {

    private static JTextField configField = new JTextField(5);
    private static JPanel configPanel = new JPanel();
    /**
     * Mouseevent to open the filechooser option to specify config.txt TMP_SSD_DRIVE content.
     * @param evt
     */
    private static void configFieldDirMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDirMouseReleased
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (jfc.showOpenDialog(configPanel) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getSelectedFile() != null) {
                configField.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        }
        fireStateChanged();
    }

    private static void configFieldDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDirActionPerformed
    }

    private static void fireStateChanged() {
        PropertyChangeListener[] chl = configPanel.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(configPanel, "dir", "...", configField.getText()));
        }
    }


    /*
    Method to create a new dialog box to be input at the start-up of the application, when it is run
    on the machine the first time.
    */
    public static void numberOfGpuDialog() {

        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }

        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));
        configPanel.add(new JLabel("Select Folder: "));
        configPanel.add(configField);

        configField.setText("...");
        configField.setEnabled(false);
        configField.setMaximumSize(new java.awt.Dimension(3000, 20));
        configField.setMinimumSize(new java.awt.Dimension(300, 20));
        configField.setPreferredSize(new java.awt.Dimension(3000, 20));
        configField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                configFieldDirMouseReleased(evt);
            }
        });
        configField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configFieldDirActionPerformed(evt);
            }
        });

        int result = JOptionPane.showConfirmDialog(null, configPanel,
                "Specify configuration", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
        else {
            try {
                if(configField.getText().equals(null) || configField.getText().equalsIgnoreCase("...")) {
                    JOptionPane.showMessageDialog(configPanel,"Please specify directory before proceeding!");
                    System.exit(0);
                }
            }
            catch(Exception e) {
                logger.showException(e);
                JOptionPane.showMessageDialog(configPanel,"Could not locate directory. Try again!");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        numberOfGpuDialog();
        String[] arg = new String[3];
        arg[0] = configField.getText();
        //1. Call Main
        logger.print("Starting Main Segmentation...");
        Main.main(arg);
        logger.print("Main done");

        //2. Call ConcatenateResults
        logger.print("Starting ConcatenateResults...");
        ConcatenateResults.main(arg);
        logger.print("ConcatenateResults done");

        //3. Call MakeFCS
        logger.print("Starting MakeFCS...");
        MakeFCS.main(arg);
        logger.print("MakeFCS done");
    }
}