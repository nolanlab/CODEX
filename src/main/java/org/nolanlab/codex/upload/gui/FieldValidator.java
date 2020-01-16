package org.nolanlab.codex.upload.gui;

import javax.swing.*;

public class FieldValidator {
    public static final InputVerifier INTEGER_VERIFIER = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            JTextField tf = (JTextField) input;
            try {
                int val = Integer.parseInt(tf.getText());
                if (val < 1) {
                    throw new NumberFormatException("The number must be 1 or greater");
                }
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Not a valid integer: " + e.getMessage());
                return false;
            }
        }
    };

    public static final InputVerifier DOUBLE_VERIFIER = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            JTextField tf = (JTextField) input;
            try {
                double val = Double.parseDouble(tf.getText());
                if (val <= 0) {
                    throw new NumberFormatException("The number cannot be negative or zero");
                }
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Not a valid floating-point number: " + e.getMessage());
                return false;

            }
        }
    };
}
