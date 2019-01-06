/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload.driffta;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Nikolay
 */
public class RescaleImages {
    public static void main(String[] args) {
        File dir = new File("E:\\CODEX datasets\\5-20-16 pfizer-01\\deconv");
        ImageJ ij = new ImageJ();
        int i = 0;
        for (File f : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("tiff")|| name.endsWith("tif");
            }
        })){
           i++;
           if(i<=8) continue;
           System.err.println("Rescaling file#"+i + " " + f.getName());
           ImagePlus imp = IJ.openImage(f.getPath());
           IJ.run(imp, "Multiply...", "value=5");
           IJ.save(imp, f.getPath());
           imp.close();
        }
        
        
    }
}
