package org.nolanlab.codex.segm;

import org.nolanlab.codex.DefaultOptionPane;
import org.nolanlab.codex.OkayMockOptionPane;
import org.nolanlab.codex.OptionPane;
import org.nolanlab.codex.upload.TextAreaOutputStream;
import org.nolanlab.codex.upload.logger;


import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author Vishal
 */

public class SegmMain extends JFrame {

    private JTextField configField = new JTextField(5);
    private JTextField configName = new JTextField(5);
    private JPanel configPanel = new JPanel();
    private static int version = 1;
    private JTextArea textArea = new JTextArea(15,30);
    private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea, "");
    private SegmConfigFrm segmConfigFrm;
    private JButton cmdCreate;
    private JButton cmdPreview;
    private OptionPane optionPane = new DefaultOptionPane();
    private OptionPane previewOptionPane = new DefaultOptionPane();
    private JPanel previewPanel = new JPanel();
    private JComboBox<String> previewRegion = new JComboBox<>();


    public SegmMain() throws Exception {
        System.setOut(new PrintStream(taOutputStream));
        //initComponents();
    }

    public void initComponents() throws Exception {
        segmConfigFrm = new SegmConfigFrm();
        inputFolderDialog();
        cmdCreate = new JButton();
        cmdPreview = new JButton();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Create Segmentation configuration");

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
            System.out.println(e.getMessage());
        }

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
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
        c.weighty =1;
        newPanel.add(segmConfigFrm, c);

        JScrollPane pane = new JScrollPane(newPanel);
        pane.setLayout(new ScrollPaneLayout());
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(980,200));
        c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=2;
        c.weightx =1;
        c.weighty =1;
        c.fill  = GridBagConstraints.HORIZONTAL;
        newPanel.add(scrollPane, c);

        cmdCreate.setText("Start");
        cmdCreate.setAlignmentX(0.5F);
        cmdCreate.setAlignmentY(0.0F);
        cmdCreate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdCreate.setMaximumSize(new java.awt.Dimension(150, 150));
        cmdCreate.setMinimumSize(new java.awt.Dimension(150, 30));
        cmdCreate.setPreferredSize(new java.awt.Dimension(150, 30));
        cmdCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCreateButtonClicked(evt);
            }
        });

        cmdPreview.setText("Preview for one tile");
        cmdPreview.setAlignmentX(0.5F);
        cmdPreview.setAlignmentY(0.0F);
        cmdPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdPreview.setMaximumSize(new java.awt.Dimension(150, 150));
        cmdPreview.setMinimumSize(new java.awt.Dimension(150, 30));
        cmdPreview.setPreferredSize(new java.awt.Dimension(150, 30));
        cmdPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPreviewButtonClicked(evt);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(cmdCreate);
        buttonPanel.add(cmdPreview);

        c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=6;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill  = GridBagConstraints.NONE;

        newPanel.add(buttonPanel, c);

        pane.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));

        mainPanel.add(pane, BorderLayout.CENTER);
        getContentPane().add(mainPanel);

        pack();
    }
    /**
     * Mouseevent to open the filechooser option to specify config.txt TMP_SSD_DRIVE content.
     * @param evt
     */
    private void configFieldDirMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDirMouseReleased
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

    private void fireStateChanged() {
        PropertyChangeListener[] chl = configPanel.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(configPanel, "dir", "...", configField.getText()));
        }
    }


    /*
    Method to create a new dialog box to be input at the start-up of the application, when it is run
    on the machine the first time.
    */
    public void inputFolderDialog() {

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

        GridBagLayout gridBag = new GridBagLayout();
        configPanel.setLayout(gridBag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        c.fill  = GridBagConstraints.BOTH;
        c.weightx =1;
        c.weighty =1;
        configPanel.add(new JLabel("Select input folder to be segmented: "), c);

        c.gridx=1;
        c.gridy=0;
        c.fill  = GridBagConstraints.BOTH;
        c.weightx =1;
        c.weighty =1;
        configPanel.add(configField, c);

        c.gridx=0;
        c.gridy=1;
        c.fill  = GridBagConstraints.BOTH;
        c.weightx =1;
        c.weighty =1;
        configPanel.add(new JLabel("For image sequence format, save configuration as: "), c);

        c.gridx=1;
        c.gridy=1;
        c.fill  = GridBagConstraints.BOTH;
        c.weightx =1;
        c.weighty =1;
        configName.setText("segm1");
        configPanel.add(configName, c);

        if(!(optionPane instanceof OkayMockOptionPane)) {
            configField.setText("...");
        }
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
        configField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                configFieldMouseReleased(evt);
            }
        });

        c.gridx=0;
        c.gridy=2;
        c.fill  = GridBagConstraints.BOTH;
        c.weightx =1;
        c.weighty =1;
        configPanel.add(new JLabel("If your input folder is not in image sequence format, you can leave the configuration name as is..."), c);

        int result = optionPane.showConfirmDialog(null, configPanel,
                "Specify folder", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
        else {
            if(configField.getText().equals(null) || configName.getText().equals("")) {
                JOptionPane.showMessageDialog(configPanel,"The configuration name cannot be blank. Try again!");
                System.exit(0);
            }
            try {
                if(configField.getText().equals(null) || configField.getText().equalsIgnoreCase("...")) {
                    JOptionPane.showMessageDialog(configPanel,"Please specify directory before proceeding!");
                    System.exit(0);
                }
                else {
                    File dir = new File(configField.getText());
                    if(dir.exists() && dir.isDirectory()) {
                        File tilesDir = new File(dir + File.separator + "tiles");
                        if(!tilesDir.exists()) {
                            File[] regFolders = dir.listFiles(f -> (f.getName().startsWith("reg") && f.isDirectory()) || (f.getName().contains(".tif")));
                            if (regFolders == null || regFolders.length < 1) {
                                JOptionPane.showMessageDialog(configPanel, "No tif files/image sequence folder structure recognized in the folder. Choose the right folder!");
                                System.exit(0);
                            }
                        }
                    }
                }
            }
            catch(Exception e) {
                logger.showException(e);
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(configPanel,"Could not locate directory. Try again!");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) throws Exception {
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
        SegmMain segmMain = new SegmMain();
        segmMain.initComponents();
        segmMain.setVisible(true);
    }


    public Thread cmdCreateButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        Thread th = new Thread(() -> {
            try {
                cmdCreate.setEnabled(false);
                File dir;
                File tilesDir = new File(configField.getText() + File.separator + "tiles");
                if(tilesDir.exists()) {
                    dir = new File(configField.getText() + File.separator + "segmented_" + configName.getText());
                    if(!dir.exists()) {
                        dir.mkdirs();
                    } else {

                    }
                } else {
                    dir = new File(configField.getText());
                }
                //Create importConfig.txt
                List<String> lines = Arrays.asList("radius=" + segmConfigFrm.getRadius(), "maxCutoff=" + segmConfigFrm.getMaxCutOff(), "minCutoff=" + segmConfigFrm.getMinCutOff(),
                        "relativeCutoff=" + segmConfigFrm.getRelativeCutOff(), "cell_size_cutoff_factor=" + segmConfigFrm.getCellSizeCutOff(), "nuclearStainChannel=" + segmConfigFrm.getNuclearStainChannel(),
                        "nuclearStainCycle=" + segmConfigFrm.getNuclearStainCycle(), "membraneStainChannel=" + segmConfigFrm.getMembraneStainChannel(),
                        "membraneStainCycle=" + segmConfigFrm.getMembraneStainCycle(), //"readoutChannels=1,2,3",
                        "use_membrane=false", "inner_ring_size=1.0", "delaunay_graph=false", "anisotropic_region_growth="+segmConfigFrm.isAnisotropicRegionGrowth(),
                        "single_plane_quantification="+segmConfigFrm.isSinglePlaneQuant());

                Path file = Paths.get(dir.getCanonicalPath() + File.separator + "config.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
                log("Config file for segmentation was successfully created.");
                callSegm();
            } catch (Exception e) {
                logger.showException(e);
                System.out.println(e.getMessage());
            }
        });
        th.start();
        return th;
    }

    public Thread cmdPreviewButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        Thread th = new Thread(() -> {
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
            boolean imgSeq = false;
            File[] regNamesFolder = null;
            String[] regNames = null;
            File dir;
            File tilesDir = new File(configField.getText() + File.separator + "tiles");
            // For image sequence
            if(tilesDir.exists()) {
                imgSeq = true;
                regNamesFolder = tilesDir.listFiles(t -> t.isDirectory() && t.getName().startsWith("re"));
            }
            // Regular tiff
            else {
                imgSeq = false;
                File oldFileSt = new File(configField.getText());
                regNamesFolder = oldFileSt.listFiles(t -> t.isFile() && t.getName().toLowerCase().startsWith("re") && t.getName().toLowerCase().endsWith(".tif"));
            }
            regNames = new String[regNamesFolder.length];
            for (int i = 0; i < regNamesFolder.length; i++) {
                regNames[i] = regNamesFolder[i].getName();
            }

            GridBagLayout gridBag = new GridBagLayout();
            previewPanel.setLayout(gridBag);
            GridBagConstraints c = new GridBagConstraints();

            c.gridx=0;
            c.gridy=0;
            c.fill  = GridBagConstraints.BOTH;
            c.weightx =1;
            c.weighty =1;
            previewPanel.add(new JLabel("Choose region from the experiment: "), c);

            c.gridx=1;
            c.gridy=0;
            c.fill  = GridBagConstraints.BOTH;
            c.weightx =1;
            c.weighty =1;
            previewPanel.add(previewRegion, c);

            previewRegion.setModel(new DefaultComboBoxModel<>(regNames));
            previewRegion.setMaximumSize(new java.awt.Dimension(300, 20));
            previewRegion.setMinimumSize(new java.awt.Dimension(300, 20));
            previewRegion.setPreferredSize(new java.awt.Dimension(300, 20));

            int result = optionPane.showConfirmDialog(null, previewPanel,
                    "Specify region for preview", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }
            else {
                if(previewRegion.getSelectedItem().toString().equals(null) || previewRegion.getSelectedItem().toString().equals("")) {
                    JOptionPane.showMessageDialog(previewPanel,"Please specify region before proceeding!");
                    System.exit(0);
                }
                try {
                    SegConfigParam segParam = new SegConfigParam();
                    segParam.setRootDir(regNamesFolder[previewRegion.getSelectedIndex()]);
                    segParam.setShowImage(false);
                    segParam.setRadius(Integer.parseInt(segmConfigFrm.getRadius()));
                    segParam.setUse_membrane(false);
                    segParam.setMaxCutoff(Double.parseDouble(segmConfigFrm.getMaxCutOff()));
                    segParam.setMinCutoff(Double.parseDouble(segmConfigFrm.getMinCutOff()));
                    segParam.setRelativeCutoff(Double.parseDouble(segmConfigFrm.getRelativeCutOff()));
                    segParam.setNuclearStainChannel(Integer.parseInt(segmConfigFrm.getNuclearStainChannel()));
                    segParam.setNuclearStainCycle(Integer.parseInt(segmConfigFrm.getNuclearStainCycle()));
                    segParam.setMembraneStainChannel(Integer.parseInt(segmConfigFrm.getMembraneStainChannel()));
                    segParam.setMembraneStainCycle(Integer.parseInt(segmConfigFrm.getMembraneStainCycle()));
                    segParam.setInner_ring_size(1.0);
                    segParam.setCount_puncta(false);
                    segParam.setDont_inverse_memb(false);
                    segParam.setConcentricCircles(0);
                    segParam.setDelaunay_graph(true);
                    segParam.setSizeCutoffFactor(Double.parseDouble(segmConfigFrm.getCellSizeCutOff()));
                    segParam.setAnisotropicRegionGrowth(segmConfigFrm.isAnisotropicRegionGrowth());
                    segParam.setSingle_plane_quant(segmConfigFrm.isSinglePlaneQuant());
                    log("Segmentation version: " + version);
                    log("Starting segmentation preview for region: " + previewRegion.getSelectedItem().toString());
                    log("----------------------------");
                    Main.previewSegm(segParam, imgSeq);
                    log("Preview done");
                    log("----------------------------");
                }
                catch(Exception e) {
                    logger.showException(e);
                    System.out.println(e.getMessage());
                    JOptionPane.showMessageDialog(previewPanel,"Could not locate directory. Try again!");
                    System.exit(0);
                }
            }
        });
        th.start();
        return th;
    }

    private void callSegm() throws Exception {
        log("Segmentation version: " + version);
        String[] arg = new String[4];
        arg[0] = configField.getText();
        arg[3] = configName.getText();
        //1. Call Main
        log("Starting Main Segmentation...");
        Main.main(arg);
        log("Main done");

        //2. Call ConcatenateResults
        log("Starting ConcatenateResults...");
        ConcatenateResults.main(arg);
        log("ConcatenateResults done");

        //3. Call MakeFCS
        log("Starting MakeFCS...");
        try {
            MakeFCS.main(arg);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        log("MakeFCS done");
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private void configFieldMouseReleased(java.awt.event.MouseEvent evt) {
        File configTxt;
        File tilesDir = new File(configField.getText() + File.separator + "tiles");
        if(tilesDir.exists()) {
            configTxt = new File(configField.getText() + File.separator + "segmented_" + configName.getText() + File.separator + "config.txt");
        } else {
            configTxt = new File(configField.getText() + File.separator + "config.txt");
        }
        if (configTxt.exists()) {
            try {
                //set values here
                BufferedReader br = new BufferedReader(new FileReader(configTxt));
                String st;
                while ((st = br.readLine()) != null) {
                    if(st.contains("radius=")) {
                        segmConfigFrm.setRadius(st.replace("radius=",""));
                    }
                    else if(st.contains("maxCutoff=")) {
                        segmConfigFrm.setMaxCutOff(st.replace("maxCutoff=",""));
                    }
                    else if(st.contains("minCutoff=")) {
                        segmConfigFrm.setMinCutOff(st.replace("minCutoff=",""));
                    }
                    else if(st.contains("relativeCutoff=")) {
                        segmConfigFrm.setRelativeCutOff(st.replace("relativeCutoff=",""));
                    }
                    else if(st.contains("cell_size_cutoff_factor=")) {
                        segmConfigFrm.setCellSizeCutOff(st.replace("cell_size_cutoff_factor=",""));
                    }
                    else if(st.contains("nuclearStainChannel=")) {
                        segmConfigFrm.setNuclearStainChannel(st.replace("nuclearStainChannel=",""));
                    }
                    else if(st.contains("nuclearStainCycle=")) {
                        segmConfigFrm.setNuclearStainCycle(st.replace("nuclearStainCycle=",""));
                    }
                    else if(st.contains("membraneStainChannel=")) {
                        segmConfigFrm.setMembraneStainChannel(st.replace("membraneStainChannel=",""));
                    }
                    else if(st.contains("membraneStainCycle=")) {
                        segmConfigFrm.setMembraneStainCycle(st.replace("membraneStainCycle=",""));
                    }
                    else if(st.contains("anisotropic_region_growth=")) {
                        segmConfigFrm.setAnisotropicRegionGrowth(Boolean.parseBoolean(st.replace("anisotropic_region_growth=","")));
                    }
                }

            }
            catch (Exception e) {
                logger.showException(e);
                System.out.println(e.getMessage());
            }
        }
    }

    public SegmConfigFrm getSegmConfigFrm() {
        return segmConfigFrm;
    }

    public void setSegmConfigFrm(SegmConfigFrm segmConfigFrm) {
        this.segmConfigFrm = segmConfigFrm;
    }

    public JTextField getConfigField() {
        return configField;
    }

    public void setConfigField(JTextField configField) {
        this.configField = configField;
    }

    public JPanel getConfigPanel() {
        return configPanel;
    }

    public void setConfigPanel(JPanel configPanel) {
        this.configPanel = configPanel;
    }

    public JButton getCmdCreate() {
        return cmdCreate;
    }

    public void setCmdCreate(JButton cmdCreate) {
        this.cmdCreate = cmdCreate;
    }

    public void setOptionPane(OptionPane o) {
        this.optionPane = o;
    }
}