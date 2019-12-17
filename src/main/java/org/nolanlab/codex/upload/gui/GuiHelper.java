package org.nolanlab.codex.upload.gui;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nolanlab.codex.MicroscopeTypeEnum;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GuiHelper {

    /*
    Set the microscope type
    */
    public void guessMicroscope(File dir, JComboBox microscopeTypeComboBox) {
        boolean flag = false;
        if(dir != null) {
            for (File cyc : dir.listFiles()) {
                if (cyc != null && cyc.isDirectory() && cyc.getName().toLowerCase().startsWith("cyc")) {
                    microscopeTypeComboBox.setSelectedItem(MicroscopeTypeEnum.KEYENCE);
                    flag = true;
                    break;
                }
            }
            if(!flag) {
                microscopeTypeComboBox.setSelectedItem(MicroscopeTypeEnum.ZEISS);
            }
        }
    }

    public void openTextEditor(String textPath) {
        if (SystemUtils.IS_OS_WINDOWS) {
            String editorPath = "";
            String[] editorPaths = new String[]{
                    "C:\\Program Files\\Notepad++\\notepad++.exe",
                    "C:\\Program Files (x86)\\Notepad++\\notepad++.exe",
                    "C:\\Windows\\notepad.exe"};
            for (String p : editorPaths) {
                if (new File(p).exists()) {
                    editorPath = p;
                    break;
                }
            }
            if (!editorPath.isEmpty()) {
                try {
                    Runtime.getRuntime().exec(editorPath + " \"" + textPath + "\"");
                } catch (IOException e) {
//                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else {
//                log.error("Could not find a Windows text editor!");
            }
        } else if (SystemUtils.IS_OS_MAC) {
            try {
                new ProcessBuilder("open", "\"" + textPath + "\"").start();
            } catch (IOException e) {
//                log.error(ExceptionUtils.getStackTrace(e));
            }
        } else {
//            log.error("Unsupported operating system!");
        }
    }
}
