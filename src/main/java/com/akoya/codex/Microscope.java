package com.akoya.codex;

import com.akoya.codex.upload.Experiment;
import com.akoya.codex.upload.ExperimentView;
import java.io.File;

/**
 *
 * @author Vishal
 */
public interface Microscope {

    public void guessZSlices(File dir, ExperimentView experimentView);
    public void guessChannelNamesAndWavelength(File dir, ExperimentView experimentView);
    public void guessCycleRange(File dir, ExperimentView experimentView);
    public boolean isTilesAProductOfRegionXAndY(File dir, ExperimentView experimentView);
    public int getMaxCycNumberFromFolder(File dir);

}
