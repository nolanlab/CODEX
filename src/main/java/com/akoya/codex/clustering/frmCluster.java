package com.akoya.codex.clustering;

import com.akoya.codex.DefaultOptionPane;
import com.akoya.codex.OkayMockOptionPane;
import com.akoya.codex.OptionPane;
import com.akoya.codex.upload.TextAreaOutputStream;
import com.akoya.codex.upload.logger;
import dataIO.DatasetStub;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
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

/**
 *
 * @author Vishal
 */
public class frmCluster extends JFrame {
    private JTextArea textArea = new JTextArea(15,30);
    private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea, "");
    private String[] colNames = null;
    private static int version = 1;
    private ImportConfigFrm impConfigFrm;
    private JButton cmdCreate;
    private JTextField fcsFolderField = new JTextField(5);
    private String clustCols = "";
    private OptionPane optionPane = new DefaultOptionPane();

    public frmCluster() {
        System.setOut(new PrintStream(taOutputStream));
    }
    public void initComponents() {
        initFolder();
        impConfigFrm = new ImportConfigFrm(colNames);
        cmdCreate = new JButton();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Create cluster config");

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
        newPanel.add(impConfigFrm, c);

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

        cmdCreate.setText("Create");
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
    public static void main(String args[]) {
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
                    frmCluster frm = new frmCluster();
                    frm.initComponents();
                    frm.setVisible(true);
    }

    /**
     * Mouseevent to open the filechooser option to specify config.txt TMP_SSD_DRIVE content.
     * @param evt
     */
    private void configFieldDirMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDirMouseReleased
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getSelectedFile() != null) {
                fcsFolderField.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        }
        fireStateChanged();
    }

    private void configFieldDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDirActionPerformed
    }

    private void fireStateChanged() {
        PropertyChangeListener[] chl = this.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener c : chl) {
            c.propertyChange(new PropertyChangeEvent(this, "dir", "...", fcsFolderField.getText()));
        }
    }

    public void initFolder() {
        JPanel initPanel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        initPanel.setLayout(gridBag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        initPanel.add(new JLabel("\nEnter segmented region location containing FCS files: \t"), c);

        c= new GridBagConstraints();
        c.gridx=1;
        c.gridy=0;
        initPanel.add(fcsFolderField, c);

        if(!(optionPane instanceof OkayMockOptionPane)) {
            fcsFolderField.setText("...");
        }
        fcsFolderField.setEnabled(false);
        fcsFolderField.setMaximumSize(new java.awt.Dimension(3000, 20));
        fcsFolderField.setMinimumSize(new java.awt.Dimension(300, 20));
        fcsFolderField.setPreferredSize(new java.awt.Dimension(300, 20));
        fcsFolderField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                configFieldDirMouseReleased(evt);
            }
        });
        fcsFolderField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configFieldDirActionPerformed(evt);
            }
        });

        int result = optionPane.showConfirmDialog(null, initPanel,
                "Specify folder", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
        else {
            //Add content to config.txt
            if(fcsFolderField.getText().equals(null) || fcsFolderField.getText().equalsIgnoreCase("...")) {
                JOptionPane.showMessageDialog(this,"Please specify the correct directory.");
                System.exit(0);
            }
            File dir = new File(fcsFolderField.getText());
            File[] fcsFiles = getFcsFiles(dir);
            if(fcsFiles == null || fcsFiles.length == 0) {
                JOptionPane.showMessageDialog(this, "No FCS Files present in the directory, choose another directory and try again!");
                System.exit(0);
            }
            for(File aFcsFile : fcsFiles) {
                DatasetStub ds = DatasetStub.createFromFCS(aFcsFile);
                colNames = ds.getShortColumnNames();
                }
            }
        }

    public Thread cmdCreateButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        Thread th = new Thread(() -> {
            try {
                File dir = new File(fcsFolderField.getText());
                if(impConfigFrm.getLstColNamesIndex() != null && !impConfigFrm.getLstColNamesIndex().isEmpty()) {
                    for (int i = 0; i < impConfigFrm.getLstColNamesIndex().size() - 1; i++) {
                        clustCols += impConfigFrm.getLstColNamesIndex().get(i).toString() + ",";
                    }
                    clustCols += impConfigFrm.getLstColNamesIndex().get(impConfigFrm.getLstColNamesIndex().size()-1).toString();
                }
                else {
                    if(StringUtils.isBlank(clustCols)) {
                        JOptionPane.showMessageDialog(this, "Please select/add at least 1 clustering column from the list.");
                        return;
                    }
                }
                log("Clustering tool to read configuration to be used for invoking X-shift. Version: " + version);

                //Create importConfig.txt
                List<String> lines = Arrays.asList("clustering_columns=" + clustCols, "limit_events_per_file=" + impConfigFrm.getLimitEvents(), "transformation=" + impConfigFrm.getTransformation(),
                        "scaling_factor=" + impConfigFrm.getScalingFactor(), "noise_threshold=" + impConfigFrm.getNoiseThreshold(),
                        "euclidian_length_threshold=1", "rescale=" + impConfigFrm.getRescale(), "quantile=" + impConfigFrm.getQuantile(),
                        "rescale_separately=" + impConfigFrm.getRescaleSeparately().toLowerCase());
                Path file = Paths.get(dir.getCanonicalPath() + File.separator + "importConfig.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
                log("The importConfig.txt file was created successfully!");

                //Create fcsFileList.txt
                createFcsFileListTxt(dir);

                //Copy VorteX.jar
                File vortexJar = new File(System.getProperty("user.dir") + File.separator + "VorteX.jar");
                copyJarFile(vortexJar, dir);
                log("Successfully copied the VorteX.jar file to "+dir.getAbsolutePath());

                ProcessBuilder pb1 = new ProcessBuilder("cmd", "/C start /B /belownormal java -Xms5G -Xmx48G -Xmn50m -cp " + fcsFolderField.getText() + File.separator + "VorteX.jar standalone.Xshift");
                pb1.directory(new File(fcsFolderField.getText()));
                pb1.redirectErrorStream(true);
                pb1.redirectOutput();
                pb1.redirectError();
                Process proc = pb1.start();

                waitAndPrint(proc);

                //Remove the newly created Files after process is done
                File fcsF = new File(dir.getCanonicalPath() + File.separator + "fcsFileList.txt");
                File impF = new File(dir.getCanonicalPath() + File.separator + "importConfig.txt");
                File vorJ = new File(dir.getCanonicalPath() + File.separator + "VorteX.jar");
                removeFile(fcsF);
                removeFile(impF);
                removeFile(vorJ);

            } catch (Exception e) {
                log(e.getMessage());
            }
        });
        th.start();
        return th;
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static File[] getFcsFiles(File dir) {
        File[] fcsFiles = null;
        if(dir != null) {
            fcsFiles = dir.listFiles(f -> f.getName().endsWith(".fcs"));
        }
        return fcsFiles;
    }

    public static void waitAndPrint(Process proc) throws IOException {
        do {
            try {
                BufferedReader brOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String s;
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

    public static void createFcsFileListTxt(File dir) throws IOException{
        File[] fcsFiles = getFcsFiles(dir);
        String compensated = "_Compensated_";
        List<String> fcsLines = new ArrayList<>();
        for(File aFcsFile : fcsFiles) {
            if(aFcsFile.getName().contains(compensated)) {
                fcsLines.add(aFcsFile.getAbsolutePath());
            }
        }
        Path fcsFile = Paths.get(dir.getCanonicalPath() + File.separator + "fcsFileList.txt");
        Files.write(fcsFile, fcsLines, Charset.forName("UTF-8"));
        log("The fcsFileList.txt file was created successfully!");
    }

    public static void copyJarFile(File jarFile, File destDir) throws IOException {
        FileUtils.copyFileToDirectory(jarFile, destDir);
    }

    public static void removeFile(File dir) {
        if(dir != null) {
            if (dir.delete()) {
                log("Removing the newly created file "+dir.getName());
            }
        }
    }

    public ImportConfigFrm getImpConfigFrm() {
        return impConfigFrm;
    }

    public void setImpConfigFrm(ImportConfigFrm impConfigFrm) {
        this.impConfigFrm = impConfigFrm;
    }

    public JTextField getFcsFolderField() {
        return fcsFolderField;
    }

    public void setFcsFolderField(JTextField fcsFolderField) {
        this.fcsFolderField = fcsFolderField;
    }

    public String getClustCols() {
        return clustCols;
    }

    public void setClustCols(String clustCols) {
        this.clustCols = clustCols;
    }

    public void setOptionPane(OptionPane o) {
        this.optionPane = o;
    }

}
