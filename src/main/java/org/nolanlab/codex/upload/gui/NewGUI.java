package org.nolanlab.codex.upload.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vishal
 */

public class NewGUI {

    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JPanel progressPanel;
    private JProgressBar progressBar;
    private JProgressBar progressAnimation;
    private JPanel essentialsPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel projectNameLabel;
    private JTextField projectNameField;
    private JLabel regionWidthLabel;
    private JLabel regionHeightLabel;
    private JLabel tileOverlapXLabel;
    private JLabel tileOverlapYLabel;
    private JTextField regionWidthField;
    private JTextField regionHeightField;
    private JTextField tileOverlapXField;
    private JTextField tileOverlapYField;
    private JLabel logoLabel;
    private JCheckBox deconvolutionCheckBox;
    private JCheckBox hAndEStainCheckBox;
    private JCheckBox backgroundSubtractionCheckBox;
    private JPanel outputDirPanel;
    private JTextField outputDirField;
    private JButton outputPathBrowseButton;
    private JPanel viewFilesPanel;
    private JButton startButton;
    private JButton stopButton;
    private JButton openInputButton;
    private JButton editChannelNamesButton;
    private JButton editExperimentJsonButton;
    private JButton editExposureTimesButton;
    private JButton openOutputButton;
    private JPanel pathPanel;
    private JTextField inputPathField;
    private JButton inputPathBrowseButton;
    private JPanel processPanel;
    private JPanel processLeftColumnPanel;
    private JPanel imagingParametersPanel;
    private JLabel objectiveTypeLabel;
    private JComboBox objectiveTypeComboBox;
    private JLabel magnificationLabel;
    private JTextField magnificationField;
    private JLabel apertureLabel;
    private JTextField apertureField;
    private JLabel xyResolutionLabel;
    private JTextField xyResolutionField;
    private JLabel zPitchLabel;
    private JTextField zPitchField;
    private JLabel wavelengthsLabel;
    private JTextField wavelengthsField;
    private JPanel autoDetectedPanel;
    private JLabel numRegionsLabel;
    private JTextField numRegionsField;
    private JLabel numCyclesLabel;
    private JLabel numPlanesLabel;
    private JLabel numChannelsLabel;
    private JTextField numCyclesField;
    private JTextField numPlanesField;
    private JTextField numChannelsField;
//    private JTextField tileWidthField;
//    private JTextField tileHeightField;
    private JPanel processRightColumnPanel;
    private JTextField driftReferenceCycleField;
    private JTextField driftReferenceChannelField;
    private JTextField focusingOffsetField;
    private JTextField cycleRangeField;
    private JTextField regionNamesField;
    private JTextField channelNamesField;
    private JCheckBox useBlindDeconvolutionCheckBox;
    private JCheckBox useBleachMinimizingCropCheckBox;
    private JTextField deconvolutionIterationsField;
    private JComboBox deconvolutionModelComboBox;
    private JPanel advancedPanel;
    private JPanel advancedRightColumnPanel;
    private JPanel previewPanel;
    private JLabel previewRegionLabel;
    private JTextField previewRegionField;
    private JLabel previewCycleLabel;
    private JTextField previewCycleField;
    private JLabel previewChannelLabel;
    private JTextField previewChannelField;
    private JLabel previewZPlaneLabel;
    private JTextField previewZPlaneField;
    private JButton previewGenerateButton;
    private JPanel advancedLeftColumnPanel;
    private JPanel stepSelectionPanel;
    private JPanel bottomPanel;
    private JPanel loggingPanel;
    private JScrollPane loggingPane;
    private JTextArea loggingTextArea;
    private JCheckBox imgSeqCheckBox;
    private JLabel processRegionsLabel;
    private JLabel processTilesLabel;
    private JLabel microscopeLabel;
    private JComboBox microscopeTypeComboBox;
    private JTextField bestFocusCycleField;
    private JTextField bestFocusChannelField;
    private JLabel colorModeLabel;
    private JComboBox colorModeComboBox;
    private JCheckBox optionalFocusFragmentCheckBox;
    private JPanel actionsPanel;
    private JPanel processingOptionsPanel;
    private JLabel driftReferenceCycleLabel;
    private JLabel driftReferenceChannelLabel;
    private JLabel focusingOffsetLabel;
    private JLabel bestFocusCycleLabel;
    private JLabel bestFocusChannelLabel;
    private JLabel deconvolutionIterationsLabel;
    private JLabel deconvolutionModelLabel;
    private JTextField processRegionsField;
    private JTextField processTilesField;
    private JButton openLogsButton;
    private JLabel cycleRangeLabel;
    private JLabel regionNamesLabel;
    private JLabel channelNamesLabel;
    private JCheckBox tmaCheckBox;

    private boolean isTMA = false;

    private TextAreaOutputStream taOutputStream;
    private ArrayList<Process> allProcess = new ArrayList<>();

    private JSpinner spinGPU = new JSpinner();
    private JSpinner spinRAM = new JSpinner();
    private JTextField configField = new JTextField(5);
    private static GuiHelper guiHelper = new GuiHelper();

    public boolean isTMA() {
        return isTMA;
    }

    public void setTMA(boolean TMA) {
        isTMA = TMA;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JProgressBar getProgressAnimation() {
        return progressAnimation;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getProjectNameField() {
        return projectNameField;
    }

    public JTextField getRegionWidthField() {
        return regionWidthField;
    }

    public JTextField getRegionHeightField() {
        return regionHeightField;
    }

    public JTextField getTileOverlapXField() {
        return tileOverlapXField;
    }

    public JTextField getTileOverlapYField() {
        return tileOverlapYField;
    }

    public JCheckBox getDeconvolutionCheckBox() {
        return deconvolutionCheckBox;
    }

    public JCheckBox gethAndEStainCheckBox() {
        return hAndEStainCheckBox;
    }

    public JCheckBox getTmaCheckBox() {
        return tmaCheckBox;
    }

    public JCheckBox getBackgroundSubtractionCheckBox() {
        return backgroundSubtractionCheckBox;
    }

    public JTextField getOutputDirField() {
        return outputDirField;
    }

    public JButton getOutputPathBrowseButton() {
        return outputPathBrowseButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JButton getOpenInputButton() {
        return openInputButton;
    }

    public JButton getEditChannelNamesButton() {
        return editChannelNamesButton;
    }

    public JButton getEditExperimentJsonButton() {
        return editExperimentJsonButton;
    }

    public JButton getEditExposureTimesButton() {
        return editExposureTimesButton;
    }

    public JButton getOpenOutputButton() {
        return openOutputButton;
    }

    public JButton getOpenLogsButton() {
        return openLogsButton;
    }

    public JTextField getInputPathField() {
        return inputPathField;
    }

    public JButton getInputPathBrowseButton() {
        return inputPathBrowseButton;
    }

    public JComboBox getMicroscopeTypeComboBox() {
        return microscopeTypeComboBox;
    }

    public JComboBox getObjectiveTypeComboBox() {
        return objectiveTypeComboBox;
    }

    public JTextField getMagnificationField() {
        return magnificationField;
    }

    public JTextField getApertureField() {
        return apertureField;
    }

    public JTextField getXyResolutionField() {
        return xyResolutionField;
    }

    public JComboBox getColorModeComboBox() {
        return colorModeComboBox;
    }

    public JTextField getzPitchField() {
        return zPitchField;
    }

    public JTextField getWavelengthsField() {
        return wavelengthsField;
    }

    public JTextField getNumRegionsField() {
        return numRegionsField;
    }

    public JTextField getNumCyclesField() {
        return numCyclesField;
    }

    public JTextField getNumPlanesField() {
        return numPlanesField;
    }

    public JTextField getNumChannelsField() {
        return numChannelsField;
    }

//    public JTextField getTileWidthField() {
//        return tileWidthField;
//    }
//
//    public JTextField getTileHeightField() {
//        return tileHeightField;
//    }

    public JTextField getDriftReferenceCycleField() {
        return driftReferenceCycleField;
    }

    public JTextField getDriftReferenceChannelField() {
        return driftReferenceChannelField;
    }

    public JTextField getBestFocusCycleField() {
        return bestFocusCycleField;
    }

    public JTextField getBestFocusChannelField() {
        return bestFocusChannelField;
    }

    public JCheckBox getOptionalFocusFragmentCheckBox() {
        return optionalFocusFragmentCheckBox;
    }

    public JTextField getFocusingOffsetField() {
        return focusingOffsetField;
    }

    public JTextField getCycleRangeField() {
        return cycleRangeField;
    }

    public JTextField getRegionNamesField() {
        return regionNamesField;
    }

    public JTextField getChannelNamesField() {
        return channelNamesField;
    }

    public JCheckBox getUseBlindDeconvolutionCheckBox() {
        return useBlindDeconvolutionCheckBox;
    }

    public JCheckBox getUseBleachMinimizingCropCheckBox() {
        return useBleachMinimizingCropCheckBox;
    }

    public JTextField getDeconvolutionIterationsField() {
        return deconvolutionIterationsField;
    }

    public JComboBox getDeconvolutionModelComboBox() {
        return deconvolutionModelComboBox;
    }

    public JTextField getPreviewRegionField() {
        return previewRegionField;
    }

    public JTextField getPreviewCycleField() {
        return previewCycleField;
    }

    public JTextField getPreviewChannelField() {
        return previewChannelField;
    }

    public JTextField getPreviewZPlaneField() {
        return previewZPlaneField;
    }

    public JButton getPreviewGenerateButton() {
        return previewGenerateButton;
    }

    public JTextArea getLoggingTextArea() {
        return loggingTextArea;
    }

    public JCheckBox getImgSeqCheckBox() {
        return imgSeqCheckBox;
    }

    public JSpinner getSpinGPU() {
        return spinGPU;
    }

    public JSpinner getSpinRAM() {
        return spinRAM;
    }

    public JTextField getConfigField() {
        return configField;
    }

    public JTextField getProcessRegionsField() {
        return processRegionsField;
    }

    public JTextField getProcessTilesField() {
        return processTilesField;
    }

    public TextAreaOutputStream getTaOutputStream() {
        return taOutputStream;
    }

    public void setTaOutputStream(TextAreaOutputStream taOutputStream) {
        this.taOutputStream = taOutputStream;
    }


    public NewGUI() {
        checkConfigFile(this);
        initEnables();
        loadLogo();
        addListeners();
        addFieldValidations();
        taOutputStream = new TextAreaOutputStream(loggingTextArea, "", null);
        System.setOut(new PrintStream(taOutputStream));
        System.setErr(new PrintStream(taOutputStream));
    }

    private void initEnables() {
        inputPathField.setEnabled(false);
        inputPathBrowseButton.setEnabled(true);
        outputDirField.setEnabled(false);
        outputPathBrowseButton.setEnabled(false);

        openInputButton.setEnabled(false);
        editChannelNamesButton.setEnabled(false);
        editExperimentJsonButton.setEnabled(false);
        editExposureTimesButton.setEnabled(false);
        openOutputButton.setEnabled(false);

        startButton.setEnabled(false);
        stopButton.setEnabled(false);

        previewGenerateButton.setEnabled(false);
        GuiHelper.enableAll(this, false);
    }

    private void addListeners() {
        inputPathBrowseButton.addActionListener(new GuiActionListener(this));
        outputPathBrowseButton.addActionListener(new GuiActionListener(this));
        openInputButton.addActionListener(new GuiActionListener(this));
        editChannelNamesButton.addActionListener(new GuiActionListener(this));
        editExperimentJsonButton.addActionListener(new GuiActionListener(this));
        editExposureTimesButton.addActionListener(new GuiActionListener(this));
        openOutputButton.addActionListener(new GuiActionListener(this));
        openLogsButton.addActionListener(new GuiActionListener(this));
        startButton.addActionListener(new GuiActionListener(this));
        stopButton.addActionListener(new GuiActionListener(this));
        previewGenerateButton.addActionListener(new GuiActionListener(this));
    }

    private void addFieldValidations() {
        // Essentials
        regionWidthField.setName("Region Width");
        regionWidthField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        regionHeightField.setName("Region Height");
        regionHeightField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        tileOverlapXField.setName("Tile Overlap X");
        tileOverlapXField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        tileOverlapYField.setName("Tile Overlap Y");
        tileOverlapYField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);

        // Imaging params
        magnificationField.setName("Magnification");
        magnificationField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        apertureField.setName("Aperture");
        apertureField.setInputVerifier(FieldValidator.DOUBLE_VERIFIER);
        xyResolutionField.setName("XY Resolution");
        xyResolutionField.setInputVerifier(FieldValidator.DOUBLE_VERIFIER);
        zPitchField.setName("Z Pitch");
        zPitchField.setInputVerifier(FieldValidator.DOUBLE_VERIFIER);

        // Auto-detected params
        numRegionsField.setName("Number of Regions");
        numRegionsField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        numCyclesField.setName("Number of Cycles");
        numCyclesField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        numPlanesField.setName("Number of Planes");
        numPlanesField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        numChannelsField.setName("Number of Channels");
        numChannelsField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);

        // Deconvolution params
        deconvolutionIterationsField.setName("Deconvolution Iterations");
        deconvolutionIterationsField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);

        // Optional params
//        tileWidthField.setName("Tile Width");
//        tileWidthField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
//        tileHeightField.setName("Tile Height");
//        tileHeightField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        driftReferenceCycleField.setName("Drift Reference Cycle");
        driftReferenceCycleField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        driftReferenceChannelField.setName("Drift Reference Channel");
        driftReferenceChannelField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        bestFocusCycleField.setName("Best Focus Cycle");
        bestFocusCycleField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        bestFocusChannelField.setName("Best Focus Channel");
        bestFocusChannelField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        focusingOffsetField.setName("Focusing Offset");
        focusingOffsetField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);

        // Region preview params
        previewRegionField.setName("Preview Region");
        previewRegionField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        previewRegionField.setName("Preview Cycle");
        previewCycleField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        previewRegionField.setName("Preview Channel");
        previewChannelField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
        previewRegionField.setName("Preview Z-plane");
        previewZPlaneField.setInputVerifier(FieldValidator.INTEGER_VERIFIER);
    }

    private void loadLogo() {
        URL url = this.getClass().getClassLoader().getResource("resources/codex/nolanlab-logo.png");
        Image image;
        if (url != null) {
            image = new ImageIcon(url).getImage().getScaledInstance(246, 90, Image.SCALE_SMOOTH);
        } else {
            image = new BufferedImage(246, 90, BufferedImage.TYPE_INT_RGB);
        }
        logoLabel.setIcon(new ImageIcon(image));
    }

    private void checkConfigFile(NewGUI gui) {
        File dir = new File(System.getProperty("user.home"));
        try {
            File in = new File(dir.getCanonicalPath() + File.separator + "config.txt");
            if (in != null && !in.isDirectory() && !in.exists()) {
                numberOfGpuDialog(gui);
            }
        } catch (Exception e) {
            guiHelper.log(ExceptionUtils.getStackTrace(e));
            System.exit(0);
        }
    }

    /*
       Method to create a new dialog box to be input at the start-up of the application, when it is run
       on the machine the first time.
    */
    private void numberOfGpuDialog(NewGUI gui) {

        JPanel gpuPanel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        gpuPanel.setLayout(gridBag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        gpuPanel.add(new JLabel("Number of GPUs: \t"), c);


        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        gpuPanel.add(spinGPU, c);
        spinGPU.setMaximumSize(new Dimension(3000, 20));
        spinGPU.setMinimumSize(new Dimension(60, 20));
        spinGPU.setPreferredSize(new Dimension(60, 20));
        spinGPU.setModel(new SpinnerNumberModel(1, 1, 200, 1));

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        gpuPanel.add(new JLabel("\nTMP_SSD_DRIVE: \t"), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        configField.setMaximumSize(new Dimension(3000, 20));
        configField.setMinimumSize(new Dimension(300, 20));
        configField.setPreferredSize(new Dimension(300, 20));
        gpuPanel.add(configField, c);
        configField.setText("...");
        configField.setEnabled(false);
        configField.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evt) {
                guiHelper.configFieldDirMouseReleased(gui);
            }
        });

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        gpuPanel.add(new JLabel("\nMax RAM size: \t"), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        spinRAM.setMaximumSize(new Dimension(3000, 20));
        spinRAM.setMinimumSize(new Dimension(60, 20));
        spinRAM.setPreferredSize(new Dimension(60, 20));
        gpuPanel.add(spinRAM, c);
        spinRAM.setModel(new SpinnerNumberModel(48, 4, 256, 4));

        int result = JOptionPane.showConfirmDialog(null, gpuPanel,
                "Specify configuration", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        } else {
            //Add content to config.txt
            try {
                File dir = new File(System.getProperty("user.home"));
                if (configField.getText().equals(null) || configField.getText().equalsIgnoreCase("...")) {
                    JOptionPane.showMessageDialog(this.getMainPanel(),"Could not save config.txt file, please specify directory for TMP_SSD_DRIVE");
                    System.exit(0);
                }
                if (StringUtils.isBlank(spinGPU.getValue().toString())) {
                    JOptionPane.showMessageDialog(this.getMainPanel(),"Could not save config.txt file, please enter value for number of GPUs");
                    System.exit(0);
                }
                if (StringUtils.isBlank(spinRAM.getValue().toString())) {
                    JOptionPane.showMessageDialog(this.getMainPanel(),"Could not save config.txt file, please enter value for RAM size");
                    System.exit(0);
                }
                String str = configField.getText().replaceAll("\\\\", "/");
                List<String> lines = Arrays.asList("TMP_SSD_DRIVE=" + str, "numGPU=" + spinGPU.getValue(), "maxRAM=" + spinRAM.getValue());
                Path file = Paths.get(dir.getCanonicalPath() + File.separator + "config.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this.getMainPanel(),"Could not save the config.txt file");
                guiHelper.log(ExceptionUtils.getStackTrace(e));
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setDefaultFonts();
            NewGUI gui = new NewGUI();
            JFrame frame = new JFrame();
            addWindowClosingListener(frame);
            frame.setTitle(getTitle());
            frame.setIconImage(getIconImage());
            frame.setContentPane(gui.rootPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setMinimumSize(new Dimension(800, 895));
            frame.toFront();
            frame.setVisible(true);
            guiHelper.logMicrovolutionInfo(gui.getDeconvolutionCheckBox());
        } catch (Exception e) {

        }
    }

    private static void addWindowClosingListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int selection = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to close this window?", "Close uploader", JOptionPane.YES_NO_OPTION);
                if (selection == JOptionPane.YES_OPTION) {
                    guiHelper.log("Uploader closed");
                    System.exit(0);
                }
            }
        });
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private static String getTitle() {
        try {
            return String.format("%s %s", SystemInfo.getAppName(), SystemInfo.getAppVersion());
        } catch (Exception e) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            return String.format("CODEX Processor Custom Build (%s)", dtf.format(now));
        }
    }

    private static Image getIconImage() {
        URL url = NewGUI.class.getClassLoader().getResource("resources/codex/codex-icon-circle-256x256.png");
        Image image;
        if (url != null) {
            image = new ImageIcon(url).getImage();
        } else {
            image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
        }
        return image;
    }

    private static void setDefaultFonts() {
        Font defaultFont = new Font("Arial", Font.PLAIN, 16);
        Font panelFont = new Font("Arial", Font.BOLD, 16);
        Font logFont = new Font("Consolas", Font.PLAIN, 12);

        UIManager.put("Button.font", defaultFont);
        UIManager.put("ToggleButton.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        UIManager.put("ColorChooser.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("List.font", defaultFont);
        UIManager.put("MenuBar.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
        UIManager.put("RadioButtonMenuItem.font", defaultFont);
        UIManager.put("CheckBoxMenuItem.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("PopupMenu.font", defaultFont);
        UIManager.put("OptionPane.font", defaultFont);
        UIManager.put("Panel.font", panelFont);
        UIManager.put("ProgressBar.font", defaultFont);
        UIManager.put("ScrollPane.font", defaultFont);
        UIManager.put("Viewport.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        UIManager.put("TextArea.font", logFont);
        UIManager.put("TextPane.font", defaultFont);
        UIManager.put("EditorPane.font", defaultFont);
        UIManager.put("TitledBorder.font", defaultFont);
        UIManager.put("ToolBar.font", defaultFont);
        UIManager.put("ToolTip.font", defaultFont);
        UIManager.put("Tree.font", defaultFont);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(rootPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabbedPane = new JTabbedPane();
        rootPanel.add(tabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(12, 6, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Main", mainPanel);
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(11, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 10, 10, 10), -1, 0));
        mainPanel.add(progressPanel, new GridConstraints(10, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Progress", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, progressPanel.getFont())));
        progressBar = new JProgressBar();
        progressBar.setStringPainted(false);
        progressPanel.add(progressBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 30), null, 0, false));
        progressAnimation = new JProgressBar();
        progressPanel.add(progressAnimation, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 5), null, 0, false));
        essentialsPanel = new JPanel();
        essentialsPanel.setLayout(new GridLayoutManager(6, 7, new Insets(5, 10, 10, 10), -1, -1));
        mainPanel.add(essentialsPanel, new GridConstraints(7, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        essentialsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Essentials", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, essentialsPanel.getFont())));
        nameLabel = new JLabel();
        nameLabel.setText("Name");
        essentialsPanel.add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        nameField = new JTextField();
        nameField.setText("<Experiment name>");
        essentialsPanel.add(nameField, new GridConstraints(0, 2, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(180, -1), null, 0, false));
        regionWidthLabel = new JLabel();
        regionWidthLabel.setText("Region Width");
        essentialsPanel.add(regionWidthLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        regionHeightLabel = new JLabel();
        regionHeightLabel.setText("Region Height");
        essentialsPanel.add(regionHeightLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tileOverlapXLabel = new JLabel();
        tileOverlapXLabel.setText("Tile Overlap X");
        essentialsPanel.add(tileOverlapXLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tileOverlapYLabel = new JLabel();
        tileOverlapYLabel.setText("Tile Overlap Y");
        essentialsPanel.add(tileOverlapYLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        regionWidthField = new JTextField();
        essentialsPanel.add(regionWidthField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        regionHeightField = new JTextField();
        essentialsPanel.add(regionHeightField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tileOverlapXField = new JTextField();
        essentialsPanel.add(tileOverlapXField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tileOverlapYField = new JTextField();
        essentialsPanel.add(tileOverlapYField, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        essentialsPanel.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer6 = new Spacer();
        essentialsPanel.add(spacer6, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer7 = new Spacer();
        essentialsPanel.add(spacer7, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        logoLabel = new JLabel();
        logoLabel.setBackground(new Color(-1));
        logoLabel.setText("");
        essentialsPanel.add(logoLabel, new GridConstraints(2, 6, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(240, 80), new Dimension(240, 80), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        essentialsPanel.add(panel2, new GridConstraints(2, 4, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        imgSeqCheckBox = new JCheckBox();
        imgSeqCheckBox.setEnabled(true);
        imgSeqCheckBox.setSelected(false);
        imgSeqCheckBox.setText("<html>Export as Image Sequence<br>(For compatibility with MAV)</html>");
        panel2.add(imgSeqCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deconvolutionCheckBox = new JCheckBox();
        deconvolutionCheckBox.setEnabled(true);
        deconvolutionCheckBox.setSelected(false);
        deconvolutionCheckBox.setText("Deconvolution");
        panel2.add(deconvolutionCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hAndEStainCheckBox = new JCheckBox();
        hAndEStainCheckBox.setEnabled(true);
        hAndEStainCheckBox.setSelected(false);
        hAndEStainCheckBox.setText("HandE Staining");
        panel2.add(hAndEStainCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundSubtractionCheckBox = new JCheckBox();
        backgroundSubtractionCheckBox.setSelected(false);
        backgroundSubtractionCheckBox.setText("Background Subtraction");
        panel2.add(backgroundSubtractionCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tmaCheckBox = new JCheckBox();
        tmaCheckBox.setEnabled(false);
        tmaCheckBox.setSelected(false);
        tmaCheckBox.setText("Multipoint");
        panel2.add(tmaCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        projectNameLabel = new JLabel();
        projectNameLabel.setText("Project Name");
        essentialsPanel.add(projectNameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        projectNameField = new JTextField();
        projectNameField.setText("");
        essentialsPanel.add(projectNameField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        mainPanel.add(spacer8, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 5), null, 0, false));
        outputDirPanel = new JPanel();
        outputDirPanel.setLayout(new GridLayoutManager(1, 3, new Insets(5, 10, 10, 10), -1, -1));
        mainPanel.add(outputDirPanel, new GridConstraints(3, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputDirPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Output Path", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, outputDirPanel.getFont())));
        outputDirField = new JTextField();
        outputDirPanel.add(outputDirField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(300, -1), null, 0, false));
        outputPathBrowseButton = new JButton();
        outputPathBrowseButton.setText("Browse");
        outputDirPanel.add(outputPathBrowseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        outputDirPanel.add(spacer9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        pathPanel = new JPanel();
        pathPanel.setLayout(new GridLayoutManager(1, 3, new Insets(5, 10, 10, 10), -1, -1));
        mainPanel.add(pathPanel, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Input Path", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, pathPanel.getFont())));
        inputPathField = new JTextField();
        pathPanel.add(inputPathField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(300, -1), null, 0, false));
        final Spacer spacer10 = new Spacer();
        pathPanel.add(spacer10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        inputPathBrowseButton = new JButton();
        inputPathBrowseButton.setText("Browse");
        pathPanel.add(inputPathBrowseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayoutManager(1, 7, new Insets(5, 10, 10, 10), -1, -1));
        mainPanel.add(actionsPanel, new GridConstraints(9, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Actions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, actionsPanel.getFont())));
        startButton = new JButton();
        startButton.setEnabled(true);
        Font startButtonFont = this.$$$getFont$$$(null, Font.BOLD, -1, startButton.getFont());
        if (startButtonFont != null) startButton.setFont(startButtonFont);
        startButton.setText("Start");
        actionsPanel.add(startButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(130, 50), null, 0, false));
        final Spacer spacer11 = new Spacer();
        actionsPanel.add(spacer11, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        stopButton = new JButton();
        stopButton.setEnabled(true);
        stopButton.setText("Stop");
        actionsPanel.add(stopButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(130, 50), null, 0, false));
        final Spacer spacer12 = new Spacer();
        actionsPanel.add(spacer12, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer13 = new Spacer();
        actionsPanel.add(spacer13, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer14 = new Spacer();
        actionsPanel.add(spacer14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer15 = new Spacer();
        actionsPanel.add(spacer15, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer16 = new Spacer();
        mainPanel.add(spacer16, new GridConstraints(8, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 5), null, 0, false));
        viewFilesPanel = new JPanel();
        viewFilesPanel.setLayout(new GridLayoutManager(1, 11, new Insets(5, 10, 10, 10), -1, -1));
        mainPanel.add(viewFilesPanel, new GridConstraints(5, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewFilesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "View Files", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, viewFilesPanel.getFont())));
        editExposureTimesButton = new JButton();
        editExposureTimesButton.setEnabled(true);
        editExposureTimesButton.setText("ExposureTimes");
        viewFilesPanel.add(editExposureTimesButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
        openInputButton = new JButton();
        openInputButton.setEnabled(true);
        openInputButton.setText("Input");
        viewFilesPanel.add(openInputButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
        final Spacer spacer17 = new Spacer();
        viewFilesPanel.add(spacer17, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        openOutputButton = new JButton();
        openOutputButton.setEnabled(true);
        openOutputButton.setText("Output");
        viewFilesPanel.add(openOutputButton, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
        final Spacer spacer18 = new Spacer();
        viewFilesPanel.add(spacer18, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        editChannelNamesButton = new JButton();
        editChannelNamesButton.setEnabled(true);
        editChannelNamesButton.setText("ChannelNames");
        viewFilesPanel.add(editChannelNamesButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
        final Spacer spacer19 = new Spacer();
        viewFilesPanel.add(spacer19, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer20 = new Spacer();
        viewFilesPanel.add(spacer20, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        editExperimentJsonButton = new JButton();
        editExperimentJsonButton.setEnabled(true);
        editExperimentJsonButton.setText("ExperimentJSON");
        viewFilesPanel.add(editExperimentJsonButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 2, false));
        openLogsButton = new JButton();
        openLogsButton.setEnabled(true);
        openLogsButton.setText("Logs");
        viewFilesPanel.add(openLogsButton, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
        final Spacer spacer21 = new Spacer();
        viewFilesPanel.add(spacer21, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
        final Spacer spacer22 = new Spacer();
        mainPanel.add(spacer22, new GridConstraints(6, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 5), null, 0, false));
        final Spacer spacer23 = new Spacer();
        mainPanel.add(spacer23, new GridConstraints(4, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 5), null, 0, false));
        processPanel = new JPanel();
        processPanel.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Process", processPanel);
        processLeftColumnPanel = new JPanel();
        processLeftColumnPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        processPanel.add(processLeftColumnPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        imagingParametersPanel = new JPanel();
        imagingParametersPanel.setLayout(new GridLayoutManager(8, 3, new Insets(5, 10, 10, 10), -1, -1));
        processLeftColumnPanel.add(imagingParametersPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        imagingParametersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Imaging Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, imagingParametersPanel.getFont())));
        objectiveTypeLabel = new JLabel();
        objectiveTypeLabel.setText("Objective Type");
        imagingParametersPanel.add(objectiveTypeLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        objectiveTypeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("air");
        defaultComboBoxModel1.addElement("water");
        defaultComboBoxModel1.addElement("oil");
        objectiveTypeComboBox.setModel(defaultComboBoxModel1);
        imagingParametersPanel.add(objectiveTypeComboBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(180, -1), null, 0, false));
        magnificationLabel = new JLabel();
        magnificationLabel.setText("Magnification");
        imagingParametersPanel.add(magnificationLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        magnificationField = new JTextField();
        magnificationField.setEnabled(true);
        magnificationField.setText("20");
        imagingParametersPanel.add(magnificationField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        apertureLabel = new JLabel();
        apertureLabel.setText("Aperture");
        imagingParametersPanel.add(apertureLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        apertureField = new JTextField();
        apertureField.setText("0.75");
        imagingParametersPanel.add(apertureField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xyResolutionLabel = new JLabel();
        xyResolutionLabel.setText("XY Resolution (nm)");
        imagingParametersPanel.add(xyResolutionLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xyResolutionField = new JTextField();
        xyResolutionField.setText("377.442");
        imagingParametersPanel.add(xyResolutionField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zPitchLabel = new JLabel();
        zPitchLabel.setText("Z Pitch (nm)");
        imagingParametersPanel.add(zPitchLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zPitchField = new JTextField();
        zPitchField.setText("1500");
        imagingParametersPanel.add(zPitchField, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wavelengthsLabel = new JLabel();
        wavelengthsLabel.setText("Wavelengths (nm)");
        imagingParametersPanel.add(wavelengthsLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wavelengthsField = new JTextField();
        wavelengthsField.setText("425;525;595;670");
        imagingParametersPanel.add(wavelengthsField, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        microscopeLabel = new JLabel();
        microscopeLabel.setText("Microscope");
        imagingParametersPanel.add(microscopeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer24 = new Spacer();
        imagingParametersPanel.add(spacer24, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        microscopeTypeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Keyence BZ-X710");
        defaultComboBoxModel2.addElement("Zeiss ZEN");
        defaultComboBoxModel2.addElement("Leica DMI8");
        microscopeTypeComboBox.setModel(defaultComboBoxModel2);
        imagingParametersPanel.add(microscopeTypeComboBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(180, -1), null, 0, false));
        colorModeLabel = new JLabel();
        colorModeLabel.setText("Color Mode");
        imagingParametersPanel.add(colorModeLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        colorModeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("grayscale");
        defaultComboBoxModel3.addElement("color");
        colorModeComboBox.setModel(defaultComboBoxModel3);
        imagingParametersPanel.add(colorModeComboBox, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(180, -1), null, 0, false));
        final Spacer spacer25 = new Spacer();
        processLeftColumnPanel.add(spacer25, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        autoDetectedPanel = new JPanel();
        autoDetectedPanel.setLayout(new GridLayoutManager(7, 3, new Insets(5, 10, 10, 10), -1, -1));
        processLeftColumnPanel.add(autoDetectedPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoDetectedPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Auto-detected Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, autoDetectedPanel.getFont())));
        numRegionsLabel = new JLabel();
        numRegionsLabel.setText("Number of Regions");
        autoDetectedPanel.add(numRegionsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer26 = new Spacer();
        autoDetectedPanel.add(spacer26, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        numRegionsField = new JTextField();
        autoDetectedPanel.add(numRegionsField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numCyclesLabel = new JLabel();
        numCyclesLabel.setText("Number of Cycles");
        autoDetectedPanel.add(numCyclesLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numPlanesLabel = new JLabel();
        numPlanesLabel.setText("Number of Planes");
        autoDetectedPanel.add(numPlanesLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numChannelsLabel = new JLabel();
        numChannelsLabel.setText("Number of Channels");
        autoDetectedPanel.add(numChannelsLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numCyclesField = new JTextField();
        autoDetectedPanel.add(numCyclesField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numPlanesField = new JTextField();
        autoDetectedPanel.add(numPlanesField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numChannelsField = new JTextField();
        autoDetectedPanel.add(numChannelsField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cycleRangeLabel = new JLabel();
        cycleRangeLabel.setText("Cycle Range");
        autoDetectedPanel.add(cycleRangeLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cycleRangeField = new JTextField();
        autoDetectedPanel.add(cycleRangeField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        regionNamesLabel = new JLabel();
        regionNamesLabel.setText("Region Names");
        autoDetectedPanel.add(regionNamesLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        channelNamesLabel = new JLabel();
        channelNamesLabel.setText("Channel Names");
        autoDetectedPanel.add(channelNamesLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        regionNamesField = new JTextField();
        autoDetectedPanel.add(regionNamesField, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        channelNamesField = new JTextField();
        channelNamesField.setText("");
        autoDetectedPanel.add(channelNamesField, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer27 = new Spacer();
        processLeftColumnPanel.add(spacer27, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        processRightColumnPanel = new JPanel();
        processRightColumnPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        processPanel.add(processRightColumnPanel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer28 = new Spacer();
        processRightColumnPanel.add(spacer28, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer29 = new Spacer();
        processRightColumnPanel.add(spacer29, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        processingOptionsPanel = new JPanel();
        processingOptionsPanel.setLayout(new GridLayoutManager(8, 3, new Insets(5, 10, 10, 10), -1, -1));
        processRightColumnPanel.add(processingOptionsPanel, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processingOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Optional Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, processingOptionsPanel.getFont())));
        driftReferenceCycleLabel = new JLabel();
        driftReferenceCycleLabel.setText("Drift Reference Cycle");
        processingOptionsPanel.add(driftReferenceCycleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer30 = new Spacer();
        processingOptionsPanel.add(spacer30, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        focusingOffsetLabel = new JLabel();
        focusingOffsetLabel.setText("Focusing Offset");
        processingOptionsPanel.add(focusingOffsetLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        bestFocusCycleLabel = new JLabel();
        bestFocusCycleLabel.setText("Best Focus Cycle");
        processingOptionsPanel.add(bestFocusCycleLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        bestFocusChannelLabel = new JLabel();
        bestFocusChannelLabel.setText("Best Focus Channel");
        processingOptionsPanel.add(bestFocusChannelLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        driftReferenceCycleField = new JTextField();
        driftReferenceCycleField.setText("1");
        processingOptionsPanel.add(driftReferenceCycleField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        driftReferenceChannelField = new JTextField();
        driftReferenceChannelField.setText("1");
        processingOptionsPanel.add(driftReferenceChannelField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        driftReferenceChannelLabel = new JLabel();
        driftReferenceChannelLabel.setText("Drift Reference Channel");
        processingOptionsPanel.add(driftReferenceChannelLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        bestFocusCycleField = new JTextField();
        bestFocusCycleField.setText("1");
        processingOptionsPanel.add(bestFocusCycleField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        bestFocusChannelField = new JTextField();
        bestFocusChannelField.setText("1");
        processingOptionsPanel.add(bestFocusChannelField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        focusingOffsetField = new JTextField();
        focusingOffsetField.setText("0");
        processingOptionsPanel.add(focusingOffsetField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        optionalFocusFragmentCheckBox = new JCheckBox();
        optionalFocusFragmentCheckBox.setEnabled(true);
        optionalFocusFragmentCheckBox.setSelected(false);
        optionalFocusFragmentCheckBox.setText("Focusing Fragment");
        optionalFocusFragmentCheckBox.setVerticalAlignment(0);
        processingOptionsPanel.add(optionalFocusFragmentCheckBox, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer31 = new Spacer();
        processingOptionsPanel.add(spacer31, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer32 = new Spacer();
        processingOptionsPanel.add(spacer32, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 3, new Insets(5, 10, 10, 10), -1, -1));
        processRightColumnPanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Deconvolution Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel3.getFont())));
        useBlindDeconvolutionCheckBox = new JCheckBox();
        useBlindDeconvolutionCheckBox.setEnabled(true);
        useBlindDeconvolutionCheckBox.setSelected(false);
        useBlindDeconvolutionCheckBox.setText("Use Blind Deconvolution");
        useBlindDeconvolutionCheckBox.setVerticalAlignment(0);
        panel3.add(useBlindDeconvolutionCheckBox, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        useBleachMinimizingCropCheckBox = new JCheckBox();
        useBleachMinimizingCropCheckBox.setEnabled(true);
        useBleachMinimizingCropCheckBox.setText("<html>Use Bleach Minimizing Crop<br>(unstable with MAV)</html>");
        panel3.add(useBleachMinimizingCropCheckBox, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deconvolutionIterationsLabel = new JLabel();
        deconvolutionIterationsLabel.setText("Deconvolution Iterations");
        panel3.add(deconvolutionIterationsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        deconvolutionModelLabel = new JLabel();
        deconvolutionModelLabel.setText("Deconvolution Model");
        panel3.add(deconvolutionModelLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer33 = new Spacer();
        panel3.add(spacer33, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        deconvolutionIterationsField = new JTextField();
        deconvolutionIterationsField.setText("25");
        panel3.add(deconvolutionIterationsField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deconvolutionModelComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("vectorial");
        defaultComboBoxModel4.addElement("scalar");
        deconvolutionModelComboBox.setModel(defaultComboBoxModel4);
        panel3.add(deconvolutionModelComboBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(180, -1), null, 0, false));
        final Spacer spacer34 = new Spacer();
        panel3.add(spacer34, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer35 = new Spacer();
        processPanel.add(spacer35, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer36 = new Spacer();
        processPanel.add(spacer36, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer37 = new Spacer();
        processPanel.add(spacer37, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer38 = new Spacer();
        processPanel.add(spacer38, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer39 = new Spacer();
        processPanel.add(spacer39, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        advancedPanel = new JPanel();
        advancedPanel.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Advanced", advancedPanel);
        final Spacer spacer40 = new Spacer();
        advancedPanel.add(spacer40, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        advancedRightColumnPanel = new JPanel();
        advancedRightColumnPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        advancedPanel.add(advancedRightColumnPanel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        previewPanel = new JPanel();
        previewPanel.setLayout(new GridLayoutManager(5, 4, new Insets(5, 10, 10, 10), -1, -1));
        advancedRightColumnPanel.add(previewPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "<html>Region Preview<br>(Not supported for HandE cycle)</html>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, previewPanel.getFont())));
        previewRegionLabel = new JLabel();
        previewRegionLabel.setText("Region");
        previewPanel.add(previewRegionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewRegionField = new JTextField();
        previewPanel.add(previewRegionField, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewCycleLabel = new JLabel();
        previewCycleLabel.setText("Cycle");
        previewPanel.add(previewCycleLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewCycleField = new JTextField();
        previewPanel.add(previewCycleField, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewChannelLabel = new JLabel();
        previewChannelLabel.setText("Channel");
        previewPanel.add(previewChannelLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewChannelField = new JTextField();
        previewPanel.add(previewChannelField, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewZPlaneLabel = new JLabel();
        previewZPlaneLabel.setText("Z-plane");
        previewPanel.add(previewZPlaneLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewZPlaneField = new JTextField();
        previewPanel.add(previewZPlaneField, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewGenerateButton = new JButton();
        previewGenerateButton.setText("Generate Preview");
        previewPanel.add(previewGenerateButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(130, 50), null, 0, false));
        final Spacer spacer41 = new Spacer();
        previewPanel.add(spacer41, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer42 = new Spacer();
        previewPanel.add(spacer42, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer43 = new Spacer();
        previewPanel.add(spacer43, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer44 = new Spacer();
        advancedPanel.add(spacer44, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer45 = new Spacer();
        advancedPanel.add(spacer45, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        advancedLeftColumnPanel = new JPanel();
        advancedLeftColumnPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        advancedPanel.add(advancedLeftColumnPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        stepSelectionPanel = new JPanel();
        stepSelectionPanel.setLayout(new GridLayoutManager(3, 3, new Insets(5, 10, 10, 10), -1, -1));
        Font stepSelectionPanelFont = this.$$$getFont$$$(null, -1, -1, stepSelectionPanel.getFont());
        if (stepSelectionPanelFont != null) stepSelectionPanel.setFont(stepSelectionPanelFont);
        advancedLeftColumnPanel.add(stepSelectionPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stepSelectionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "<html>Selective Processing<br>(Not supported for TMA)</html>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, stepSelectionPanel.getFont())));
        processRegionsLabel = new JLabel();
        processRegionsLabel.setText("Regions");
        stepSelectionPanel.add(processRegionsLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer46 = new Spacer();
        stepSelectionPanel.add(spacer46, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        processRegionsField = new JTextField();
        stepSelectionPanel.add(processRegionsField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer47 = new Spacer();
        stepSelectionPanel.add(spacer47, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        processTilesLabel = new JLabel();
        processTilesLabel.setText("Tiles");
        stepSelectionPanel.add(processTilesLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processTilesField = new JTextField();
        stepSelectionPanel.add(processTilesField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer48 = new Spacer();
        advancedPanel.add(spacer48, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer49 = new Spacer();
        advancedPanel.add(spacer49, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(bottomPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        loggingPanel = new JPanel();
        loggingPanel.setLayout(new GridLayoutManager(1, 1, new Insets(5, 10, 10, 10), -1, -1));
        bottomPanel.add(loggingPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 200), null, 0, false));
        loggingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Logging", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, loggingPanel.getFont())));
        loggingPane = new JScrollPane();
        loggingPane.setVerticalScrollBarPolicy(22);
        loggingPanel.add(loggingPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 200), null, null, 0, false));
        loggingTextArea = new JTextArea();
        loggingTextArea.setEditable(true);
        loggingTextArea.setRows(0);
        loggingPane.setViewportView(loggingTextArea);
        final Spacer spacer50 = new Spacer();
        bottomPanel.add(spacer50, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer51 = new Spacer();
        bottomPanel.add(spacer51, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer52 = new Spacer();
        bottomPanel.add(spacer52, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 5), null, 0, false));
        final Spacer spacer53 = new Spacer();
        bottomPanel.add(spacer53, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(10, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

}
