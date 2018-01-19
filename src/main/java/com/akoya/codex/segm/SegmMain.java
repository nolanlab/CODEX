package com.akoya.codex.segm;

import com.akoya.codex.upload.TextAreaOutputStream;
import com.akoya.codex.upload.logger;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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

    private static JTextField configField = new JTextField(5);
    private static JPanel configPanel = new JPanel();
    private static int version = 1;
    private JTextArea textArea = new JTextArea(15,30);
    private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea, "");
    private SegmConfigFrm segmConfigFrm;
    private JButton cmdCreate;

    public SegmMain() throws Exception {
        System.setOut(new PrintStream(taOutputStream));
        initComponents();
    }

    private void initComponents() throws Exception {
        inputFolderDialog();
        segmConfigFrm = new SegmConfigFrm();
        cmdCreate = new JButton();
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
        c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=6;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill  = GridBagConstraints.NONE;

        newPanel.add(cmdCreate, c);

        pane.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));

        mainPanel.add(pane, BorderLayout.CENTER);
        getContentPane().add(mainPanel);

        pack();
    }
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

        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));
        configPanel.add(new JLabel("Select input folder to be segmented: "));
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
                "Specify folder", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
        else {
            try {
                if(configField.getText().equals(null) || configField.getText().equalsIgnoreCase("...")) {
                    JOptionPane.showMessageDialog(configPanel,"Please specify directory before proceeding!");
                    System.exit(0);
                }
                else {
                    File dir = new File(configField.getText());
                    if(dir.exists() && dir.isDirectory()) {
                        File[] tifFiles = dir.listFiles(f -> f.getName().endsWith(".tif") || f.getName().endsWith(".tiff"));
                        if(tifFiles == null || tifFiles.length < 1) {
                            JOptionPane.showMessageDialog(configPanel, "No tif files present in the folder. Specify the folder with tif files and best focus folder.");
                            System.exit(0);
                        }
                    }
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
        segmMain.setVisible(true);
    }


    private void cmdCreateButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        new Thread(() -> {
            try {
                File dir = new File(configField.getText());
                //Create importConfig.txt
                if(!StringUtils.isBlank(segmConfigFrm.getReadOutChannels())) {
                    List<String> lines = Arrays.asList("radius=" + segmConfigFrm.getRadius(), "maxCutoff=" + segmConfigFrm.getMaxCutOff(), "minCutoff=" + segmConfigFrm.getMinCutOff(),
                            "relativeCutoff=" + segmConfigFrm.getRelativeCutOff(), "nuclearStainChannel=" + segmConfigFrm.getNuclearStainChannel(),
                            "nuclearStainCycle=" + segmConfigFrm.getNuclearStainCycle(), "membraneStainChannel=" + segmConfigFrm.getMembraneStainChannel(),
                            "membraneStainCycle=" + segmConfigFrm.getMembraneStainCycle(), "readoutChannels=" + segmConfigFrm.getReadOutChannels(),
                            "use_membrane=false", "inner_ring_size=1.0", "delaunay_graph=false");

                    Path file = Paths.get(dir.getCanonicalPath() + File.separator + "config.txt");
                    Files.write(file, lines, Charset.forName("UTF-8"));
                    log("Config file for segmentation was successfully created.");
                    callSegm();
                }
                else {
                    JOptionPane.showMessageDialog(this,"Readout channels cannot be blank or empty. Try again!");
                    return;
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }).start();
    }

    private static void callSegm() throws Exception {
        log("Segmentation version: " + version);
        String[] arg = new String[3];
        arg[0] = configField.getText();
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
        MakeFCS.main(arg);
        log("MakeFCS done");
    }

    private static void log(String s) {
        System.out.println(s);
    }

}