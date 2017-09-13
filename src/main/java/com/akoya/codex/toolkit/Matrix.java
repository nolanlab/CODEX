/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.toolkit;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import dataIO.DatasetStub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Nikolay Samusik
 */
public class Matrix {

    private String[] columnNames;
    private String[] rowNames;
    private DoubleMatrix2D mtx;

    private final String name;

    public Matrix(File f) {
        DatasetStub ds = DatasetStub.createFromTXT(f);
        columnNames = Arrays.copyOf(ds.getShortColumnNames(), ds.getShortColumnNames().length);
        rowNames = new String[(int) ds.getRowCount()];
        double[][] m = new double[rowNames.length][columnNames.length];
        for (int i = 0; i < rowNames.length; i++) {
            m[i] = ds.getRow(i);
            rowNames[i] = ds.getRowName(i);
        }
        mtx = new DenseDoubleMatrix2D(m);
        this.name = f.getName().split("\\.")[0];
    }

    public static Matrix sum(Matrix a, Matrix b) {
        Matrix ret = a.clone(a.name + " + " + b.name);
        for (int i = 0; i < a.rowNames.length; i++) {
            for (int j = 0; j < a.columnNames.length; j++) {
                ret.mtx().setQuick(i, j, a.mtx().getQuick(i, j) + b.mtx().getQuick(i, j));
            }
        }
        return ret;
    }

    public static Matrix avg(List<Matrix> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        Matrix avg = list.get(0).clone("avg of " + list.size() + " matrices");
        avg = list.stream().reduce(avg, Matrix::sum);
        avg.mtx().assign((double d) -> d / list.size());
        return avg;
    }

    public String getName() {
        return name;
    }

    public static Matrix SD(List<Matrix> list) {
        if (list.size() < 2) {
            throw new IllegalArgumentException("List size is less than 2");
        }
        Matrix avg = avg(list);

        Matrix res = avg.clone("SD of " + list.size() + " matrices");

        res.mtx().assign(0);

        for (Matrix m : list) {
            for (int i = 0; i < res.mtx().rows(); i++) {
                for (int j = 0; j < res.mtx().columns(); j++) {
                    res.mtx().set(i, j, res.mtx().get(i, j) + Math.pow(m.mtx().getQuick(i, j) - avg.mtx().getQuick(i, j), 2));
                }
            }
        }

        for (int i = 0; i < res.mtx().rows(); i++) {
            for (int j = 0; j < res.mtx().columns(); j++) {
                res.mtx().set(i, j, Math.sqrt(res.mtx().get(i, j) / (list.size() - 1)));
            }
        }

        return res;
    }

    public DoubleMatrix2D mtx() {
        return mtx;
    }

    public String getColumnName(int i) {
        return columnNames[i];
    }

    public String getRowName(int i) {
        return rowNames[i];
    }

    public void writeToFile(File f) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(f));
        br.write(this.toString());
        br.close();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t");
        sb.append(Arrays.toString(columnNames).replaceAll("[\\[\\]]", "").replaceAll(",", "\t"));
        sb.append("\n");
        for (int i = 0; i < rowNames.length; i++) {
            sb.append(rowNames[i]);
            for (int j = 0; j < columnNames.length; j++) {
                sb.append("\t").append(mtx.getQuick(i, j));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Matrix(String[] columnNames, String[] rowNames, DoubleMatrix2D mtx, String name) {
        this.columnNames = columnNames;
        this.rowNames = rowNames;
        if (columnNames.length != mtx.columns()) {
            throw new IllegalArgumentException("columnNames.size() != mtx.rows()");
        }
        if (rowNames.length != mtx.rows()) {
            throw new IllegalArgumentException("rowNames.size() != mtx.rows()");
        }
        this.mtx = mtx;
        this.name = name;
    }

    public Matrix clone(String newName) {
        return new Matrix(Arrays.copyOf(rowNames, rowNames.length), Arrays.copyOf(columnNames, columnNames.length), mtx.copy(), newName);
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String[] getRowNames() {
        return rowNames;
    }

}
