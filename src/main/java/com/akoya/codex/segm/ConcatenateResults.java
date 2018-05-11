package com.akoya.codex.segm;

import com.akoya.codex.upload.TileObject;
import com.akoya.codex.upload.logger;
import org.apache.commons.io.FilenameUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
        System.out.println("Creating a map to calculate X and Y offsets...");
        LinkedHashMap<TileObject, ArrayList<Integer>> map = createMapFromTileMap(dir);

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
                TileObject fileTo = new TileObject();
                fileTo.createTileFromFileNameWithoutImage(FilenameUtils.removeExtension(f.getName()));
                if(map == null || map.isEmpty()) {
                    logger.print("The map created from tileMap is empty. Please check the tileMap.txt file!");
                }
                List<Integer> a = map.get(fileTo);
                if(a == null || a.isEmpty()) {
                    logger.print("The offsets are empty or null. Please check the tileMap.txt file!");
                }
                while ((s = br.readLine()) != null) {
                    String[] sArr = s.split("\\t");

                    int x = Integer.parseInt(sArr[2]);
                    int y = Integer.parseInt(sArr[3]);
                    int xOffset = a.get(0) + x;
                    int yOffset = a.get(1) + y;
                    sArr[2] = Integer.toString(xOffset);
                    sArr[3] = Integer.toString(yOffset);

                    s = String.join("\t", sArr);
                    bw.write("\n" + f.getName().split("\\.tif")[0] + "\t" + s);
                }
            }
            bw.flush();
            bw.close();
        }
        }

    }

    /**
     * Compute the offset for each tile to find the position of X and Y in the region
     */
    private static LinkedHashMap<TileObject, ArrayList<Integer>> createMapFromTileMap(File dir) throws IOException {

        BufferedReader br = null;
        String xPos = "";
        String yPos = "";
        LinkedHashMap<TileObject, ArrayList<Integer>> tifWithXposYpos = new LinkedHashMap<>();
        File tileMapFile = new File(dir + File.separator + "tileMap.txt");
        if(!tileMapFile.exists()){
            System.out.println("!!!WARNING: TileMap.txt file could not be found, therefore coordinate remapping will be disabled \n cell coordinates will be relative to the top-left corner of each tile.");
        }


        try {

            br = new BufferedReader(new FileReader(tileMapFile));
            boolean columnName= true;
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (columnName) {
                    columnName = false;
                    continue;
                }
                String[] aRow = currentLine.split("\\t");
                if(aRow != null && aRow.length != 0) {
                    String region = aRow[0];
                    int regInt = Integer.parseInt(region);

                    String tileX = aRow[1];
                    int tileXInt = Integer.parseInt(tileX);

                    String tileY = aRow[2];
                    int tileYInt = Integer.parseInt(tileY);

                    xPos = aRow[3];
                    yPos = aRow[4];

                    String fileName = String.format("reg%03d_X%02d_Y%02d", regInt, tileXInt, tileYInt);
                    TileObject to = new TileObject();
                    to = to.createTileFromFileNameWithoutImage(fileName);

                    //List to hold the X offset and Y offset
                    ArrayList<Integer> arr = new ArrayList<>();
                    if(xPos != null && yPos != null) {
                        arr.add(Integer.parseInt(xPos));
                        arr.add(Integer.parseInt(yPos));
                    }

                    if(!tifWithXposYpos.containsKey(to)) {
                        tifWithXposYpos.put(to, arr);
                    }
                }
            }
        }
        catch(IOException e) {
           throw e;
        }
        finally {
            br.close();
        }
        return tifWithXposYpos;
    }
}
