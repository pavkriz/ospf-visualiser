package org.hkfree.ospf.gui.netstateswin;

import javax.swing.JCheckBox;

import org.hkfree.ospf.model.ospf.OspfModel;

/**
 * Třída přeedstavující položku listu k zaškrtnutí pro výběr ke zpracování
 * @author Jakub Menzel
 */
public class ModelCheckBoxItem extends JCheckBox {

    private static final long serialVersionUID = 1L;
    private Object modelName = null;
    private OspfModel ospfModel = null;


    /**
     * Konstruktor
     * @param modelName
     * @param selected
     * @param ospfModel
     */
    public ModelCheckBoxItem(Object modelName, boolean selected, OspfModel ospfModel) {
	super(modelName == null ? "" : "" + modelName, selected);
	this.ospfModel = ospfModel;
	setModelName(modelName);
    }


    /**
     * Vrací příznak, zda je zaškrtnutý
     * @return boolean
     */
    public boolean isSelected() {
	return super.isSelected();
    }


    /**
     * Nastavuje zaškrtnutí
     * @param selected
     */
    public void setSelected(boolean selected) {
	super.setSelected(selected);
    }


    /**
     * Vrací název modelu
     * @return modelName
     */
    public Object getModelName() {
	return modelName;
    }


    /**
     * Nastavuje název modelu
     * @param modelName
     */
    public void setModelName(Object modelName) {
	this.modelName = modelName;
    }


    /**
     * Vrací OspfModel
     * @return OspfModel
     */
    public OspfModel getOspfModel() {
	return ospfModel;
    }
}
