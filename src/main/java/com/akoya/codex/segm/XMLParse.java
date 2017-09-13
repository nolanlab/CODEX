/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.segm;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 *
 * @author CODEX
 */
public class XMLParse {
   

  public static void main(String argv[]) {

    try {

	File fXmlFile = new File("/Users/mkyong/staff.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(new File("C:\\Users\\CODEX\\Desktop\\tonsil.xml"));

	//optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

	NodeList nList = doc.getChildNodes();

	System.out.println("----------------------------");

	printChildRecur(nList);
    } catch (Exception e) {
	e.printStackTrace();
    }
  }
  
  public static void printChildRecur(NodeList nList){
      for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		//System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if(nNode.getNodeName().equals("Key")){
                    System.out.println("<A href=\"https://storage.googleapis.com/5-30-17-tonsil8529-multicycle/"+nNode.getFirstChild().getNodeValue()+"\">" + nNode.getFirstChild().getNodeValue()+ "</A>");
                }
                
                printChildRecur(nNode.getChildNodes());
	}
  }

}

