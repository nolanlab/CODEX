/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.segm;

import java.awt.*;

/**
 *
 * @author Nikolay
 */
public class SegmentedObject {

    private final Point3D center;
    private final Point3D[] regionPts;
    private Point3D[] hull;
    private Color c;
    private double intensity;

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setC(Color c) {
        this.c = c;
    }

    public Point3D getCenter() {
        return center;
    }

    public Point3D[] getHull() {
        if (hull == null) {
            computeHull();
        }
        return hull;
    }

    private void computeHull() {

    }

    public Color getColor() {
        return c;
    }

    public Point3D[] getPoints() {
        return regionPts;
    }

    public SegmentedObject(Point3D center, Point3D[] regionPts) {
        this.center = center;
        this.regionPts = regionPts;
        c = new Color(100 + (int) (Math.random() * 155), 100 + (int) (Math.random() * 155), 100 + (int) (Math.random() * 155));
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(center.toString());
        sb.append("\t");

        for (Point3D regionPt : regionPts) {
            sb.append(regionPt.toString());
        }

        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
}
