package com.akoya.codex.segm;

import java.io.*;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nikolay Samusik
 */
public class ConcatenateResults {

    public static void main(String[] args) throws Exception {
        
        File dir = new File(args[0]);
        
        ArrayList<String> regions = new ArrayList<>();
        
        for (File f : dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().startsWith("reg")&&f.getName().contains("_X")&&f.getName().contains("_Expression")&&(f.getName().endsWith(".txt")||f.getName().endsWith(".csv"));
                }
            })){
            String reg= f.getName().split("_")[0];
            if(!regions.contains(reg))regions.add(reg);
        }
        
       
        
        System.out.println("Found regions: " + regions.toString());
        
        
        
        for (String reg : regions) {
            
        for (String st : new String[]{"_Expression_Uncompensated.txt", "_Expression_Compensated.txt"}) {
            String headerLine = null;
            BufferedWriter bw = new BufferedWriter(new FileWriter(dir.getAbsolutePath() + File.separator + reg + st));
            for (File f : dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(st) && f.getName().startsWith(reg)&&f.getName().contains("_X")&&f.getName().contains("_Expression");
                }
            })) {
                System.out.println("Concatenating: " + f.getName());
                BufferedReader br = new BufferedReader(new FileReader(f));

                String s = br.readLine();
                if (s == null) {
                    continue;
                }
                if (headerLine == null) {
                    headerLine = s;
                    bw.write("Filename:Filename\t" + headerLine);
                }
                while ((s = br.readLine()) != null) {
                    bw.write("\n" + f.getName().split("\\.tif")[0] + "\t" + s);
                }
            }
            bw.flush();
            bw.close();
        }
        }

    }
}
