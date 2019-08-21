/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload.driffta;

import org.nolanlab.codex.upload.TileObject;
import org.nolanlab.codex.upload.logger;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.plugin.HyperStackConverter;
import ij.plugin.StackCombiner;
import ij.process.LUT;
import ij.process.StackProcessor;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.nolanlab.codex.upload.driffta.Driffta.log;

/**
 *
 * @author Nikolay Samusik
 */
public class MakeMontage {
    static int stackWidth = 0;
    static int stackHeight = 0;
    static int stackSize = 0;
    static int stackBitDepth = 0;

    public static void main(String[] args) throws IOException {
        //args = new String[]{"I:\\Nikolay\\4-20-16 Panel test on tonsil\\bestFocus", "2"};
        //args = new String[]{"I:\\Nikolay\\41-parameter 16 cycles melanoma Nikolay 4-18-17\\analysis\\montages\\New folder", "4"};
        if (args.length == 0) {
            System.err.println("USAGE:\n MakeMontage <path-to-best-focused-stacks> <optional:downsampling-factor (default:2)>");
            return;
        }

        int fc = 2;
        if (args.length == 2) {
            fc = Integer.parseInt(args[1]);
        }

        final int factor = fc;

        final File file = new File(args[0]);
        File[] tiff = file.listFiles(n -> n.getName().startsWith("reg") && n.getName().contains("_X") && n.getName().contains("_Y") && (n.getName().endsWith(".tiff") || n.getName().endsWith(".tif")));

        logger.print("Found " + tiff.length + " TIFF files:");
        for (File file1 : tiff) {
            logger.print(file1);
        }

        //Create the regionMap.txt file with the TileObject content here
        logger.print("Creating tileMap.txt file...");
        createTileMap(tiff, file);

        ImagePlus imp = new Opener().openImage(tiff[0].getAbsolutePath());

        Arrays.asList(tiff).stream().collect(Collectors.groupingBy(t -> t.getName().split("_")[0])).forEach((regname, filesInReg) -> {
            //final String regName = regname;
            int maxX = 0;
            int maxY = 0;
            for (File f2 : filesInReg) {
                try {
                    int[] coord = extractXYCoord(f2);
                    maxX = Math.max(maxX, coord[0]);
                    maxY = Math.max(maxY, coord[1]);
                } catch (Exception e) {

                }
            }
            ImageStack[][] grid = new ImageStack[maxX][maxY];

            for (File f2 : filesInReg) {
                try {
                    int[] coord = extractXYCoord(f2);

                    ImagePlus tmp = new Opener().openImage(f2.getAbsolutePath());

                    ImageStack is = tmp.getImageStack();
                    if(is != null) {
                        stackWidth = is.getWidth()/factor;
                        stackHeight = is.getHeight()/factor;
                        stackSize = is.getSize()/factor;
                        stackBitDepth = is.getBitDepth();
                    }

                    StackProcessor sp = new StackProcessor(is);

                    grid[coord[0] - 1][coord[1] - 1] = sp.resize(tmp.getWidth() / factor, tmp.getHeight() / factor);

                } catch (Exception e) {
                    logger.showException(e);
                    System.out.println(e.getMessage());
                }
            }

            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[x].length; y++) {
                    if (grid[x][y] == null) {
                        log("Tile is null: " + regname + " X=" + (x + 1) + ", Y=" + (y + 1));
                        log("Will proceed with creating montage with a blank tile here...");
                        grid[x][y] = ImageStack.create(stackWidth, stackHeight, stackSize, stackBitDepth);
                    }

                }
            }

            ImageStack[] horizStacks = new ImageStack[grid[0].length];

            StackCombiner stackCombiner = new StackCombiner();

            for (int y = 0; y < horizStacks.length; y++) {
                horizStacks[y] = grid[0][y];
                for (int x = 1; x < grid.length; x++) {
                    horizStacks[y] = stackCombiner.combineHorizontally(horizStacks[y], grid[x][y]);
                }
            }

            ImageStack out = horizStacks[0];

            for (int i = 1; i < horizStacks.length; i++) {
                if (horizStacks[i] != null) {
                    out = stackCombiner.combineVertically(out, horizStacks[i]);
                }
            }

            ImagePlus comb = new ImagePlus(regname, out);

            logger.print("combined stack has " + comb.getStackSize() + " slices");
            logger.print("imp stack has " + imp.getStackSize() + " slices, " + imp.getNChannels() + " channels, " + imp.getNFrames() + " frames, " + imp.getNSlices() + " slices");

            ImagePlus hyp = HyperStackConverter.toHyperStack(comb, imp.getNChannels(), 1, imp.getStackSize() / imp.getNChannels(), "xyczt", "composite");
            if (hyp.getNChannels() == 4) {
                ((CompositeImage) hyp).setLuts(new LUT[]{LUT.createLutFromColor(Color.WHITE), LUT.createLutFromColor(Color.RED), LUT.createLutFromColor(Color.GREEN), LUT.createLutFromColor(new Color(0, 70, 255))});
            }

            if(!file.getAbsolutePath().contains("tiles")) {
                logger.print("Saving in regular folder structure...");
                IJ.saveAsTiff(hyp, file.getAbsolutePath() + File.separator + regname + "_montage.tif");
            } else {
                // Image sequence folder structure
                logger.print("Saving in image sequence folder structure...");
                File mkMonLoc = new File(file.getParentFile().getParentFile() + File.separator + "stitched");
                if(!mkMonLoc.exists()) {
                    mkMonLoc.mkdirs();
                }
                IJ.saveAsTiff(hyp, mkMonLoc + File.separator + regname + "_montage.tif");
            }

        });

    }

    /**
     * Method to calculate the x and y position/offset of a tile with respect to the region.
     * @param tiff
     * @param f
     * @throws IOException
     */

    public static void createTileMap(File[] tiff, File f) throws IOException {

        Arrays.sort(tiff);
        PrintWriter bwTileMap = null;
        LinkedList<TileObject> tiles = new LinkedList<>();

        for(File aTifFile : tiff) {
            ImagePlus tiffImp = IJ.openImage(aTifFile.getPath());
            if (tiffImp != null) {
                TileObject aTile = new TileObject(tiffImp, aTifFile.getName());
                tiles.add(aTile);
            }
        }

        int xPos = 0;
        int yPos = 0;

        if(tiles == null || tiles.isEmpty()) {
            return;
        }
        TileObject firstTile = tiles.getFirst();

        List<Integer> xTracker = new LinkedList<>();
        List<Integer> refList = new LinkedList<>();

        try {
            if(f == null) {
                throw new IOException("Output directory to store tileMap.txt not found!");
            }
            bwTileMap = new PrintWriter(f.getParent() + File.separator + "tileMap.txt");
            bwTileMap.write("RegionNumber\tTileX\tTileY\tXposition\tYposition");
            bwTileMap.println();
            for (TileObject currentTile : tiles) {
                if (firstTile.getRegionNumber() != currentTile.getRegionNumber()) {
                    xPos = 0;
                    yPos = 0;
                    firstTile = currentTile;
                    xTracker = new LinkedList<>();
                    refList = new LinkedList<>();
                }

                bwTileMap.write("" + currentTile.getRegionNumber() + "\t");
                bwTileMap.write("" + currentTile.getXNumber() + "\t");
                bwTileMap.write("" + currentTile.getYNumber() + "\t");
                if (firstTile.getXNumber() == currentTile.getXNumber()) {
                    if (refList.isEmpty()) {
                        bwTileMap.write("" + xPos + "\t");
                        bwTileMap.write("" + yPos);
                        bwTileMap.println();
                    } else {
                        xPos = 0;
                        xPos += refList.get(currentTile.getYNumber() - 1);
                        bwTileMap.write("" + xPos + "\t");
                        bwTileMap.write("" + yPos);
                        bwTileMap.println();
                    }
                    xTracker.add(currentTile.getWidth());
                    yPos += currentTile.getHeight();
                } else if (firstTile.getXNumber() != currentTile.getXNumber()) {
                    if (!refList.isEmpty()) {
                        for (int i = 0; i < refList.size(); i++) {
                            refList.set(i, refList.get(i)+currentTile.getWidth());
                        }
                    }
                    else {
                        for (int i = 0; i < xTracker.size(); i++) {
                            refList.add(xTracker.get(i));
                        }
                    }
                    xTracker.clear();
                    xTracker.add(currentTile.getWidth());
                    xPos = 0;
                    xPos += refList.get(currentTile.getYNumber() - 1);
                    yPos = 0;
                    bwTileMap.write("" + xPos + "\t");
                    bwTileMap.write("" + yPos);
                    bwTileMap.println();
                    yPos += currentTile.getHeight();
                }
                firstTile = currentTile;
            }
        }
        catch (IOException e) {
            logger.print(e.getMessage());
        }
        finally {
            bwTileMap.close();
        }
    }

    public static int[] extractXYCoord(File f) {
        String[] s = f.getName().split("[_\\.]");
        int[] ret = new int[]{Integer.parseInt(s[1].substring(1)), Integer.parseInt(s[2].substring(1))};
        return ret;
    }
}
