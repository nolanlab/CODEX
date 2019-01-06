/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nolanlab.codex.toolkit;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Nikolay Samusik
 */
public class VerifyDelaunayGraph {
    public static void main(String[] args) throws IOException{
        File f = new File("D:\\CODEX paper revision");
        DelaunayGraph.verifyGraph(f);
    }
}
