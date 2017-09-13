/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload.driffta;

import com.akoya.codex.upload.Experiment;
import com.akoya.codex.upload.ProcessingOptions;
import com.akoya.codex.upload.logger;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.plugin.Concatenator;
import ij.plugin.HyperStackConverter;
import ij.plugin.ZProjector;
import ij.process.ImageConverter;
import ij.process.LUT;
import org.scijava.util.FileUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

//Those dependenceis are unused but necessary for driftcomp to work
//imagescience-3.0.0.jar, legacy-imglib1-1-5, vecmath-scijava

/**
 *
 * @author Nikolay
 */
public class Driffta {

    private static final int version = 11;
    
    private static Deconvolve_mult dec;

    private static final boolean copy = false;
    private static boolean color = false;

    // Define the path to a local file.
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        SessionIdentifierGenerator gen = new SessionIdentifierGenerator();

        //args = new String[]{"H:\\3-7-17_Pfizer_tissue_10_multicycle", "H:\\processed_Pfizer_tissue10_withH&E", "1", "1"};
        Properties config = new Properties();

        config.load(new FileInputStream("config.txt"));

        final String TMP_SSD_DRIVE = config.get("TMP_SSD_DRIVE").toString();

        try {
            log("Starting drift compensation. Version " + version);
            log("Arguments as seen by the application:");

            for (int i = 0; i < args.length; i++) {
                log(i + ":" + args[i]);
            }

            if (args.length != 4) {
                throw new IllegalArgumentException("Invalid number of parameters. Must be 4.\nUsage: java -jar CODEX.jar Driffta <base_dir> <out_dir> <region=int> <tile=int> \nAll indices are one-based");
            }

            String baseDir = args[0];
            String outDir = args[1];

            final int region = Integer.parseInt(args[2]);
            final int tile = Integer.parseInt(args[3]);

            File expFile = new File(baseDir + File.separator + "Experiment.json");
            if (!expFile.exists()) {
                throw new IllegalStateException("Config file not found: " + expFile);
            }

            File propFile = new File(baseDir + File.separator + "processingOptions.json");
            if (!propFile.exists()) {
                throw new IllegalStateException("Config file not found: " + propFile);
            }

            final Experiment exp = Experiment.loadFromJSON(expFile);

            ProcessingOptions po = ProcessingOptions.load(propFile);

            color = exp.channel_arrangement.toLowerCase().trim().equals("color");

            if (!exp.deconvolution.equals("Microvolution")) {
                log("Deconvolution disabled based on Experiment.json");
            }

            final int numDeconvolutionDevices = po.getNumGPUs();

            File chNamesFile = new File(baseDir + File.separator + "channelNames.txt");

            ArrayList<String> chNamesAL = new ArrayList<>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(chNamesFile));
                String s = null;
                while ((s = br.readLine()) != null) {
                    chNamesAL.add(s);
                }
                br.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            final String[] channelNames = chNamesAL.toArray(new String[chNamesAL.size()]);

            int[][] deconvIterations = new int[exp.num_cycles][exp.channel_names.length];

            for (int i = 0; i < deconvIterations.length; i++) {
                for (int j = 0; j < deconvIterations[i].length; j++) {
                    if (i == 0 || j != exp.drift_comp_channel - 1) {
                        if (i == exp.num_cycles - 1 && exp.HandEstain) {
                            continue;
                        }
                        deconvIterations[i][j] = 25;
                    }
                }
            }

            /*
            File deconvMap = new File(baseDir + File.separator + "deconvolutionMap.txt");
            
           
            if (!deconvMap.exists() && !exp.deconvolution.equals("Microvolution")) {
                log("Could not find the file " + deconvMap.getPath() + "\n"
                        + "This file specifices the number of iterations of deconvolution for each cycle and channel. 0 iterations = no deconvolution. 25 is the expected default"
                        + "\nExpected file format:\n"
                        + "Cycle \t CH1 \t CH2 \t CH3 \t CH4 \t\n"
                        + "1 \t 25 \t 25 \t 25 \t 25\n"
                        + "2 \t 0 \t 25 \t 25 \t 25\n"
                        + "3 \t 0 \t 25 \t 25 \t 25\n"
                        + "4 ...");
                log("Deconvolution is disabled");
            } else {
                log("reading " + deconvMap.getPath());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(deconvMap));
                    br.readLine();
                    for (int i = 0; i < deconvIterations.length; i++) {
                        String line = br.readLine();
                        if (line == null) {
                            throw new IllegalStateException("Error in deconvolutionMap.txt: file shorter than the number of cycles");
                        }
                        String[] s = line.split("\t");
                        for (int j = 0; j < deconvIterations[i].length; j++) {
                            deconvIterations[i][j] = Integer.parseInt(s[j + 1]);
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    throw new IllegalStateException(e);
                }
            }*/
            String tmpDestDir = TMP_SSD_DRIVE + File.separator + gen.nextSessionId();

            File f = new File(tmpDestDir);
            f.mkdirs();

            ImagePlus imp = null;

            //com.sun.imageio.spi.FileImageInputStreamSpi stream = new FileImageInputStreamSpi();
            log("Opening files");
            final ImagePlus[] stack = new ImagePlus[exp.num_cycles * exp.num_z_planes * exp.channel_names.length];
            ExecutorService es = Executors.newWorkStealingPool(Math.min(32, Runtime.getRuntime().availableProcessors() * 2));

            HashSet<Callable<String>> alR = new HashSet<>();

            for (int cycle = 1; cycle <= exp.num_cycles; cycle++) {
                final int cycF = cycle;
                final String sourceDir = baseDir + File.separator + exp.getDirName(cycle, region, baseDir);
                for (int chIdx = 0; chIdx < exp.channel_names.length; chIdx++) {
                    final int chIdxF = chIdx;
                    final String ch = exp.channel_names[chIdx];
                    for (int zSlice = 1; zSlice <= exp.num_z_planes; zSlice++) {
                        final int lz = zSlice;
                        final String sourceFileName = sourceDir + File.separator + exp.getSourceFileName(sourceDir, exp.microscope, tile, zSlice, chIdxF);
                        final int idx = ((exp.num_z_planes * exp.channel_names.length) * (cycle - 1)) + (exp.channel_names.length * (zSlice - 1)) + chIdx;
                        //log("Creating file opening job for cycle="+cycle+", chIdx="+chIdx + ", zSlice="+zSlice);

                        if (!new File(sourceFileName).exists()) {
                            if (!exp.getDirName(cycF, region , baseDir).startsWith("HandE")){
                            log("Source file does not exist: " + sourceFileName);
                            }
                            continue;
                        }

                        final String destFileName = tmpDestDir + File.separator + "Cyc" + cycle + "_reg" + region + "_" + exp.getSourceFileName(sourceDir, exp.microscope, tile, zSlice, chIdxF);

                        final String cmd = "./lib/tiffcp -c none \"" + sourceFileName + "\" \"" + destFileName + "\"";

                        if (new File(destFileName).exists()) {
                            if (new File(destFileName).length() > 10000) {
                                //log("File already exists, skipping: " + destFileName);
                                continue;
                            }
                        }

                        alR.add(new Callable<String>() {
                            @Override
                            public String call() throws IOException, InterruptedException {

                                //log("Opening file: " + sourceFileName);
                                File f = new File(destFileName);
                                do {
                                    Process p = Runtime.getRuntime().exec(cmd);
                                    p.waitFor();
                                    if (!f.exists()) {
                                        log("Copy process finished but the dest file does not exist: " + destFileName + " trying again.");
                                    }
                                } while (!f.exists());

                                Opener o = new Opener();
                                stack[idx] = o.openImage(destFileName);

                                if (stack[idx] != null) {
                                    if (color || stack[idx].getStack().getSize() != 1) {
                                        if (!exp.getDirName(cycF, region, baseDir).startsWith("HandE")) {
                                            ZProjector zp = new ZProjector();
                                            log("Flattening " + stack[idx].getTitle() + ", nslices" + stack[idx].getNSlices() + "ch=" + stack[idx].getNChannels() + "stacksize=" + stack[idx].getStack().getSize());
                                            zp.setImage(stack[idx]);
                                            zp.setMethod(ZProjector.MAX_METHOD);
                                            zp.doProjection();
                                            stack[idx] = zp.getProjection();
                                        }
                                    }
                                    //stack[idx].setTitle(channelNames[(cycF - 1) * exp.channel_names.length + chIdxF]);
                                    return "Image opened: " + destFileName;
                                } else {
                                    return "Image opening failed: " + sourceFileName;
                                }

                            }
                        });

                    }
                }
            }

            log("Submitting file opening jobs");

            List<Future<String>> lst = es.invokeAll(alR);

            log("All file opening jobs submitted");

            for (Future<String> future : lst) {
                future.get();
            }

            es.shutdown();
            es.awaitTermination(100000, TimeUnit.HOURS);

            log("All files opened. Deleting temporary dir");

            new Thread() {
                @Override
                public void run() {
                    FileUtils.deleteRecursively(new File(tmpDestDir));
                }
            }.start();

            if (exp.HandEstain) {
                int cycle = exp.num_cycles;
                for (int zSlice = 1; zSlice <= exp.num_z_planes; zSlice++) {
                    int ch = -1;
                    for (int chIdx = 0; chIdx < exp.channel_names.length; chIdx++) {
                        int idx = ((exp.num_z_planes * exp.channel_names.length) * (cycle - 1)) + (exp.channel_names.length * (zSlice - 1)) + chIdx;
                        if (stack[idx] != null) {
                            ch = chIdx;
                        }
                    }
                    if (ch == -1) {
                        throw new IllegalStateException("H&E image slice is absent for z=" + zSlice);
                    }

                    int idx = ((exp.num_z_planes * exp.channel_names.length) * (cycle - 1)) + (exp.channel_names.length * (zSlice - 1)) + ch;

                    ImagePlus he = stack[idx];

                    if (he.getBitDepth() != 24) {
                        throw new IllegalStateException("Expected a 24-bit RGB image");
                    }

                    //ImagePlus he_R = he.getImageStack();
                    int numCh = exp.channel_names.length;

                    String[] colorNames = new String[]{"R", "G", "B"};

                    ImageConverter ic = new ImageConverter(he);
                    ic.convertToRGBStack();
                    ic.convertToGray16();

                    int k = 1;
                    for (int i = numCh - 3; i < numCh; i++) {
                        idx = ((exp.num_z_planes * exp.channel_names.length) * (cycle - 1)) + (exp.channel_names.length * (zSlice - 1)) + i;
                        ImagePlus he_S = new ImagePlus("HandE_" + colorNames[i - 1], he.getStack().getProcessor(k++).duplicate());
                        stack[idx] = he_S;
                        stack[idx].getProcessor().multiply(250);
                    }

                    int driftCh = exp.drift_comp_channel;
                    ImagePlus he_R = new ImagePlus("HandE_R_inv", he.getStack().getProcessor(1).duplicate());
                    he_R.getProcessor().invert();
                    idx = ((exp.num_z_planes * exp.channel_names.length) * (cycle - 1)) + (exp.channel_names.length * (zSlice - 1)) + (driftCh - 1);
                    stack[idx] = he_R;
                }
            }
            imp = new Concatenator().concatenate(stack, false);


            /* else {
                log("Enumerating cycles, copying files");
                ExecutorService es = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
                for (int cycle = 1; cycle <= exp.num_cycles; cycle++) {
                    String sourceDir = baseDir + File.separator + Experiment.getDirName(cycle, region);
                    for (int chIdx = 0; chIdx < exp.channel_names.length; chIdx++) {
                        String ch = exp.channel_names[chIdx];
                        for (int zSlice = 1; zSlice < exp.num_z_planes; zSlice++) {
                            String sourceFileName = sourceDir + File.separator + exp.getSourceFileName(sourceDir, exp.microscope, tile, zSlice, ch);
                            String destFileName = tmpDestDir + File.separator + Experiment.getDestFileName(exp.tiling_mode, tile, zSlice, chIdx + 1, cycle, region, exp.region_width);
                            File src = new File(sourceFileName);
                            int k = 1;
                            while (!src.exists() && zSlice - k >= 1) {
                                log("source file doesn not exist:" + sourceFileName);
                                sourceFileName = sourceDir + File.separator + exp.getSourceFileName(sourceDir, exp.microscope, tile, zSlice - k, ch);
                                log("trying z-1:" + sourceFileName);
                                src = new File(sourceFileName);
                            }
                            File fcp = new File("tiffcp.exe");
                            final String cmd = fcp.exists() ? "tiffcp -c none \"" + sourceFileName + "\" \"" + destFileName + "\"" : "\"C:\\Program Files (x86)\\GnuWin32\\bin\\tiffcp\" -c none \"" + sourceFileName + "\" \"" + destFileName + "\"";

                            if (new File(destFileName).exists()) {
                                if (new File(destFileName).length() > 10000) {
                                    //log("File already exists, skipping: " + destFileName);
                                    continue;
                                }
                            }
                            //log(cmd);
                            es.execute(new Runnable() {
                                public void run() {
                                    try {
                                        int k = 0;

                                        do {
                                            Process p = Runtime.getRuntime().exec(cmd);
                                            p.waitFor();
                                            File f = new File(destFileName);
                                            if (!f.exists()) {
                                                log("Copy process finished but the dest file does not exist: " + destFileName + " trying again:" + (k++));
                                            }
                                        } while (!f.exists());

                                        if (color) {
                                            final Opener o = new Opener();
                                            ImagePlus ip = o.openTiff(destFileName, "");
                                            ZProjector zp = new ZProjector(ip);
                                            zp.setMethod(ZProjector.MAX_METHOD);
                                            zp.doProjection();
                                            ip = zp.getProjection();
                                            FileSaver fs = new FileSaver(ip);
                                            fs.saveAsTiff(destFileName);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }

                es.shutdown();
                boolean finshed = es.awaitTermination(100000, TimeUnit.HOURS);
                String destFileName = tmpDestDir + File.separator + Experiment.getDestFileName(exp.tiling_mode, tile, 1, 1, 1, region, exp.region_width);

                log("Opening image sequence");
                IJ.run("Image Sequence...", "open=[" + destFileName + "] sort");

                imp = IJ.getImage();
            }*/
            final double XYres = exp.per_pixel_XY_resolution;
            final double Zpitch = exp.z_pitch;

            final int[] wavelenghths = exp.emission_wavelengths;
            //Alt impl

            ImagePlus hyp = HyperStackConverter.toHyperStack(imp, exp.channel_names.length, exp.num_z_planes, exp.num_cycles, "xyczt", "composite");

            imp = null;

            log("Drift compensation");
            log("Waiting for driftcomp interlock");
            DriftcompInterlockDispatcher.gainLock();
            log("Interlock acquired");

            Driftcomp.compensateDrift(hyp, exp.drift_comp_channel - 1);

            DriftcompInterlockDispatcher.releaseLock();

            log("Cropping");

            int Y = Integer.parseInt(Experiment.getDestStackFileName(exp.tiling_mode, tile, region, exp.region_width).split("Y")[1].split("\\.")[0]);
            int X = Integer.parseInt(Experiment.getDestStackFileName(exp.tiling_mode, tile, region, exp.region_width).split("X")[1].split("_")[0]);

            if (po.isUseBleachMinimizingCrop()) {
                hyp = (Y % 2 == 1) ? new ImagePlus(hyp.getTitle(), hyp.getImageStack().crop(X == 1 ? 0 : exp.tile_overlap_X, Y == 1 ? 0 : exp.tile_overlap_Y, 0, hyp.getWidth() - (X == 1 ? 0 : exp.tile_overlap_X), hyp.getHeight() - (Y == 1 ? 0 : exp.tile_overlap_Y), hyp.getStackSize()))
                        : new ImagePlus(hyp.getTitle(), hyp.getImageStack().crop(0, exp.tile_overlap_Y, 0, hyp.getWidth() - (X == exp.region_width ? 0 : exp.tile_overlap_X), hyp.getHeight() - exp.tile_overlap_Y, hyp.getStackSize()));
            } else {
                hyp = new ImagePlus(hyp.getTitle(), hyp.getImageStack().crop((int) Math.floor(exp.tile_overlap_X / 2), (int) Math.floor(exp.tile_overlap_Y / 2), 0, hyp.getWidth() - (int) Math.ceil(exp.tile_overlap_X), hyp.getHeight() - (int) Math.ceil(exp.tile_overlap_Y), hyp.getStackSize()));
            }

            hyp = HyperStackConverter.toHyperStack(hyp, exp.channel_names.length, exp.num_z_planes, exp.num_cycles, "xyczt", "composite");

            log("Running deconvolution");

            double ObjectiveRI = 1.0;

            if ("oil".equals(exp.objectiveType)) {
                ObjectiveRI = 1.5115;
            }

            if ("water".equals(exp.objectiveType)) {
                ObjectiveRI = 1.33;
            }

            log("Waiting for deconvolution interlock");
            DeconvolutionInterlockDispatcher.gainLock();
            log("Interlock acquired");
            dec = new Deconvolve_mult(!exp.deconvolution.equals("Microvolution"), numDeconvolutionDevices, po.isUseBlindDeconvolution());

            dec.runDeconvolution(hyp, XYres, Zpitch, wavelenghths, deconvIterations, exp.drift_comp_channel - 1, exp.numerical_aperture, ObjectiveRI);

            DeconvolutionInterlockDispatcher.releaseLock();

            if (hyp.getNChannels() == 4) {
                ((CompositeImage) hyp).setLuts(new LUT[]{LUT.createLutFromColor(Color.WHITE), LUT.createLutFromColor(Color.RED), LUT.createLutFromColor(Color.GREEN), LUT.createLutFromColor(new Color(0, 70, 255))});
            } else if (hyp.getNChannels() == 3) {
                ((CompositeImage) hyp).setLuts(new LUT[]{LUT.createLutFromColor(Color.RED), LUT.createLutFromColor(Color.GREEN), LUT.createLutFromColor(new Color(0, 70, 255))});
            }

            FileSaver fs = new FileSaver(hyp);
            String outStr = outDir + File.separator + Experiment.getDestStackFileName(exp.tiling_mode, tile, region, exp.region_width);

            log("Saving result file: " + outStr);
            fs.saveAsTiff(outStr);

            if (copy) {
                delete(new File(tmpDestDir));
            }

            String bestFocus = outDir + File.separator + "bestFocus" + File.separator;

            File bfFile = new File(bestFocus);
            if (!bfFile.exists()) {
                bfFile.mkdirs();
            }
            
            log("Running best focus");
            
            ImagePlus focused = BestFocus.createBestFocusStackFromHyperstack(hyp, exp.drift_comp_channel);
           
            log("Saving the focused tiff");
            fs = new FileSaver(focused);
            fs.saveAsTiff(bestFocus + File.separator + Experiment.getDestStackFileName(exp.tiling_mode, tile, region, exp.region_width));

            /*
            Duplicator dup = new Duplicator();
            for (int fr = 1; fr <= focused.getNFrames(); fr++) {
                fs = new FileSaver(dup.run(focused, 1, focused.getNChannels(), 1, focused.getNSlices(), fr, fr));
                fs.saveAsPng(bestFocus + File.separator + Experiment.getDestPNGFileName(exp.tiling_mode, tile, region, exp.region_width, fr));
            }*/
            
            WindowManager.closeAllWindows();

            exp.tile_width = hyp.getWidth();
            exp.tile_height = hyp.getHeight();

            exp.saveToFile(expFile);
            
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            if (logStream != null) {
                e.printStackTrace(logStream);
                logStream.flush();
                logStream.close();
            }
            System.exit(2);
        }
    }

    private static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    private static File logFile = null;
    private static PrintStream logStream;

    public static void log(String s) {
        logger.print(s);
        /*if (false && logFile == null) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_h-mm-ss");
            String formattedDate = sdf.format(date);

            logFile = new File("Driffta_log_" + formattedDate);
            try {
                logStream = new PrintStream(logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logStream.println(s);
        }
        System.out.println(s);*/
    }

    @Override
    protected void finalize() throws Throwable {
        if (logStream != null) {
            logStream.println("writtenFromFinalize");
            logStream.flush();
            logStream.close();
        }
        DriftcompInterlockDispatcher.releaseLock();
        DeconvolutionInterlockDispatcher.releaseLock();
    }

}
