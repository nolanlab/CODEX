# CODEX

**Image processing software for CODEX: driftcompensation, deconvolution, uploading and segmentation**

If you are planning to clone/fork this repository, make sure to download the non-public maven dependencies, otherwise there will be compilation issues.

# Code dependencies for CODEX
*Non-public maven dependencies can be found here:* https://1drv.ms/u/s!AmLT3VlVj2P_lf09PpFz6KxhKNboOw
* You need to modify the batch file inside according to the location where JAVA_HOME is set in your development machine. 

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
* Once you have the raw experiment folder data acquired from microscope, use CODEXUploader.exe to process your experiment. Currently supported microscope types are Keyence and Zeiss.
* For example, if you have a multicycle and multi region data produced from Keyence microscope, make sure your raw experiment data contains folders of the following format:
 - Cyc1_reg1
   - filename.bcf
   - filename_00001_Z001_CH1.tif
   - ...
 - Cyc2_reg1
 ...
and so on. Also, make sure that HandE folders are named as HandE_reg1, HandE_reg2.. and so on. This is mandatory to use with uploader, otherwise it would not work as expected. So if you have a different name format, rename it to the above format for processing
* From the uploader windows, select the inout raw experiment folder. It should auto-populate almost all the fields. Verify them.
* Input the tile overlap percentage for X and Y values and choose where you want to save the processed data from the field called as "Temporary storage location"
* If you would like the processed output to be stored in regular format, select **"Export as Tiff". This format is NOT Compatible with CodexMAV.**
* If you would like the processed output to be stored in Image Sequence format, select **"Export as Image Sequence"**. When doing so, **make sure the "Temporary storage location"/output folder is set as "processed_experimentName" inside the raw experiment folder. This is the format that is compatible with the use of CodexMAV, a Fiji plugin developed at Akoya Biosciences Inc. that is used for visualizing and analysing the processed data. If the folder structure is different, CodexMAV would not be supported! This is critical. ** 
* **UPDATE: CodexMAV latest versions do not support the image sequence format produced after processing with the Nolan lab uploader. It is unstable, so DO NOT USE IT! So kindly use regular tiff image format, if you are using all of Nolab lab's pipeline. If you want to use CodexMAV for analysis, talk to suppport team at Akoya.**
* Hit the "Start" button to start processing your experiment
* Uploader takes care of performing the drift compensation, deconvolution(microvolution with license) and stitching all the tiles to create a stitched(or montage) image per region

# CODEXSegm - Run segmentation for a processed data
* Once the experiment is processed successfully without any issues, use CODEXSegm.exe to run segmentation 
* Irrespective of microscope types, the input for segmentation is either going to be of processed tiff format or image sequence format based on how it was processed using the uploader. The user would have specified this when they processed the data in the previous step. By default, it is regular tiff. The only important thing required to run segmentation is, the location of the processed folder once the uploader finishes processing the experiment.
* From the initial segmentation window, choose the processed experiment folder and if the processed data is in image sequence format, enter the configuration name for the segmentation run. This is useful when you try to analyze the data in CodexMAV. If the processed data is in regular tif file format, ignore the configuration name field, also regular tif file format cannot be used to analyze with CodexMAV. This is Vital! If you want to perform analysis with CodexMAV, contact Akoya for support! 
**UPDATE - As mentioned above, CodexMAV is unstable with image sequence produced from Nolan lab uploader. DO NOT USE IT!**
* Make sure all the parameters you set are right and hit the "Start" button
* Once segmentation is completed, you will see that the FCS files were created successfully

# CodexMAV - Visualize & analyze processed and segmented data
* UPDATE - The latest versions of CodexMAV is not stable with latest versions Nolan lab CodexUploader. 
* CodexMAV is a Fiji plugin developed at Akoya Biosciences Inc., used for analyzing the processed Codex data
* Choose offline mode and select the experiment that you want to analyze by specifying the processed folder
* There are bunch of tools that one can use to analyze. Follow the user documentation for CodexMAV here: https://help.codex.bio/codex/mav/user-instructions

# Issues or problems?
* If you encounter any issues with the CODEX software, please feel free to create a new issue from the issues section of this repository: https://github.com/nolanlab/CODEX/issues
* While creating issues in CODEX Nolanlab repository in Github, please give relevant errors or exceptions that get thrown from the console or log. This is available both in Uploader and CodexSegm
* If no errors were given in the console area either in uploader or segmentation tool, open command window and use command "cd" to browse and point to the location where you actually installed the CODEXtoolkit application(not start programs or desktop); this is where the CODEX-1.0-jar-with-dependencies.jar should be present. Now, do the following:

  * To run uploader, use the java command:                                                                                                
  `java -cp CODEX-1.0-jar-with-dependencies.jar org.nolanlab.codex.upload.legacy.frmMain`                                                        
  Check the command window for any exceptions being thrown during the error and send a screenshot of this information
     
  * To run segmentation, use the java command:                                                                                            
  `java -cp CODEX-1.0-jar-with-dependencies.jar org.nolanlab.codex.segm.SegmMain`                                                        
  Check the command window for any exceptions being thrown during the error and send a screenshot of this information
