/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.toolkit;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import clustering.ClusterMember;
import umontreal.iro.lecuyer.probdist.BetaDist;
import umontreal.iro.lecuyer.probdist.BinomialDist;
import util.Correlation;
import util.MatrixOp;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Nikolay Samusik
 */
public class CellTypeCooccurence {

    public static Matrix getCorrMtx(Entry<ClusterMember[], ClusterMember[][]> graph) {
        ClusterMember[] dp = graph.getKey();
        ClusterMember[][] neigh = graph.getValue();

        String[] clusterNames = Arrays.asList(dp).stream().map(c -> c.getCluster()).distinct().sorted((a, b) -> a.getID() - b.getID()).map(c -> c.getCaption()).toArray(String[]::new);

        double[][] counts = new double[clusterNames.length][dp.length];

        for (int i = 0; i < dp.length; i++) {
            for (ClusterMember cm : neigh[i]) {
                counts[cm.getCluster().getID()][i]++;
            }
        }

        DenseDoubleMatrix2D correl = new DenseDoubleMatrix2D(counts.length, counts.length);

        for (int i = 0; i < counts.length; i++) {
            correl.setQuick(i, i, 1.0);
            for (int j = i + 1; j < counts.length; j++) {
                double c = Correlation.getUncenteredCorrelation(counts[i], counts[j]);
                double expectedCorr = (MatrixOp.sum(counts[i]) * (MatrixOp.sum(counts[j]) / counts[j].length)) / Math.sqrt(MatrixOp.sum(counts[i]) * MatrixOp.sum(counts[j]));
                c = Math.tanh(arctanh(c) - arctanh(expectedCorr));
                correl.setQuick(i, j, c);
                correl.setQuick(j, i, c);
            }
        }

        return new Matrix(clusterNames, clusterNames, correl, "correlation");
    }

    public static Matrix getZScoreMtx(List<Matrix> control, Matrix mtx) {

        Matrix avg = Matrix.avg(control);

        Matrix sd = Matrix.SD(control);

        if (avg.getColumnNames().length != avg.getRowNames().length) {
            throw new IllegalArgumentException("the matrix must be square");
        }

        String[] names = avg.getColumnNames();

        Matrix zscore = mtx.clone("zScore_" + mtx.getName());

        for (int i = 0; i < names.length; i++) {

            for (int j = 0; j < names.length; j++) {

                //double a = Math.max(avg.mtx().getQuick(i, j), avg.mtx().getQuick(j, i));
                double SD = sd.mtx().getQuick(i, j);
                double AVG = avg.mtx().getQuick(i, j);

                double w = mtx.mtx().getQuick(i, j);
                zscore.mtx().set(i, j, (w-AVG)/SD);
            }
            
        }

        return zscore;
    }

    public static Matrix getLogsOddsRatioMtx(Entry<ClusterMember[], ClusterMember[][]> graph, String name) {
        ClusterMember[] dp = graph.getKey();
        ClusterMember[][] neigh = graph.getValue();

        String[] clusterNames = Arrays.asList(dp).stream().map(c -> c.getCluster()).distinct().sorted((a, b) -> a.getID() - b.getID()).map(c -> c.getCaption()).toArray(String[]::new);

        double[][] counts = new double[clusterNames.length][dp.length];

        for (int i = 0; i < dp.length; i++) {
            for (ClusterMember cm : neigh[i]) {
                counts[cm.getCluster().getID()][i]++;
            }
        }

        DenseDoubleMatrix2D correl = new DenseDoubleMatrix2D(counts.length, counts.length);

        for (int i = 0; i < counts.length; i++) {

            for (int j = 0; j < counts.length; j++) {

                double countCellsOfThisType = 0;
                double countCellsOtherType = 0;

                double TotalInteractionsOfThisType = 0;

                int InteractionsBtw = 0;

                for (int k = 0; k < counts[i].length; k++) {
                    if (dp[k].getCluster().getID() == i) {
                        countCellsOfThisType++;
                        for (int l = 0; l < counts.length; l++) {
                            TotalInteractionsOfThisType += counts[l][k];
                        }
                        InteractionsBtw += counts[j][k];
                    }
                    if (dp[k].getCluster().getID() == j) {
                        countCellsOtherType++;
                    }
                }

                //HypergeometricDist.logPDF(InteractionsBtw, (int) (TotalInteractionsOfThisType * (countCellsOtherType / dp.length)), j, j);
                BetaDist bd = new BetaDist(InteractionsBtw + 0.0001, (TotalInteractionsOfThisType - InteractionsBtw) + 0.0001);
                double oddsRatio = bd.getMean() / (countCellsOtherType / dp.length);//-2.0 * Math.log(pd.cdf(countCellsOtherType));
                correl.setQuick(i, j, Math.log(oddsRatio));
                /*
                if (i == 13 && j == 6) {
                    logger.print(clusterNames[i]);
                    logger.print(clusterNames[j]);
                    logger.print(InteractionsBtw);
                    logger.print(countCellsOfThisType);
                    logger.print(countCellsOtherType);
                    logger.print(TotalInteractionsOfThisType);
                    logger.print(Math.log(oddsRatio));
                }*/
            }
        }
        return new Matrix(clusterNames, clusterNames, correl, name);
    }

    public static Matrix getInteractionCountMtx(Entry<ClusterMember[], ClusterMember[][]> graph, String name) {
        ClusterMember[] dp = graph.getKey();
        ClusterMember[][] neigh = graph.getValue();

        String[] clusterNames = Arrays.asList(dp).stream().map(c -> c.getCluster()).distinct().sorted((a, b) -> a.getID() - b.getID()).map(c -> c.getCaption()).toArray(String[]::new);

        double[][] counts = new double[clusterNames.length][dp.length];

        for (int i = 0; i < dp.length; i++) {
            for (ClusterMember cm : neigh[i]) {
                counts[cm.getCluster().getID()][i]++;
            }
        }

        DenseDoubleMatrix2D correl = new DenseDoubleMatrix2D(counts.length, counts.length);

        for (int i = 0; i < counts.length; i++) {

            for (int j = 0; j < counts.length; j++) {

                double countCellsOfThisType = 0;
                double countCellsOtherType = 0;

                double TotalInteractionsOfThisType = 0;

                int InteractionsBtw = 0;

                for (int k = 0; k < counts[i].length; k++) {
                    if (dp[k].getCluster().getID() == i) {
                        countCellsOfThisType++;
                        for (int l = 0; l < counts.length; l++) {
                            TotalInteractionsOfThisType += counts[l][k];
                        }
                        InteractionsBtw += counts[j][k];
                    }
                    if (dp[k].getCluster().getID() == j) {
                        countCellsOtherType++;
                    }
                }

                //HypergeometricDist.logPDF(InteractionsBtw, (int) (TotalInteractionsOfThisType * (countCellsOtherType / dp.length)), j, j);
                correl.setQuick(i, j, InteractionsBtw);
                /*
                if (i == 13 && j == 6) {
                    logger.print(clusterNames[i]);
                    logger.print(clusterNames[j]);
                    logger.print(InteractionsBtw);
                    logger.print(countCellsOfThisType);
                    logger.print(countCellsOtherType);
                    logger.print(TotalInteractionsOfThisType);
                    logger.print(Math.log(oddsRatio));
                }*/
            }
        }
        return new Matrix(clusterNames, clusterNames, correl, name);
    }

    public static Matrix getPvalueMtx(Entry<ClusterMember[], ClusterMember[][]> graph, String name) {
        ClusterMember[] dp = graph.getKey();
        ClusterMember[][] neigh = graph.getValue();

        String[] clusterNames = Arrays.asList(dp).stream().map(c -> c.getCluster()).distinct().sorted((a, b) -> a.getID() - b.getID()).map(c -> c.getCaption()).toArray(String[]::new);

        double[][] counts = new double[clusterNames.length][dp.length];

        for (int i = 0; i < dp.length; i++) {
            for (ClusterMember cm : neigh[i]) {
                counts[cm.getCluster().getID()][i]++;
            }
        }

        DenseDoubleMatrix2D correl = new DenseDoubleMatrix2D(counts.length, counts.length);

        for (int i = 0; i < counts.length; i++) {

            for (int j = 0; j < counts.length; j++) {

                double countCellsOfThisType = 0;
                double countCellsOtherType = 0;

                double TotalInteractionsOfThisType = 0;

                int InteractionsBtw = 0;

                for (int k = 0; k < counts[i].length; k++) {
                    if (dp[k].getCluster().getID() == i) {
                        countCellsOfThisType++;
                        for (int l = 0; l < counts.length; l++) {
                            TotalInteractionsOfThisType += counts[l][k];
                        }
                        InteractionsBtw += counts[j][k];
                    }
                    if (dp[k].getCluster().getID() == j) {
                        countCellsOtherType++;
                    }
                }

                double pval = BinomialDist.cdf((int) TotalInteractionsOfThisType, (countCellsOtherType / dp.length), InteractionsBtw);

                correl.setQuick(i, j, pval);
                /*
                if (i == 13 && j == 6) {
                    logger.print(clusterNames[i]);
                    logger.print(clusterNames[j]);
                    logger.print(InteractionsBtw);
                    logger.print(countCellsOfThisType);
                    logger.print(countCellsOtherType);
                    logger.print(TotalInteractionsOfThisType);
                    logger.print(Math.log(oddsRatio));
                }*/
            }
        }
        return new Matrix(clusterNames, clusterNames, correl, name);
    }

    public static double arctanh(double x) {
        return 0.5 * Math.log((1 + x) / (1 - x));
    }
    /*
    public static Matrix partialCorrelMatrix(Matrix in) {
        if (in.mtx().rows() != in.mtx().columns()) {
            throw new IllegalArgumentException("not a square matrix");
        }
        DoubleMatrix2D inv = Algebra.DEFAULT.inverse(in.mtx());
        for (int i = 0; i < inv.rows(); i++) {
            for (int j = 0; j < inv.columns(); j++) {
                if (i == j) {
                    continue;
                }
                inv.setQuick(i, j, -inv.getQuick(i, j) / Math.sqrt(inv.getQuick(i, i) * inv.getQuick(j, j)));
            }
        }
        return new Matrix(in.getRowNames(), in.getColumnNames(), inv);
    }
     */
}
