package org.hkfree.ospf.gui.mappanel;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro změnu ceny spoje
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class ChangeLinkCostDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private boolean loadStandardGUI = true;
    private boolean dataSaved = false;
    private JTextField cost1Input = new JTextField();
    private JTextField cost2Input = new JTextField();
    private JLabel linkNameLabel = new JLabel();
    private JLabel vertex1Name = new JLabel();
    private JLabel vertex2Name = new JLabel();
    private JLabel vertex1IP = new JLabel();
    private JLabel vertex2IP = new JLabel();
    private JDialog thisDialog = this;
    private int changedCost1 = 0;
    private int changedCost2 = 0;


    /**
     * Konstruktor
     */
    public ChangeLinkCostDialog(LinkEdge linkEdge) {
	if (linkEdge.isEdgeOfMultilink()) {
	    loadStandardGUI = false;
	}
	cost1Input.setText(Integer.toString(linkEdge.getCost1v4()));
	cost2Input.setText(Integer.toString(linkEdge.getCost2v4()));
	linkNameLabel.setText(linkEdge.getLinkIDv4());
	vertex1Name.setText(linkEdge.getVertex1().getLabel());
	vertex1IP.setText(linkEdge.getVertex1().getInfo());
	vertex2Name.setText(linkEdge.getVertex2().getLabel());
	vertex2IP.setText(linkEdge.getVertex2().getInfo());
	createComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void createComponents() {
	JLabel lnkIdlbl = new JLabel(rb.getString("clcd.0") + ":");
	linkNameLabel.setFont(new Font("Arial", 1, 14));
	JLabel routersLbl = new JLabel(rb.getString("clcd.1") + ":");
	routersLbl.setFont(new Font("Arial", 2, 11));
	vertex1Name.setFont(new Font("Arial", 1, 11));
	vertex1IP.setBounds(130, 75, 100, 20);
	vertex2Name.setFont(new Font("Arial", 1, 11));
	JButton btnOk = new JButton(rb.getString("ok"));
	btnOk.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		try {
		    changedCost1 = Integer.parseInt(cost1Input.getText());
		    changedCost2 = Integer.parseInt(cost2Input.getText());
		    dataSaved = true;
		    thisDialog.setVisible(false);
		} catch (Exception e) {
		    JOptionPane.showMessageDialog(thisDialog, "\n" + rb.getString("clcd.2") + " \n\n",
			    rb.getString("warning"), JOptionPane.WARNING_MESSAGE);
		}
	    }
	});
	JButton btnStorno = new JButton(rb.getString("storno"));
	btnStorno.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		thisDialog.setVisible(false);
	    }
	});
	
	JPanel panelCosts = new JPanel();
	if (loadStandardGUI) {
	    panelCosts.setLayout(new GridLayout(2, 3, 10, 5));
	} else {
	    panelCosts.setLayout(new GridLayout(1, 3, 10, 5));
	}
	panelCosts.add(vertex1Name);
	panelCosts.add(vertex1IP);
	panelCosts.add(cost1Input);
	if (loadStandardGUI) {
	    panelCosts.add(vertex2Name);
	    panelCosts.add(vertex2IP);
	    panelCosts.add(cost2Input);
	}
	 
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addGroup(layout.createSequentialGroup()
			.addComponent(lnkIdlbl)
			.addComponent(linkNameLabel))
		.addComponent(routersLbl)
		.addComponent(panelCosts)
		.addGroup(layout.createSequentialGroup()
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(btnOk, 100,100,100)
			.addComponent(btnStorno, 100,100,100))
		);
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(lnkIdlbl, Alignment.TRAILING)
			.addComponent(linkNameLabel)).addGap(20)
		.addComponent(routersLbl)
		.addComponent(panelCosts)
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(btnOk, Alignment.TRAILING)
			.addComponent(btnStorno))
		);
	
	this.setTitle(rb.getString("clcd.title"));
	this.pack();
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	this.setModal(true);
	this.setVisible(true);
    }


    /**
     * Vrací příznak zda byla data úspěšně zadána pro uložení
     * @return boolean
     */
    public boolean costDataSaved() {
	return dataSaved;
    }


    /**
     * Vrací první zadanou cenu
     * @return int
     */
    public int getChangedCost1() {
	return changedCost1;
    }


    /**
     * Vrací druhou zadanou cenu
     * @return int
     */
    public int getChangedCost2() {
	return changedCost2;
    }
}
