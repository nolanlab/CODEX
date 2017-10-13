/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload.driffta;

import com.akoya.codex.upload.logger;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.ChannelSplitter;
import ij.plugin.Concatenator;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.LUT;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;

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

    public static int[] computeBestFocusIndices(ImagePlus imp, int focusChannel) {
        logger.print("Best focus on stack: " + imp.getTitle());
        ImagePlus[] stack = new ImagePlus[imp.getNFrames()];
        int[] bestFocusPlanes = new int[imp.getNFrames()];
        ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

        List<Callable<Entry<Integer, Integer>>> fut = new ArrayList<>();
        Duplicator dup = new Duplicator();
        for (int frame = 1; frame <= imp.getNFrames(); frame++) {
            final int fr = frame;
            final ImagePlus tp = dup.run(imp, 1, imp.getNChannels(), 1, imp.getNSlices(), fr, fr);
            fut.add(new Callable<Entry<Integer, Integer>>() {
                @Override
                public Entry<Integer, Integer> call() throws Exception {
                    int z = findBestFocusStackFromSingleTimepoint(tp, focusChannel);
                    z = Math.max(1, z);
                    return new AbstractMap.SimpleEntry<Integer, Integer>(fr-1,z);
                }
            });
        }

        try {
            List<Future<Entry<Integer, Integer>>> lst = es.invokeAll(fut);
            for (Future<Entry<Integer, Integer>> f : lst) {
                Entry<Integer, Integer> e = f.get();
                bestFocusPlanes[e.getKey()] = e.getValue();
            }
        } catch (Exception e) {
            logger.print(e);
            e.printStackTrace();
            return null;
        }

        return bestFocusPlanes;
    }

    /*
    Method to compute bestFocus Z slices and the indices based on the mean absolute deviation.
    Find the dot product and use it to find eucledian distance
     */
    public static int[] computeBestFocusIndicesBasedOnDotProduct(ImagePlus imp, int focusChannel) {
        int[] bestFocusPlanes = new int[imp.getNFrames()];
        ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        Duplicator dup = new Duplicator();

        ImagePlus rp = dup.run(imp, focusChannel, focusChannel, 1, imp.getNSlices(), 1, 1);
        int refZ = findBestFocusStackFromSingleTimepoint(rp, focusChannel);
        refZ = Math.max(1, refZ);
        bestFocusPlanes[0] = refZ;

        ImagePlus refImp = dup.run(imp, focusChannel, focusChannel, refZ, refZ, 1, 1);

        double refImpEucLen = Math.sqrt(calculateDotProduct(refImp, refImp));

        List<Callable<Entry<Integer, Integer>>> fut = new ArrayList<>();

        for(int frame = 2; frame <= imp.getNFrames(); frame++) {
            final int fr = frame;
            fut.add(new Callable<Entry<Integer, Integer>>() {
                @Override
                public Entry<Integer, Integer> call() throws Exception {
                    double minDist = Double.POSITIVE_INFINITY;
                    int bestIdx = -1;
                    for (int z = 1; z <= imp.getNSlices(); z++) {
                        final ImagePlus calcImp = dup.run(imp, focusChannel, focusChannel, z, z, fr, fr);
                        double dist = findMedianAbsoluteDeviation(refImp, calcImp, refImpEucLen);
                        if (dist < minDist) {
                            bestIdx = z;
                            minDist = dist;
                        }
                    }
                    return new AbstractMap.SimpleEntry<Integer, Integer>(fr - 1, bestIdx);
                    //bestFocusPlanes[frame - 1] = bestIdx;
                }
            });
        }

        try {
            List<Future<Entry<Integer, Integer>>> lst = es.invokeAll(fut);
            for (Future<Entry<Integer, Integer>> f : lst) {
                Entry<Integer, Integer> e = f.get();
                bestFocusPlanes[e.getKey()] = e.getValue();
            }
        } catch (Exception e) {
            logger.print(e);
            e.printStackTrace();
            return null;
        }
        return bestFocusPlanes;
    }

    /*
    Method to find the median absolute deviation for the 2 images using eucledian distance.
     */
    public static double findMedianAbsoluteDeviation(ImagePlus refImp, ImagePlus calcImp,  double refImpEuclLen) {
        double dist = 0.0;
        if(calcImp.getWidth()!=refImp.getWidth() || calcImp.getHeight()!=refImp.getHeight()) throw new IllegalArgumentException("Image dimensions don't match");
        if(calcImp.getStackSize()!=1) throw new IllegalArgumentException("imp image contains more than one plane");
        if(refImp.getStackSize()!=1) throw new IllegalArgumentException("refImp image contains more than one plane");

        short [] refImpArr = (short[])refImp.getProcessor().getPixels();
        short [] calcImpArr = (short[])calcImp.getProcessor().getPixels();

        double calcImpEucLen= Math.sqrt(calculateDotProduct(calcImp, calcImp));
        double [] MAD = new double[refImpArr.length];

        for (int i = 0; i < refImpArr.length; i++) {
            MAD[i] = Math.abs(calcImpArr[i]/calcImpEucLen-refImpArr[i]/refImpEuclLen);
        }
        Arrays.sort(MAD);
        return MAD[MAD.length/2];
    }

    /*
    Calculate dot product between 2 images - reference image and the image to be computed with.
     */
    public static double calculateDotProduct(ImagePlus refImp, ImagePlus calcImp) {
        double dotP = 0.0;

        if(calcImp.getWidth()!=refImp.getWidth() || calcImp.getHeight()!=refImp.getHeight()) throw new IllegalArgumentException("Image dimensions don't match");
        if(calcImp.getStackSize()!=1) throw new IllegalArgumentException("imp image contains more than one plane");
        if(refImp.getStackSize()!=1) throw new IllegalArgumentException("refImp image contains more than one plane");

        short [] refImpArr = (short[])refImp.getProcessor().getPixels();
        short [] calcImpArr = (short[])calcImp.getProcessor().getPixels();

        for (int i = 0; i < refImpArr.length; i++) {
            dotP +=  calcImpArr[i]*refImpArr[i];
        }

        return dotP;
    }

    public static ImagePlus createBestFocusStackFromHyperstack(ImagePlus imp, int[] bestFocusZIndices, int focusChannel) {

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

    public static int findBestFocusStackFromSingleTimepoint(ImagePlus imp, int focusChannel) {
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
                    ImageProcessor cropped = ip.crop();

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

    }
}
