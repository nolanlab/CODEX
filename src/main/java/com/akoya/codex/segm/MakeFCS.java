/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author Nikolay
 */
public class MakeFCS {

    public static void main(String[] args) throws Exception {

        //args = new String[]{"I:\\Nikolay\\41-parameter 16 cycles melanoma Nikolay 4-18-17"};
        
        File dir = new File(args[0]);
        File config = new File(args[0] + File.separator + "config.txt");
        File chNames = new File(args[0] + File.separator + "channelNames.txt");

        ArrayList<File> concatFiles = new ArrayList<>();

        for (File f : dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().startsWith("reg") && (!f.getName().contains("_X")) && f.getName().contains("_Expression") && f.getName().endsWith(".txt");
            }
        })) {
            concatFiles.add(f);
        }

        System.out.println("Found regions: " + concatFiles.toString());

        for (File reg : concatFiles) {

            if (args.length > 1) {
                processFile(reg, config, args[1]);
            } else {
                processFile(reg, config, null);
            }

        }

    }

    public static void processFile(File f, File configFile, String blankCycIDXString) throws IOException, InterruptedException {

        CSVReader csv = new CSVReader(new FileReader(f), '\t');
        Iterator<String[]> it = csv.iterator();

        String[] header = it.next();

        Properties prop = new Properties();
        InputStream input = new FileInputStream(configFile);
        prop.load(input);
        String nuclearStainChannelS = prop.getProperty("nuclearStainChannel", "-1");
        int nuclearStainCycle = Integer.parseInt(prop.getProperty("nuclearStainCycle", "-1"));


        String[] blankCycIdxS = (blankCycIDXString == null) ? new String[0] : blankCycIDXString.split(",");
        int[] blankCycleIdx = new int[blankCycIdxS.length];
        for (int i = 0; i < blankCycIdxS.length; i++) {
            blankCycleIdx[i] = Integer.parseInt(blankCycIdxS[i]);
        }

        int size_idx = 0;

        for (int i = 0; i < header.length; i++) {
            if (header[i].equals("size")) {
                size_idx = i;
            }
        }

        int offset = size_idx;

        String outPath = f.getAbsolutePath().replaceAll("\\.txt", "_normalized_FCSsrc.csv");
        File out = new File(outPath);
        CSVWriter outWr = new CSVWriter(new FileWriter(out), ',');

        String[] splitHeader = split(header, 1)[1];

        for (int i = 0; i < offset; i++) {
            splitHeader[i] += ":" + splitHeader[i];
        }

        String[][] chNames = null;
        File chNamesF = new File(f.getParent() + File.separator + "channelNames.txt");
        if (chNamesF.exists()) {
            System.out.println("Found channel names file!");
            java.util.List<String[]> chNamesL = new CSVReader(new FileReader(chNamesF), '\t').readAll();
            chNames = chNamesL.toArray(new String[chNamesL.size()][]);
        } else {
            chNamesF = new File(f.getParentFile().getParent() + File.separator + "channelNames.txt");
            if(chNamesF.exists()) {
                System.out.println("Found channel names file!");
                java.util.List<String[]> chNamesL = new CSVReader(new FileReader(chNamesF), '\t').readAll();
                chNames = chNamesL.toArray(new String[chNamesL.size()][]);
            }
            else {
                throw new IllegalStateException("channelNames.txt file does not exist! Exiting...");
            }
        }

        if (chNames.length < splitHeader.length - offset) {
            throw new IllegalStateException("ChannelNames file is too short: " + chNames.length + ", expected " + (splitHeader.length - offset));
        }

        for (int i = offset; i < splitHeader.length; i++) {
            splitHeader[i] += ":" + chNames[i - offset][0];
        }

        splitHeader = append(splitHeader, "Fiter1:Profile_Homogeneity");

        //System.out.println(Arrays.toString(splitHeader));
        outWr.writeNext(splitHeader);

        while (it.hasNext()) {
            String[] l = it.next();
            if (l[l.length - 1].isEmpty()) {
                l = Arrays.copyOf(l, l.length - 1);
            }
            double size = getSize(l, offset);

            String[] l2 = split(l, 1)[1];

            double sum = 0;
            double sumsq = 0;
            for (int k = offset; k < l.length; k++) {
                try {
                    double d = Double.parseDouble(l[k]);
                    sum += d;
                    sumsq += d * d;
                } catch (NumberFormatException e) {
                    System.err.println("Corrupt number: " + (l[k]) + "\nRow length" + l.length + "\nheader len" + header.length + "\nindex:" + k + "\nString: " + Arrays.toString(l));
                    e.printStackTrace();
                }
            }
            l2 = append(l2, String.valueOf(sum / Math.sqrt(sumsq)));
            outWr.writeNext(l2);
        }

        outWr.flush();
        outWr.close();
        System.out.println("Making FCS command:");
        String s  = "-InputFile:\"" + outPath + "\"";
        System.out.println(s);

        String cmd = "java -cp \".\\*\"  net.sf.flowcyt.gp.module.csv2fcs.CSV2FCSApp "+s;
        System.out.println(cmd);

        Runtime.getRuntime().exec(cmd).waitFor();

    }

    private static double[] getVecForCycle(String[] line, int offset, int numReadoutChannels, int cycle) {
        double[] out = new double[numReadoutChannels];
        int idx = ((cycle - 1) * numReadoutChannels) + offset;
        for (int i = 0; i < numReadoutChannels; i++) {
            out[i] = Double.parseDouble(line[idx + i]);
        }
        return out;
    }

    private static double getSize(String[] line, int offset) {
        return Double.parseDouble(line[offset - 1]);
    }

    private static String[] append(String[] src, String s) {
        String[] out = Arrays.copyOf(src, src.length + 1);
        out[src.length] = s;
        return out;
    }

    private static String[][] split(String[] src, int idx) {
        String[][] out = new String[2][];
        out[0] = Arrays.copyOf(src, idx);
        out[1] = Arrays.copyOfRange(src, idx, src.length);
        return out;
    }

    private class DP {

        String[] geom;
        double size;
        double[] cycle_profile;
        double nucl_StainDensity;
        double profileHomog;
        double avgBG;
    }

}
