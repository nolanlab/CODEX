/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import ij.ImagePlus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Nikolay Samusik
 */
public class RegionImageWriter {

    public static BufferedImage[] writeRegionImage(SegmentedObject[] reg, ImagePlus segmImage, String srcFileName, File outputDir) throws IOException {
        if (srcFileName.contains(".")) {
            srcFileName = srcFileName.substring(0, srcFileName.lastIndexOf("."));
        }
        int w = segmImage.getWidth();
        int h = segmImage.getHeight();
        int d = segmImage.getNSlices();

        BufferedImage[] bi = new BufferedImage[d];
        for (int z = 0; z < d; z++) {
            bi[z] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        for (SegmentedObject segmentedObject : reg) {
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            int a = 255;
            int col = (a << 24) | (r << 16) | (g << 8) | b;
            for (Point3D pt : segmentedObject.getPoints()) {
                bi[pt.z].setRGB(pt.x, pt.y, col);
            }
        }

        BufferedImage[] bi2 = new BufferedImage[d];
        for (int z = 0; z < d; z++) {
            bi2[z] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            bi[z].getGraphics().setColor(Color.black);
            bi2[z].getGraphics().fillRect(0,0,w,h);
        }

        for (int z = 0; z < d; z++) {
            BufferedImage img = bi[z];
            for (int x = 1; x < w - 1; x++) {
                for (int y = 1; y < h - 1; y++) {
                    int col = img.getRGB(x, y);

                    int a = (col >> 24) & 0xFF;
                    if (a > 0) {
                        boolean inner = true;
                        for (int[] xy : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                            int col2 = img.getRGB(x + xy[0], y + xy[1]);
                            inner &= col2 == col;
                        }
                        if (!inner) {
                            bi2[z].setRGB(x, y, col);
                        }else{
                            bi2[z].setRGB(x, y, 0);
                        }
                    }else{
                            bi2[z].setRGB(x, y, 0);
                    }
                }
            }
            bi2[z].getGraphics().setColor(Color.black);
            bi2[z].getGraphics().drawRect(0,0,w-1,h-1);
            /*
            if (Main.printParams) {
                Graphics2D gr = bi2[z].createGraphics();
                gr.setPaint(Color.WHITE);
                String[] s = (Main.revision + "\n" + Main.params.toString().replace(',', '\n')).split("\n");
                int offset = 15;
                for (String string : s) {
                    gr.drawString(string, 15, offset += 15);
                }
            }*/
        }

        for (int z = 0; z < d; z++) {
            String fileName = String.format(outputDir + File.separator + "regions_" + srcFileName + "_Z%02d", z+1);
            ImageIO.write(bi2[z], "PNG", new File(fileName + ".png"));
        }
    return bi2;
    }
}
