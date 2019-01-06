/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload.driffta;

import com.microvolution.DeconParameters;
import com.microvolution.PSFModel;
import com.microvolution.PreFilter;
import com.microvolution.Scaling;
import com.microvolution.dispatch.DeconvolutionDispatch;
import ij.ImagePlus;
import ij.plugin.Duplicator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Nikolay
 */

public class Deconvolve_mult {

    private final boolean disableDecon;

    final DeconvolutionDispatch dispatch;
    final boolean blind;

    final int numDevices;

    public Deconvolve_mult(boolean disableDeconvolution, int numDevices, boolean blind) {
        
        this.numDevices = Math.max(1, numDevices);
        disableDecon = disableDeconvolution;
        int[] dev = new int[this.numDevices];
        for (int i = 0; i < dev.length; i++) {
            dev[i] = i;
        }
        this.blind = blind;
        Driffta.log("Creating deconvolution dispatcher with "+ numDevices + " GPU devices");
       if(!disableDecon){
           dispatch = DeconvolutionDispatch.GetInstance();
           dispatch.setDevices(dev);
       }else{
           dispatch = null;
       }
        
    }

    public void runDeconvolution(ImagePlus stack, double XYres, double zPitch, int[] wavelengths, int[][] iterationMap, final int zeroBasedDriftCompChannel, double ObjectiveNA, double ObjectiveRI) throws Exception {
        
        try {
            for (int frame = 1; frame <= stack.getNFrames(); frame++) {
                for (int ch = 1; ch <= stack.getNChannels(); ch++) {
                    int iterations = iterationMap[frame - 1][ch - 1];
                    if (iterations == 0 || disableDecon) {
                        Driffta.log("Skipping deconvolution ch:" + ch + " frame:" + frame + "iterations");
                    } else {
                        
                        final ImagePlus dup = new Duplicator().run(stack, ch, ch, 1, stack.getNSlices(), frame, frame);
                        
                        DeconvolutionTask task = new DeconvolutionTask(dup, frame, ch, stack);
                        // Make sure params have been set, either here or inside task itself. Otherwise, expect exceptions thrown.
                        DeconParameters params = new DeconParameters();
                        params.scaling(Scaling.U16);
                        params.background(0);
                        params.preFilter(PreFilter.None);
                        params.generatePsf(true);
                        params.psfModel(PSFModel.Vectorial);
                        params.NA(ObjectiveNA);
                        params.RI(ObjectiveRI);
                        params.ns(1.33);
                        params.dr(XYres);
                        params.dz(zPitch);
                        params.blind(blind);
                        params.xPadding(10);
                        params.yPadding(10);
                        params.zPadding(3);
                        params.tiles(0, 0, 0);
                        params.lambda(wavelengths[ch - 1]);
                        params.iterations(iterations);
                        params.nx(stack.getWidth());
                        params.ny(stack.getHeight());
                        params.nz(stack.getNSlices());
                        params.scaling(com.microvolution.Scaling.U16);

                        task.setParams(params);
                        // Add to queue
                        Driffta.log("Dispatching deconvolution: XYres:" + XYres + ", zPitch:" + zPitch + " ch:" + ch + " frame:" + frame + " iterations" + iterations);
                        dispatch.addTask(task);
                    }
                }
            }
            Driffta.log("Waiting for task to finish");
            
            Runnable r = new Runnable() {
                @Override
                public void run()  {
                     try{
                         dispatch.waitFinished();
                         dispatch.shutdown();
                     }catch(InterruptedException e){
                         e.printStackTrace();
                     }
                }
            };
            
            ExecutorService es = Executors.newSingleThreadExecutor();
            
            es.submit(r);
            es.shutdown();
            es.awaitTermination(5, TimeUnit.MINUTES);
            
           
            
            
        } catch (Exception e) {
            Driffta.log("Fatal exception in Deconvolve.java: " + e.getMessage() + "\n" + e.getStackTrace()[0].toString());
            dispatch.shutdown();
            throw e;
        } finally {

            if (dispatch != null) {
                dispatch.shutdown();
            }

        }

    }

}
