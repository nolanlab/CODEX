/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload.driffta;

import com.akoya.codex.upload.logger;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.ChannelSplitter;
import ij.plugin.Concatenator;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.LUT;

import java.awt.*;

import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author CODEX
 */
public class BestFocus {

    public static ImagePlus createBestFocusStackFromHyperstack(ImagePlus imp, int[] bestFocusZIndices) {

        logger.print("Best focus on stack: " + imp.getTitle());

        ImagePlus[] stack = new ImagePlus[imp.getNFrames()];
        ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Entry<Integer, ImagePlus>>> fut = new ArrayList<>();
        Duplicator dup = new Duplicator();

        for (int frame = 1; frame <= imp.getNFrames(); frame++) {
            final int fr = frame;
            final ImagePlus tp = dup.run(imp, 1, imp.getNChannels(), 1, imp.getNSlices(), fr, fr);
            int z= bestFocusZIndices[fr-1];
            stack[fr-1] = retrieveFocusedPlane(tp, z);
        }

        ImagePlus focused = new Concatenator().concatenate(stack, false);
        ImagePlus hyp = HyperStackConverter.toHyperStack(focused, imp.getNChannels(), 1, imp.getNFrames(), "xyczt", "composite");
        if (hyp.getNChannels() == 4) {
            ((CompositeImage) hyp).setLuts(new LUT[]{LUT.createLutFromColor(Color.WHITE), LUT.createLutFromColor(Color.RED), LUT.createLutFromColor(Color.GREEN), LUT.createLutFromColor(new Color(0, 70, 255))});
        }

        return hyp;
    }

    public static int findBestFocusStackFromSingleTimepoint(ImagePlus imp, int focusChannel, boolean optionalFocusFragment) {
        Find_focused_slices plg = new Find_focused_slices();
        ImageStack ch = ChannelSplitter.getChannel(imp, focusChannel);
        imp = new ImagePlus("ch" + focusChannel, ch);
        ImageStatistics is = imp.getStatistics();
        int yStep = (imp.getHeight() / 4) + 1;
        int xStep = (imp.getWidth() / 4) + 1;
        ArrayList<Integer> al = new ArrayList<>();

        for (int x = 0; x < imp.getWidth(); x += xStep) {
            for (int y = 0; y < imp.getHeight(); y += yStep) {
                ImageStack out = null;
                for (int i = 1; i <= imp.getNSlices(); i++) {
                    ImageProcessor ip = imp.getStack().getProcessor(i);
                    ip.setRoi(x, y, xStep, yStep);
                    ImageProcessor cropped;
                    if(optionalFocusFragment) {
                        cropped = ip.crop();
                    }
                    else {
                        cropped = ip;
                    }
                    if (out == null) {
                        out = new ImageStack(cropped.getWidth(), cropped.getHeight());
                    }
                    out.addSlice("slice" + i, cropped);
                }

                ImagePlus tmp = new ImagePlus("tmp_crop", out);
                ImageStatistics isTmp = tmp.getStatistics();

                if (isTmp.mean > is.mean - is.stdDev) {
                    plg.setup("select=100 variance=0.000", tmp);
                    int z = plg.run(null);
                    if (z == 0) {
                        double maxIntens = 0;
                        int maxZ = 0;
                        for (int i = 1; i <= imp.getNSlices(); i++) {
                            ImageProcessor ip = imp.getStack().getProcessor(i);
                             ImageStatistics isTmp2 = ip.getStatistics();
                             if(isTmp2.mean> maxIntens){
                                 maxIntens = isTmp2.mean;
                                 maxZ= i;
                             }
                        }
                        z = maxZ;
                    }
                    logger.print("trying subtile x=" + x + ", y=" + y + ", best_slice=" + z);
                    al.add(z);
                }
            }
        }

        int bestSlice = imp.getNSlices() / 2;

        if (al.size() > 0) {
            Collections.sort(al);
            bestSlice = al.get(al.size() / 2);
        }

        logger.print("best Z = " + bestSlice);
        return bestSlice;
    }

    public static ImagePlus retrieveFocusedPlane(ImagePlus imp, int z) {
        ImageStack out = new ImageStack(imp.getWidth(), imp.getHeight());

        for (int i = 1; i <= imp.getNChannels(); i++) {
            ImageStack s = ChannelSplitter.getChannel(imp, i);
            out.addSlice("ch" + i, s.getProcessor(z));
        }

        ImagePlus ret = new ImagePlus(imp.getTitle() + "bestFocus", out);
        ImagePlus hyp = HyperStackConverter.toHyperStack(ret, imp.getNChannels(), 1, 1, "xyczt", "composite");

        return hyp;
    }
/*
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("USAGE:\n com.akoya.codex.upload.driffta.BestFocus <path-to-cropped-stacks>");
        }

        File outDir = new File(args[0] + (args[0].endsWith(File.separator) ? "" : File.separator) + "bestfocus");
        outDir.mkdirs();

        File dir = new File(args[0]);

        for (File f : dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File p) {
                return p.isFile() && p.getName().startsWith("reg") && p.getName().contains("_") && (p.getName().endsWith(".tif") || p.getName().endsWith(".tiff"));
            }
        })) {
            File destFile = new File(outDir.getAbsolutePath() + File.separator + f.getName());
            if (!destFile.exists()) {
                ImagePlus in = IJ.openImage(f.getAbsolutePath());
                int[] zPlanes = computeBestFocusIndicesBasedOnDotProduct(in, 1);
                ImagePlus out = createBestFocusStackFromHyperstack(in, zPlanes,1);
                IJ.save(out, outDir.getAbsolutePath() + File.separator + f.getName());
            }
        }

    }*/
}
