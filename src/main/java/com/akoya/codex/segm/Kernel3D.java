/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import ij.ImageStack;

/**
 *
 * @author Nikolay
 */
public class Kernel3D {

    double[][][] weightMatrix;
    int[] center;
    int w, d, h;

    public Kernel3D(int w, int h, int d) {
        this.w = w;
        this.d = d;
        this.h = h;
        weightMatrix = new double[w][h][d];

        center = new int[]{w / 2, h / 2, d / 2};
        double[] sigmas = new double[]{w / 6.0, h / 6.0, d / 6.0};
        for (int z = 0; z < d; z++) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    double sqdist = Math.pow((x - center[0]) / sigmas[0], 2) + Math.pow((y - center[1]) / sigmas[1], 2) + Math.pow((z - center[2]) / sigmas[2], 2);
                    weightMatrix[x][y][z] = sqdist < 9 ? (Math.exp(-sqdist / 2.0)) : 0;// + (Math.exp(-Math.abs(sqdist) / 2.0))*0.3;
                }
            }
        }
    }

    public int[] getWeightedAverage(final ImageStack in, int pointX, int pointY, int pointZ) {
        double[] wAvg = new double[3];
        double sumWeights = 0;

        for (int kX = 0; kX < w; kX++) {
            for (int kY = 0; kY < h; kY++) {
                for (int kZ = 0; kZ < d; kZ++) {

                    int xpos = (kX - center[0]) + pointX;
                    int ypos = (kY - center[1]) + pointY;
                    int zpos = (kZ - center[2]) + pointZ;

                    if (0 <= xpos && xpos < in.getWidth() && 0 <= ypos && ypos < in.getHeight() && 0 <= zpos && zpos < in.size()) {
                        double weight = in.getVoxel(xpos, ypos, zpos) * weightMatrix[kX][kY][kZ];
                        wAvg[0] += kX * weight;
                        wAvg[1] += kY * weight;
                        wAvg[2] += kZ * weight;
                        sumWeights += weight;
                    }
                }
            }
        }
        int xpos = (((int) (wAvg[0] / sumWeights)) - center[0]) + pointX;
        int ypos = (((int) (wAvg[1] / sumWeights)) - center[1]) + pointY;
        int zpos = (((int) (wAvg[2] / sumWeights)) - center[2]) + pointZ;

        xpos = Math.max(0, Math.min(xpos, w - 1));
        ypos = Math.max(0, Math.min(ypos, w - 1));
        zpos = Math.max(0, Math.min(zpos, w - 1));

        return new int[]{xpos, ypos, zpos, (int) sumWeights};
    }

}
