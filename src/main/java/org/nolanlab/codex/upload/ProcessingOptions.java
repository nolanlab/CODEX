/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URL;

/**
 *
 * @author Nikolay Samusik
 */
public class ProcessingOptions {

    private File tempDir;
    private boolean useBleachMinimizingCrop;
    private boolean useBlindDeconvolution;
    private URL destinationUrl;
    private String username;
    private String password;
    private boolean doProcessing;
    private boolean doUpload;
    private boolean exportTiff;
    private boolean exportImgSeq;
    private int numThreads;
    
    

    private static String encoder = "encode";

    public static ProcessingOptions load(File f) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(f));
        ProcessingOptions out = gson.fromJson(reader, ProcessingOptions.class);
        return out;
    }

    public int getNumThreads() {
        return numThreads;
    }

    
    
    public ProcessingOptions(File tempDir, boolean useBleachMinimizingCrop, boolean useBlindDeconvolution, int numThreads, URL destinationUrl, String username, String password, boolean doUpload, boolean exportTiff, boolean exportImgSeq) {
        this.tempDir = tempDir;
        this.useBleachMinimizingCrop = useBleachMinimizingCrop;
        this.useBlindDeconvolution = useBlindDeconvolution;
        this.numThreads = numThreads;
        this.destinationUrl = destinationUrl;
        this.username = username;
        this.password = EncryptUtils.xorMessage(password, encoder);
        this.doProcessing = true;
        this.doUpload = doUpload;
        this.exportTiff = exportTiff;
        this.exportImgSeq = exportImgSeq;
    }

    public URL getDestinationUrl() {
        return destinationUrl;
    }

    public boolean doProcessing() {
        return doProcessing;
    }

    public boolean doUpload() {
        return doUpload;
    }

    public boolean isUseBleachMinimizingCrop() {
        return useBleachMinimizingCrop;
    }

    public boolean isUseBlindDeconvolution() {
        return useBlindDeconvolution;
    }

    public File getTempDir() {
        return tempDir;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return EncryptUtils.xorMessage(password, encoder);
    }

    public boolean isExportTiff() {
        return exportTiff;
    }

    public void setExportTiff(boolean exportTiff) {
        this.exportTiff = exportTiff;
    }

    public boolean isExportImgSeq() {
        return exportImgSeq;
    }

    public void setExportImgSeq(boolean exportImgSeq) {
        this.exportImgSeq = exportImgSeq;
    }

    public void saveToFile(File f) throws IOException {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT) // include static
                .create();
        String js = gson.toJson(this).replaceAll(",", ",\n");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(js);
        bw.flush();
        bw.close();
    }
}
