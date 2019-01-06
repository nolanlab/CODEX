package org.nolanlab.codex;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vishal
 */
public class OkayMockOptionPane extends DefaultOptionPane {

    //MockOptionPane is just an abstract class implementing default methods from OptionPane
    @Override
    public int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
        return JOptionPane.OK_OPTION;
    }
}