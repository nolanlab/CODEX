package org.nolanlab.codex.segm;

import java.io.File;

public class SegConfigParam {
    private File rootDir = null;
    private File config = null;
    private boolean use_membrane = false;
    private double maxCutoff = 0.99;
    private double minCutoff = 0.01;
    private double relativeCutoff = 0.4;
    private int nuclearStainChannel = -1;
    private int nuclearStainCycle = -1;
    private int membraneStainChannel = -1;
    private int membraneStainCycle = -1;
    private int radius = 2;
    private boolean count_puncta = false;
    private double inner_ring_size = 0.6;
    //determines the lower bound for cell size (relative to the expected cell volume, i.e. (4/3)*PI*radius^3)) expected values are 0.1 to 10, default is 1
    private double sizeCutoffFactor = 1.0;
    private int[] readoutChannels = null;
    //this variable enables subtraction of the inner ring average
    private boolean subtractInnerRing = false;
    //eto to
    private boolean showImage = false;
    private boolean dont_inverse_memb = false;
    private boolean delaunay_graph = true;
    private int concentricCircles = 0;
    private boolean anisotropic_reg_growth = true;
    private boolean single_plane_quant = false;

    public boolean isSingle_plane_quant() {
        return single_plane_quant;
    }

    public void setSingle_plane_quant(boolean single_plane_quant) {
        this.single_plane_quant = single_plane_quant;
    }

    public double getSizeCutoffFactor() {
        return sizeCutoffFactor;
    }

    public void setAnisotropicRegionGrowth(boolean anisotropic_reg_growth) {
        this.anisotropic_reg_growth = anisotropic_reg_growth;
    }

    public boolean isAnisotropicRegionGrowth() {
        return anisotropic_reg_growth;
    }

    public void setSizeCutoffFactor(double sizeCutoffFactor) {
        this.sizeCutoffFactor = sizeCutoffFactor;
    }

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
