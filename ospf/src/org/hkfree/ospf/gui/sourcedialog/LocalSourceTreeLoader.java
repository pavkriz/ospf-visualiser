package org.hkfree.ospf.gui.sourcedialog;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Třída sloužící k vytvoření rozbalovacího stromu
 * @author Jakub Menzel
 */
public class LocalSourceTreeLoader {

    String path = "";


    /**
     * Konstruktor
     */
    public LocalSourceTreeLoader() {}


    /**
     * Nastavení cesty rootu
     * @param path
     */
    public void setPath(String path) {
	this.path = path;
    }


    @SuppressWarnings("finally")
    public DefaultMutableTreeNode loadTree() {
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(path);
	try {
	    readAndParsePaths(rootNode, path);
	} catch (Exception ex) {} finally {
	    return rootNode;
	}
    }


    /**
     * Zpracuje cestu a vytvoří strukturu
     * @param node
     * @param path
     * @throws Exception
     */
    public void readAndParsePaths(DefaultMutableTreeNode node, String path) throws Exception {
	try {
	    File actualFolder = new File(path);
	    String actFiles[] = actualFolder.list();
	    for (String filename : actFiles) {
		if (new File(path + "/" + filename).isDirectory()) {
		    node.add(new DefaultMutableTreeNode(filename));
		    this.readAndParsePaths((DefaultMutableTreeNode) node.getLastLeaf(), path + "/" + filename);
		} else {
		    node.add(new DefaultMutableTreeNode(new FileNameCheckBoxNode(filename)));
		}
	    }
	} finally {}
    }
}
