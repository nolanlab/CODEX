package com.akoya.codex.clustering;

import com.akoya.codex.OkayMockOptionPane;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Scanner;

/**
 * @author Vishal
 */
public class ClusteringTest {

    private frmCluster frm;
    private ImportConfigFrm importConfigFrm;
    private JTextField fcsFolderField;
    private String inDir;

    @BeforeTest
    public void setUp() throws Exception {
        frm = new frmCluster();
        frm.setOptionPane(new OkayMockOptionPane());
        fcsFolderField = frm.getFcsFolderField();
//        fcsFolderField.setText("F:\\exp2TestPro");
        fcsFolderField.setText("C:\\exp2TestProcessed");
        frm.initComponents();
        importConfigFrm = frm.getImpConfigFrm();
        inDir = fcsFolderField.getText();
        frm.setClustCols("3,4,5");
        importConfigFrm.setLimitEvents("-1");
        importConfigFrm.setTransformation("NONE");
        importConfigFrm.setScalingFactor("5");
        importConfigFrm.setNoiseThreshold("1.0");
        importConfigFrm.setRescale("NONE");
        importConfigFrm.setQuantile("1.0");
        importConfigFrm.setRescaleSeparately("True");
    }

    @Test(priority = 1)
    public void testConfig() throws Exception {
        File clusteringTestRunFile = new File(inDir + File.separator  + "clusteringTestLog.txt");
        PrintStream p = new PrintStream(clusteringTestRunFile);
        System.setOut(p);
        System.setErr(p);

        int sec =0;
        Thread th= frm.cmdCreateButtonClicked(new ActionEvent(this, 1, "TestEvt"));
        do {
            Thread.currentThread().sleep(1000);
        }while(th.isAlive() && (sec++) < 600);


        Assert.assertTrue(sec < 600);
        Assert.assertTrue(clusteringTestRunFile.exists());
    }

    @Test(priority = 2)
    public void testLogFileForErrors() throws Exception {
        File clusteringTestRunFile = new File(inDir+File.separator+"clusteringTestLog.txt");
        Scanner scanner = new Scanner(clusteringTestRunFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Assert.assertFalse(line == null || (line.toLowerCase().contains("error") || line.toLowerCase().contains("exception")));
        }
    }

    @Test(priority = 3)
    public void testClusteringResults() throws Exception {
        //Check if out folder and .fcs files are created
        File dir = new File(inDir + File.separator + "out");
        Assert.assertTrue(dir != null && dir.exists() && dir.isDirectory());

        File[] fcsFiles = dir.listFiles(f -> f.getName().toLowerCase().endsWith(".fcs"));
        Assert.assertTrue(fcsFiles != null && fcsFiles.length != 0);
    }
}
