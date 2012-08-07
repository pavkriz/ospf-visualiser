package org.hkfree.ospf.gui.sourcedialog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Třída sloužící k načítání rozbalovacího stromu vstupních dat umístěných na serveru
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class RemoteSourceTreeLoader {

    private String address = "";
    private String folderIdentification = "folder";
    private String fileIdentification = "compressed";
    private String fileNameStartFilter = "";


    /**
     * Konstruktor
     */
    public RemoteSourceTreeLoader() {}


    /**
     * Nastaví filter pro začátek názvu soborů, které mají být brány v úvahu
     * @param startString
     */
    public void setFileNameStartFilter(String startString) {
	this.fileNameStartFilter = startString;
    }


    /**
     * Nastaví adresu
     * @param address
     */
    public void setAddress(String address) {
	this.address = address;
    }


    /**
     * Nastaví idenfitikaci na okdaz složky a identifikaci na odkaz soboru
     * @param folderIdentification
     * @param fileIdentification
     */
    public void setParsingIdentifications(String folderIdentification, String fileIdentification) {
	this.folderIdentification = folderIdentification;
	this.fileIdentification = fileIdentification;
    }


    /**
     * Načte rozbalovací strom
     * @return node
     */
    public DefaultMutableTreeNode loadTree(boolean isLoadedLogs) {
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(address);
	if (isLoadedLogs) {
	    readAndParsePageStructure(rootNode, address);
	    return rootNode;
	}
	try {
	    GZIPInputStream gzipInStream = new GZIPInputStream(new BufferedInputStream(
		    new URL(address + fileIdentification).openStream()));
	    InputStreamReader isr = new InputStreamReader(gzipInStream);
	    BufferedReader inBfrdRdr = new BufferedReader(isr);
	    String line = "";
	    DefaultMutableTreeNode nodeY = null;
	    DefaultMutableTreeNode nodeM = null;
	    DefaultMutableTreeNode nodeD = null;
	    while ((line = inBfrdRdr.readLine()) != null) {
		if (line.isEmpty())
		    break;
	    }
	    while ((line = inBfrdRdr.readLine()) != null) {
		if (line.endsWith(":")) {
		    if (nodeY == null && nodeM == null && nodeD == null) {
			nodeY = new DefaultMutableTreeNode(line.substring(2, 6));
			rootNode.add(nodeY);
			nodeM = new DefaultMutableTreeNode(line.substring(7, 9));
			nodeY.add(nodeM);
			nodeD = new DefaultMutableTreeNode(line.substring(10, 12));
			nodeM.add(nodeD);
		    } else if (!line.substring(2, 6).equals(nodeY.getUserObject())) {
			nodeY = new DefaultMutableTreeNode(line.substring(2, 6));
			nodeM = new DefaultMutableTreeNode(line.substring(7, 9));
			nodeM = new DefaultMutableTreeNode(line.substring(10, 12));
			nodeM.add(nodeD);
			nodeY.add(nodeM);
			rootNode.add(nodeY);
		    } else if (!line.substring(7, 9).equals(nodeM.getUserObject())) {
			nodeM = new DefaultMutableTreeNode(line.substring(7, 9));
			nodeD = new DefaultMutableTreeNode(line.substring(10, 12));
			nodeM.add(nodeD);
			nodeY.add(nodeM);
		    } else {
			nodeD = new DefaultMutableTreeNode(line.substring(10, 12));
			nodeM.add(nodeD);
		    }
		} else {
		    nodeD.add(new DefaultMutableTreeNode(new FileNameCheckBoxNode(line)));
		}
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return rootNode;
    }


    /**
     * Zpracuje strukturu zdorojového kódu a vytváří strom
     * @param node
     * @param urlpath
     * @throws Exception
     */
    public void readAndParsePageStructure(DefaultMutableTreeNode node, String urlpath) {
	/*
	 * BufferedReader reader = null;
	 * Pattern ipPattern = Pattern.compile("^.*(href=\")(.*)(\".*</a>)");
	 * Matcher ipMatcher = null;
	 * try {
	 * URL address = new URL(urlpath);
	 * reader = new BufferedReader(new InputStreamReader(address.openStream()));
	 * String line = "";
	 * while ((line = reader.readLine()) != null) {
	 * if (line.contains(folderIdentification)) {
	 * ipMatcher = ipPattern.matcher(line);
	 * ipMatcher.find();
	 * String name = ipMatcher.group(2).replace("/", "");
	 * node.add(new DefaultMutableTreeNode(name));
	 * this.readAndParsePageStructure((DefaultMutableTreeNode) node.getLastLeaf(), urlpath + "/" + name);
	 * }
	 * if (line.contains(fileIdentification)) {
	 * ipMatcher = ipPattern.matcher(line);
	 * ipMatcher.find();
	 * String name = ipMatcher.group(2);
	 * if (name.startsWith(fileNameStartFilter)) {
	 * node.add(new DefaultMutableTreeNode(new FileNameCheckBoxNode(name)));
	 * }
	 * }
	 * }
	 * } finally {
	 * reader.close();
	 * }
	 */
    }


    public String getFileIdentification() {
	return fileIdentification;
    }


    public void setFileIdentification(String fileIdentification) {
	this.fileIdentification = fileIdentification;
    }
}
