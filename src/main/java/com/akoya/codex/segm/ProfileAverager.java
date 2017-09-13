/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import java.util.Arrays;

/**
 *
 * @author Nikolay
 */
public class ProfileAverager {

    double[] avg = null;
    double count = 0;

    public double getCount() {
        return count;
    }

    
    
    
    public ProfileAverager() {
    }

    public double[] getAverage() {
        if (count == 0) {
            throw new IllegalStateException("No profiles have been added to this averager");
        }
        double[] res = Arrays.copyOf(avg, avg.length);
        MatrixOp.mult(res, 1.0 / count);
        return res;
    }

    public double[] getAverageUnityLen() {
        if (count == 0) {
            throw new IllegalStateException("No profiles have been added to this averager");
        }
        double[] res = getAverage();
        return MatrixOp.toUnityLen(res);
    }

    public void addProfile(double[] vec) {
        if (vec == null) {
            return;
        }
        if (avg == null) {
            avg = Arrays.copyOf(vec, vec.length);
        } else {
            if (vec.length != avg.length) {
                throw new IllegalArgumentException("the vector size " + vec.length + " doesn't match the required size of " + vec.length);
            }
            avg = MatrixOp.sum(avg, vec);
        }
        count++;
    }

    public void addProfile(double[] vec, double weight) {
        if (vec == null) {
            return;
        }
        if (avg == null) {
            avg = Arrays.copyOf(vec, vec.length);
            MatrixOp.mult(avg, weight);
        } else {
            if (vec.length != avg.length) {
                throw new IllegalArgumentException("the vector size " + vec.length + " doesn't match the required size of " + vec.length);
            }
            double[] vec2 = Arrays.copyOf(vec, vec.length);
            MatrixOp.mult(vec2, weight);
            avg = MatrixOp.sum(avg, vec2);
        }
        count += weight;
    }
}
