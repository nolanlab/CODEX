/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.segm;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

import ij.ImagePlus;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author Nikolay
 */
public class GMM {

    /**
     *
     * @param in - denoised image stack to be segmented
     * @param radius - search radius for local maxima
     * @param maxCutoff - pixels above or equal to this intensity won't be
     * considered. intensity is relative to the dynamic range of the image
     * (2^bits_per_pixel). Possible range of values: [0,1] Default: 0.99
     * @param minCutoff - below above or equal to this intensity won't be
     * considered. intensity is relative to the dynamic range of the image
     * (2^bits_per_pixel). Possible range of values: [0,1]. Default: 0.1
     * @param relativeCutoff - Limits the size of the region by defining the
     * high-pass cutoff for the pixel intensity, relative to the intensity of
     * the local maximum. Default: 0.4
     * @param showImage - flag that controls whether the segmentation result
     * will be shown. Setting it to zero makes things faster
     * @return
     */
    public static SegmentedObject[] findRegionsByIntensityGradient(final ImagePlus in, int radius, double maxCutoff, double minCutoff, double relativeCutoff, boolean showImage, final double nuclMaskCutoff, final int MAX_OPT_CYCLES) {
        final int w = in.getWidth();
        final int h = in.getHeight();
        final int d = in.getStackSize();
       

        int initNumCells = Math.max(10, (int) ((w * h) / (Math.PI * (radius * radius)))); //Assuming that the tile is completely occupied by cells;

        int cellsPerRow = w / radius;
        int cellsPerCol = h / radius;

        int stepX = w / cellsPerRow;
        int stepY = h / cellsPerCol;

        Set<CellBubble> bubbli = new ConcurrentSkipListSet<>();

        for (int i = 0; i < initNumCells; i++) {

            int x = (i % cellsPerRow) * stepX;
            int y = (i / cellsPerRow) * stepY;
            
            DenseDoubleMatrix2D cov = new DenseDoubleMatrix2D(3, 3);
            
            for (int j = 0; j < 3; j++) {
               cov.setQuick(j, j, 1);
            }
            
            bubbli.add(new CellBubble(new DenseDoubleMatrix1D(new double[]{x, y, d/2.0}),  cov));
            
        }
        
        //Now, optimization
        
        

        //init CMs: radius, intensity
        //if CM intensity drops below a threshold, eliminate it from the model
        //merge CMs based on midpoint density
        //likelihood is calculated on max, not sum (non-overlapping  clustering)
        //overall model fitness is calculated using a residue, not likelihood (better penatry for unaccounted intensity)
        //membrane intensity on all channels is taken into the penalty
        //System.out.println("Filtered maxima: " + maxima.length);
        return null;//out.toArray(new SegmentedObject[out.size()]);
    }

    private static class CellBubble {

        public DenseDoubleMatrix2D covMtx;
        public DenseDoubleMatrix1D center;
        private DoubleMatrix2D invCovMtx;

        double wIntensity;

        public CellBubble(DenseDoubleMatrix1D center, DenseDoubleMatrix2D covMtx) {

            this.covMtx = covMtx;
            this.center = center;
            this.invCovMtx = Algebra.DEFAULT.inverse(covMtx);
        }

        public double distTo(double[] x) {
            DenseDoubleMatrix1D diff = new DenseDoubleMatrix1D(x.length);
            for (int i = 0; i < x.length; i++) {
                diff.setQuick(i, x[i] - center.getQuick(i));
            }
            double dist = Algebra.DEFAULT.mult(diff, Algebra.DEFAULT.mult(invCovMtx, diff));
            return Math.sqrt(dist);
        }

    }

}
