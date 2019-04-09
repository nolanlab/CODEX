# CODEX

**Image processing software for CODEX: driftcompensation, deconvolution, uploading and segmentation**

If you are planning to clone/fork this repository, make sure to download the non-public maven dependencies, otherwise there will be compilation issues.

# Code dependencies for CODEX
*Non-public maven dependencies can be found here:* https://1drv.ms/u/s!AmLT3VlVj2P_lf09PpFz6KxhKNboOw

# Prerequisites
* Processing computer
* Java 64bit version. Link to java 64 bit: https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
* Make sure "Java" folder is present inside C:\Program Files and not in C:\Program Files(x86)
* Download the latest .msi installer from the releases section of this repository: https://github.com/nolanlab/CODEX/releases

# Installation & running the programs
* Once you download the latest .msi, install the application and follow the steps during installation
* A new CODEXToolkit folder containing 3 differnt files would be created in Desktop and Win Start -> Applications
* From the CODEXToolkit folder: use CODEXUploader.exe to run and process the raw experiments and use CODEXSegm.exe to segment the processed data

# CODEXUploader - Process a raw experiment from microscope
* Once you have the raw experiment folder data acquired from microscope, use CODEXUploader.exe to process your experiment.
* From the uploader windows, select the inout raw experiment folder. It should auto-populate almost all the fields. Verify them.
* Input the tile overlap percentage for X and Y values and choose where you want to save the processed data from the field called as "Temporary storage location"
* If you would like the processed output to be stored in regular format, select "Export as Tiff".
* If you would like the processed output to be stored in Image Sequence format, select "Export as Image Sequence". This is the format that is compatible with the use of CodexMAV, a Fiji plugin developed at Akoya Biosciences Inc. that is used for visualizing and analysing the processed data
* Hit the "Start" button to start processing your experiment
* Uploader takes care of performing the drift compensation, deconvolution(microvolution with license) and stitching all the tiles to create a stitched(or montage) image per region

# CODEXSegm - Run segmentation for a processed data
* Once the experiment is processed successfully without any issues, use CODEXSegm.exe to run segmentation 
* From the initial segmentation window, choose the processed experiment folder and if the processed data is in image sequence format, enter the configuration name for the segmentation run. This is useful when you try to analyze the data in CodexMAV. If the processed data is in regular tif file format, ignore the configuration name field, also regular tif file format cannot be used to analyze with CodexMAV
* Make sure all the parameters you set are right and hit the "Start" button

# CodexMAV - Visualize & analyze processed and segmented data
* CodexMAV is a Fiji plugin developed at Akoya Biosciences Inc., used for analyzing the processed Codex data
* Choose offline mode and select the experiment that you want to analyze by specifying the processed folder
* There are bunch of tools that one can use to analyze. Follow the user documentation for CodexMAV here: https://help.codex.bio/codex/mav/user-instructions

# Issues or problems?
* If you encounter any issues with the CODEX software, please feel free to create a new issue from the issues section of this repository: https://github.com/nolanlab/CODEX/issues
* While creating issues in CODEX Nolanlab repository in Github, please give relevant errors or exceptions that get thrown from the console or log. This is available both in Uploader and CodexSegm
* If no errors were given in the console area either in uploader or segmentation tool, open command window and use command "cd" to browse and point to the location where you actually installed the CODEXtoolkit application(not start programs or desktop); this is where the CODEX-1.0-jar-with-dependencies.jar should be present. Now, do the following:

  * To run uploader, use the java command:
  `java -cp CODEX-1.0-jar-with-dependencies.jar org.nolanlab.codex.upload.frmMain`
  Check the command window for any exceptions being thrown during the error and send a screenshot of this information
     
  * To run segmentation, use the java command:
  `java -cp CODEX-1.0-jar-with-dependencies.jar org.nolanlab.codex.segm.SegmMain`
  Check the command window for any exceptions being thrown during the error and send a screenshot of this information










