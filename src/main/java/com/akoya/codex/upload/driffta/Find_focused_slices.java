
package com.akoya.codex.upload.driffta;

import ij.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.filter.*;
import ij.process.*;

import java.awt.*;


public class Find_focused_slices {

    ImagePlus imp;
    boolean abort = false;
    double percent, vThr;
    boolean consecutive, verbose, edge ;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return PlugInFilter.DOES_ALL;
    }

    public int run(ImageProcessor ip) {
        
        if(imp.isHyperStack()){
        	IJ.error("HyperStack is not supported.\nPlease split channels or time frames\nthen do the find focus seperately");
            return -1;
        }
        
        ImageStack stack = imp.getStack();
        int width = imp.getWidth();
        int height = imp.getHeight();
        String name = imp.getTitle();
        ImageStack stack2 = new ImageStack(width, height, imp.getProcessor().getColorModel());
        int fS = 0;

        int size = stack.getSize();
        if (size == 1){
        	IJ.error("Stack required.");
            return -1;
        }

        double vMax = 0;
        double[] varA = new double[size];

        if (!getParam()) {
            return-1;
        }

        if (verbose) {
            IJ.log("\n" + "Processing: " + name);
        }
        for (int slice = 1; slice <= size; slice++) {
            imp.setSlice(slice);
            IJ.showStatus(" " + slice + "/" + size);
            ip = imp.getProcessor();
            varA[slice - 1] = calVar(ip);
            if (verbose) {
                IJ.log("Slice: " + slice + "\t\t Variance: " + varA[slice - 1]);
            }
            if (varA[slice - 1] > vMax) {
                vMax = varA[slice - 1];
                fS = slice;
            }

        }
        if (vMax < vThr) {
            IJ.error("All slices are below the variance threshold value");
            return-1;
        }
        if (verbose) {
            IJ.log("Slices selected: ");
        }
		
	
        return fS;

    }

    double calVar(ImageProcessor ip) {

        double variance = 0;
        int W = ip.getWidth();
        int H = ip.getHeight();

        Rectangle r = ip.getRoi();
        if (r == null) {
            r.x = 0;
            r.y = 0;
            r.height = H;
            r.width = W;
        }
        ImageProcessor edged = ip.duplicate();
        if (edge) edged.findEdges();
        double mean = ImageStatistics.getStatistics(edged, Measurements.MEAN, null).mean;
        double a = 0;
        for (int y = r.y; y < (r.y + r.height); y++) {
            for (int x = r.x; x < (r.x + r.width); x++) {
                a += Math.pow(edged.getPixel(x, y) - mean, 2);
            }
        }
        variance = (1 / (W * H * mean)) * a;
        return variance;

    }

    private boolean getParam() {
        /*GenericDialog gd = new GenericDialog("Find focused slices", IJ.getInstance());

        gd.addNumericField("Select images with at least", 80, 1, 4, "% of maximum variance.");
        gd.addNumericField("Variance threshold: ", 0, 3);
        gd.addCheckbox("Edge filter?", false);
        gd.addCheckbox("Select_only consecutive slices?", false);
        gd.addCheckbox("verbose mode?", true);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }*/

        percent = 100;//gd.getNextNumber();
        vThr = 0;//gd.getNextNumber();
        edge = true;//gd.getNextBoolean();
        consecutive = false;//gd.getNextBoolean();
        verbose = false;//gd.getNextBoolean();


        return true;

    }
}
