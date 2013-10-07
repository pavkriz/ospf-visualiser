package org.hkfree.ospf.gui.mappanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro přidávání nových routerů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class AddRouterVertexDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JTextField txtRVName = new JTextField("");
    private List<RouterVertex> routerVertexes = null;
    private boolean successfulyConfirmed = false;
    private JDialog thisDialog = this;


    /**
     * Konstruktor
     */
    public AddRouterVertexDialog(List<RouterVertex> routerVertexes) {
	this.routerVertexes = routerVertexes;
	createComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void createComponents() {
	JLabel lnkIdlbl = new JLabel(rb.getString("arvd.0") + ":");
	txtRVName.setMinimumSize(new Dimension(250, 30));
	JButton btnOk = new JButton(rb.getString("ok"));
	btnOk.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		successfulyConfirmed = true;
		for (RouterVertex rv : routerVertexes) {
		    if (rv.getName().equals(txtRVName.getText()) || rv.getInfo().equals(txtRVName.getText())) {
			JOptionPane.showMessageDialog(thisDialog, "\n" + rb.getString("arvd.1") + " \n\n",
				rb.getString("warning"), JOptionPane.WARNING_MESSAGE);
			successfulyConfirmed = false;
			break;
		    }
		}
		if (txtRVName.getText().equals("")) {
		    JOptionPane.showMessageDialog(thisDialog, "\n" + rb.getString("arvd.2") + " \n\n",
			    rb.getString("warning"), JOptionPane.WARNING_MESSAGE);
		    successfulyConfirmed = false;
		}
		if (successfulyConfirmed) {
		    thisDialog.setVisible(false);
		}
	    }
	});
	JButton btnStorno = new JButton(rb.getString("storno"));
	btnStorno.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		thisDialog.setVisible(false);
	    }
	});
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
			.addComponent(lnkIdlbl)
			.addComponent(txtRVName))
		.addGroup(layout.createSequentialGroup()
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(btnOk, 100, 100, 100)
			.addComponent(btnStorno, 100, 100, 100)));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup()
			.addComponent(lnkIdlbl)
			.addComponent(txtRVName))
		.addGroup(layout.createParallelGroup()
			.addComponent(btnOk)
			.addComponent(btnStorno)));
	this.setTitle(rb.getString("arvd.title"));
	this.pack();
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	this.setModal(true);
	this.setVisible(true);
    }


    /**
     * Dialog byl/nebyl úspěšně potrvrzen
     * @return boolean
     */
    public boolean successfulyConfirmed() {
	return successfulyConfirmed;
    }


    /**
     * Vrací zadanou hodnotu název routeru
     * @return string
     */
    public String getEnteredName() {
	return txtRVName.getText();
    }
}
