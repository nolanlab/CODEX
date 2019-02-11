/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.upload;

/**
 *
 * @author Nikolay Samusik
 */
public class util {

    public static String concat(String[] s) {
        if (s.length == 0) {
            return "";
        }
        String out = s[0];
        for (int i = 1; i < s.length; i++) {
            out += (";" + s[i]);
        }
        return out;
    }

    public static String concat(int[] s) {
        if (s.length == 0) {
            return "";
        }
        String out = String.valueOf(s[0]);
        for (int i = 1; i < s.length; i++) {
            out += (";" + s[i]);
        }
        return out;
    }
}
