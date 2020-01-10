package org.nolanlab.codex.upload.gui;

import ij.IJ;
import ij.ImagePlus;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.nolanlab.codex.upload.Experiment;
import org.nolanlab.codex.upload.ProcessingOptions;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class WorkerHelper {
    private GuiHelper guiHelper = new GuiHelper();

    /*
    Method to process tiles and call driffta based on selective processing or not
     */
    public void processTiles(Experiment exp, NewGUI gui, ProcessingOptions po,
                              List<Process> allProcess, int currCnt, String maxRAM, int minTile, int maxTile, int reg) throws IOException {
        for (int tile = minTile; tile <= maxTile; tile++) {
            File d;
            if (!po.isExportImgSeq()) {
                d = new File(po.getTempDir() + File.separator + Experiment.getDestStackFileName(exp.tiling_mode, tile, reg, exp.region_width));
            } else {
                d = new File(po.getTempDir() + File.separator + "tiles" + File.separator + FilenameUtils.removeExtension(Experiment.getDestStackFileName(exp.tiling_mode, tile, reg, exp.region_width)));
            }
            int numTrial = 0;
            while (!d.exists() && numTrial < 3) {
                numTrial++;
                if (SystemUtils.IS_OS_WINDOWS) {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \".\\*\" org.nolanlab.codex.upload.driffta.Driffta \"" + gui.getInputPathField().getText() + "\" \"" + po.getTempDir() + "\" " + reg + " " + tile);
                    pb.redirectErrorStream(true);

                    guiHelper.log("Starting process: " + pb.command().toString());
                    Process proc = pb.start();
                    allProcess.add(proc);

                    guiHelper.waitAndPrint(proc);
                    guiHelper.log("Driffta done");
                } else if (SystemUtils.IS_OS_LINUX) {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "java -Xms5G -Xmx" + maxRAM + "G -Xmn50m -cp \"./*\" org.nolanlab.codex.upload.driffta.Driffta \"" + gui.getInputPathField().getText() + "\" \"" + po.getTempDir() + "\" " + reg + " " + tile);
                    pb.redirectErrorStream(true);

                    guiHelper.log("Starting process: " + pb.command().toString());
                    Process proc = pb.start();
                    allProcess.add(proc);

                    guiHelper.waitAndPrint(proc);
                    guiHelper.log("Driffta done");
                }
            }
            if (!d.exists()) {
                guiHelper.log("Tile processing failed 3 times in a row: " + d.getName());
            }
            gui.getProgressBar().setValue(currCnt++);
            gui.getMainPanel().repaint();
        }
    }

    /*
   Replaces tile overlap in percent with pixel value in the exp.json file
    */
    public void replaceTileOverlapInExp(File dir, Experiment exp) {
        if(dir != null) {
            for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File[] cycFiles = cyc.listFiles(tif->tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif"));
                    ImagePlus imp = IJ.openImage(cycFiles[0].getAbsolutePath());
                    exp.tile_overlap_X  = (int)((double)(exp.tile_overlap_X *imp.getWidth()/100));
                    exp.tile_overlap_Y = (int)((double)(exp.tile_overlap_Y*imp.getHeight()/100));
                    break;
                }
            }
        }
    }

    public boolean areEmptyFields(NewGUI gui) {
        if (gui.getPreviewCycleField().getText().equals(null) || gui.getPreviewCycleField().getText().equals("")) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please specify a valid cycle before proceeding!");
            return false;
        }
        else if (gui.getPreviewRegionField().getText().equals(null) || gui.getPreviewRegionField().getText().equals("")) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please specify a valid region before proceeding!");
            return false;
        }
        else if (gui.getPreviewChannelField().getText().equals(null) || gui.getPreviewChannelField().getText().equals("")) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please specify a valid channel before proceeding!");
            return false;
        }
        else if (gui.getPreviewZPlaneField().getText().equals(null) || gui.getPreviewZPlaneField().getText().equals("")) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Please specify a valid z-slice before proceeding!");
            return false;
        }
        return true;
    }

    public boolean areValidFields(Experiment exp, int cyc, int reg, int ch, int z, NewGUI gui) {
        if (cyc < 0 || cyc > exp.num_cycles) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Cycle cannot be lesser than 0 or greater than total number of cycles!");
            return false;
        } else if (reg < 0 || reg > exp.regIdx.length) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Region cannot be lesser than 0 or greater than total number of regions!");
            return false;
        } else if (ch < 0 || ch > exp.readout_channels.length) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Channel cannot be lesser than 0 or greater than total number of channels!");
            return false;
        } else if (z < 0 || z > exp.num_z_planes) {
            JOptionPane.showMessageDialog(gui.getMainPanel(), "Z-slice cannot be lesser than 0 or greater than total number of z-planes!");
            return false;
        }
        return true;
    }

    public int[] extractXYFromFile(File f, Experiment exp, int reg) {
        String[] s = f.getName().split("[_\\.]");
        int tileNumber = Integer.parseInt(s[1]);
        int Y = Integer.parseInt(Experiment.getDestStackFileName(exp.tiling_mode, tileNumber, reg, exp.region_width).split("Y")[1].split("\\.")[0]);
        int X = Integer.parseInt(Experiment.getDestStackFileName(exp.tiling_mode, tileNumber, reg, exp.region_width).split("X")[1].split("_")[0]);
        int[] ret = new int[]{X, Y};
        return ret;
    }
}
