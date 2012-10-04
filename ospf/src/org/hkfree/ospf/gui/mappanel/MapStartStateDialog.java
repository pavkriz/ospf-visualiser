package org.hkfree.ospf.gui.mappanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;

import org.hkfree.ospf.gui.summarydialog.RouterListModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro výběr typu prvotního zobrazení okna pro návrh sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapStartStateDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private ButtonGroup radioGroup = new ButtonGroup();
    private JRadioButton wholeModelRB = new JRadioButton();
    private JRadioButton startRouterRB = new JRadioButton();
    private JLabel depthLabel = new JLabel();
    private JLabel rSearchLabel = new JLabel();
    private JTextField rSearchTxt = new JTextField();
    private JList<RouterListModel> routersJList = null;
    private JScrollPane routerScrollPane = null;
    private RouterListModel routerListModel = null;
    private JSpinner neighbourDepth = new JSpinner();
    private SpinnerNumberModel depthModel = new SpinnerNumberModel();
    private JButton btnOk = new JButton();
    private JButton btnStorno = new JButton();
    private boolean selectionConfirmed = false;


    /**
     * Konstrutkor
     * @param routerList
     * @param preselectedRouter
     */
    public MapStartStateDialog(List<Router> routerList, Router preselectedRouter) {
	this.routerListModel = new RouterListModel(routerList);
	radioGroup.add(wholeModelRB);
	wholeModelRB.setText(rb.getString("mdssd.0"));
	wholeModelRB.addActionListener(this);
	wholeModelRB.setSelected(true);
	radioGroup.add(startRouterRB);
	startRouterRB.setText(rb.getString("mdssd.1") + ":");
	startRouterRB.addActionListener(this);
	JPanel panel1 = new JPanel();
	panel1.setBorder(BorderFactory.createTitledBorder(rb.getString("mdssd.4") + ":"));
	rSearchLabel.setText(rb.getString("mdssd.3") + ":");
	rSearchTxt.addActionListener(this);
	routersJList = new JList<RouterListModel>(this.routerListModel);
	routerScrollPane = new JScrollPane(routersJList);
	depthLabel.setText(rb.getString("mdssd.2") + ":");
	btnOk.setText(rb.getString("ok"));
	btnOk.addActionListener(this);
	btnStorno.setText(rb.getString("storno"));
	btnStorno.addActionListener(this);
	depthModel.setValue(0);
	depthModel.setMinimum(0);
	depthModel.setMaximum(5);
	neighbourDepth.setModel(depthModel);
	// setPreselectedRouter(preselectedRouter);
	routerScrollPane.setAutoscrolls(true);
	activateWholeModelChoice(true);
	GroupLayout l0 = new GroupLayout(panel1);
	panel1.setLayout(l0);
	l0.setAutoCreateContainerGaps(true);
	l0.setAutoCreateGaps(true);
	l0.setHorizontalGroup(l0.createSequentialGroup()
		.addGroup(l0.createParallelGroup()
			.addGroup(l0.createSequentialGroup()
				.addComponent(rSearchLabel)
				.addComponent(rSearchTxt))
			.addComponent(routerScrollPane)
			.addGroup(l0.createSequentialGroup()
				.addComponent(depthLabel)
				.addComponent(neighbourDepth)
			)));
	l0.setVerticalGroup(l0.createSequentialGroup()
		.addGroup(l0.createParallelGroup()
			.addComponent(rSearchLabel)
			.addComponent(rSearchTxt))
		.addComponent(routerScrollPane)
		.addGroup(l0.createParallelGroup()
			.addComponent(depthLabel)
			.addComponent(neighbourDepth)
		));
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(
			layout.createParallelGroup()
				.addComponent(wholeModelRB)
				.addComponent(startRouterRB)
				.addComponent(panel1)
				.addGroup(
					layout.createSequentialGroup()
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
							GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnOk, 100, 100, 100)
						.addComponent(btnStorno, 100, 100, 100)))
		);
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(wholeModelRB)
		.addComponent(startRouterRB).addGap(30)
		.addComponent(panel1)
		.addGroup(layout.createParallelGroup()
			.addComponent(btnStorno)
			.addComponent(btnOk))
		);
	this.setTitle(rb.getString("mdssd.title"));
	// this.setSize(440, 400);
	this.pack();
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	this.setModal(true);
    }


    /**
     * Zpřístupní / znepřístupní komponenty GUI
     * @param value
     */
    public void activateWholeModelChoice(boolean value) {
	routersJList.setEnabled(!value);
	depthLabel.setEnabled(!value);
	neighbourDepth.setEnabled(!value);
	rSearchLabel.setEnabled(!value);
	rSearchTxt.setEnabled(!value);
	routerScrollPane.setEnabled(!value);
    }


    /**
     * Vrací příznak zda bylo vybráno zobrazení celé sítě
     * @return boolean
     */
    public boolean wholeModelIsSelected() {
	return wholeModelRB.isSelected();
    }


    /**
     * Vrací hloubku pro zobrazení sousedů
     * @return int
     */
    public int getNeighboursDepth() {
	return depthModel.getNumber().intValue();
    }


    /**
     * Vrací vybraný centrální router
     * @return Router
     */
    public Router getSelectedRouter() {
	return routerListModel.getRouterByIndex(routersJList.getSelectedIndex());
    }


    /**
     * Vrací příznak, zda byl výběr potvrzen
     * @return boolean
     */
    public boolean selectionConfirmed() {
	return selectionConfirmed;
    }


    /**
     * Nastavuje předvybraný router
     * @param router
     */
    public void setPreselectedRouter(Router router) {
	routerListModel.setSelectedRouter(router);
    }


    /**
     * Obsluha událostí
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == wholeModelRB) {
	    activateWholeModelChoice(true);
	}
	if (e.getSource() == startRouterRB) {
	    activateWholeModelChoice(false);
	}
	if (e.getSource() == btnStorno) {
	    this.setVisible(false);
	}
	if (e.getSource() == btnOk) {
	    if (wholeModelIsSelected()) {
		selectionConfirmed = true;
		this.setVisible(false);
	    } else {
		if (routerListModel.getSelectedItem() != null) {
		    selectionConfirmed = true;
		    this.setVisible(false);
		} else {
		    JOptionPane.showMessageDialog(this, "\n" + rb.getString("mdssd.6") + "\n", rb.getString("warning"),
			    JOptionPane.WARNING_MESSAGE);
		}
	    }
	}
	if (e.getSource() == rSearchTxt) {
	    routerListModel.updadteList(rSearchTxt.getText(), 1);
	    routersJList.setSelectedIndex(0);
	    routersJList.repaint();
	}
    }
}
