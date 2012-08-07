package org.hkfree.ospf.gui.costdifferencesdialog;

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.hkfree.ospf.model.netchange.CostDifference;
import org.hkfree.ospf.tools.Factory;

/**
 * Table model sloužící pro zobrazení změn costů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class CostDifferenceTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<CostDifference> costDifferences = null;
    private String[] columnNames;


    /**
     * Konstruktor
     * @param costDifferences
     */
    public CostDifferenceTableModel(List<CostDifference> costDifferences) {
	columnNames = new String[getColumnCount()];
	for (int i = 0; i < getColumnCount(); i++) {
	    columnNames[i] = rb.getString("cdtm.col" + i);
	}
	this.costDifferences = costDifferences;
    }


    /**
     * Vrací počet sloupců tabulky
     * @return int
     */
    public int getColumnCount() {
	return 4;
    }


    /**
     * Vrací počet řádků tabulky
     * @return int
     */
    public int getRowCount() {
	return costDifferences.size();
    }


    /**
     * Vrací hodnoty buněk tabulky
     * @return Object
     */
    public Object getValueAt(int row, int column) {
	switch (column) {
	    case 0:
		return costDifferences.get(row).getRouterDescription();
	    case 1:
		return costDifferences.get(row).getLinkId();
	    case 2:
		return costDifferences.get(row).getPreviousCost();
	    case 3:
		return costDifferences.get(row).getActualCost();
	}
	return null;
    }


    @Override
    public String getColumnName(int columnIndex) {
	return columnNames[columnIndex];
    }
}
