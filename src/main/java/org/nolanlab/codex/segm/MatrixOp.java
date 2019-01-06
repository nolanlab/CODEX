/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.segm;

import java.util.Arrays;

/**
 *
 * @author Nikolay
 */
public class MatrixOp {

    public static double mult(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors differ in length");
        }
        double prod = 0;
        for (int x = 0; x < vec1.length; x++) {
            prod += vec1[x] * vec2[x];
        }
        return prod;
    }

    public static double[] subset(double[] source, int[] selectedIdx) {
        double[] res = new double[selectedIdx.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = source[selectedIdx[i]];
        }
        return res;
    }

    public static double[] concat(double[] vec1, double[] vec2) {
        if (vec2 == null) {
            return vec1;
        }
        if (vec1 == null) {
            return vec2;
        }
        double[] res = Arrays.copyOf(vec1, vec1.length + vec2.length);
        for (int i = 0; i < vec2.length; i++) {
            res[i + vec1.length] = vec2[i];
        }
        return res;
    }

    public static double getEuclideanDistance(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors differ in length");
        }
        double prod = 0;
        for (int x = 0; x < vec1.length; x++) {
            prod += (vec1[x] - vec2[x]) * (vec1[x] - vec2[x]);
        }
        return Math.sqrt(prod);
    }

    public static double getEuclideanCosine(double[] vec1, double[] vec2) {
        return Math.min(1.0, mult(vec1, vec2) / (lenght(vec1) * lenght(vec2)));
    }

    public static double[] copy(double[] vec) {
        if (vec == null) {
            return null;
        }
        return Arrays.copyOf(vec, vec.length);
    }

    public static void mult(double[] vec, double val) {
        for (int i = 0; i < vec.length; i++) {
            vec[i] *= val;
        }
    }

    public static void mult(double[][] mtx, double[] vec, double[] prod) {
        int col = vec.length;
        int row = mtx.length;
        for (int r = 0; r < row; r++) {
            prod[r] = 0;
            for (int c = 0; c < col; c++) {
                prod[r] += mtx[r][c] * vec[c];
            }
        }
    }

    public static double[] mult(double[][] mtx, double[] vec) {
        int col = vec.length;
        int row = mtx.length;
        double[] prod = new double[row];
        for (int r = 0; r < row; r++) {
            prod[r] = 0;
            for (int c = 0; c < col; c++) {
                prod[r] += mtx[r][c] * vec[c];
            }
        }
        return prod;
    }

    public static void mult(double[] vec, double val, double[] res) {
        for (int i = 0; i < vec.length; i++) {
            res[i] = vec[i] * val;
        }
    }

    public static double lenght(double vec[]) {
        return Math.sqrt(mult(vec, vec));
    }

    public static double[] toUnityLen(double[] vec) {
        double[] result = new double[vec.length];
        double len = lenght(vec);
        for (int i = 0; i < result.length; i++) {
            result[i] = vec[i] / len;
        }
        return result;
    }

    /**
     * @return x1+x2
     */
    public static double[] sum(double[] vec1, double[] vec2) {
        double[] result = copy(vec1);
        for (int x = 0; x < vec1.length; x++) {
            result[x] += vec2[x];
        }
        return result;
    }

    /**
     * @return x1-x2
     */
    public static double[] diff(double[] vec1, double[] vec2) {
        double[] result = copy(vec1);
        for (int x = 0; x < vec1.length; x++) {
            result[x] -= vec2[x];
        }
        return result;
    }
}
