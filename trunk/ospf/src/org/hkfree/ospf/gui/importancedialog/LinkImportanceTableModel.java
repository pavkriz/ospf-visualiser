package org.hkfree.ospf.gui.importancedialog;

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.model.ospfcomponent.LinkImportance;
import org.hkfree.ospf.tools.Factory;

/**
 * Table model sloužící k zobrazení tabulky se spoji jejichž výpadek rozpadne
 * síť na víc částí
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LinkImportanceTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<LinkImportance> linkImportances = null;
    private String[] columnNames;


    /**
     * Konstruktor
     * @param linkImportances
     */
    public LinkImportanceTableModel(List<LinkImportance> linkImportances) {
	columnNames = new String[getColumnCount()];
	for (int i = 0; i < getColumnCount(); i++) {
	    columnNames[i] = rb.getString("litm.col" + i);
	}
	this.linkImportances = linkImportances;
    }


    /**
     * Vrací počet sloupců tabulky
     * @return int
     */
    public int getColumnCount() {
	return 3;
    }


    /**
     * Vrací počet řádků tabulky
     * @return int
     */
    public int getRowCount() {
	return linkImportances.size();
    }


    /**
     * Vrací hodnoty buněk tabulky
     * @return int
     */
    public Object getValueAt(int row, int column) {
	switch (column) {
	    case 0:
		return linkImportances.get(row).getOspfLink().getLinkID();
	    case 1:
		return linkImportances.get(row).getOspfLink().getSubnetMask();
	    case 2:
		return linkImportances.get(row).getAfterDisableNetPartsCount();
	}
	return null;
    }


    @Override
    public String getColumnName(int columnIndex) {
	return columnNames[columnIndex];
    }


    /**
     * Vrací tooltip text řádku
     * @param rowIndex
     * @return string
     */
    public String getRowToolTip(int rowIndex) {
	String toolTip = "<html>" 
		+ rb.getString("litm.col0") 
		+ ": <b>"
		+ linkImportances.get(rowIndex).getOspfLink().getLinkID() 
		+ "</b> <br>" 
		+ rb.getString("litm.col1")
		+ ": <b>" 
		+ linkImportances.get(rowIndex).getOspfLink().getSubnetMask() 
		+ "</b> <br>"
		+ rb.getString("litm.0") 
		+ ": " 
		+ linkImportances.get(rowIndex).getOspfLink().getNetworkAddress() 
		+ " <br>"
		+ rb.getString("litm.1") 
		+ ": " 
		+ linkImportances.get(rowIndex).getOspfLink().getBroadcastAddress()
		+ " <br><br>" 
		+ rb.getString("litm.2")
		+ ": <br>";
	for (OspfLinkData old : linkImportances.get(rowIndex).getOspfLink().getOspfLinkData()) {
	    toolTip += "<b>" + old.getRouter().getRouterID() + "</b> " + old.getRouter().getRouterName() + " <br>";
	}
	toolTip += "</html>";
	return toolTip;
    }
}
