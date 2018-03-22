package com.akoya.codex.segm;

import java.io.File;

public class SegConfigParam {
    File rootDir = null;
    File config = null;
    boolean use_membrane = false;
    double maxCutoff = 0.99;
    double minCutoff = 0.01;
    double relativeCutoff = 0.4;
    int nuclearStainChannel = -1;
    int nuclearStainCycle = -1;
    int membraneStainChannel = -1;
    int membraneStainCycle = -1;
    int radius = 2;
    boolean count_puncta = false;
    double inner_ring_size = 0.6;
    int[] readoutChannels = null;
    //this variable enables subtraction of the inner ring average
    boolean subtractInnerRing = false;
    //eto to
    boolean showImage = false;
    boolean dont_inverse_memb = false;
    boolean delaunay_graph = true;
    int concentricCircles = 0;


    public File getRootDir() {
        return rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public File getConfig() {
        return config;
    }

    public void setConfig(File config) {
        this.config = config;
    }

    public boolean isUse_membrane() {
        return use_membrane;
    }

    public void setUse_membrane(boolean use_membrane) {
        this.use_membrane = use_membrane;
    }

    public double getMaxCutoff() {
        return maxCutoff;
    }

    public void setMaxCutoff(double maxCutoff) {
        this.maxCutoff = maxCutoff;
    }

    public double getMinCutoff() {
        return minCutoff;
    }

    public void setMinCutoff(double minCutoff) {
        this.minCutoff = minCutoff;
    }

    public double getRelativeCutoff() {
        return relativeCutoff;
    }

    public void setRelativeCutoff(double relativeCutoff) {
        this.relativeCutoff = relativeCutoff;
    }

    public int getNuclearStainChannel() {
        return nuclearStainChannel;
    }

    public void setNuclearStainChannel(int nuclearStainChannel) {
        this.nuclearStainChannel = nuclearStainChannel;
    }

    public int getNuclearStainCycle() {
        return nuclearStainCycle;
    }

    public void setNuclearStainCycle(int nuclearStainCycle) {
        this.nuclearStainCycle = nuclearStainCycle;
    }

    public int getMembraneStainChannel() {
        return membraneStainChannel;
    }

    public void setMembraneStainChannel(int membraneStainChannel) {
        this.membraneStainChannel = membraneStainChannel;
    }

    public int getMembraneStainCycle() {
        return membraneStainCycle;
    }

    public void setMembraneStainCycle(int membraneStainCycle) {
        this.membraneStainCycle = membraneStainCycle;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isCount_puncta() {
        return count_puncta;
    }

    public void setCount_puncta(boolean count_puncta) {
        this.count_puncta = count_puncta;
    }

    public double getInner_ring_size() {
        return inner_ring_size;
    }

    public void setInner_ring_size(double inner_ring_size) {
        this.inner_ring_size = inner_ring_size;
    }

    public int[] getReadoutChannels() {
        return readoutChannels;
    }

    public void setReadoutChannels(int[] readoutChannels) {
        this.readoutChannels = readoutChannels;
    }

    public boolean isSubtractInnerRing() {
        return subtractInnerRing;
    }

    public void setSubtractInnerRing(boolean subtractInnerRing) {
        this.subtractInnerRing = subtractInnerRing;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public boolean isDont_inverse_memb() {
        return dont_inverse_memb;
    }

    public void setDont_inverse_memb(boolean dont_inverse_memb) {
        this.dont_inverse_memb = dont_inverse_memb;
    }

    public boolean isDelaunay_graph() {
        return delaunay_graph;
    }

    public void setDelaunay_graph(boolean delaunay_graph) {
        this.delaunay_graph = delaunay_graph;
    }

    public int getConcentricCircles() {
        return concentricCircles;
    }

    public void setConcentricCircles(int concentricCircles) {
        this.concentricCircles = concentricCircles;
    }
}
