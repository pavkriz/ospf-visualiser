package org.hkfree.ospf.gui.loadedlogslistdialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro zobrazení načtených logů
 * 
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LoadedLogsListDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JButton btnOk = new JButton();
    private List<String> loadedLogsList = null;


    /**
     * Konstruktor
     * 
     * @param loadedLogsList
     */
    public LoadedLogsListDialog(List<String> loadedLogsList) {
	this.loadedLogsList = loadedLogsList;
	createGUI();
    }


    /**
     * Vytvoří GUI
     */
    private void createGUI() {
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	JTable table = new JTable(new LoadedLogsListTableModel(this.loadedLogsList)) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getToolTipText(MouseEvent e) {
		int rowIndex = rowAtPoint(e.getPoint());
		return ((LoadedLogsListTableModel) getModel()).getRowToolTip(rowIndex);
	    }
	};
	panel.add(new JScrollPane(table), BorderLayout.CENTER);
	btnOk.setText(rb.getString("ok"));
	btnOk.addActionListener(this);
	
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup()
			.addComponent(panel)
			.addComponent(btnOk, Alignment.TRAILING, 100,100,100)
			));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(panel)
		.addComponent(btnOk)
		);
	
	
	this.setResizable(false);
	this.pack();
	this.setLocationRelativeTo(null);
	this.setTitle(rb.getString("llld.title"));
	this.setModal(true);
    }


    /**
     * Odchytávání událostí
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btnOk) {
	    this.setVisible(false);
	}
    }
}
