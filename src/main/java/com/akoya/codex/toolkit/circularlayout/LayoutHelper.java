package com.akoya.codex.toolkit.circularlayout;

import org.gephi.graph.api.*;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.openide.util.Lookup;
import util.ColorScale;

import java.awt.*;
import java.util.*;

public abstract class LayoutHelper implements Layout {

    private final LayoutBuilder layoutBuilder;
    protected GraphModel graphModel;
    private boolean converged;
    private CircularDirection NodePlacementDirection = CircularDirection.CW;
    private Double intSteps = Double.valueOf(1.0D);
    private boolean boolNoOverlap = true;
    private boolean boolTransition = true;

    public static enum CircularDirection {
        CCW, CW;

        private CircularDirection() {
        }
    }

    public LayoutHelper(LayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }

    public LayoutBuilder getBuilder() {
        return this.layoutBuilder;
    }

    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
        resetPropertiesValues();
    }

    public boolean canAlgo() {
        return (!isConverged()) && (this.graphModel != null);
    }

    public void endAlgo() {
    }

    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public boolean isConverged() {
        return this.converged;
    }

    public void resetPropertiesValues() {
        setNodePlacementNoOverlap(Boolean.valueOf(true));
        setNodePlacementDirection(CircularDirection.CCW);
        setNodePlacementTransition(Boolean.valueOf(false));
        setTransitionSteps(Double.valueOf(100000.0D));
    }

    public CircularDirection getNodePlacementDirection() {
        return this.NodePlacementDirection;
    }

    public void setNodePlacementDirection(CircularDirection NodePlacementDirection) {
        this.NodePlacementDirection = NodePlacementDirection;
    }

    public void setNodePlacementDirection(String NodePlacementDirection) {
        for (CircularDirection enumValue : (CircularDirection[]) CircularDirection.class.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(NodePlacementDirection)) {
                this.NodePlacementDirection = enumValue;
            }
        }
    }

    public boolean isCW() {
        if (this.NodePlacementDirection == CircularDirection.CW) {
            return true;
        }
        return false;
    }

    public boolean isCCW() {
        if (this.NodePlacementDirection == CircularDirection.CCW) {
            return true;
        }
        return false;
    }

    public boolean isNodePlacementNoOverlap() {
        return this.boolNoOverlap;
    }

    public void setNodePlacementNoOverlap(Boolean boolNoOverlap) {
        this.boolNoOverlap = boolNoOverlap.booleanValue();
    }

    public boolean isNodePlacementTransition() {
        return this.boolTransition;
    }

    public void setNodePlacementTransition(Boolean boolTransition) {
        this.boolTransition = boolTransition.booleanValue();
    }

    public Double getTransitionSteps() {
        return this.intSteps;
    }

    public void setTransitionSteps(Double steps) {
        this.intSteps = steps;
    }

    public Node[] sortNodes(Node[] nodes, String strNodeplacement, boolean sortdirection) {
        final Graph graph = this.graphModel.getGraphVisible();
        Arrays.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                double sumWo2 = 0;
                EdgeIterable ei = graph.getEdges(o2);
                for (Edge edge : ei) {
                    sumWo2 += edge.getWeight();
                }
                ei = graph.getEdges(o1);
                double sumWo1 = 0;
                for (Edge edge : ei) {
                    sumWo1 += edge.getWeight();
                }
                return (int) Math.signum(sumWo2 - sumWo1);
            }

        });
        
        double maxEdgeW = 0;
        
        EdgeIterable ei = graph.getEdges();

            for (Edge edge : ei) {
                maxEdgeW = Math.max(maxEdgeW, edge.getWeight());
            }
            
        for (int i = nodes.length - 1; i >= 0; i--) {
            
            Color c = ColorScale.getRainbowColorForValue(1.0f-(i/(float)(nodes.length-1)));

            nodes[i].setColor(c);
            
             ei = graph.getEdges(nodes[i]);

            for (Edge edge : ei) {
                
                Color tmp = ColorScale.getRainbowColorForValue((edge.getWeight()/maxEdgeW));
                double val = Math.sqrt(edge.getWeight()/maxEdgeW);
                
                edge.setColor(new Color(255 - (int)((255-tmp.getRed())*val),255 - (int)((255-tmp.getGreen())*val),255 - (int)((255-tmp.getBlue())*val) ));
            }
        }

        return nodes;
    }

    public static Map getPlacementMap() {
        return getPlacementMap(true);
    }

    public static Map getPlacementMap(boolean boolIncludeRandom) {
        GraphController graphController = (GraphController) Lookup.getDefault().lookup(GraphController.class);
        GraphModel objGraphModel = graphController.getGraphModel();

        Map<String, String> map = new TreeMap();
        if (boolIncludeRandom) {
            map.put("Random", Bundle.Layout_NodePlacement_Random_name());
        }
        map.put("NodeID", Bundle.Layout_NodePlacement_NodeID_name());
        map.put("Degree", Bundle.Layout_NodePlacement_Degree_name());
        if (objGraphModel != null) {
            if (objGraphModel.isDirected()) {
                map.put("InDegree", Bundle.Layout_NodePlacement_InDegree_name());
                map.put("OutDegree", Bundle.Layout_NodePlacement_OutDegree_name());
            }
            for (Column c : objGraphModel.getNodeTable()) {
                map.put(c.getId() + "-Att", c.getTitle() + " (Attribute)");
            }
        }
        return map;
    }

    public Object getLayerAttribute(Node n, String Placement) {
        Object layout = null;
        Graph graph = this.graphModel.getGraphVisible();
        if (Placement.equals("Random")) {
            layout = Integer.valueOf(1);
        } else if (Placement.equals("NodeID")) {
            layout = n.getId();
        } else if (Placement.equals("Degree")) {
            layout = Integer.valueOf(graph.getDegree(n));
        } else if (Placement.equals("InDegree")) {
            DirectedGraph objGraph = this.graphModel.getDirectedGraph();
            layout = Integer.valueOf(objGraph.getInDegree(n));
        } else if (Placement.equals("OutDegree")) {
            DirectedGraph objGraph = this.graphModel.getDirectedGraph();
            layout = Integer.valueOf(objGraph.getOutDegree(n));
        } else {
            Placement = Placement.substring(0, Placement.length() - 4);
            layout = n.getAttribute(Placement);
        }
        return layout;
    }

    public static Map getRotationMap() {
        EnumMap<CircularDirection, String> map = new EnumMap(CircularDirection.class);
        map.put(CircularDirection.CCW, Bundle.Layout_NodePlacement_CCW());
        map.put(CircularDirection.CW, Bundle.Layout_NodePlacement_CW());
        return map;
    }

    public float[] cartCoors(double radius, int whichInt, double theta) {
        float[] coOrds = new float[2];
        coOrds[0] = ((float) (radius * Math.cos(theta * whichInt + 1.5707963267948966D)));
        coOrds[1] = ((float) (radius * Math.sin(theta * whichInt + 1.5707963267948966D)));
        return coOrds;
    }
}
