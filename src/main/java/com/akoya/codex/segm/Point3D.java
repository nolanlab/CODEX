/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import java.awt.*;

/**
 *
 * @author Nikolay
 */
public class Point3D {

    final int x, y, z;
    double intensity;
    Color color;

    public Point3D(int x, int y, int z, double intensity, Color c) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.intensity = intensity;
        this.color = c;
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "," + z + "}";
    }

    @Override
    public int hashCode() {
        return (String.valueOf(x) + String.valueOf(y) + String.valueOf(z)).hashCode();
    }

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.intensity = 0;
        this.color = Color.BLACK;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Point3D) && ((Point3D) obj).x == this.x && ((Point3D) obj).y == this.y && ((Point3D) obj).z == this.z;
    }

}
