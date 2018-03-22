/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload;

import com.akoya.codex.Microscope;
import com.akoya.codex.MicroscopeFactory;
import com.akoya.codex.upload.driffta.BestFocus;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.Duplicator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Nikolay Samusik
 */
public class frmMain extends javax.swing.JFrame {

    private JTextArea textArea = new JTextArea(15,30);
    private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea, "");
    private ArrayList<Process> allProcess = new ArrayList<>();

    /**
     * Creates new form frmMain
     */
    public frmMain() {
        System.setOut(new PrintStream(taOutputStream));
        initComponents();
        experimentView.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                File dir = new File(experimentView.getPath());
                File poFile = new File(dir + File.separator + "processingOptions.json");
                try {
                    //Directly replace ProcessionOptions content
                    ProcessingOptions po = ProcessingOptions.load(poFile);
                    uploadOptionsView.load(po);
                } catch (Exception e) {
                    log("Failed to load processingOptions.json file");
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        File dir = new File(System.getProperty("user.home"));

        try {
            File in = new File(dir.getCanonicalPath() + File.separator + "config.txt");
            if(in != null && !in.isDirectory() && !in.exists()) {
                numberOfGpuDialog();
            }
        } catch (Exception e) {
            logger.showException(e);
            System.exit(0);
        }

        buttonGroup1 = new javax.swing.ButtonGroup();
        experimentView = new com.akoya.codex.upload.ExperimentView();
        uploadOptionsView = new com.akoya.codex.upload.ProcessingOptionsView();
        prg = new javax.swing.JProgressBar();
        cmdStart = new javax.swing.JButton();
        cmdStop = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CODEXuploader");

        try {
            File workingDir = new File(".");
            if(workingDir != null) {
                ImageIcon img = new ImageIcon(workingDir.getCanonicalPath() + File.separator + "codexlogo.png");
                if (img != null) {
                    setIconImage(img.getImage());
                }
            }
        } catch(Exception e) {
            logger.showException(e);
        }

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JPanel newPanel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        newPanel.setLayout(gridBag);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.fill  = GridBagConstraints.BOTH;
        c.weightx =1;
        c.weighty =0;
        newPanel.add(experimentView, c);

        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=2;
        c.weightx =1;
        c.fill  = GridBagConstraints.HORIZONTAL;
        newPanel.add(uploadOptionsView, c);

        JScrollPane pane = new JScrollPane(newPanel);
        pane.setLayout(new ScrollPaneLayout());
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(980,200));
        c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=3;
        c.weightx =1;
        c.fill  = GridBagConstraints.HORIZONTAL;
        newPanel.add(scrollPane, c);

        prg.setMaximumSize(new java.awt.Dimension(320, 20));
        prg.setMinimumSize(new java.awt.Dimension(10, 20));
        prg.setName(""); // NOI18N
        prg.setPreferredSize(new java.awt.Dimension(300, 20));
        c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=4;
        c.weightx =0;
        c.fill  = GridBagConstraints.NONE;

        newPanel.add(prg, c);

        String upload = "Start the upload";
        String processing = "Start the processing";

        //default
        cmdStart.setText(processing);
        JRadioButton rbProcessing = uploadOptionsView.getRbProcessing();

        //Item listener to capture the state of the radio button to display the text
        ItemListener itl = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    cmdStart.setText(processing);
                }  else {
                    cmdStart.setText(upload);
                }
            }
        };
        rbProcessing.addItemListener(itl);

        cmdStart.setAlignmentX(0.5F);
        cmdStart.setAlignmentY(0.0F);
        cmdStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdStart.setMaximumSize(new java.awt.Dimension(150, 30));
        cmdStart.setMinimumSize(new java.awt.Dimension(150, 30));
        cmdStart.setPreferredSize(new java.awt.Dimension(150, 30));
        cmdStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    cmdStartActionPerformed(evt);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        //Stop button
        cmdStop.setText("Stop");
        cmdStop.setEnabled(false);
        cmdStop.setAlignmentX(0.5F);
        cmdStop.setAlignmentY(0.0F);
        cmdStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdStop.setMaximumSize(new java.awt.Dimension(150, 30));
        cmdStop.setMinimumSize(new java.awt.Dimension(150, 30));
        cmdStop.setPreferredSize(new java.awt.Dimension(150, 30));
        cmdStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStopActionPerformed(evt);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(cmdStart);
        buttonPanel.add(cmdStop);

        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=5;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill  = GridBagConstraints.NONE;

        newPanel.add(buttonPanel, c);

        pane.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));

        mainPanel.add(pane, BorderLayout.CENTER);
        getContentPane().add(mainPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Mouseevent to open the filechooser option to specify config.txt TMP_SSD_DRIVE content.
     * @param evt
     */
    private void configFieldDirMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDirMouseReleased
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getSelectedFile() != null) {
                configField.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        }
        fireStateChanged();
    }

    private void configFieldDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDirActionPerformed
    }

    private void fireStateChanged() {
        PropertyChangeListener[] chl = this.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(this, "dir", "...", configField.getText()));
        }
    }

    /*
        Method to create a new dialog box to be input at the start-up of the application, when it is run
        on the machine the first time.
     */
    public void numberOfGpuDialog() {

        JPanel gpuPanel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        gpuPanel.setLayout(gridBag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        gpuPanel.add(new JLabel("Number of GPUs: \t"), c);


        c= new GridBagConstraints();
        c.gridx=1;
        c.gridy=0;
        gpuPanel.add(spinGPU, c);
        spinGPU.setMaximumSize(new java.awt.Dimension(3000, 20));
        spinGPU.setMinimumSize(new java.awt.Dimension(60, 20));
        spinGPU.setPreferredSize(new java.awt.Dimension(60, 20));
        spinGPU.setModel(new javax.swing.SpinnerNumberModel(1, 1, 200, 1));

        c= new GridBagConstraints();
        c.gridx=0;
        c.gridy=1;
        gpuPanel.add(new JLabel("\nTMP_SSD_DRIVE: \t"), c);

        c= new GridBagConstraints();
        c.gridx=1;
        c.gridy=1;
        configField.setMaximumSize(new java.awt.Dimension(3000, 20));
        configField.setMinimumSize(new java.awt.Dimension(300, 20));
        configField.setPreferredSize(new java.awt.Dimension(300, 20));
        gpuPanel.add(configField, c);
        configField.setText("...");
        configField.setEnabled(false);
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

        c= new GridBagConstraints();
        c.gridx=0;
        c.gridy=2;
        gpuPanel.add(new JLabel("\nMax RAM size: \t"), c);

        c= new GridBagConstraints();
        c.gridx=1;
        c.gridy=2;
        spinRAM.setMaximumSize(new java.awt.Dimension(3000, 20));
        spinRAM.setMinimumSize(new java.awt.Dimension(60, 20));
        spinRAM.setPreferredSize(new java.awt.Dimension(60, 20));
        gpuPanel.add(spinRAM, c);
        spinRAM.setModel(new javax.swing.SpinnerNumberModel(48, 4, 256, 4));

        int result = JOptionPane.showConfirmDialog(null, gpuPanel,
                "Specify configuration", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
        else {
            //Add content to config.txt
            try {
                File dir = new File(System.getProperty("user.home"));
                if(configField.getText().equals(null) || configField.getText().equalsIgnoreCase("...")) {
                    JOptionPane.showMessageDialog(this,"Could not save config.txt file, please specify directory for TMP_SSD_DRIVE");
                    System.exit(0);
                }
                if(StringUtils.isBlank(spinGPU.getValue().toString())) {
                    JOptionPane.showMessageDialog(this,"Could not save config.txt file, please enter value for number of GPUs");
                    System.exit(0);
                }
                if(StringUtils.isBlank(spinRAM.getValue().toString())) {
                    JOptionPane.showMessageDialog(this,"Could not save config.txt file, please enter value for RAM size");
                    System.exit(0);
                }
                String str = configField.getText().replaceAll("\\\\", "/");
                List<String> lines = Arrays.asList("TMP_SSD_DRIVE="+str, "numGPU="+spinGPU.getValue(), "maxRAM="+spinRAM.getValue());
                Path file = Paths.get(dir.getCanonicalPath() + File.separator + "config.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
            }
            catch(IOException e) {
               logger.showException(e);
               JOptionPane.showMessageDialog(this,"Could not save the config.txt file");
               System.exit(0);
            }
        }
    }

    /*
    Replaces tile overlap in percent with pixel value in the exp.json file
     */
    private void replaceTileOverlapInExp(File dir, Experiment exp) {
        if(dir != null) {
            for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory()) {
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
            log(e.getMessage());
        }
    }

    public Thread cmdStartActionPerformed(java.awt.event.ActionEvent evt) throws Exception {//GEN-FIRST:event_cmdStartActionPerformed
       Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                File dir = new File(experimentView.getPath());

                if(dir == null || dir.getName().equals("...")) {
                    log("Please select an experiment folder and try again!");
                }

                Experiment exp = experimentView.getExperiment();
                replaceTileOverlapInExp(dir, exp);

                String experimentJS = exp.toJSON();

                String microscopeType = exp != null && exp.microscope != null ? exp.microscope.toString() : "";
                if(microscopeType == null || microscopeType.equals("")) {
                    JOptionPane.showMessageDialog(null, "Microscope type is invalid");
                }
                Microscope microscope = MicroscopeFactory.getMicroscope(microscopeType);
                //Included a feature to check if the product of region size X and Y is equal to the number of tiles
                File expJSON = null;
                if(microscope.isTilesAProductOfRegionXAndY(dir, experimentView)) {
                    expJSON = new File(dir + File.separator + "Experiment.json");
                    exp.saveToFile(expJSON);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Check the values of Region Size X and Y and then try again!");
                    return;
                }

                File poFile = new File(dir + File.separator + "processingOptions.json");

                ProcessingOptions po = uploadOptionsView.getUploadOptions();
                boolean doUpload = po.doUpload();
                po.saveToFile(poFile);

                //Copy Experiment.JSON to processed folder.
                if(expJSON != null) {
                    copyFileFromSourceToDest(expJSON, po.getTempDir());
                }

                        //Included a feature to check if the channelNames.txt file is present
                if (!experimentView.isChannelNamesPresent(dir)) {
                    JOptionPane.showMessageDialog(null, "channelNames.txt file is not present in the experiment folder. Please check and try again!");
                    return;
                }

                log("Copying channelNames.txt file from experiment folder to processed folder location");

                File source = new File(dir + File.separator + "channelNames.txt");
                File dest = po.getTempDir();
                copyFileFromSourceToDest(source, dest);

                cmdStart.setEnabled(false);
                cmdStop.setEnabled(true);

                Uploader upl = doUpload ? new Uploader(po.getDestinationUrl(), po.getNumThreads()) : null;

                if (doUpload) {
                    log("\nAuthorizing..."); 
                }
                final String token = doUpload ? upl.sendAuthRequest(po.getUsername(), po.getPassword()) : null;
                if (doUpload) {
                    log("\nCreating new experiment...");
                }
                Uploader.FileShareAccess fsa = doUpload ? upl.sendExpCreateRequest(token, experimentJS) : null;
                if (doUpload) {
                    log("\nStarting upload...");
                }

                log("Verifying names...");

                for (File f : dir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() && file.getName().startsWith("Cyc");
                    }
                })) {
                    String name = f.getName();
                    String[] s = name.split("_");
                    if (s.length > 2) {
                        f.renameTo(new File(dir + File.separator + s[0] + "_" + s[1]));
                    }
                }
                File f = new File(".\\");

                f.getAbsolutePath();

                boolean chNamesUpl = true;

                int totalCount = exp.region_names.length * exp.region_width * exp.region_height;

                prg.setMaximum(totalCount);

                int currCnt = 1;

                Properties config = new Properties();
                config.load(new FileInputStream(System.getProperty("user.home")+File.separator+"config.txt"));
                String maxRAM = "";
                if(config.toString().contains("maxRAM") && !StringUtils.isEmpty(config.get("maxRAM").toString())) {
                    maxRAM = config.get("maxRAM").toString();
                }
                maxRAM = maxRAM.equals("") ? "48":maxRAM;

                for (int reg : exp.regIdx) {
                    for (int tile = 1; tile <= exp.region_height * exp.region_width; tile++) {
                        File d = null;
                        if(!po.isExportImgSeq()) {
                            d = new File(po.getTempDir() + File.separator + Experiment.getDestStackFileName(exp.tiling_mode, tile, reg, exp.region_width));
                        }
                        else {
                            d = new File(po.getTempDir() + File.separator + FilenameUtils.removeExtension(Experiment.getDestStackFileName(exp.tiling_mode, tile, reg, exp.region_width)));
                        }
                        int numTrial = 0;
                        while (!d.exists() && numTrial < 3) {
                            numTrial++;
                            if(SystemUtils.IS_OS_WINDOWS) {
                                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \".\\*\" com.akoya.codex.upload.driffta.Driffta \"" + experimentView.getPath() + "\" \"" + po.getTempDir() + "\" " + String.valueOf(reg) + " " + String.valueOf(tile));
                                pb.redirectErrorStream(true);

                                log("Starting process: " + pb.command().toString());
                                Process proc = pb.start();
                                allProcess.add(proc);

                                waitAndPrint(proc);
                                log("Driffta done");
                            }
                            else if(SystemUtils.IS_OS_LINUX) {
                                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \"./*\" com.akoya.codex.upload.driffta.Driffta \"" + experimentView.getPath() + "\" \"" + po.getTempDir() + "\" " + String.valueOf(reg) + " " + String.valueOf(tile));
                                pb.redirectErrorStream(true);

                                log("Starting process: " + pb.command().toString());
                                Process proc = pb.start();
                                allProcess.add(proc);

                                waitAndPrint(proc);
                                log("Driffta done");

                            }
                        }

                        if (!d.exists()) {
                            log("Tile processing failed 3 times in a row: " + d.getName());
                        }

                        if (doUpload) {
                            d = new File(po.getTempDir() + File.separator + Experiment.getDestStackFileName(exp.tiling_mode, tile, reg, exp.region_width));
                            if (!d.exists()) {
                                throw new IllegalStateException("Driftcompensation completed, but the result file does not exist:" + d.getPath());
                            } else {
                                logger.print("File exists:" + Experiment.getDestStackFileName(exp.tiling_mode, tile, reg, exp.region_width));
                                upl.uploadFilesMultith(d, fsa, reg, tile, token, 1);
                                if (chNamesUpl) {
                                    upl.uploadFilesMultith(new File(experimentView.getPath() + File.separator + "channelNames.txt"), fsa, 0, 0, token, 1);
                                    chNamesUpl = false;
                                }
                            }
                        }
                        prg.setValue(currCnt++);
                        frmMain.this.repaint();
                    }
                }

                log("Checking if bestFocus folder is present...");

                File bf = new File(po.getTempDir() + File.separator + "bestFocus");
                if(!bf.exists()) {
                    log("Best focus folder is not present. Running it for all the tiffs inside the processed folder.");
                    File processed = new File(po.getTempDir().getPath());
                    String bestFocus = po.getTempDir() + File.separator + "bestFocus";
                    File mkBestFocus = new File(bestFocus);
                    mkBestFocus.mkdir();
                    if(processed.isDirectory()) {
                        File[] procTiff = processed.listFiles(fName -> (fName.getName().endsWith(".tiff") || fName.getName().endsWith(".tif")));
                        for(File aTif : procTiff) {
                            ImagePlus p = IJ.openImage(aTif.getPath());
                            int[] bestFocusPlanes = new int[p.getNFrames()];
                            Duplicator dup = new Duplicator();
                            ImagePlus rp = dup.run(p, exp.best_focus_channel, exp.best_focus_channel, 1, p.getNSlices(), exp.bestFocusReferenceCycle-exp.cycle_lower_limit+1,  exp.bestFocusReferenceCycle-exp.cycle_lower_limit+1);
                            int refZ = Math.max(1,BestFocus.findBestFocusStackFromSingleTimepoint(rp, 1, exp.optionalFocusFragment));
                            //Add offset here
                            refZ = refZ + exp.focusing_offset;
                            Arrays.fill(bestFocusPlanes, refZ);

                            ImagePlus focused = BestFocus.createBestFocusStackFromHyperstack(p, bestFocusPlanes);
                            log("Saving the focused tiff " + aTif.getName()+ "where Z: " +bestFocusPlanes[0]);
                            FileSaver fs = new FileSaver(focused);
                            fs.saveAsTiff(bestFocus + File.separator + Experiment.getDestStackFileNameWithZIndexForTif(exp.tiling_mode, aTif.getName(), bestFocusPlanes[0]));
                        }
                    }
                }

                log("Creating montages");
                if(SystemUtils.IS_OS_WINDOWS) {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/C start /B /belownormal java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \".\\*\" com.akoya.codex.upload.driffta.MakeMontage \"" + po.getTempDir() + File.separator + "bestFocus\" 2");
                    log("Starting process: " + pb.command().toString());
                    pb.redirectErrorStream(true);
                    Process proc = pb.start();
                    allProcess.add(proc);
                    waitAndPrint(proc);
                }

                else if(SystemUtils.IS_OS_LINUX) {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \"./*\" com.akoya.codex.upload.driffta.MakeMontage \"" + po.getTempDir() + File.separator + "bestFocus\" 2");
                    log("Starting process: " + pb.command().toString());
                    pb.redirectErrorStream(true);
                    Process proc = pb.start();
                    allProcess.add(proc);
                    waitAndPrint(proc);
                }

            } catch (Exception e) {
                System.out.println(new Error(e));
            }
          }
        });
        th.start();
        return th;
    }//GEN-LAST:event_cmdStartActionPerformed

    private void cmdStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        new Thread(() -> {
            cmdStop.setEnabled(false);
            cmdStart.setEnabled(true);
            prg.setValue(0);
            for(Process proc : allProcess) {
                if(proc != null) {
                    proc.destroy();
                }
            }
            log("All Processes stopped.");
            throw new IllegalStateException("Process stopped.");
        }).start();
    }

    public static void waitAndPrint(Process proc) throws IOException {
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

    public static void log(String s) {
        System.out.println(s);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

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

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frmMain frm = new frmMain();
                    frm.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - frm.getWidth()) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - frm.getHeight()) / 2, frm.getWidth(), frm.getHeight());
                    frm.setVisible(true);
                } catch (Throwable e) {
                    logger.showException(e);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cmdStart;
    private com.akoya.codex.upload.ExperimentView experimentView;
    private javax.swing.JProgressBar prg;
    private com.akoya.codex.upload.ProcessingOptionsView uploadOptionsView;
    private JSpinner spinGPU = new JSpinner();
    private JSpinner spinRAM = new JSpinner();
    private JButton cmdStop;
    private JTextField configField = new JTextField(5);

    public ExperimentView getExperimentView() {
        return experimentView;
    }

    public void setExperimentView(ExperimentView experimentView) {
        this.experimentView = experimentView;
    }

    public ProcessingOptionsView getUploadOptionsView() {
        return uploadOptionsView;
    }

    public void setUploadOptionsView(ProcessingOptionsView uploadOptionsView) {
        this.uploadOptionsView = uploadOptionsView;
    }

    public JButton getCmdStart() {
        return cmdStart;
    }

    public void setCmdStart(JButton cmdStart) {
        this.cmdStart = cmdStart;
    }

    // End of variables declaration//GEN-END:variables
}
