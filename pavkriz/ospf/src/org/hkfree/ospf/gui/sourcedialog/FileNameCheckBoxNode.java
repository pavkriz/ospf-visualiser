package org.hkfree.ospf.gui.sourcedialog;

/**
 * Třída představující položku rozbalovacího stromu pro zaškrtnutí
 * @author Jakub Menzel
 */
public class FileNameCheckBoxNode {

    protected final String fileName;
    protected boolean selected;


    /**
     * Konstruktor
     * @param fileName
     */
    public FileNameCheckBoxNode(String fileName) {
	this.fileName = fileName;
    }


    /**
     * Vrací název souboru
     * @return name
     */
    public String getName() {
	return fileName;
    }


    /**
     * Vrací příznak, zda je pole zaškrtnuté
     * @return boolean
     */
    public boolean isSelected() {
	return selected;
    }


    /**
     * Nastavuje příznak zaškrtnutí
     * @param value
     */
    public void setSelected(boolean value) {
	this.selected = value;
    }


    /**
	   * 
	   */
    public String toString() {
	return fileName;
    }
}
