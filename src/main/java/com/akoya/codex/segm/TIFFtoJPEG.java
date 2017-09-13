/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.plugin.Duplicator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 *
 * @author Nikolay
 */
public class TIFFtoJPEG {

    public static void main(String[] args) throws IOException {

        File inList = null;
        int quality = 90;
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Invalid number of arguments, expected 2 arg");
            }
            inList = new File(args[0]);
            quality = Integer.parseInt(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Provided arguments as seen by the application:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("arg#" + i + "=" + args[i]);
            }
            System.out.println("Usage: java -jar CODEX.jar TIFFtoJPEG.class <directory-with-inFiles> <jpeg-quality[1:100]");
            System.exit(0);
        }

        Duplicator dup = new Duplicator();
        if (!inList.exists()) {
            throw new IllegalArgumentException("Error: Cannot find the input directoty");
        }
        for (File in : inList.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith("tiff") || pathname.getName().toLowerCase().endsWith("tif");
            }
        })) {

            System.out.print("processing file: " + in.getName());
            if (!in.exists()) {
                throw new IllegalArgumentException("Error: Cannot find the input file:" + in);
            }

            final ImagePlus imp = new Opener().openImage(in.getAbsolutePath());

            if (imp == null) {
                throw new IllegalStateException("Couldn't open the image file: " + in);
            }

            imp.setDisplayMode(IJ.GRAYSCALE);

            for (int ch = 1; ch < imp.getNChannels() + 1; ch++) {
                imp.setC(ch);
                IJ.run(imp, "Grays", "");
            }

            for (int frame = 1; frame < imp.getNFrames() + 1; frame++) {
                for (int ch = 1; ch < imp.getNChannels() + 1; ch++) {

                    for (int z = 1; z < imp.getNSlices() + 1; z++) {
                        ImagePlus slice = dup.run(imp, ch, ch, z, z, frame, frame);
                        
                        slice.getProcessor().multiply(1/256.0);
                        slice.setProcessor(null, slice.getProcessor().convertToByte(false));
                        slice.setCalibration(slice.getCalibration());
                        
                        int virtualCh = ((frame - 1) * imp.getNChannels()) + ch;
                        FileSaver fs = new FileSaver(slice);
                        FileSaver.setJpegQuality(quality);

                        fs.saveAsJpeg(inList + File.separator + in.getName().substring(0, in.getName().lastIndexOf(".")) + "_Z" + z + "_C" + virtualCh + ".jpg");
                    }
                }

            }
            imp.getNFrames();
            imp.getNSlices();

        }

    }
}
