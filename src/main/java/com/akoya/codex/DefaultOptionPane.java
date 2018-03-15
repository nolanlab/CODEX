package com.akoya.codex;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vishal
 */
public class DefaultOptionPane implements OptionPane {
    @Override
    public int showConfirmDialog(Component parentComponent,
                                 Object message, String title, int optionType) {

        return JOptionPane.showConfirmDialog(parentComponent,message,title,optionType);
    }
}