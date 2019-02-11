/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.segm;

/**
 *
 * @author Nikolay
 */
public class Cell {

    private SegmentedObject segmentedObject;
    private int id;
    private double[] expressionVector;
    private int tile;
    private double[] neighFeaturizationVec;

    public Cell(int id, SegmentedObject segmentedObject, int tile, double[] expressionVector, double[] neighFeaturizationVec) {
        this.segmentedObject = segmentedObject;
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

    public SegmentedObject getSegmentedObject() {
        return segmentedObject;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }
        return this.id == ((Cell) obj).id;
    }

}
