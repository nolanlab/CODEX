/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload.driffta;

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

/**
 *
 * @author Nikolay
 */
public class Driftcomp {
    
    public static void compensateDrift(ImagePlus decStacks, final int zeroBasedDriftCompChannel, int zeroBasedReferenceCycle) {
        ImagePlus[][] stacks = new ImagePlus[decStacks.getNFrames()][decStacks.getNChannels()];
        Duplicator dup = new Duplicator();

        for (int i = 0; i < decStacks.getNFrames(); i++) {
            for (int ch = 0; ch < stacks[i].length; ch++) {
                stacks[i][ch] = dup.run(decStacks, ch + 1, ch + 1, 1, decStacks.getNSlices(), i+1, i+1);
            }

        }

        ExecutorService es = Executors.newWorkStealingPool(stacks.length);

        for (int i = 0; i < stacks.length; i++) {

            if(i == zeroBasedReferenceCycle) {
                continue;
            }

            final int idx = i;
            es.execute(() -> {
                System.out.println("Driftcompensating cycle: " + idx);
                int[] shift = computeShift(stacks[zeroBasedReferenceCycle][zeroBasedDriftCompChannel], stacks[idx][zeroBasedDriftCompChannel], idx);

                for (int ch = 0; ch < stacks[idx].length; ch++) {
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
            Driffta.log(e.getMessage());
        }
    }
    
    private static int[] computeShift(ImagePlus imp1, ImagePlus imp2, int idx) {
        PhaseCorrelation phc = new PhaseCorrelation(ImagePlusAdapter.wrap(imp1), ImagePlusAdapter.wrap(imp2), 1, false);

        phc.setNumThreads(Runtime.getRuntime().availableProcessors());
        phc.setComputeFFTinParalell(true);
        phc.process();
        int[] p = phc.getShift().getPosition();
        Driffta.log("Cycle: " + idx + " Phase correlation: " + Arrays.toString(p));
        return p;
    }
    
    private static ImagePlus applyShift3D(int[] shift, ImagePlus imp) {
        
        ImageStack out = new ImageStack(imp.getWidth(), imp.getHeight(), imp.getProcessor().getColorModel());
        
        int absShift = Math.min(Math.abs(shift[2]), (imp.getNSlices() / 2 + 1));
        
        shift[2] = (int) (absShift * Math.signum(shift[2]));
        
        int slicesToAddAtTheBeginning = Math.max(0, shift[2]);

        for (int i = 0; i < slicesToAddAtTheBeginning; i++) {
 //         here we get  a copy of first slice of non-deconvolved frame
            ImageProcessor ip = imp.getImageStack().getProcessor(1);
//          here we create a new blank processor with same dimensions as the frame
            ImageProcessor ip2 = ip.createProcessor(imp.getWidth(), imp.getHeight());
//          here we insert into the blank processor the first slice with a shift
            ip2.insert(ip, shift[0], shift[1]);
            out.addSlice(ip2);
        }

        // XY drift compensation happens here - ip2 is a single slice where we insert ip with x-shift[0] and y-shift[1]
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
//            ImageProcessor ip = imp.getImageStack().getProcessor(1);
//         here we get  a copy of the last slice of non-deconvolved frame
            ImageProcessor ip = imp.getImageStack().getProcessor(imp.getNSlices());
            ImageProcessor ip2 = ip.createProcessor(imp.getWidth(), imp.getHeight());
            ip2.insert(ip, shift[0], shift[1]);
            out.addSlice(ip2);
        }

        //ImagePlus dc = new ImagePlus("After Driftcomp", out);
        //imp.show();
        //dc.show();
        ImagePlus dc = new ImagePlus("After Driftcomp", out);
        //System.out.println("Driftcomp.applyShift3D in Z=" + imp.getNSlices() + " out Z=" + dc.getNSlices());
        return dc;
    }
}
