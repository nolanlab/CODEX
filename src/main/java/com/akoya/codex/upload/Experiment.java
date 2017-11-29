/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Nikolay
 */
public class Experiment {
    //Experiment.json

    public final String name;
    public final String date;
    public final String codex_instrument;
    public final String microscope;
    public final String deconvolution;
    public final int magnification;
    public final double numerical_aperture;
    public final double per_pixel_XY_resolution;
    public final double z_pitch;
    public final int num_z_planes;
    public final String channel_arrangement;
    public final String[] channel_names;
    public final int[] emission_wavelengths;
    public final int drift_comp_channel;
    public final int num_cycles;
    public final int[] regIdx;
    public final String[] region_names;
    public final String tiling_mode;
    public final int region_width;
    public final int region_height;
    public final int tile_overlap_X;
    public final int tile_overlap_Y;
    public final int[] readout_channels;
    public final String objectiveType;
    public boolean HandEstain;
    public int tile_height;
    public int tile_width;
    public int driftCompReference;

    public final String projName;

    public Experiment(String name, String date, String codex_instrument, String microscope,
            String deconvolution, int magnification, double numerical_aperture, double per_pixel_XY_resolution,
            double z_pitch, int num_z_planes, String channel_arrangement, String[] channel_names,
            int[] channelWavelen, int drift_comp_channel, int driftCompReference, int num_cycles, int[] regIdx,
            String[] region_names, String tiling_mode, int region_width,
            int region_height, int tile_overlap_X, int tile_overlap_Y,
            String objectiveType, boolean HandEstain, String projName) {
        this.name = name;
        this.date = date;
        this.codex_instrument = codex_instrument;
        this.microscope = microscope;
        this.deconvolution = deconvolution;
        this.magnification = magnification;
        this.numerical_aperture = numerical_aperture;
        this.per_pixel_XY_resolution = per_pixel_XY_resolution;
        this.z_pitch = z_pitch;
        this.num_z_planes = num_z_planes;
        this.channel_arrangement = channel_arrangement;
        this.channel_names = channel_names;
        this.drift_comp_channel = drift_comp_channel;
        this.driftCompReference = driftCompReference;
        this.num_cycles = num_cycles;
        this.regIdx = regIdx;
        this.region_names = region_names;
        this.tiling_mode = tiling_mode;
        this.region_width = region_width;
        this.region_height = region_height;
        this.tile_overlap_X = tile_overlap_X;
        this.tile_overlap_Y = tile_overlap_Y;
        this.emission_wavelengths = channelWavelen;
        this.readout_channels = new int[channel_names.length - 1];
        this.objectiveType = objectiveType;
        if (!(objectiveType.equals("air") || objectiveType.equals("water") || objectiveType.equals("oil"))) {
            throw new IllegalArgumentException("Illegal objective type:" + objectiveType + ". Allowed values are: air, water, oil");
        }
        int k = 0;
        for (int i = 1; i <= channel_names.length; i++) {
            if (i == drift_comp_channel) {
                continue;
            }
            readout_channels[k++] = i;
        }
        this.HandEstain = HandEstain;
        this.projName = projName;
    }

    public String getDirName(int cycle, int region, String baseDir) {

        String name = null;

        switch (microscope) {
            case "Keyence BZ-X710":
                name = "Cyc" + cycle + "_reg" + region;

                if (cycle == this.num_cycles && this.HandEstain) {
                    name = getHandEDirName(region);
                }
                break;
            case "Zeiss ZEN":
                if (region > 1) {
                    throw new UnsupportedOperationException("The processing of Zeiss data supports only 1 region at the moment  ");
                }
                File sourceDirF = new File(baseDir);

                Map<String, List<File>> map = Arrays.asList(sourceDirF.listFiles(z -> (z.isDirectory() && z.getName().contains("Image Export")))).stream().collect(Collectors.groupingBy(t -> t.getName().split("-")[t.getName().split("-").length - 1]));
                String key = String.format("%02d", cycle);
                List<File> f = map.get(key);
                if(f.size()==0){
                    throw new IllegalStateException("No directory fond for cycle = " + cycle + ", region = " + region + " basedir = " + baseDir);
                }
                if(f.size()>1){
                    throw new IllegalStateException("Multiple directoris fond for cycle = " + cycle + ", region = " + region + " basedir = " + baseDir+ "\nPlease remove duplicate directories before continuing\n"+f.toString()) ;
                }
                name = f.get(0).getName();
                break;
            default:
                throw new IllegalArgumentException("Unsupported microscope: " + microscope);
        }

        //System.out.println("Cycle=" + cycle + ", region=" + region + "this.numCycles=" + this.num_cycles + "HandEstain=" + this.HandEstain + ", name=" + name);
        return name;
    }

    public static String getHandEDirName(int region) {
        return "HandE_reg" + region;
    }

   

    public static String getDestFileName(final String tilingMode, final int tile, final int zSlice, final int channel, final int cycle, final int region, final int region_width) {
        final int zbTile = tile - 1;
        switch (tilingMode) {
            case "snake":
                int X = zbTile % region_width;
                int Y = zbTile / region_width;
                if (Y % 2 == 1) {
                    X = region_width - X;
                    X--;
                }
                X++;
                Y++;
                return String.format("cyc%03d_reg%03d_X%02d_Y%02d_Z%03d_C%01d", cycle, region, X, Y, zSlice, channel) + ".tif";
            default:
                throw new IllegalArgumentException("Unsupported tiling mode: " + tilingMode);
        }
    }

    public String toJSON() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT) // include static
                .create();
        String js = gson.toJson(this).replaceAll(",", ",\n");
        return js;
    }

    public static Experiment loadFromJSON(File f) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(f));
        Experiment exp = gson.fromJson(reader, Experiment.class);
        return exp;
    }
    
    
     public void saveToFile(File f) throws IOException {
        String js = this.toJSON();
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(js);
        bw.flush();
        bw.close();
    }

    public static String getDestStackFileName(final String tilingMode, final int tile, final int region, final int region_width) {
        final int zbTile = tile - 1;
        switch (tilingMode) {
            case "snake":
                int X = zbTile % region_width;
                int Y = zbTile / region_width;
                if (Y % 2 == 1) {
                    X = region_width - X;
                    X--;
                }
                X++;
                Y++;
                return String.format("reg%03d_X%02d_Y%02d", region, X, Y) + ".tif";
            default:
                throw new IllegalArgumentException("Unsupported tiling mode: " + tilingMode);
        }
    }

    public static String getDestStackFileNameWithZIndex(final String tilingMode, final int tile, final int region, final int region_width, final int zIndex) {
        final int zbTile = tile - 1;
        switch (tilingMode) {
            case "snake":
                int X = zbTile % region_width;
                int Y = zbTile / region_width;
                if (Y % 2 == 1) {
                    X = region_width - X;
                    X--;
                }
                X++;
                Y++;
                return String.format("reg%03d_X%02d_Y%02d_Z%02d", region, X, Y, zIndex) + ".tif";
            default:
                throw new IllegalArgumentException("Unsupported tiling mode: " + tilingMode);
        }
    }

    public static String getDestPNGFileName(final String tilingMode, final int tile, final int region, final int region_width, int cycle) {
        final int zbTile = tile - 1;
        switch (tilingMode) {
            case "snake":
                int X = zbTile % region_width;
                int Y = zbTile / region_width;
                if (Y % 2 == 1) {
                    X = region_width - X;
                    X--;
                }
                X++;
                Y++;
                return String.format("reg%03d_X%02d_Y%02d_Cyc%02d", region, X, Y, cycle) + ".png";
            default:
                throw new IllegalArgumentException("Unsupported tiling mode: " + tilingMode);
        }
    }

    public static HashMap<String, String> projectNameCache = new HashMap<>();

    public static final String[] microscopeTypes = new String[]{"Keyence BZ-X710", "Zeiss ZEN"};

    public String getSourceFileName(final String sourceDir, final String microscope, final int tile, final int zSlice, final int channel) {

        switch (microscope) {
            case "Keyence BZ-X710":
                String pname = projectNameCache.get(sourceDir);
                if (pname == null) {
                    File[] f = new File(sourceDir).listFiles((a) -> (a.isFile() && a.getName().endsWith(".bcf")));
                    if (f == null) {
                        throw new IllegalArgumentException("Directory does not contain a .bcf file: " + sourceDir);
                    }
                    if (f.length == 0) {
                        throw new IllegalArgumentException("Directory does not contain a .bcf file: " + sourceDir);
                    }
                    pname = f[0].getName().substring(0, f[0].getName().length() - 4);
                    projectNameCache.put(sourceDir, pname);
                }
                return pname + String.format("_%05d_Z%03d_", tile, zSlice) + channel_names[channel] + ".tif";
            case "Zeiss ZEN":
                File sourceDirF = new File(sourceDir);
                String n1 = sourceDirF.getName();
                String n2 = String.format("z%02dc%01dm%01d", zSlice, channel+1, tile) + "_ORG.tif";
                return n1 + "_" + n2;
            default:
                throw new IllegalArgumentException("Unsupported microscope: " + microscope);
        }
    }

}
