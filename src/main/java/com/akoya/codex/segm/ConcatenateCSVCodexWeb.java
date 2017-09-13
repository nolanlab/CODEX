package com.akoya.codex.segm;

import net.sf.flowcyt.gp.module.csv2fcs.CSV2FCSApp;

import java.io.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nikolay Samusik
 */
public class ConcatenateCSVCodexWeb {

    public static void main(String[] args) throws IOException {
        File dir = new File(args[0]);
        final File out = new File(args[0] + File.separator + "compensated_all.csv");
        BufferedWriter bw = null;


        for (File f : dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f == null) return false;
                return (f.getName().startsWith("compensated") && f.getName().endsWith(".csv"));
            }
        })) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(out));

                String s2 = br.readLine();
                if(s2==null){
                    continue;
                }
                String [] s = s2.split(",");

                String newHed = "";

                for (int i = 0; i < s.length; i++) {
                   if(!s[i].contains(":")){
                       newHed += s[i]+":"+s[i]+",";
                   }else{
                       newHed = s[i] +",";
                   }
                }

                bw.write(newHed + "\n");
            } else {
                br.readLine();
            }
            String l = null;
            while ((l = br.readLine()) != null) {
                bw.write(l);
                bw.write("\n");
            }
        }
        if (bw != null) {
            bw.flush();
            bw.close();
        }

        System.out.print("Creating FCS files");

        CSV2FCSApp.main(new String[]{"-InputFile:" + out.getAbsolutePath() + ""});


        
    }

}
