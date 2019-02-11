/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload.driffta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author CODEX
 */
public class DeconvolutionInterlockDispatcher {

    private static File f = new File(System.getProperty("java.io.tmpdir")+"deconvolutionLock.lck");
    
    private static boolean hasLck;
    
    private static BufferedWriter br;

    public static boolean hasLock() {
        return hasLck;
    }
    
    
    public static void waitForLock() throws InterruptedException{
        while(f.exists()){
            try {
                f.delete();
            } catch (Exception e) {
                Driffta.log(e.toString());
            }
            Thread.sleep(100);
        }
    }
    
    public static void gainLock()throws IOException, InterruptedException{
        waitForLock();
        br = new BufferedWriter(new FileWriter(f));
        br.write("locked");
        
    }
    
    public static void releaseLock()throws IOException, InterruptedException{
        br.flush();
        br.close();
        f.delete();
    }
    

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
        try {
            f.delete();
        } catch (Exception e) {
            Driffta.log(e.toString());
        }
    }

}
