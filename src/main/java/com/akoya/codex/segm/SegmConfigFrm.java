package com.akoya.codex.segm;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Vishal
 */
public class SegmConfigFrm extends JPanel {

    public SegmConfigFrm() {
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Clustering Configuration"));
        mainPanel.setLayout(new GridBagLayout());

        //Radius
        radiusLabel = new JLabel();
        radiusLabel.setText("Radius");
        radiusLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        radiusLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        radiusLabel.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        mainPanel.add(radiusLabel, gridBagConstraints);

        radius = new JSpinner();
        radius.setPreferredSize(new java.awt.Dimension(500, 20));
        radius.setModel(new SpinnerNumberModel(5, 1, 200, 1));
        radius.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor) radius.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        mainPanel.add(radius, gridBagConstraints);

        //Max cutoff
        maxCutOffLabel = new JLabel();
        maxCutOffLabel.setText("Max cutoff");
        maxCutOffLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        maxCutOffLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        maxCutOffLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        mainPanel.add(maxCutOffLabel, gridBagConstraints);

        maxCutOff = new JSpinner();
        maxCutOff.setModel(new SpinnerNumberModel(0.99, 0, 1, 0.01));
        maxCutOff.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor) maxCutOff.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        maxCutOff.setMaximumSize(new java.awt.Dimension(500, 20));
        maxCutOff.setMinimumSize(new java.awt.Dimension(100, 20));
        maxCutOff.setPreferredSize(new java.awt.Dimension(100, 20));
        maxCutOff.setInputVerifier(floatVerifier);
        ((JSpinner.DefaultEditor) maxCutOff.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        mainPanel.add(maxCutOff, gridBagConstraints);

        //Min cutoff
        minCutOffLabel = new JLabel();
        minCutOffLabel.setText("Min cutoff");
        minCutOffLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        minCutOffLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        minCutOffLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        mainPanel.add(minCutOffLabel, gridBagConstraints);

        minCutOff = new JSpinner();
        minCutOff.setModel(new SpinnerNumberModel(0.05, 0, 1, 0.01));
        minCutOff.setMaximumSize(new java.awt.Dimension(100, 20));
        minCutOff.setMinimumSize(new java.awt.Dimension(100, 20));
        minCutOff.setPreferredSize(new java.awt.Dimension(100, 20));
        minCutOff.setInputVerifier(floatVerifier);
        ((JSpinner.DefaultEditor) minCutOff.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        mainPanel.add(minCutOff, gridBagConstraints);

        //Relative Cutoff
        relativeCutOffLabel = new JLabel();
        relativeCutOffLabel.setText("Relative cutoff");
        relativeCutOffLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        relativeCutOffLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        relativeCutOffLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        mainPanel.add(relativeCutOffLabel, gridBagConstraints);

        relativeCutOff = new JSpinner();
        relativeCutOff.setModel(new SpinnerNumberModel(0.2, 0.0001, 0.9, 0.1));
        relativeCutOff.setMaximumSize(new java.awt.Dimension(500, 20));
        relativeCutOff.setMinimumSize(new java.awt.Dimension(100, 20));
        relativeCutOff.setPreferredSize(new java.awt.Dimension(100, 20));
        relativeCutOff.setInputVerifier(floatVerifier);
        ((JSpinner.DefaultEditor) relativeCutOff.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        mainPanel.add(relativeCutOff, gridBagConstraints);

        //Cell Size Cutof
        CellSizeCutOffLabel = new JLabel();
        CellSizeCutOffLabel.setText("Cell Size cutoff factor (smaller values will keep smaller cells)");
        CellSizeCutOffLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        CellSizeCutOffLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        CellSizeCutOffLabel.setPreferredSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        mainPanel.add(CellSizeCutOffLabel, gridBagConstraints);

        CellSizeCutOff = new JSpinner();
        CellSizeCutOff.setModel(new SpinnerNumberModel(1.0, 0.1, 10, 0.1));
        CellSizeCutOff.setMaximumSize(new java.awt.Dimension(500, 20));
        CellSizeCutOff.setMinimumSize(new java.awt.Dimension(100, 20));
        CellSizeCutOff.setPreferredSize(new java.awt.Dimension(100, 20));
        CellSizeCutOff.setInputVerifier(floatVerifier);
        ((JSpinner.DefaultEditor) CellSizeCutOff.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        mainPanel.add(CellSizeCutOff, gridBagConstraints);

        //Nuclear Stain Channel
        nuclearStainChannelLabel = new JLabel();
        nuclearStainChannelLabel.setText("Nuclear Stain Channel");
        nuclearStainChannelLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        nuclearStainChannelLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        nuclearStainChannelLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        mainPanel.add(nuclearStainChannelLabel, gridBagConstraints);

        nuclearStainChannel = new JSpinner();
        nuclearStainChannel.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        nuclearStainChannel.setMaximumSize(new java.awt.Dimension(500, 20));
        nuclearStainChannel.setMinimumSize(new java.awt.Dimension(100, 20));
        nuclearStainChannel.setPreferredSize(new java.awt.Dimension(100, 20));
        nuclearStainChannel.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor) nuclearStainChannel.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        mainPanel.add(nuclearStainChannel, gridBagConstraints);

        //Nuclear stain cycle
        nuclearStainCycleLabel = new JLabel();
        nuclearStainCycleLabel.setText("Nuclear Stain Cycle");
        nuclearStainCycleLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        nuclearStainCycleLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        nuclearStainCycleLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        mainPanel.add(nuclearStainCycleLabel, gridBagConstraints);

        nuclearStainCycle = new JSpinner();
        nuclearStainCycle.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        nuclearStainCycle.setMaximumSize(new java.awt.Dimension(500, 20));
        nuclearStainCycle.setMinimumSize(new java.awt.Dimension(100, 20));
        nuclearStainCycle.setPreferredSize(new java.awt.Dimension(100, 20));
        nuclearStainCycle.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor) nuclearStainCycle.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        mainPanel.add(nuclearStainCycle, gridBagConstraints);

        //Membrane Stain Channel
        membraneStainChannelLabel = new JLabel();
        membraneStainChannelLabel.setText("Membrane Stain Channel");
        membraneStainChannelLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        membraneStainChannelLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        membraneStainChannelLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        mainPanel.add(membraneStainChannelLabel, gridBagConstraints);

        membraneStainChannel = new JSpinner();
        membraneStainChannel.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        membraneStainChannel.setMaximumSize(new java.awt.Dimension(100, 20));
        membraneStainChannel.setMinimumSize(new java.awt.Dimension(100, 20));
        membraneStainChannel.setPreferredSize(new java.awt.Dimension(100, 20));
        membraneStainChannel.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor) membraneStainChannel.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        mainPanel.add(membraneStainChannel, gridBagConstraints);

        //Membrane Stain Cycle
        membraneStainCycleLabel = new JLabel();
        membraneStainCycleLabel.setText("Membrane Stain Cycle");
        membraneStainCycleLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        membraneStainCycleLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        membraneStainCycleLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        mainPanel.add(membraneStainCycleLabel, gridBagConstraints);

        membraneStainCycle = new JSpinner();
        membraneStainCycle.setModel(new SpinnerNumberModel(-1, -1, 100, 1));
        membraneStainCycle.setMaximumSize(new java.awt.Dimension(500, 20));
        membraneStainCycle.setMinimumSize(new java.awt.Dimension(100, 20));
        membraneStainCycle.setPreferredSize(new java.awt.Dimension(100, 20));
        membraneStainCycle.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor) membraneStainCycle.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        mainPanel.add(membraneStainCycle, gridBagConstraints);

        //Membrane Stain Cycle
        anisotropic_Region_Growth_Label = new JLabel();
        anisotropic_Region_Growth_Label.setText("Anisotropic region growth");
        anisotropic_Region_Growth_Label.setMaximumSize(new java.awt.Dimension(3000, 20));
        anisotropic_Region_Growth_Label.setMinimumSize(new java.awt.Dimension(100, 20));
        anisotropic_Region_Growth_Label.setPreferredSize(new java.awt.Dimension(100, 20));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        mainPanel.add(anisotropic_Region_Growth_Label, gridBagConstraints);

        anisotropic_Region_Growth = new JCheckBox();
        anisotropic_Region_Growth.setText("<html>Restricts cell growth in Z, improves pos-neg separation on biaxials, but must be disabled for compatibility with CODEX1 Cell paper (Goltsev et al)</html>");
        anisotropic_Region_Growth.setSelected(true);
        anisotropic_Region_Growth.setMaximumSize(new java.awt.Dimension(500, 40));
        anisotropic_Region_Growth.setMinimumSize(new java.awt.Dimension(100, 40));
        anisotropic_Region_Growth.setPreferredSize(new java.awt.Dimension(100, 40));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        mainPanel.add(anisotropic_Region_Growth, gridBagConstraints);

        //Readout Channel
//        readOutChannelsLabel = new JLabel();
//        readOutChannelsLabel.setText("Readout Channels");
//        readOutChannelsLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
//        readOutChannelsLabel.setMinimumSize(new java.awt.Dimension(100, 20));
//        readOutChannelsLabel.setPreferredSize(new java.awt.Dimension(100, 20));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
//        //gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 8;
//        mainPanel.add(readOutChannelsLabel, gridBagConstraints);
//
//        readOutChannels = new JTextField();
//        readOutChannels.setMaximumSize(new java.awt.Dimension(100, 20));
//        readOutChannels.setMinimumSize(new java.awt.Dimension(100, 20));
//        readOutChannels.setPreferredSize(new java.awt.Dimension(100, 20));
//        readOutChannels.setText("1,2,3,4");
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.CENTER;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 8;
//        mainPanel.add(readOutChannels, gridBagConstraints);

        //Use Membrane
//        useMembraneLabel = new JLabel();
//        useMembraneLabel.setText("Use Membrane");
//        useMembraneLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
//        useMembraneLabel.setMinimumSize(new java.awt.Dimension(100, 20));
//        useMembraneLabel.setPreferredSize(new java.awt.Dimension(100, 20));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
//        //gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 9;
//        mainPanel.add(useMembraneLabel, gridBagConstraints);
//
//        useMembrane = new JComboBox<>();
//        useMembrane.setModel(new DefaultComboBoxModel<>(new String[] { "True", "False"}));
//        useMembrane.setMaximumSize(new java.awt.Dimension(100, 20));
//        useMembrane.setMinimumSize(new java.awt.Dimension(100, 20));
//        useMembrane.setPreferredSize(new java.awt.Dimension(100, 20));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.CENTER;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 9;
//        mainPanel.add(useMembrane, gridBagConstraints);
//
//        //Inner ring size
//        innerRingSizeLabel = new JLabel();
//        innerRingSizeLabel.setText("Inner Ring Size");
//        innerRingSizeLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
//        innerRingSizeLabel.setMinimumSize(new java.awt.Dimension(100, 20));
//        innerRingSizeLabel.setPreferredSize(new java.awt.Dimension(100, 20));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
//        //gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 10;
//        mainPanel.add(innerRingSizeLabel, gridBagConstraints);
//
//        innerRingSize = new JSpinner();
//        innerRingSize.setModel(new SpinnerNumberModel(1.0, 0, 25, 0.1));
//        innerRingSize.setMaximumSize(new java.awt.Dimension(500, 20));
//        innerRingSize.setMinimumSize(new java.awt.Dimension(100, 20));
//        innerRingSize.setPreferredSize(new java.awt.Dimension(100, 20));
//        innerRingSize.setInputVerifier(floatVerifier);
//        ((JSpinner.DefaultEditor) innerRingSize.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.CENTER;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 10;
//        mainPanel.add(innerRingSize, gridBagConstraints);
//
//
//        //Delaunay Graph
//        delaunayGraphLabel = new JLabel();
//        delaunayGraphLabel.setText("Delaunay Graph");
//        delaunayGraphLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
//        delaunayGraphLabel.setMinimumSize(new java.awt.Dimension(100, 20));
//        delaunayGraphLabel.setPreferredSize(new java.awt.Dimension(100, 20));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
//        //gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 11;
//        mainPanel.add(delaunayGraphLabel, gridBagConstraints);
//
//        delaunayGraph = new JComboBox<>();
//        delaunayGraph.setModel(new DefaultComboBoxModel<>(new String[] { "True", "False"}));
//        delaunayGraph.setMaximumSize(new java.awt.Dimension(100, 20));
//        delaunayGraph.setMinimumSize(new java.awt.Dimension(100, 20));
//        delaunayGraph.setPreferredSize(new java.awt.Dimension(100, 20));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = GridBagConstraints.CENTER;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 11;
//        mainPanel.add(delaunayGraph, gridBagConstraints);

        this.setLayout(new BorderLayout());
        this.add(mainPanel);
    }

    private final InputVerifier integerVerifier = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            JTextField tf = (JTextField) input;
            try {
                int val = Integer.parseInt(tf.getText());
                if (val < 1) {
                    throw new NumberFormatException("the number must be 1 or greater");
                }
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(SegmConfigFrm.this, "Not a valid integer: " + e.getMessage());
                return false;
            }
        }
    };

    private final InputVerifier floatVerifier = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            JTextField tf = (JTextField) input;
            try {
                float val = Float.parseFloat(tf.getText());
                if (val < 1) {
                    throw new NumberFormatException("the number must be 1 or greater");
                }
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(SegmConfigFrm.this, "Not a valid float value: " + e.getMessage());
                return false;
            }
        }
    };

    private JLabel radiusLabel;
    private JSpinner radius;

    public String getRadius() {
        return radius.getValue().toString();
    }
    public void setRadius(String radius) {
        this.radius.setValue(Integer.parseInt(radius));
    }

    private JLabel maxCutOffLabel;
    private JSpinner maxCutOff;

    public String getMaxCutOff() {
        return maxCutOff.getValue().toString();
    }
    public void setMaxCutOff(String maxCutOff) {
        this.maxCutOff.setValue(Float.parseFloat(maxCutOff));
    }




    private JLabel minCutOffLabel;
    private JSpinner minCutOff;

    public String getMinCutOff() {
        return minCutOff.getValue().toString();
    }
    public void setMinCutOff(String minCutOff) {
        this.minCutOff.setValue(Float.parseFloat(minCutOff));
    }

    private JLabel relativeCutOffLabel;
    private JSpinner relativeCutOff;

    public String getRelativeCutOff() {
        return relativeCutOff.getValue().toString();
    }
    public void setRelativeCutOff(String relativeCutOff) {
        this.relativeCutOff.setValue(Float.parseFloat(relativeCutOff));
    }

    public String getCellSizeCutOff() {
        return CellSizeCutOff.getValue().toString();
    }

    public void setCellSizeCutOff(String cellSizeCutOff) {
        this.CellSizeCutOff.setValue(Float.parseFloat(cellSizeCutOff));
    }


    private JLabel nuclearStainChannelLabel;
    private JSpinner nuclearStainChannel;

    public String getNuclearStainChannel() {
        return nuclearStainChannel.getValue().toString();
    }
    public void setNuclearStainChannel(String nuclearStainChannel) {
        this.nuclearStainChannel.setValue(Integer.parseInt(nuclearStainChannel));
    }

    private JLabel nuclearStainCycleLabel;
    private JSpinner nuclearStainCycle;

    public String getNuclearStainCycle() {
        return nuclearStainCycle.getValue().toString();
    }
    public void setNuclearStainCycle(String nuclearStainCycle) {
        this.nuclearStainCycle.setValue(Integer.parseInt(nuclearStainCycle));
    }

    private JLabel membraneStainChannelLabel;
    private JSpinner membraneStainChannel;

    public String getMembraneStainChannel() {
        return membraneStainChannel.getValue().toString();
    }
    public void setMembraneStainChannel(String membraneStainChannel) {
        this.membraneStainChannel.setValue(Integer.parseInt(membraneStainChannel));
    }

    private JLabel membraneStainCycleLabel;
    private JSpinner membraneStainCycle;

    public String getMembraneStainCycle() {
        return membraneStainCycle.getValue().toString();
    }
    public void setMembraneStainCycle(String membraneStainCycle) {
        this.membraneStainCycle.setValue(Integer.parseInt(membraneStainCycle));
    }

    public boolean isAnisotropicRegionGrowth() {
        return anisotropic_Region_Growth.isSelected();
    }
    public void setAnisotropicRegionGrowth(boolean value) {
        this.anisotropic_Region_Growth.setSelected(value);
    }

    private JPanel mainPanel;

    private JSpinner CellSizeCutOff;
    private JLabel CellSizeCutOffLabel;

    private JCheckBox anisotropic_Region_Growth;
    private JLabel anisotropic_Region_Growth_Label;


//    private JLabel readOutChannelsLabel;
//    private JTextField readOutChannels;
//
//    public String getReadOutChannels() {
//        return readOutChannels.getText().toString();
//    }


//    Later MayBe

//    private JLabel useMembraneLabel;
//    private JComboBox<String> useMembrane;
//
//    public String getUseMembrane() {
//        return useMembrane.getSelectedItem().toString();
//    }
//
//    private JLabel innerRingSizeLabel;
//    private JSpinner innerRingSize;
//
//    public String getInnerRingSize() {
//        return innerRingSize.getValue().toString();
//    }
//
//    private JLabel delaunayGraphLabel;
//    private JComboBox<String> delaunayGraph;
//
//    public String getDelaunayGraph() {
//        return delaunayGraph.getSelectedItem().toString();
//    }
}
