/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

/**
 *
 * @author Nikolay
 */
public class Cell {

    private Region region;
    private int id;
    private double[] expressionVector;
    private int tile;
    private double[] neighFeaturizationVec;

    public Cell(int id, Region region, int tile, double[] expressionVector, double[] neighFeaturizationVec) {
        this.region = region;
        this.id = id;
        this.expressionVector = expressionVector;
        this.tile = tile;
        this.neighFeaturizationVec = neighFeaturizationVec;
    }

    public double[] getExpressionVector() {
        return expressionVector;
    }

    public double[] getNeighFeaturizationVec() {
        return neighFeaturizationVec;
    }
    
    

    public int getTile() {
        return tile;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }
        return this.id == ((Cell) obj).id;
    }

}
