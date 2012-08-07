package org.hkfree.ospf.gui.importancedialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.hkfree.ospf.model.ospfcomponent.LinkImportance;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog zobrazení důležitosti spojů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LinkImportanceDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JButton btnOk = new JButton();
    private List<LinkImportance> linkImportances = null;


    /**
     * Konstruktor
     * @param linkImportances
     */
    public LinkImportanceDialog(List<LinkImportance> linkImportances) {
	this.linkImportances = linkImportances;
	createGUI();
    }


    /**
     * Vytvoří GUI
     */
    private void createGUI() {
	JLabel description = new JLabel("<HTML>" + rb.getString("lid.desription") + ".</HTML>");
	description.setFont(new Font("Arial", 2, 11));
	description.setMaximumSize(new Dimension(400,40));
	description.setPreferredSize(new Dimension(400,40));
	JPanel panel1 = new JPanel();
	panel1.setLayout(new BorderLayout());
	panel1.setBorder(BorderFactory.createTitledBorder(rb.getString("lid.0") + ":"));
	JTable table = new JTable(new LinkImportanceTableModel(this.linkImportances)) {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public String getToolTipText(MouseEvent e) {
		int rowIndex = rowAtPoint(e.getPoint());
		return ((LinkImportanceTableModel) getModel()).getRowToolTip(rowIndex);
	    }
	};
	panel1.add(new JScrollPane(table), BorderLayout.CENTER);
	btnOk.setText(rb.getString("ok"));
	btnOk.addActionListener(this);
	
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup()
			.addComponent(description)
			.addComponent(panel1, 440,440,440)
			.addGroup(layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnOk, 100,100,100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(description).addGap(30)
		.addComponent(panel1, 550,550,550)
		.addComponent(btnOk));
	this.setResizable(false);
	this.pack();
	this.setLocationRelativeTo(null);
	this.setTitle(rb.getString("lid.title"));
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
