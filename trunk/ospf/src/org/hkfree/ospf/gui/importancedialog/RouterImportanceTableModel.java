package org.hkfree.ospf.gui.importancedialog;

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.hkfree.ospf.model.ospfcomponent.RouterImportance;
import org.hkfree.ospf.tools.Factory;

/**
 * Table model sloužící k zobrazení tabulky s routery jejichž výpadek rozpadne
 * síť na víc částí
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class RouterImportanceTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<RouterImportance> routerImportances = null;
    private String[] columnNames;


    /**
     * Konstruktor
     * @param routerImportances
     */
    public RouterImportanceTableModel(List<RouterImportance> routerImportances) {
	columnNames = new String[getColumnCount()];
	for (int i = 0; i < getColumnCount(); i++) {
	    columnNames[i] = rb.getString("ritm.col" + i);
	}
	this.routerImportances = routerImportances;
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
	return routerImportances.size();
    }


    /**
     * Vrací hodnoty buněk tabulky
     * @return Object
     */
    public Object getValueAt(int row, int column) {
	switch (column) {
	    case 0:
		return routerImportances.get(row).getRouter().getRouterID();
	    case 1:
		return routerImportances.get(row).getRouter().getRouterName();
	    case 2:
		return routerImportances.get(row).getAfterDisableNetPartsCount();
	}
	return null;
    }


    @Override
    public String getColumnName(int columnIndex) {
	return columnNames[columnIndex];
    }
}
