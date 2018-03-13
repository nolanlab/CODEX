package com.akoya.codex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestHelper {

    public static void waitAndPrint(Process proc) throws IOException {
        do {
            try {
                BufferedReader brOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String s = null;
                while ((s = brOut.readLine()) != null) {
                    log(s);
                }

                BufferedReader brErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                while ((s = brErr.readLine()) != null) {
                    log("ERROR>" + s);
                }

                Thread.sleep(100);

            } catch (InterruptedException e) {
                log("Process interrupted");
                return;
            }
        } while (proc.isAlive());
        log("Process done");
    }

    public static void log(String s) {
        System.out.println(s);
    }
}
