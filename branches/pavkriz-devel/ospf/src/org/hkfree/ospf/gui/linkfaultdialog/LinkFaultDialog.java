package org.hkfree.ospf.gui.linkfaultdialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.hkfree.ospf.model.ospffault.IntervalFault;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro zobrazení výpadků spojů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LinkFaultDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JButton btnOk = new JButton();
    private List<IntervalFault> intervalFaults = null;
    private String linkID = "";


    /**
     * Konstruktor
     * @param intervalFaults
     * @param linkID
     */
    public LinkFaultDialog(List<IntervalFault> intervalFaults, String linkID) {
	this.intervalFaults = intervalFaults;
	this.linkID = linkID;
	createGUI();
    }


    /**
     * Vytvoří GUI
     */
    public void createGUI() {
	JLabel lbllnk = new JLabel(rb.getString("lfd.1") + ": ");
	JLabel lbllnkId = new JLabel(linkID);
	lbllnkId.setFont(new Font("Arial", 1, 11));
	JTable table = new JTable(new LinkFaultTableModel(this.intervalFaults));
	JScrollPane scroll = new JScrollPane(table);
	btnOk.setText(rb.getString("ok"));
	btnOk.addActionListener(this);
	
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addGroup(layout.createSequentialGroup()
				.addComponent(lbllnk)
				.addComponent(lbllnkId))
			.addComponent(scroll, 300,300,300)
			.addGroup(layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnOk, 100,100,100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup()
			.addComponent(lbllnk)
			.addComponent(lbllnkId))
		.addComponent(scroll, 250,250,250)
		.addComponent(btnOk));
	this.setResizable(false);
	this.pack();
	this.setLocationRelativeTo(null);
	this.setTitle(rb.getString("lfd.title"));
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
