package org.hkfree.ospf.gui.mappanel;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
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

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída přestavující dialog pro přidávání nových spojů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class AddLinkEdgeDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JTextField txtLEName = new JTextField("");
    private List<LinkEdge> linkEdges = null;
    private JTextField txtCost1 = new JTextField("0");
    private JTextField txtCost2 = new JTextField("0");
    private int cost1 = 0;
    private int cost2 = 0;
    private RouterVertex routerV1 = null;
    private RouterVertex routerV2 = null;
    private boolean successfulyConfirmed = false;
    private JDialog thisDialog = this;


    /**
     * Konstruktor
     */
    public AddLinkEdgeDialog(List<LinkEdge> linkEdges, RouterVertex rv1, RouterVertex rv2) {
	this.linkEdges = linkEdges;
	if (rv1.isMultilink()) {
	    routerV1 = rv2;
	    routerV2 = rv1;
	} else {
	    routerV1 = rv1;
	    routerV2 = rv2;
	}
	createComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void createComponents() {
	JLabel lnkIdlbl = new JLabel(rb.getString("aled.0") + ":");
	JLabel costslbl = new JLabel(rb.getString("aled.1") + ":");
	costslbl.setFont(new Font("Arial", Font.ITALIC, 11));
	JLabel cost1lbl = new JLabel(routerV1.getName() + " (" + routerV1.getDescription() + ")");
	cost1lbl.setFont(new Font("Arial", Font.BOLD, 11));
	JButton btnOk = new JButton(rb.getString("ok"));
	btnOk.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		successfulyConfirmed = true;
		for (LinkEdge le : linkEdges) {
		    if (le.getLinkIDv4().equals(txtLEName.getText())) {
			JOptionPane.showMessageDialog(thisDialog, "\n" + rb.getString("aled.2") + " \n\n",
				rb.getString("warning"), JOptionPane.WARNING_MESSAGE);
			successfulyConfirmed = false;
			break;
		    }
		}
		if (txtLEName.getText().equals("")) {
		    JOptionPane.showMessageDialog(thisDialog, "\n" + rb.getString("aled.3") + " \n\n",
			    rb.getString("warning"), JOptionPane.WARNING_MESSAGE);
		    successfulyConfirmed = false;
		}
		try {
		    cost1 = Integer.parseInt(txtCost1.getText());
		    cost2 = Integer.parseInt(txtCost2.getText());
		} catch (Exception ex) {
		    successfulyConfirmed = false;
		    JOptionPane.showMessageDialog(thisDialog, "\n" + rb.getString("aled.4") + " \n\n",
			    rb.getString("warning"), JOptionPane.WARNING_MESSAGE);
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
	
	JPanel panelCosts = new JPanel();
	if (!routerV2.isMultilink()) {
	    panelCosts.setLayout(new GridLayout(2, 2, 10, 5));
	} else {
	    panelCosts.setLayout(new GridLayout(1, 2, 10, 5));
	}
	panelCosts.add(cost1lbl);
	panelCosts.add(txtCost1);
	if (!routerV2.isMultilink()) {
	    JLabel cost1lb2 = new JLabel(routerV2.getName() + " (" + routerV2.getDescription() + ")");
	    cost1lb2.setFont(new Font("Arial", 1, 11));
	    panelCosts.add(cost1lb2);
	    panelCosts.add(txtCost2);
	}
	
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addGroup(layout.createSequentialGroup()
			.addComponent(lnkIdlbl)
			.addComponent(txtLEName))
		.addComponent(costslbl)
		.addComponent(panelCosts)
		.addGroup(layout.createSequentialGroup()
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(btnOk, 100,100,100)
			.addComponent(btnStorno, 100,100,100))
		);
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(lnkIdlbl, Alignment.TRAILING)
			.addComponent(txtLEName)).addGap(20)
		.addComponent(costslbl)
		.addComponent(panelCosts)
		.addGroup(layout.createParallelGroup()
			.addComponent(btnOk)
			.addComponent(btnStorno))
		);
	
	this.setTitle(rb.getString("aled.title"));
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
     * Vrací zadanou hodnotu název spoje
     * @return string
     */
    public String getEnteredName() {
	return txtLEName.getText();
    }


    /**
     * Vrací zadanou cenu1
     * @return int
     */
    public int getCost1() {
	return Math.abs(cost1);
    }


    /**
     * Vrací zadanou cenu2
     * @return int
     */
    public int getCost2() {
	return Math.abs(cost2);
    }


    /**
     * Vrací router1
     * @return RouterVertex
     */
    public RouterVertex getRouterV1() {
	return routerV1;
    }


    /**
     * Vrací router2
     * @return RouterVertex
     */
    public RouterVertex getRouterV2() {
	return routerV2;
    }
}
