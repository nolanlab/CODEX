package com.akoya.codex.upload.driffta;

import com.microvolution.DeconvolutionLauncher;
import com.microvolution.dispatch.DeconTask;
import ij.ImagePlus;
import ij.process.FloatProcessor;

import static com.akoya.codex.upload.driffta.Driffta.log;

public class DeconvolutionTask extends DeconTask {

    // Any necessary start up, like loading file from disk
    @Override
    public void start() {
        System.out.println("starting");
    }

    private final ImagePlus imp;
    private final ImagePlus ret;

    private final int frame;
    private final int channel;
    private float prevSumIntensities = 0;

    public DeconvolutionTask(ImagePlus singleStack, int frame, int channel, ImagePlus returnStack) {
        this.imp = singleStack;
        this.frame = frame;
        this.channel = channel;
        this.ret = returnStack;
    }

    // Transfer data to launcher
    @Override
    public void toLauncher(DeconvolutionLauncher launcher) {
        log("Frame#" + frame + ", ch#" + channel + " loading data");
        prevSumIntensities = 0;
        for (int i = 0; i < params.nz(); i++) {
            float[][] pix = ((imp != null) ? imp.getImageStack().getProcessor(i + 1) : ret.getImageStack().getProcessor(ret.getStackIndex(channel, i + 1, frame))).getFloatArray();
            float[] data = new float[(int) (params.nx() * params.ny())];
            int cnt = 0;
            for (int y = 0; y < pix[0].length; y++) {
                for (int x = 0; x < pix.length; x++) {
                    data[cnt++] = pix[x][y];
                    prevSumIntensities += pix[x][y];
                }

            }
            launcher.SetImageSlice(i, data);
        }
        log("Frame#" + frame + ", ch#" + channel + " starting deconvolution");
    }

    // Copy back from launcher
    @Override
    public void fromLauncher(DeconvolutionLauncher launcher) {
        log("Frame#" + frame + ", ch#" + channel + " ready");
        float[][] slices = new float[(int) params.nz()][];
        float sum = 0;
        
        for (int i = 0; i < params.nz(); i++) {
            float[] data = new float[(int) (params.nx() * params.ny())];
            launcher.RetrieveImageSlice(i, data);
            for (float f : data) {
                sum += f;
            }
            slices[i] = data;
        }

        float scaling = (prevSumIntensities / sum)/2.0f;
        
        log("scaling: " + scaling);

        for (int i = 0; i < params.nz(); i++) {
            for (int j = 0; j < slices[i].length; j++) {
                slices[i][j] *= scaling;
            }
            ret.getStack().setProcessor(new FloatProcessor((int) params.nx(), (int) params.ny(), slices[i], ret.getImageStack().getProcessor(ret.getStackIndex(channel, i + 1, frame)).getColorModel()).convertToShortProcessor(false), ret.getStackIndex(channel, i + 1, frame));
        }

    }

    // Finish up, eg save to disk
    @Override
    public void finish() {
        System.out.println("finishing");
    }
}
