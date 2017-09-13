/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 * @author Nikolay
 */
public class logger {

    private static int outputMode = 0;
    public static final int OUTPUT_MODE_CONSOLE = 0;
    public static final int OUTPUT_MODE_GUI = 1;
    public static final int OUTPUT_MODE_NONE = 2;
    public static PrintStream addlStream;
    
    public static void setAdditionalWriter(PrintStream stream){
        addlStream = stream;
    }

    public static void print(Object... obj) {
        
        if(outputMode==OUTPUT_MODE_NONE) return;
        if (obj.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < obj.length - 1; i++) {
            sb.append(obj[i]);
            sb.append(", ");
        }
        sb.append(obj[obj.length - 1]);
        
        System.err.println(sb.toString());
        if(addlStream!=null){
            addlStream.println(sb.toString());
        }
    }

    public static void setOutputMode(int mode) {
        if (mode < 0 || mode > 2) {
            throw new IllegalArgumentException("Mode value is outside of the allowed range");
        }
        outputMode = mode;
    }

    public static void showException(Throwable e) {
        if (outputMode == OUTPUT_MODE_NONE) {
            return;
        }
        if (outputMode == OUTPUT_MODE_GUI) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bs);
            e.printStackTrace(ps);
            String s = bs.toString();
            String[] s2 = s.split("\n");
            String s3 = "";
            for (int i = 0; i < Math.min(s2.length,5); i++) {
                s3 += s2[i] + "\n";
            }
            s3 += "...";
            JOptionPane.showMessageDialog(null, s3, "Exception", JOptionPane.ERROR_MESSAGE);
        } else {
            e.printStackTrace();
        }
        
        if(addlStream!=null){
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bs);
            e.printStackTrace(ps);
            String s = bs.toString();
            String[] s2 = s.split("\n");
            String s3 = "";
            for (int i = 0; i < s2.length; i++) {
                s3 += s2[i] + "\n";
            }
            s3 += "...";
            addlStream.print(s3 + "\n");
        }

    }
}
