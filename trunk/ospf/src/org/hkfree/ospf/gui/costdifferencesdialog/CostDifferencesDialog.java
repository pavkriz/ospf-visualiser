package org.hkfree.ospf.gui.costdifferencesdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.hkfree.ospf.model.netchange.CostDifference;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro zobrazení změn costů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class CostDifferencesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JButton btnOk = new JButton();
    private List<CostDifference> costDifferences = null;


    /**
     * Konstruktor
     * @param costDifferences
     */
    public CostDifferencesDialog(List<CostDifference> costDifferences) {
	this.costDifferences = costDifferences;
	createGUI();
    }


    /**
     * Vytvoří GUI
     */
    public void createGUI() {
	JTable table = new JTable(new CostDifferenceTableModel(this.costDifferences));
	JScrollPane scroll = new JScrollPane(table);
	btnOk.setText(rb.getString("ok"));
	btnOk.addActionListener(this);
	
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(scroll, 650,650,650)
			.addGroup(layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnOk, 100,100,100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(scroll, 400,400,400)
		.addComponent(btnOk));
	this.pack();
	this.setResizable(false);
	this.setLocationRelativeTo(null);
	this.setTitle(rb.getString("cdd.title"));
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
