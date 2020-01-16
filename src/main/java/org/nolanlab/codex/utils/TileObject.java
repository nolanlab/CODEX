package org.nolanlab.codex.utils;

import ij.ImagePlus;
import org.apache.commons.io.FilenameUtils;
/**
 *
 * @author Vishal
 */

public class TileObject {

    private int regionNumber;
    private int xNumber;
    private int yNumber;
    private int width;
    private int height;
    private String fileName;

    public int getRegionNumber() {
        return regionNumber;
    }

    public int getXNumber() {
        return xNumber;
    }

    public int getYNumber() {
        return yNumber;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFileName() {
        return fileName;
    }

    public TileObject() {
    }

    public TileObject(ImagePlus tiffImp, String tifFileName) {
        this.width = tiffImp.getWidth();
        this.height = tiffImp.getHeight();

        String regionNumberStr = tifFileName.substring(3, 6);
        this.regionNumber = regionNumberStr == null? 0 : Integer.parseInt(regionNumberStr);

        String xNumberStr = tifFileName.substring(8, 10);
        this.xNumber = xNumberStr == null? 0 : Integer.parseInt(xNumberStr);

        String yNumberStr = tifFileName.substring(12, 14);
        this.yNumber = yNumberStr == null? 0 : Integer.parseInt(yNumberStr);

        this.fileName = FilenameUtils.removeExtension(tifFileName);
    }

    public TileObject (String tifFileName) {
        String regionNumberStr = tifFileName.substring(3, 6);
        this.regionNumber = regionNumberStr == null? 0 : Integer.parseInt(regionNumberStr);

        String xNumberStr = tifFileName.substring(8, 10);
        this.xNumber = xNumberStr == null? 0 : Integer.parseInt(xNumberStr);

        String yNumberStr = tifFileName.substring(12, 14);
        this.yNumber = yNumberStr == null? 0 : Integer.parseInt(yNumberStr);

        this.fileName = FilenameUtils.removeExtension(tifFileName);
    }

    @Override
    public String toString() {
        return "File name: " + fileName+ " region number: "+regionNumber+" xNumber: "+xNumber+" yNumber: "+yNumber+"Image width: "+width+"Image height: "+height;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = (regionNumber*10000) + (xNumber*100) + yNumber;
        return  hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TileObject) {
            TileObject to = (TileObject) obj;
            return (to.regionNumber == this.regionNumber && to.xNumber == this.xNumber && to.yNumber == this.yNumber);
        }
        else {
            return false;
        }
    }

    public TileObject createTileFromFileNameWithoutImage(String tifFileName) {
        String regionNumberStr = tifFileName.substring(3, 6);
        this.regionNumber = regionNumberStr == null? 0 : Integer.parseInt(regionNumberStr);

        String xNumberStr = tifFileName.substring(8, 10);
        this.xNumber = xNumberStr == null? 0 : Integer.parseInt(xNumberStr);

        String yNumberStr = tifFileName.substring(12, 14);
        this.yNumber = yNumberStr == null? 0 : Integer.parseInt(yNumberStr);

        this.fileName = FilenameUtils.removeExtension(tifFileName);

        return this;
    }
}