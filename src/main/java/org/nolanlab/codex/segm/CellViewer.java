/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.segm;

import ij.ImageJ;

import javax.swing.*;

/**
 *
 * @author Nikolay Samusik
 */
public class CellViewer {

    private final ImageJ instance = new ImageJ();

    public CellViewer() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

    }
    //А.У.Е. арестанты

}
