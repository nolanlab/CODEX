package org.nolanlab.codex.upload.scope;

import org.nolanlab.codex.upload.legacy.ExperimentView;
import org.nolanlab.codex.upload.gui.NewGUI;

import java.io.File;

/**
 *
 * @author Vishal
 */
public interface Microscope {

    // Legacy
    public void guessZSlices(File dir, ExperimentView experimentView);
    public void guessChannelNamesAndWavelength(File dir, ExperimentView experimentView);
    public void guessCycleRange(File dir, ExperimentView experimentView);
    public boolean isTilesAProductOfRegionXAndY(File dir, ExperimentView experimentView);

    public void guessZSlices(File dir, NewGUI gui);
    public void guessChannelNamesAndWavelength(File dir, NewGUI gui);
    public void guessCycleRange(File dir, NewGUI gui);
    public boolean isTilesAProductOfRegionXAndY(File dir, NewGUI gui);
    public void guessTileOverlap(NewGUI gui);
    public int getMaxCycNumberFromFolder(File dir);
}
