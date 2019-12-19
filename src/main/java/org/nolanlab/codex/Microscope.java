package org.nolanlab.codex;

import org.nolanlab.codex.upload.ExperimentView;
import org.nolanlab.codex.upload.gui.NewGUI;

import java.io.File;

/**
 *
 * @author Vishal
 */
public interface Microscope {

    public void guessZSlices(File dir, ExperimentView experimentView);
    public void guessZSlices(File dir, NewGUI gui);
    public void guessChannelNamesAndWavelength(File dir, ExperimentView experimentView);
    public void guessChannelNamesAndWavelength(File dir, NewGUI gui);
    public void guessCycleRange(File dir, ExperimentView experimentView);
    public void guessCycleRange(File dir, NewGUI gui);
    public boolean isTilesAProductOfRegionXAndY(File dir, ExperimentView experimentView);
    public boolean isTilesAProductOfRegionXAndY(File dir, NewGUI gui);
    public int getMaxCycNumberFromFolder(File dir);

}
