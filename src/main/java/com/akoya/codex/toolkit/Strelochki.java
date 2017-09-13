/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.toolkit;

import com.akoya.codex.toolkit.circularlayout.CircleLayout;
import com.akoya.codex.toolkit.circularlayout.CircleLayoutBuilder;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.*;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import util.DefaultEntry;
import util.logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Nikolay Samusik
 */
public class Strelochki {

    public static Entry<Matrix, Image>[] renderInteractionMatrix(List<Matrix> refLogOddsRatioMtx, List<Matrix> listLogOddsRatioMtx, double zScoreThs) {

        Matrix avg = Matrix.avg(refLogOddsRatioMtx);

        Matrix sd = Matrix.SD(refLogOddsRatioMtx);

        Entry<Matrix, Image>[] ret = new Entry[listLogOddsRatioMtx.size()];
        int kt = 0;

        if (avg.getColumnNames().length != avg.getRowNames().length) {
            throw new IllegalArgumentException("the matrix must be square");
        }

        String[] names = avg.getColumnNames();

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Project p = pc.getCurrentProject();

        final Workspace workspace1 = pc.newWorkspace(p);

        pc.openWorkspace(workspace1);
        //Get a graph model - it exists because we have a workspace
        GraphController gc = Lookup.getDefault().lookup(GraphController.class);
        GraphModel graphModel = gc.getGraphModel(workspace1);

        Graph graph = graphModel.getUndirectedGraph();

        Node[] nodes = new Node[names.length];
        for (int idx = 0; idx < nodes.length; idx++) {
            String c = names[idx];
            if (c.equals("null")) {
                continue;
            }
            Node n0 = graphModel.factory().newNode(c);

            String repeated = new String(new char[(int) (c.length() * 1.1)]).replace("\0", " ");
            n0.setLabel(repeated + c);
            n0.setSize(100);
            n0.setX(idx * 10);
            n0.setY(idx * 10);
            n0.setColor(new Color(100, 100, 255, 127));
            graph.addNode(n0);
            nodes[idx] = n0;

        }

        ArrayList<Edge> thinEdges = new ArrayList<>();

        ArrayList<Edge> thickEdges = new ArrayList<>();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) {
                continue;
            }
            for (int j = i + 1; j < nodes.length; j++) {
                if (nodes[j] == null) {
                    continue;
                }
                double weight = Math.max(avg.mtx().getQuick(i, j), avg.mtx().getQuick(i, j));

                double SD = Math.max(sd.mtx().getQuick(i, j), sd.mtx().getQuick(i, j));

                if (weight / SD > zScoreThs) {
                    Edge e = graph.getModel().factory().newEdge(nodes[i], nodes[j], 0, weight / SD, false);
                    e.setColor(new Color(100, 100, 255, 0));
                    graph.addEdge(e);
                    thickEdges.add(e);
                } else {
                    Edge e = graph.getModel().factory().newEdge(nodes[i], nodes[j], 0, 0.1/*mtx.mtx().getQuick(i, j)*/, false);
                    e.setColor(new Color(255, 255, 255, 255));
                    graph.addEdge(e);
                    thinEdges.add(e);
                }
            }
        }

        //Get a graph model - it exists because we have a workspace
        //Preview configuration
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();

        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);

        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
        EdgeColor edc = new EdgeColor(EdgeColor.Mode.ORIGINAL);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, edc);

        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, new Font(Font.MONOSPACED, 1, 200));
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, Color.BLUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 7);

        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 20);
        //previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));
        //previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.CATEGORY_EDGE_ARROWS, 1);
        //
        //previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        //previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 1);
        //previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_COLOR, new DependantColor(new Color(100, 100, 100)));
        //previewModel.getProperties().putValue(PreviewProperty.NODE_OPACITY, 100);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, false);

        previewController.refreshPreview();

        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);

        target.resize(2000, 2000);

        ForceAtlas2 layout = new ForceAtlas2(new ForceAtlas2Builder());

        layout.setGraphModel(graphModel);
        layout.initAlgo();

        layout.setAdjustSizes(false);
        layout.setScalingRatio(500.0);
        layout.setBarnesHutOptimize(false);
        for (int i = 0; i < 10000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }

        layout.setScalingRatio(100.0);
        layout.setAdjustSizes(true);
        for (int i = 0; i < 10000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }

        graph.removeAllEdges(thinEdges);

        target.reset();
        target.refresh();
        previewController.refreshPreview();

        for (Node n : nodes) {
            if (n != null) {
                n.setX(n.x() * 7f);
                n.setY(n.y() * 7f);
            }
        }

        for (Matrix mtx : listLogOddsRatioMtx) {

            graph.removeAllEdges(thickEdges);
            graph.removeAllEdges(thinEdges);

            //graph.addAllEdges(thinEdges);
            graph.addAllEdges(thickEdges);

            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] == null) {
                    continue;
                }
                for (int j = i + 1; j < nodes.length; j++) {
                    if (nodes[j] == null) {
                        continue;
                    }
                    //double a = Math.max(avg.mtx().getQuick(i, j), avg.mtx().getQuick(j, i));

                    double SD = Math.max(sd.mtx().getQuick(i, j), sd.mtx().getQuick(j, i));

                    double w = Math.max(mtx.mtx().getQuick(i, j), mtx.mtx().getQuick(j, i));

                    Edge e = graph.getEdge(nodes[i], nodes[j]);
                    if (e == null) {
                        e = graph.getEdge(nodes[j], nodes[i]);
                    }

                    if (e == null && w / SD > zScoreThs) {
                        e = graph.getModel().factory().newEdge(nodes[i], nodes[j], 0, w / SD, false);
                        e.setColor(Color.BLUE);
                        graph.addEdge(e);
                        continue;
                    }

                    if (e != null && w / SD < zScoreThs) {
                        if (graph.contains(e)) {
                            if (e.getTarget() != null && e.getSource() != null) {
                                //System.err.println(e + " " + graph);
                                try {
                                    graph.removeEdge(e);
                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }

            for (Node n : nodes) {
                if (n != null) {
                    if (graph.contains(n)) {
                        n.setSize((graph.getDegree(n) + 1) * 30);
                    }
                }
            }

            target.reset();
            target.refresh();
            target.reset();
            target.refresh();
            previewController.refreshPreview();

            //previewController.refreshPreview();
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            try {
                ec.exportFile(new File(mtx.getName() + ".pdf"), workspace1);
            } catch (IOException e) {
                logger.showException(e);
            }

            try {
                ec.exportFile(new File(mtx.getName() + ".png"), workspace1);
            } catch (IOException e) {
                logger.showException(e);
            }

            ret[kt++] = new DefaultEntry<>(mtx, target.getImage());
        }

        return ret;

    }

    public static Entry<Matrix, Image> renderInteractionMatrixCounts(Matrix mtx, double countThs) {

        //Matrix sd = Matrix.SD(refLogOddsRatioMtx);
        Entry<Matrix, Image> ret = null;

        if (mtx.getColumnNames().length != mtx.getRowNames().length) {
            throw new IllegalArgumentException("the matrix must be square");
        }

        String[] names = mtx.getColumnNames();

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Project p = pc.getCurrentProject();

        final Workspace workspace1 = pc.newWorkspace(p);

        pc.openWorkspace(workspace1);
        //Get a graph model - it exists because we have a workspace
        GraphController gc = Lookup.getDefault().lookup(GraphController.class);
        GraphModel graphModel = gc.getGraphModel(workspace1);

        Graph graph = graphModel.getUndirectedGraph();

        Node[] nodes = new Node[names.length];
        for (int idx = 0; idx < nodes.length; idx++) {

            int sumWeight = 0;
            for (int j = 0; j < nodes.length; j++) {
                //if(j==idx)continue;
                double weight = Math.max(mtx.mtx().getQuick(idx, j), mtx.mtx().getQuick(idx, j));
                sumWeight += weight;
            }

            String c = names[idx];
            if (c.equals("null")) {
                continue;
            }
            Node n0 = graphModel.factory().newNode(c);

            String repeated = new String(new char[(int) (c.length() * 1.1)]).replace("\0", " ");
            n0.setLabel(repeated + c);
            n0.setSize((float) Math.sqrt(sumWeight));
            n0.setX(idx * 10);
            n0.setY(idx * 10);
            n0.setColor(new Color(100, 100, 255, 127));
            graph.addNode(n0);
            nodes[idx] = n0;
        }

        //ArrayList<Edge> thinEdges = new ArrayList<>();
        //ArrayList<Edge> thickEdges = new ArrayList<>();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) {
                continue;
            }
            for (int j = i + 1; j < nodes.length; j++) {
                if (nodes[j] == null) {
                    continue;
                }
                double weight = Math.sqrt(Math.max(mtx.mtx().getQuick(i, j), mtx.mtx().getQuick(i, j)));
                if (weight > countThs) {
                    Edge e = graph.getModel().factory().newEdge(nodes[i], nodes[j], 0, weight, false);
                    graph.addEdge(e);
                } else {
                    Edge e = graph.getModel().factory().newEdge(nodes[i], nodes[j], 0, 0.1/*mtx.mtx().getQuick(i, j)*/, false);
                    graph.addEdge(e);
                }
            }
        }

        //Get a graph model - it exists because we have a workspace
        //Preview configuration
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();

        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);

        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
        EdgeColor edc = new EdgeColor(EdgeColor.Mode.ORIGINAL);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, edc);

        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, new Font(Font.MONOSPACED, 1, 200));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 1);

        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 20);
        //previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));
        //previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.CATEGORY_EDGE_ARROWS, 1);
        //
        //previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        //previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 1);
        //previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_COLOR, new DependantColor(new Color(100, 100, 100)));
        //previewModel.getProperties().putValue(PreviewProperty.NODE_OPACITY, 100);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, false);

        previewController.refreshPreview();

        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);

        target.resize(2000, 2000);

        CircleLayout layout = new CircleLayout(new CircleLayoutBuilder(graphModel), 500, false);

        layout.setGraphModel(graphModel);
        layout.initAlgo();

        /*layout.setAdjustSizes(false);
        layout.setScalingRatio(500.0);
        layout.setBarnesHutOptimize(false);*/
        for (int i = 0; i < 10000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }

        /*
        layout.setScalingRatio(100.0);
        layout.setAdjustSizes(true);
        for (int i = 0; i < 10000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }*/
        target.reset();
        target.refresh();
        previewController.refreshPreview();

        for (Node n : nodes) {
            if (n != null) {
                n.setX(n.x() * 7f);
                n.setY(n.y() * 7f);
            }
        }

        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File(mtx.getName() + "_Graph_CellInteractionCounts.pdf"), workspace1);
        } catch (IOException e) {
            logger.showException(e);
        }

        try {
            ec.exportFile(new File(mtx.getName() + "_Graph_CellInteractionCounts.png"), workspace1);
        } catch (IOException e) {
            logger.showException(e);
        }

        ret = new DefaultEntry<>(mtx, target.getImage());

        return ret;

    }

}
