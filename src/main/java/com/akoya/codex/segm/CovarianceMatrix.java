/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

/**
 *
 * @author Zina
 */
public class CovarianceMatrix {

    //Given a 2D array representing a data matrix, calculates the covariance matrix.
    public static double[][] covarianceMatrix(double[][] dataTable) {
        if (dataTable == null) {
            return null;
        }
        int dim = dataTable[0].length;
        double[][] covarianceMatrix = new double[dim][dim];
        double[][] flippedMatrix = flipMatrix(dataTable);
        for (int i = 0; i < dim; i++) {
            for (int j = i; j < dim; j++) {
                double[] parameter1 = flippedMatrix[i];
                double[] parameter2 = flippedMatrix[j];
                double covariance = calculateCovariance(parameter1, parameter2);
                covarianceMatrix[i][j] = covariance;
                covarianceMatrix[j][i] = covariance;
            }
        }
        return covarianceMatrix;
    }

    //Given a 2D array representing a data table, flips the matrix representing that table.
    private static double[][] flipMatrix(double[][] dataTable) {
        if (dataTable == null) {
            return null;
        }
        double[][] flippedMatrix = new double[dataTable[0].length][dataTable.length];
        for (int i = 0; i < dataTable.length; i++) {
            for (int j = 0; j < dataTable[i].length; j++) {
                double datapoint = dataTable[i][j];
                flippedMatrix[j][i] = datapoint;
            }
        }
        return flippedMatrix;
    }

    //Given two arrays of parameter values, calculates covariance for these two parameters.
    private static double calculateCovariance(double[] parameter1, double[] parameter2) {
        if ((parameter1 == null) || (parameter2 == null) || (parameter1.length != parameter2.length)) {
            throw new IllegalArgumentException("You gave me bad stuff");
        }
        double covariance = 0;
        double mean1 = (double) calculateMean(parameter1);
        double mean2 = (double) calculateMean(parameter2);
        for (int i = 0; i < parameter1.length; i++) {
            double componenti = ((double) parameter1[i] - mean1) * ((double) parameter2[i] - mean2);
            covariance += componenti;
        }
        covariance /= (parameter1.length - 1);
        return covariance;
    }

    //Given an array of parameter values, calculates mean for that parameter.
    private static double calculateMean(double[] parameter) {
        if (parameter == null) {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < parameter.length; i++) {
            sum += (double) parameter[i];
        }
        double mean = sum / parameter.length;
        return mean;
    }
}
