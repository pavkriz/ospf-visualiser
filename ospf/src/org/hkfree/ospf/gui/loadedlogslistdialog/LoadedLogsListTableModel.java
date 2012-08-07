package org.hkfree.ospf.gui.loadedlogslistdialog;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.hkfree.ospf.tools.Factory;

/**
 * Table model sloužící k zobrazení tabulky se seznamem načtených logů
 * 
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LoadedLogsListTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<String> loadedLogsList = null;
    private String[] columnNames;


    /**
     * Konstruktor
     * 
     * @param loadedLogsList
     */
    public LoadedLogsListTableModel(List<String> loadedLogsList) {
	columnNames = new String[getColumnCount()];
	for (int i = 0; i < getColumnCount(); i++) {
	    columnNames[i] = rb.getString("llltm.col" + i);
	}
	this.loadedLogsList = loadedLogsList;
    }


    /**
     * Vrací počet sloupců tabulky
     * 
     * @return int
     */
    public int getColumnCount() {
	return 1;
    }


    /**
     * Vrací počet řádků tabulky
     * 
     * @return int
     */
    public int getRowCount() {
	return loadedLogsList.size();
    }


    /**
     * Vrací hodnoty buněk tabulky
     * 
     * @return Object
     */
    public Object getValueAt(int row, int column) {
	switch (column) {
	    case 0:
		File f = new File(loadedLogsList.get(row));
		return f.getName();
	}
	return null;
    }


    @Override
    public String getColumnName(int columnIndex) {
	return columnNames[columnIndex];
    }


    /**
     * Vrací tooltip text řádku
     * 
     * @param rowIndex
     * @return string
     */
    public String getRowToolTip(int rowIndex) {
	return loadedLogsList.get(rowIndex);
    }
}
