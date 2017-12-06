package com.akoya.codex.clustering;

import samusik.glasscmp.GlassListSelector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Vishal
 */
public class ImportConfigFrm extends JPanel {

    private HashMap<String, Integer> colNameVsIndex = new HashMap<>();

    public ImportConfigFrm(String[] colNames) {
        initComponents(colNames);
    }

    private void initComponents(String[] colNames) {
        GridBagConstraints gridBagConstraints;

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Clustering Configuration"));
        mainPanel.setLayout(new GridBagLayout());

        //Clustering cols
        clusteringColsLabel = new JLabel();
        clusteringColsLabel.setText("Clustering columns");
        clusteringColsLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        clusteringColsLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        clusteringColsLabel.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(3,3,3,3);
        mainPanel.add(clusteringColsLabel, gridBagConstraints);

        lstColNames = new GlassListSelector<String>();
        for (int i = 0; i < colNames.length; i++) {
            ((GlassListSelector<String>) lstColNames).getAvailableListModel().addElement(colNames[i]);
            if(!colNameVsIndex.containsKey(colNames[i])) {
                colNameVsIndex.put(colNames[i], i+1);
            }
        }
        lstColNames.setPreferredSize(new java.awt.Dimension(500, 200));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        mainPanel.add(lstColNames, gridBagConstraints);

        //Limit events
        limitEventsLabel = new JLabel();
        limitEventsLabel.setText("Limit events per file");
        limitEventsLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        limitEventsLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        limitEventsLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        mainPanel.add(limitEventsLabel, gridBagConstraints);

        limitEvents = new JSpinner();
        limitEvents.setModel(new SpinnerNumberModel(-1, -1, 200, 1));
        limitEvents.setValue(Integer.parseInt("-1"));
        limitEvents.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor)limitEvents.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        limitEvents.setMaximumSize(new java.awt.Dimension(500, 20));
        limitEvents.setMinimumSize(new java.awt.Dimension(100, 20));
        limitEvents.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        mainPanel.add(limitEvents, gridBagConstraints);

        //Transformation
        transformationLabel = new JLabel();
        transformationLabel.setText("Transformation");
        transformationLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        transformationLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        transformationLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        mainPanel.add(transformationLabel, gridBagConstraints);

        transformationCombo = new JComboBox<>();
        transformationCombo.setModel(new DefaultComboBoxModel<>(new String[] { "NONE", "ASINH", "DOUBLE_ASINH" }));
        transformationCombo.setMaximumSize(new java.awt.Dimension(100, 20));
        transformationCombo.setMinimumSize(new java.awt.Dimension(100, 20));
        transformationCombo.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        mainPanel.add(transformationCombo, gridBagConstraints);

        //Scaling Factor
        scalingFactorLabel = new JLabel();
        scalingFactorLabel.setText("Scaling factor");
        scalingFactorLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        scalingFactorLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        scalingFactorLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        mainPanel.add(scalingFactorLabel, gridBagConstraints);

        scalingFactor = new JSpinner();
        scalingFactor.setModel(new SpinnerNumberModel(5, 1, 200, 1));
        scalingFactor.setMaximumSize(new java.awt.Dimension(500, 20));
        scalingFactor.setMinimumSize(new java.awt.Dimension(100, 20));
        scalingFactor.setPreferredSize(new java.awt.Dimension(100, 20));
        scalingFactor.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor)scalingFactor.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        mainPanel.add(scalingFactor, gridBagConstraints);

        //Noise threshold
        noiseThresholdLabel = new JLabel();
        noiseThresholdLabel.setText("Noise threshold");
        noiseThresholdLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        noiseThresholdLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        noiseThresholdLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        mainPanel.add(noiseThresholdLabel, gridBagConstraints);

        noiseThreshold = new JSpinner();
        noiseThreshold.setModel(new SpinnerNumberModel(1.0, 0, 200, 0.1));
        noiseThreshold.setMaximumSize(new java.awt.Dimension(500, 20));
        noiseThreshold.setMinimumSize(new java.awt.Dimension(100, 20));
        noiseThreshold.setPreferredSize(new java.awt.Dimension(100, 20));
        noiseThreshold.setValue(Float.parseFloat("1.0"));
        noiseThreshold.setInputVerifier(floatVerifier);
        ((JSpinner.DefaultEditor)noiseThreshold.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        mainPanel.add(noiseThreshold, gridBagConstraints);

        //Eucledian Length threshold
        eucLengthThresholdLabel = new JLabel();
        eucLengthThresholdLabel.setText("Eucledian length threshold");
        eucLengthThresholdLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        eucLengthThresholdLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        eucLengthThresholdLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        mainPanel.add(eucLengthThresholdLabel, gridBagConstraints);

        eucLengthThreshold = new JSpinner();
        eucLengthThreshold.setModel(new SpinnerNumberModel(1.0, 0, 200, 0.1));
        eucLengthThreshold.setMaximumSize(new java.awt.Dimension(500, 20));
        eucLengthThreshold.setMinimumSize(new java.awt.Dimension(100, 20));
        eucLengthThreshold.setPreferredSize(new java.awt.Dimension(100, 20));
        eucLengthThreshold.setValue(Float.parseFloat("1.0"));
        eucLengthThreshold.setInputVerifier(integerVerifier);
        ((JSpinner.DefaultEditor)eucLengthThreshold.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        mainPanel.add(eucLengthThreshold, gridBagConstraints);

        //Rescale
        rescaleLabel = new JLabel();
        rescaleLabel.setText("Rescale");
        rescaleLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        rescaleLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        rescaleLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        mainPanel.add(rescaleLabel, gridBagConstraints);

        rescaleCombo = new JComboBox<>();
        rescaleCombo.setModel(new DefaultComboBoxModel<>(new String[] { "NONE", "SD", "QUANTILE" }));
        rescaleCombo.setMaximumSize(new java.awt.Dimension(100, 20));
        rescaleCombo.setMinimumSize(new java.awt.Dimension(100, 20));
        rescaleCombo.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        mainPanel.add(rescaleCombo, gridBagConstraints);

        //Quantile
        quantileLabel = new JLabel();
        quantileLabel.setText("Quantile");
        quantileLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        quantileLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        quantileLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        mainPanel.add(quantileLabel, gridBagConstraints);

        quantile = new JSpinner();
        quantile.setModel(new SpinnerNumberModel(1.0, 0, 200, 0.05));
        quantile.setMaximumSize(new java.awt.Dimension(500, 20));
        quantile.setMinimumSize(new java.awt.Dimension(100, 20));
        quantile.setPreferredSize(new java.awt.Dimension(100, 20));
        quantile.setValue(Float.parseFloat("1.0"));
        quantile.setInputVerifier(floatVerifier);
        ((JSpinner.DefaultEditor)quantile.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        mainPanel.add(quantile, gridBagConstraints);

        //Rescale separately
        rescaleSeparatelyLabel = new JLabel();
        rescaleSeparatelyLabel.setText("Rescale separately");
        rescaleSeparatelyLabel.setMaximumSize(new java.awt.Dimension(3000, 20));
        rescaleSeparatelyLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        rescaleSeparatelyLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        mainPanel.add(rescaleSeparatelyLabel, gridBagConstraints);

        rescaleSeparatelyCombo = new JComboBox<>();
        rescaleSeparatelyCombo.setModel(new DefaultComboBoxModel<>(new String[] { "True", "False"}));
        rescaleSeparatelyCombo.setMaximumSize(new java.awt.Dimension(100, 20));
        rescaleSeparatelyCombo.setMinimumSize(new java.awt.Dimension(100, 20));
        rescaleSeparatelyCombo.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        mainPanel.add(rescaleSeparatelyCombo, gridBagConstraints);

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
                JOptionPane.showMessageDialog(ImportConfigFrm.this, "Not a valid integer: " + e.getMessage());
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
                JOptionPane.showMessageDialog(ImportConfigFrm.this, "Not a valid float value: " + e.getMessage());
                return false;
            }
        }
    };

    private JLabel clusteringColsLabel;
    private GlassListSelector lstColNames;

    public ArrayList<Integer> getLstColNamesIndex() {
        ArrayList<String> selectedCols = ((GlassListSelector<String>) lstColNames).getSelectedItems();
        ArrayList<Integer> idx = new ArrayList<>();
        for(String aSelectedCol : selectedCols) {
            if(colNameVsIndex.containsKey(aSelectedCol)) {
                idx.add(colNameVsIndex.get(aSelectedCol));
            }
        }
        return idx;
    }

    private JLabel limitEventsLabel;
    private JSpinner limitEvents;

    public String getLimitEvents() {
        return limitEvents.getValue().toString();
    }

    private JLabel transformationLabel;
    private JComboBox<String> transformationCombo;

    public String getTransformation() {
        return transformationCombo.getSelectedItem().toString();
    }

    private JLabel scalingFactorLabel;
    private JSpinner scalingFactor;

    public String getScalingFactor() {
        return scalingFactor.getValue().toString();
    }

    private JLabel noiseThresholdLabel;
    private JSpinner noiseThreshold;

    public String getNoiseThreshold() {
        return noiseThreshold.getValue().toString();
    }

    private JLabel eucLengthThresholdLabel;
    private JSpinner eucLengthThreshold;

    public String getEucLengthThreshold() {
        return eucLengthThreshold.getValue().toString();
    }

    private JLabel rescaleLabel;
    private JComboBox<String> rescaleCombo;

    public String getRescale() {
        return rescaleCombo.getSelectedItem().toString();
    }

    private JLabel quantileLabel;
    private JSpinner quantile;

    public String getQuantile() {
        return quantile.getValue().toString();
    }

    private JLabel rescaleSeparatelyLabel;
    private JComboBox<String> rescaleSeparatelyCombo;

    public String getRescaleSeparately() {
        return rescaleSeparatelyCombo.getSelectedItem().toString();
    }

    private JPanel mainPanel;
}
