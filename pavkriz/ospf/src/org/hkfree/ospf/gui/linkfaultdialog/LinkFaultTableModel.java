package org.hkfree.ospf.gui.linkfaultdialog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.hkfree.ospf.model.ospffault.IntervalFault;
import org.hkfree.ospf.tools.Factory;

/**
 * Table model sloužící k zobrazení tabulky s výpadky spojů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LinkFaultTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<IntervalFault> intervalFaults = null;
    private String[] columnNames;


    /**
     * Konstruktor
     * @param intervalFaults
     */
    public LinkFaultTableModel(List<IntervalFault> intervalFaults) {
	columnNames = new String[getColumnCount()];
	for (int i = 0; i < getColumnCount(); i++) {
	    columnNames[i] = rb.getString("lftm.col" + i);
	}
	this.intervalFaults = intervalFaults;
    }


    /**
     * Vrací počet sloupců tabulky
     * @return int
     */
    public int getColumnCount() {
	return 2;
    }


    /**
     * Vrací počet řádků tabulky
     * @return int
     */
    public int getRowCount() {
	return intervalFaults.size();
    }


    /**
     * Vrací hodnoty buněk tabulky
     * @return Object
     */
    public Object getValueAt(int row, int column) {
	switch (column) {
	    case 0:
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(intervalFaults.get(row).getFaultDate());
	    case 1:
		return intervalFaults.get(row).getFaultCount();
	}
	return null;
    }


    @Override
    public String getColumnName(int columnIndex) {
	return columnNames[columnIndex];
    }
}
