package com.akoya.codex.segm;


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
public class SegmMainTest {
    private SegmMain seg;
    private SegmConfigFrm segFrm;
    private JTextField configField;
    private String inDir;
    private File configFile;

    @BeforeTest
    public void setUp() throws Exception {
        seg = new SegmMain();
        seg.setOptionPane(new OkayMockOptionPane());
        configField = seg.getConfigField();
        //configField.setText("F:\\exp2TestPro");
        configField.setText("C:\\exp2TestProcessed");
        seg.initComponents();
        segFrm = seg.getSegmConfigFrm();
        inDir = configField.getText();
        segFrm.setRadius("5");
        segFrm.setMaxCutOff("0.99");
        segFrm.setMinCutOff("0.05");
        segFrm.setRelativeCutOff("0.2");
        segFrm.setNuclearStainChannel("1");
        segFrm.setNuclearStainCycle("1");;
        segFrm.setMembraneStainChannel("1");
        segFrm.setMembraneStainCycle("-1");
    }

    @Test(priority = 1)
    public void testChannelNames() throws Exception {
        File channelNamesFile = new File(inDir + File.separator + "channelNames.txt");
        Assert.assertTrue(channelNamesFile != null && channelNamesFile.exists() && channelNamesFile.isFile());
    }

    @Test(priority = 2)
    public void testTileMap() throws Exception {
        File tileMapFile = new File(inDir + File.separator + "tileMap.txt");
        Assert.assertTrue(tileMapFile != null && tileMapFile.exists() && tileMapFile.isFile());
    }

    @Test(priority = 3)
    public void testSegm() throws Exception {
        File segmTestRunFile = new File(inDir + File.separator  + "segmMainTestLog.txt");
        PrintStream p = new PrintStream(segmTestRunFile);
        System.setOut(p);
        System.setErr(p);

        int sec =0;
        Thread th= seg.cmdCreateButtonClicked(new ActionEvent(this, 1, "TestEvt"));
        do {
            Thread.currentThread().sleep(1000);
        }while(th.isAlive() && (sec++) < 600);


        Assert.assertTrue(sec < 600);
        Assert.assertTrue(segmTestRunFile != null && segmTestRunFile.exists());
    }

    @Test(priority = 4)
    public void testConfig() throws Exception {
        configFile = new File(inDir + File.separator + "config.txt");
        Assert.assertTrue(configFile != null && configFile.exists() && configFile.isFile());
    }

    @Test(priority = 5)
    public void testLogFileForErrors() throws Exception {
        File segmTestRunFile = new File(inDir+File.separator+"segmMainTestLog.txt");
        Scanner scanner = new Scanner(segmTestRunFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Assert.assertFalse(line == null || (line.toLowerCase().contains("error") || line.toLowerCase().contains("exception")));
        }
    }

    @Test(priority = 6)
    public void testConcatenateResults() throws Exception {
        //Check if .txt files are created
        File dir = new File(inDir);
        File[] txtFiles = dir.listFiles(f -> f.getName().toLowerCase().endsWith("compensated.txt"));
        Assert.assertTrue(txtFiles != null && txtFiles.length != 0);
    }

    @Test(priority = 7)
    public void testMakeFCS() throws Exception {
        //Check if .fcs files are created
        File dir = new File(inDir);
        File[] fcsFiles = dir.listFiles(f -> f.getName().toLowerCase().endsWith(".fcs"));
        Assert.assertTrue(fcsFiles != null && fcsFiles.length != 0);
    }
}
