package org.nolanlab.codex.upload.scope;

import org.nolanlab.codex.upload.legacy.ExperimentView;
import org.nolanlab.codex.upload.gui.NewGUI;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 *
 * @author Vishal
 */
public class Keyence implements Microscope {

    public void guessZSlices(File dir, NewGUI gui) {
        if(dir != null) {
            outer: for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && (cyc.getName().toLowerCase().startsWith("cyc") ||
                        (gui.isOnlyHandE() && cyc.getName().toLowerCase().startsWith("hande")))) {
                    File[] cycFiles = cyc.listFiles();
                    if(!gui.isTMA()) {
                        Arrays.sort(cycFiles, Collections.reverseOrder());
                        for (File tif : cycFiles) {
                            if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                                int lastZIndex = tif.getName().lastIndexOf("Z");
                                String zNumber = tif.getName().substring(lastZIndex + 1, lastZIndex + 4);
                                if (zNumber != null) {
                                    int zIndex = Integer.parseInt(zNumber);
                                    zNumber = String.valueOf(zIndex);
                                }
                                gui.getNumPlanesField().setText(zNumber);
                                break outer;
                            }
                        }
                    } else {
                        File[] xyFiles = cyc.listFiles();
                        File[] tifFiles = xyFiles[0].listFiles(t -> !t.isDirectory() && t.getName().toLowerCase().endsWith(".tif"));
                        Arrays.sort(tifFiles, Collections.reverseOrder());
                        for(File tif : tifFiles) {
                            if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                                int lastZIndex = tif.getName().lastIndexOf("Z");
                                String zNumber = tif.getName().substring(lastZIndex + 1, lastZIndex + 4);
                                if (zNumber != null) {
                                    int zIndex = Integer.parseInt(zNumber);
                                    zNumber = String.valueOf(zIndex);
                                }
                                gui.getNumPlanesField().setText(zNumber);
                                break outer;
                            }
                        }
                    }
                }
            }
        }
    }

    public void guessChannelNamesAndWavelength(File dir, NewGUI gui) {
        if(dir != null) {
            outer: for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File[] cycFiles = cyc.listFiles();
                    if (!gui.isTMA()) {
                        Arrays.sort(cycFiles, Collections.reverseOrder());
                        LinkedHashMap<String, Boolean> chVsBool = new LinkedHashMap<String, Boolean>();
                        chVsBool.put("CH1", false);
                        chVsBool.put("CH2", false);
                        chVsBool.put("CH3", false);
                        chVsBool.put("CH4", false);
                        for (File tif : cycFiles) {
                            if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                                int last_Index = tif.getName().lastIndexOf("_");
                                String chNumber = tif.getName().substring(last_Index + 1, last_Index + 4);
                                if (chNumber != null) {
                                    if (chVsBool.containsKey(chNumber)) {
                                        chVsBool.put(chNumber, true);
                                    }
                                }
                            }
                        }
                        LinkedHashMap<String, String> chVsWavelength = new LinkedHashMap<String, String>();
                        chVsWavelength.put("CH1", "425");
                        chVsWavelength.put("CH2", "525");
                        chVsWavelength.put("CH3", "595");
                        chVsWavelength.put("CH4", "670");

                        String ch = "";
                        String waveL = "";

                        boolean first = true;
                        for (String key : chVsBool.keySet()) {
                            if (!first && chVsBool.get(key)) {
                                ch += ";" + key;
                                waveL += ";" + chVsWavelength.get(key);
                            } else {
                                if (chVsBool.get(key)) {
                                    first = false;
                                    ch += key;
                                    waveL += chVsWavelength.get(key);
                                }
                            }
                        }
                        gui.getNumChannelsField().setText(String.valueOf(ch.split(";").length));
                        gui.getChannelNamesField().setText(ch);
                        gui.getWavelengthsField().setText(waveL);
                        //break outer loop
                        break outer;
                    } else {
                        File[] xyFiles = cyc.listFiles();
                        File[] tifFiles = xyFiles[0].listFiles(t -> !t.isDirectory() && t.getName().toLowerCase().endsWith(".tif"));
                        Arrays.sort(tifFiles, Collections.reverseOrder());

                        LinkedHashMap<String, Boolean> chVsBool = new LinkedHashMap<String, Boolean>();
                        chVsBool.put("CH1", false);
                        chVsBool.put("CH2", false);
                        chVsBool.put("CH3", false);
                        chVsBool.put("CH4", false);
                        for (File tif : tifFiles) {
                            if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                                int last_Index = tif.getName().lastIndexOf("_");
                                String chNumber = tif.getName().substring(last_Index + 1, last_Index + 4);
                                if (chNumber != null) {
                                    if (chVsBool.containsKey(chNumber)) {
                                        chVsBool.put(chNumber, true);
                                    }
                                }
                            }
                        }
                        LinkedHashMap<String, String> chVsWavelength = new LinkedHashMap<String, String>();
                        chVsWavelength.put("CH1", "425");
                        chVsWavelength.put("CH2", "525");
                        chVsWavelength.put("CH3", "595");
                        chVsWavelength.put("CH4", "670");

                        String ch = "";
                        String waveL = "";

                        boolean first = true;
                        for (String key : chVsBool.keySet()) {
                            if (!first && chVsBool.get(key)) {
                                ch += ";" + key;
                                waveL += ";" + chVsWavelength.get(key);
                            } else {
                                if (chVsBool.get(key)) {
                                    first = false;
                                    ch += key;
                                    waveL += chVsWavelength.get(key);
                                }
                            }
                        }
                        gui.getNumChannelsField().setText(String.valueOf(ch.split(";").length));
                        gui.getChannelNamesField().setText(ch);
                        gui.getWavelengthsField().setText(waveL);
                        //break outer loop
                        break outer;
                    }
                } else if (cyc != null && cyc.isDirectory() && gui.isOnlyHandE() && cyc.getName().toLowerCase().startsWith("hande")) {
                    gui.getNumChannelsField().setText("4");
                    gui.getChannelNamesField().setText("CH1;CH2;CH3;CH4");
                    gui.getWavelengthsField().setText("425;525;595;670");
                }
            }
        }
    }

    /*
   Set the number of cyles/range field depending upon the content of Experiment folder.
   */
    public void guessCycleRange(File dir, NewGUI gui) {
        //default the cycle range value based on H&E stain cycle
        defaultCycleRange(dir, gui);

        //Item listener to capture the state of the h&e radio button to display the max cycles(upper cycle limit)
        JCheckBox rb_handE_yes = gui.gethAndEStainCheckBox();
        ItemListener itl = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                int lowL = 1;
                int upL = getMaxCycNumberFromFolder(dir)+1;
                if(upL == 0) {
                    gui.getCycleRangeField().setText(String.valueOf(lowL));
                }
                else if(lowL == upL) {
                    gui.getCycleRangeField().setText(String.valueOf(lowL));
                }
                else {
                    gui.getCycleRangeField().setText(String.valueOf(lowL) + "-" + String.valueOf(upL));
                }
            }  else {
                int lowL = 1;
                int upL = getMaxCycNumberFromFolder(dir);
                if(upL == 0) {
                    gui.getCycleRangeField().setText(String.valueOf(lowL));
                }
                else if(lowL == upL) {
                    gui.getCycleRangeField().setText(String.valueOf(lowL));
                }
                else {
                    gui.getCycleRangeField().setText(String.valueOf(lowL) + "-" + String.valueOf(upL));
                }
            }
        };
        rb_handE_yes.addItemListener(itl);
    }

    private void defaultCycleRange(File dir, NewGUI gui) {
        int lowL = 1;
        int upL = getMaxCycNumberFromFolder(dir);
        if(upL == 0) {
            gui.getCycleRangeField().setText(String.valueOf(lowL));
        }
        else if(lowL == upL) {
            gui.getCycleRangeField().setText(String.valueOf(lowL));
        }
        else {
            if(gui.gethAndEStainCheckBox().isSelected()) {
                gui.getCycleRangeField().setText(String.valueOf(lowL) + "-" + String.valueOf(upL+1));
            }
            else {
                gui.getCycleRangeField().setText(String.valueOf(lowL) + "-" + String.valueOf(upL));
            }
        }
    }

    /*
    Method to find the total number of Cycle folders present in the experiment directory.
    */
    public int getMaxCycNumberFromFolder(File dir) {
        ArrayList<Integer> cycNumbers = new ArrayList<Integer>();
        if (dir != null) {
            for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    String cycFolderName = cyc.getName();
                    String[] cycVal = cycFolderName.split("_");
                    cycNumbers.add(Integer.parseInt(cycVal[0].replaceAll("[^0-9]", "")));
                }
            }
        }
        Collections.sort(cycNumbers, Collections.reverseOrder());
        return cycNumbers == null || cycNumbers.isEmpty() ? 0 : cycNumbers.get(0);
    }

    /*
    Method to set region height and region width to 1 when TMA data is loaded
     */
    public void guessWidthAndHeight(File dir, NewGUI gui) {
        if(gui.isTMA()) {
            gui.getRegionWidthField().setText("1");
            gui.getRegionHeightField().setText("1");
        }
    }

    public boolean isTilesAProductOfRegionXAndY(File dir, NewGUI gui) {
        if (dir != null) {
            if(!gui.isTMA()) {
                outer:
                for (File cyc : dir.listFiles()) {
                    if (cyc != null && cyc.isDirectory() && (cyc.getName().toLowerCase().startsWith("cyc") ||
                            (gui.isOnlyHandE() && cyc.getName().toLowerCase().startsWith("hande")))) {
                        File[] cycFiles = cyc.listFiles();
                        Arrays.sort(cycFiles, Collections.reverseOrder());
                        for (File tif : cycFiles) {
                            if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                                int lastZIndex = tif.getName().lastIndexOf("Z");
                                String regXYNumber = tif.getName().substring(lastZIndex - 5, lastZIndex - 1);
                                if (regXYNumber != null) {
                                    int regXYIndex = Integer.parseInt(regXYNumber);
                                    if (regXYIndex == Integer.parseInt(gui.getRegionWidthField().getText()) * Integer.parseInt(gui.getRegionHeightField().getText())) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                                break outer;
                            }
                        }
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /*
    Set the tile overlap
    */
    public void guessTileOverlap(NewGUI gui) {
        gui.getTileOverlapXField().setText("30");
        gui.getTileOverlapYField().setText("30");
    }

    // Legacy code begins

    public void guessZSlices(File dir, ExperimentView experimentView) {
        if(dir != null) {
            outer: for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File[] cycFiles = cyc.listFiles();
                    Arrays.sort(cycFiles, Collections.reverseOrder());
                    for (File tif : cycFiles) {
                        if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                            int lastZIndex = tif.getName().lastIndexOf("Z");
                            String zNumber = tif.getName().substring(lastZIndex+1, lastZIndex+4);
                            if (zNumber != null) {
                                int zIndex = Integer.parseInt(zNumber);
                                zNumber = String.valueOf(zIndex);
                            }
                            experimentView.getVal9().setText(zNumber);
                            break outer;
                        }
                    }
                }
            }
        }
    }

    public void guessChannelNamesAndWavelength(File dir, ExperimentView experimentView) {
        if(dir != null) {
            outer: for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File[] cycFiles = cyc.listFiles();
                    Arrays.sort(cycFiles, Collections.reverseOrder());
                    LinkedHashMap<String, Boolean> chVsBool = new LinkedHashMap<String, Boolean>();
                    chVsBool.put("CH1", false);
                    chVsBool.put("CH2", false);
                    chVsBool.put("CH3", false);
                    chVsBool.put("CH4", false);
                    for (File tif : cycFiles) {
                        if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                            int last_Index = tif.getName().lastIndexOf("_");
                            String chNumber = tif.getName().substring(last_Index+1, last_Index+4);
                            if (chNumber != null) {
                                if(chVsBool.containsKey(chNumber)){
                                    chVsBool.put(chNumber, true);
                                }
                            }
                        }
                    }
                    LinkedHashMap<String, String> chVsWavelength = new LinkedHashMap<String, String>();
                    chVsWavelength.put("CH1","425");
                    chVsWavelength.put("CH2","525");
                    chVsWavelength.put("CH3","595");
                    chVsWavelength.put("CH4","670");

                    String ch="";
                    String waveL="";

                    boolean first = true;
                    for (String key: chVsBool.keySet()) {
                        if (!first && chVsBool.get(key)) {
                            ch += ";"+key;
                            waveL += ";"+chVsWavelength.get(key);
                        }
                        else {
                            if(chVsBool.get(key)) {
                                first = false;
                                ch += key;
                                waveL += chVsWavelength.get(key);
                            }
                        }
                    }
                    experimentView.getVal11().setText(ch);
                    experimentView.getVal21().setText(waveL);
                    //break outer loop
                    break outer;
                }
            }
        }
    }

    public void guessCycleRange(File dir, ExperimentView experimentView) {
        //default the cycle range value based on H&E stain cycle
        defaultCycleRange(dir, experimentView);

        //Item listener to capture the state of the h&e radio button to display the max cycles(upper cycle limit)
        JRadioButton rb_handE_yes = experimentView.getRb_HandE_yes();
        ItemListener itl = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                int lowL = 1;
                int upL = getMaxCycNumberFromFolder(dir)+1;
                if(upL == 0) {
                    experimentView.getVal13().setText(String.valueOf(lowL));
                }
                else if(lowL == upL) {
                    experimentView.getVal13().setText(String.valueOf(lowL));
                }
                else {
                    experimentView.getVal13().setText(String.valueOf(lowL) + "-" + String.valueOf(upL));
                }
            }  else {
                int lowL = 1;
                int upL = getMaxCycNumberFromFolder(dir);
                if(upL == 0) {
                    experimentView.getVal13().setText(String.valueOf(lowL));
                }
                else if(lowL == upL) {
                    experimentView.getVal13().setText(String.valueOf(lowL));
                }
                else {
                    experimentView.getVal13().setText(String.valueOf(lowL) + "-" + String.valueOf(upL));
                }
            }
        };
        rb_handE_yes.addItemListener(itl);
    }

    private void defaultCycleRange(File dir, ExperimentView experimentView) {
        int lowL = 1;
        int upL = getMaxCycNumberFromFolder(dir);
        if(upL == 0) {
            experimentView.getVal13().setText(String.valueOf(lowL));
        }
        else if(lowL == upL) {
            experimentView.getVal13().setText(String.valueOf(lowL));
        }
        else {
            if(experimentView.getRb_HandE_yes().isSelected()) {
                experimentView.getVal13().setText(String.valueOf(lowL) + "-" + String.valueOf(upL+1));
            }
            else {
                experimentView.getVal13().setText(String.valueOf(lowL) + "-" + String.valueOf(upL));
            }
        }
    }

    public boolean isTilesAProductOfRegionXAndY(File dir, ExperimentView expView) {
        if (dir != null) {
            outer: for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    File[] cycFiles = cyc.listFiles();
                    Arrays.sort(cycFiles, Collections.reverseOrder());
                    for (File tif : cycFiles) {
                        if (tif != null && !tif.isDirectory() && tif.getName().endsWith(".tif")) {
                            int lastZIndex = tif.getName().lastIndexOf("Z");
                            String regXYNumber = tif.getName().substring(lastZIndex - 5, lastZIndex - 1);
                            if (regXYNumber != null) {
                                int regXYIndex = Integer.parseInt(regXYNumber);
                                if (regXYIndex == Integer.parseInt(expView.getVal17().getText()) * Integer.parseInt(expView.getVal18().getText())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            break outer;
                        }
                    }

                }
            }
        }
        return false;
    }
}
