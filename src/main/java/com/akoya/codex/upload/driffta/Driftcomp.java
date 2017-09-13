/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload.driffta;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;
import mpicbg.imglib.algorithm.fft.PhaseCorrelation;
import mpicbg.imglib.image.ImagePlusAdapter;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.akoya.codex.upload.driffta.Driffta.log;

/**
 *
 * @author Nikolay
 */
public class Driftcomp {
    
    public static void compensateDrift(ImagePlus decStacks, final int zeroBasedDriftCompChannel) {
        ImagePlus[][] stacks = new ImagePlus[decStacks.getNFrames()][decStacks.getNChannels()];
        
        Duplicator dup = new Duplicator();
        
        for (int ch = 0; ch < stacks[0].length; ch++) {
            stacks[0][ch] = dup.run(decStacks, ch + 1, ch + 1, 1, decStacks.getNSlices(), 1, 1);
        }
        
        ExecutorService es = Executors.newWorkStealingPool(stacks.length);
        
        for (int i = 1; i < stacks.length; i++) {
            final int idx = i;
            es.execute(() -> {
                System.out.println("Driftcompensating cycle: " + idx);
                for (int ch = 0; ch < stacks[0].length; ch++) {
                    stacks[idx][ch] = dup.run(decStacks, ch + 1, ch + 1, 1, decStacks.getNSlices(), idx + 1, idx + 1);
                }
                int[] shift = computeShift(stacks[0][zeroBasedDriftCompChannel], stacks[idx][zeroBasedDriftCompChannel]);
                for (int ch = 0; ch < stacks[0].length; ch++) {
                    stacks[idx][ch] = applyShift3D(shift, stacks[idx][ch]);
                    for (int slice = 1; slice <= stacks[idx][ch].getNSlices(); slice++) {
                       decStacks.getStack().setProcessor(stacks[idx][ch].getStack().getProcessor(slice), decStacks.getStackIndex(ch+1, slice, idx+1));
                    }
                }
            });
        }
        
        es.shutdown();
        try{
            es.awaitTermination(1, TimeUnit.DAYS);
        }catch(InterruptedException e){
            log(e.getMessage());
        }
    }
    
    private static int[] computeShift(ImagePlus imp1, ImagePlus imp2) {
        imp1 = new ImagePlus(imp1.getTitle()+"crop", imp1.getImageStack().crop(imp1.getWidth()/4, imp1.getHeight()/4,0, imp1.getWidth()/2,imp1.getWidth()/2, imp1.getStackSize()));
        imp2 = new ImagePlus(imp2.getTitle()+"crop", imp2.getImageStack().crop(imp2.getWidth()/4, imp2.getHeight()/4,0, imp2.getWidth()/2,imp2.getWidth()/2, imp2.getStackSize()));
        PhaseCorrelation phc = new PhaseCorrelation(ImagePlusAdapter.wrap(imp1), ImagePlusAdapter.wrap(imp2), 1, true);
        phc.setNumThreads(Runtime.getRuntime().availableProcessors());
        phc.setComputeFFTinParalell(true);
        phc.process();
        int[] p = phc.getShift().getPosition();
        log("Phase corr: " + Arrays.toString(p));
        return p;
    }
    
    private static ImagePlus applyShift3D(int[] shift, ImagePlus imp) {
        
        ImageStack out = new ImageStack(imp.getWidth(), imp.getHeight(), imp.getProcessor().getColorModel());
        
        int absShift = Math.min(Math.abs(shift[2]), (imp.getNSlices() / 2 + 1));
        
        shift[2] = (int) (absShift * Math.signum(shift[2]));
        
        int slicesToAddAtTheBeginning = Math.max(0, shift[2]);
        
        for (int i = 0; i < slicesToAddAtTheBeginning; i++) {
            ImageProcessor ip = imp.getImageStack().getProcessor(1);
            ImageProcessor ip2 = ip.createProcessor(imp.getWidth(), imp.getHeight());
            out.addSlice(ip2);
        }
        
        int startingSlice = Math.max(0, -shift[2]);
        for (int slice = startingSlice + 1; slice <= imp.getNSlices(); slice++) {
            ImageProcessor ip = imp.getImageStack().getProcessor(slice);
            ImageProcessor ip2 = ip.createProcessor(imp.getWidth(), imp.getHeight());
            ip2.insert(ip, shift[0], shift[1]);
            out.addSlice(ip2);
            if (out.size() == imp.getNSlices()) {
                break;
            }
        }
        
        int slicesToAdd = imp.getNSlices() - out.size();
        
        for (int i = 0; i < slicesToAdd; i++) {
            ImageProcessor ip = imp.getImageStack().getProcessor(1);
            ImageProcessor ip2 = ip.createProcessor(imp.getWidth(), imp.getHeight());
            out.addSlice(ip2);
        }

        //ImagePlus dc = new ImagePlus("After Driftcomp", out);
        //imp.show();
        //dc.show();
        ImagePlus dc = new ImagePlus("After Driftcomp", out);
        System.out.println("Driftcomp.applyShift3D in Z=" + imp.getNSlices() + " out Z=" + dc.getNSlices());
        return dc;
        
    }
    
}
