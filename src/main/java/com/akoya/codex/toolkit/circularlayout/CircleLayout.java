package com.akoya.codex.toolkit.circularlayout;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

public class CircleLayout extends LayoutHelper implements Layout {

    private Graph graph;
    private double diameter;
    private boolean boolfixeddiameter;
    private String strNodeplacement;
    static final double TWO_PI = 6.283185307179586D;
    CircleLayoutBuilder builder;
    GraphModel graphModel;

    @Override
    public void resetPropertiesValues() {

    }

    @Override
    public LayoutProperty[] getProperties() {
      return new LayoutProperty[0];
    }

    private boolean converged = false;

    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public boolean isConverged() {
        return converged;
    }

    @Override
    public LayoutBuilder getBuilder() {
        return builder;
    }

    public CircleLayout(CircleLayoutBuilder layoutBuilder, double diameter, boolean boolfixeddiameter) {
        super(layoutBuilder);
        this.builder = layoutBuilder;
        this.diameter = diameter;
        this.graphModel = layoutBuilder.gm;
        this.boolfixeddiameter = boolfixeddiameter;
    }

    public void initAlgo() {
        setConverged(false);
        this.graph = this.graphModel.getGraphVisible();
        this.graph.readLock();
        float[] nodeCoords = new float[2];
        double tmpcirc = 0.0D;
        double tmpdiameter = 0.0D;
        int index = 0;
        Node[] nodes = this.graph.getNodes().toArray();
        int nodecount = this.graph.getNodeCount();
        double theta = 6.283185307179586D / nodecount;
        double noderadius = 0.0D;
        double lasttheta = 0.0D;
        for (Node n : nodes) {
            if (!n.isFixed()) {
                tmpcirc += (diameter/7) * 2.0F;
            }
        }
        tmpcirc *= 1.2D;
        tmpdiameter = tmpcirc / 3.141592653589793D;
        
        if (isNodePlacementNoOverlap()) {
            theta = 6.283185307179586D / tmpcirc;
        }
        
        if ((isBoolFixedDiameter()) && (tmpdiameter < this.diameter)) {
            tmpdiameter = this.diameter;
        }
        
        double radius = tmpdiameter / 2.0D;
        
        nodes = sortNodes(nodes, this.strNodeplacement, false);

        if (isCW()) {
            theta = -theta;
        }

        for (int localNode1 = 0; localNode1 < nodes.length; localNode1++) {
            Node n = nodes[localNode1];
            TempLayoutData posData = new TempLayoutData();
            if (!n.isFixed()) {
                if (isNodePlacementNoOverlap()) {
                    noderadius = diameter/7;
                    double noderadian = theta * noderadius * 1.2D;
                    nodeCoords = cartCoors(radius, 1, lasttheta + noderadian);
                    lasttheta += noderadius * 2.0D * theta * 1.2D;
                } else {
                    nodeCoords = cartCoors(radius, index, theta);
                }
                posData.finishx = nodeCoords[0];
                posData.finishy = nodeCoords[1];
                index++;
            } else {
                posData.finishx = n.x();
                posData.finishy = n.y();
            }
            posData.xdistance = ((float) (1.0D / getTransitionSteps().doubleValue()) * (nodeCoords[0] - n.x()));
            posData.ydistance = ((float) (1.0D / getTransitionSteps().doubleValue()) * (nodeCoords[1] - n.y()));
            n.setLayoutData(posData);
        }
        this.graph.readUnlock();
    }

    public void goAlgo() {
        this.graph.readLock();
        setConverged(true);
        TempLayoutData position = null;
        Node[] nodes = this.graph.getNodes().toArray();
        for (Node n : nodes) {
            if (n.getLayoutData() != null) {
                position = (TempLayoutData) n.getLayoutData();
                if (isNodePlacementTransition()) {
                    float currentDistance = Math.abs(n.x() - position.finishx);
                    float nextDistance = Math.abs(n.x() + position.xdistance - position.finishx);
                    if (nextDistance < currentDistance) {
                        n.setX(n.x() + position.xdistance);
                        setConverged(false);
                    } else {
                        n.setX(position.finishx);
                    }
                    currentDistance = Math.abs(n.y() - position.finishy);
                    nextDistance = Math.abs(n.y() + position.ydistance - position.finishy);
                    if (nextDistance < currentDistance) {
                        n.setY(n.y() + position.ydistance);
                        setConverged(false);
                    } else {
                        n.setY(position.finishy);
                    }
                    if ((n.y() == position.finishy) && (n.x() == position.finishx)) {
                        n.setLayoutData(null);
                    }
                } else {
                    n.setX(position.finishx);
                    n.setY(position.finishy);
                    n.setLayoutData(null);
                }
            }
        }
        this.graph.readUnlock();
    }

    public void setNodePlacement(String strNodeplacement) {
        this.strNodeplacement = strNodeplacement;
    }

    public String getNodePlacement() {
        return this.strNodeplacement;
    }

    public void setBoolFixedDiameter(Boolean boolfixeddiameter) {
        this.boolfixeddiameter = boolfixeddiameter.booleanValue();
    }

    public boolean isBoolFixedDiameter() {
        return this.boolfixeddiameter;
    }

    public void setDiameter(Double diameter) {
        this.diameter = diameter.doubleValue();
    }

    public Double getDiameter() {
        return Double.valueOf(this.diameter);
    }
}
