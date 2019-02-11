/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload.driffta;

import com.sun.imageio.spi.FileImageInputStreamSpi;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;

/**
 *
 * @author Nikolay Samusik
 */
public class OpenImage {

    public static void main(String[] args) throws Exception {
        
        long time = Calendar.getInstance().getTimeInMillis();
        FileImageInputStreamSpi stream = new FileImageInputStreamSpi();
        BufferedImage bi = ImageIO.read(new File("D:\\001_00001_Z001_CH1.tif"));
        ImagePlus ip = new ImagePlus("tiff", bi);
        System.out.println(Calendar.getInstance().getTimeInMillis() - time);
        time = Calendar.getInstance().getTimeInMillis();

        time = Calendar.getInstance().getTimeInMillis();
        BufferedImage bi2 = ImageIO.read(new File("D:\\001_00001_Z001_CH2.tif"));
        System.out.println(Calendar.getInstance().getTimeInMillis() - time);

        time = Calendar.getInstance().getTimeInMillis();
        BufferedImage bi3 = ImageIO.read(new File("D:\\001_00001_Z001_CH3.tif"));
        System.out.println(Calendar.getInstance().getTimeInMillis() - time);

        time = Calendar.getInstance().getTimeInMillis();
        IJ.openImage("D:\\001_00001_Z001_CH1.tif");
        System.out.println(Calendar.getInstance().getTimeInMillis() - time);

        time = Calendar.getInstance().getTimeInMillis();
        IJ.openImage("D:\\001_00001_Z001_CH2.tif");
        System.out.println(Calendar.getInstance().getTimeInMillis() - time);
        
          time = Calendar.getInstance().getTimeInMillis();
        IJ.openImage("D:\\001_00001_Z001_CH3.tif");
        System.out.println(Calendar.getInstance().getTimeInMillis() - time);

        new ImageJ().setVisible(true);

        

        ip.setImage(bi);

        IJ.saveAsTiff(ip, "D:\\tiff.tiff");

        ip.show();

        //ip2.show();
    }

}
