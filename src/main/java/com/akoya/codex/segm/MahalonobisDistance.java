/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

import java.util.Arrays;

/**
 *
 * @author Zina
 */
public class MahalonobisDistance {

    public DenseDoubleMatrix2D covMtx;
    public DenseDoubleMatrix1D center;
    private DoubleMatrix2D invCovMtx;

    public MahalonobisDistance(DenseDoubleMatrix1D center, DenseDoubleMatrix2D covMtx) {
        this.covMtx = covMtx;
        this.center = center;
        this.invCovMtx = DenseDoubleAlgebra.DEFAULT.inverse(covMtx);
    }

    @Override
    public String toString() {
        return "Center: " + center.toString() + "\nCovMtx\n"
                + covMtx.toString();
    }

    public MahalonobisDistance(SegmentedObject reg) {

        double[][] dataTable = new double[reg.getPoints().length][];

        for (int i = 0; i < reg.getPoints().length; i++) {
            dataTable[i] = new double[]{reg.getPoints()[i].x, reg.getPoints()[i].y, reg.getPoints()[i].z};
        }

        center = new DenseDoubleMatrix1D(new double[]{reg.getCenter().x, reg.getCenter().y, reg.getCenter().z});
        covMtx = new DenseDoubleMatrix2D(CovarianceMatrix.covarianceMatrix(dataTable));

        //Compute covariance matrix of the cluster
        //Take the inverse cov matrix
        try {
            invCovMtx = DenseDoubleAlgebra.DEFAULT.inverse(covMtx);
        } catch (IllegalArgumentException e) {
            System.out.println("Singular matrix. Using identity matrix instead");
            invCovMtx = new DenseDoubleMatrix2D(3, 3);
            for (int i = 0; i < 3; i++) {
                invCovMtx.setQuick(i, i, 1);
            }
        }
    }

    public MahalonobisDistance(double[][] dataTable) {
        // logger.print("init MD");
        //Compute the center of the cluster

        double[] vec = Arrays.copyOf(dataTable[0], dataTable[0].length);
        for (int i = 1; i < dataTable.length; i++) {
            double[] vec2 = dataTable[i];
            for (int dim = 0; dim < vec2.length; dim++) {
                vec[dim] += vec2[dim];
            }
        }

        for (int dim = 0; dim < vec.length; dim++) {
            vec[dim] /= (double) dataTable.length;
        }
        center = new DenseDoubleMatrix1D(vec);

        covMtx = new DenseDoubleMatrix2D(CovarianceMatrix.covarianceMatrix(dataTable));

        invCovMtx = DenseDoubleAlgebra.DEFAULT.inverse(covMtx);
    }

    public double distTo(double[] x) {
        DenseDoubleMatrix1D diff = new DenseDoubleMatrix1D(x.length);
        for (int i = 0; i < x.length; i++) {
            diff.setQuick(i, x[i] - center.getQuick(i));
        }
        double dist = DenseDoubleAlgebra.DEFAULT.mult(diff, DenseDoubleAlgebra.DEFAULT.mult(invCovMtx, diff));
        return Math.sqrt(dist);
    }
}
