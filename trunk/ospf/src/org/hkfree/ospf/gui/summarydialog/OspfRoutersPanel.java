package org.hkfree.ospf.gui.summarydialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující panel se seznamem routerů a vyhledáváním
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfRoutersPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    public static final int CONTAINS_STEXT = 1;
    public static final int START_WITH_STEXT = 2;
    public static final int ENDS_WITH_STEXT = 3;
    private OspfModel ospfModel = null;
    private JScrollPane spRouters;
    private JList listOfRouters = new JList();
    private RouterListModel routerListModel = null;
    private JTextField routerText = new JTextField();
    private ButtonGroup routerTextRadioGroup = new ButtonGroup();
    private JRadioButton rTextContRB = new JRadioButton();
    private JRadioButton rTextStartRB = new JRadioButton();
    private JRadioButton rTextEndRB = new JRadioButton();
    JLabel lblRouterIp = new JLabel("");
    JLabel lblRouterName = new JLabel("");
    JLabel lblRouterNameSuffix = new JLabel("");
    JLabel lblLinkCount = new JLabel("");


    /**
     * Konstruktor
     * @param ospfModel
     * @param actionListener
     */
    public OspfRoutersPanel(OspfModel ospfModel) {
	this.ospfModel = ospfModel;
	makeComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    private void makeComponents() {
	GroupLayout layout = new GroupLayout(this);
	this.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	spRouters = new JScrollPane(listOfRouters);
	ListSelectionListener lsl = new ListSelectionListener() {

	    public void valueChanged(ListSelectionEvent e) {
		showSelectedRouterInfo();
	    }
	};
	listOfRouters.addListSelectionListener(lsl);
	// rychle hledani
	JPanel routerSearchPanel = new JPanel();
	routerSearchPanel.setBorder(BorderFactory.createTitledBorder(rb.getString("orp.1")));
	JLabel lblSearchRouter = new JLabel(rb.getString("orp.2") + ":");
	routerText.setAction(new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		updateRouterList();
	    }
	});
	routerText.setMaximumSize(new Dimension(800, 25));
	routerTextRadioGroup.add(rTextContRB);
	routerTextRadioGroup.add(rTextStartRB);
	routerTextRadioGroup.add(rTextEndRB);
	rTextContRB.setText(rb.getString("orp.3"));
	rTextContRB.setSelected(true);
	rTextStartRB.setText(rb.getString("orp.4"));
	rTextEndRB.setText(rb.getString("orp.5"));
	GroupLayout lrsp = new GroupLayout(routerSearchPanel);
	routerSearchPanel.setLayout(lrsp);
	lrsp.setAutoCreateContainerGaps(true);
	lrsp.setAutoCreateGaps(true);
	lrsp.setHorizontalGroup(lrsp.createSequentialGroup().addGroup(
		lrsp.createParallelGroup()
			.addComponent(lblSearchRouter)
			.addComponent(routerText, 200, 250, 800)
			.addGroup(
				lrsp.createSequentialGroup().addComponent(rTextContRB).addComponent(rTextStartRB)
					.addComponent(rTextEndRB))));
	lrsp.setVerticalGroup(lrsp
		.createSequentialGroup()
		.addComponent(lblSearchRouter)
		.addComponent(routerText)
		.addGroup(
			lrsp.createParallelGroup().addComponent(rTextContRB).addComponent(rTextStartRB)
				.addComponent(rTextEndRB)));
	// informace o routeru
	JPanel infoAboutrouter = new JPanel();
	infoAboutrouter.setBorder(BorderFactory.createTitledBorder(rb.getString("orp.6")));
	JLabel lblR1 = new JLabel(rb.getString("orp.7") + ":");
	JLabel lblR2 = new JLabel(rb.getString("orp.8") + ":");
	JLabel lblR3 = new JLabel(rb.getString("orp.10") + ":");
	JLabel lblR4 = new JLabel(rb.getString("orp.9") + ":");
	GroupLayout liar = new GroupLayout(infoAboutrouter);
	infoAboutrouter.setLayout(liar);
	liar.setAutoCreateContainerGaps(true);
	liar.setAutoCreateGaps(true);
	liar.setHorizontalGroup(liar
		.createSequentialGroup()
		.addGroup(
			liar.createParallelGroup().addComponent(lblR1, Alignment.TRAILING)
				.addComponent(lblR2, Alignment.TRAILING).addComponent(lblR3, Alignment.TRAILING)
				.addComponent(lblR4, Alignment.TRAILING))
		.addGroup(
			liar.createParallelGroup().addComponent(lblRouterIp).addComponent(lblRouterName)
				.addComponent(lblRouterNameSuffix).addComponent(lblLinkCount)));
	liar.setVerticalGroup(liar.createSequentialGroup()
		.addGroup(liar.createParallelGroup().addComponent(lblR1).addComponent(lblRouterIp))
		.addGroup(liar.createParallelGroup().addComponent(lblR2).addComponent(lblRouterName))
		.addGroup(liar.createParallelGroup().addComponent(lblR3).addComponent(lblRouterNameSuffix))
		.addGroup(liar.createParallelGroup().addComponent(lblR4).addComponent(lblLinkCount)));
	layout.setHorizontalGroup(layout
		.createSequentialGroup()
		.addComponent(spRouters)
		.addGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(routerSearchPanel)
				.addComponent(infoAboutrouter, 200, 200, 800)));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(spRouters)
				.addGroup(
					layout.createSequentialGroup().addComponent(routerSearchPanel)
						.addComponent(infoAboutrouter))));
    }


    /**
     * Vyplní komponenty
     */
    public void fillOspfRoutersList() {
	routerListModel = new RouterListModel(ospfModel.getRouters());
	listOfRouters.setModel(routerListModel);
	listOfRouters.setSelectedIndex(0);
	spRouters.updateUI();
    }


    /**
     * Aktualizuje seznam routerů
     */
    private void updateRouterList() {
	routerListModel.updadteList(routerText.getText(), getRouterTextSelectedMode());
	listOfRouters.setSelectedIndex(0);
	spRouters.updateUI();
	showSelectedRouterInfo();
    }


    /**
     * Vrací mód vyhledávání v seznamu routerů
     * @return int
     */
    private int getRouterTextSelectedMode() {
	int mode = CONTAINS_STEXT;
	if (rTextStartRB.isSelected())
	    mode = START_WITH_STEXT;
	if (rTextEndRB.isSelected())
	    mode = ENDS_WITH_STEXT;
	return mode;
    }


    /**
     * Zobrazí informace o vybraném routeru
     */
    private void showSelectedRouterInfo() {
	Router r = getSelectedRouter();
	if (r != null) {
	    lblRouterIp.setText(r.getRouterID());
	    lblRouterName.setText(r.getRouterName());
	    lblRouterNameSuffix.setText(r.getNameSuffix());
	    lblLinkCount.setText(Integer.toString(ospfModel.getRouterLinksCount(r)));
	} else {
	    lblRouterIp.setText("");
	    lblRouterName.setText("");
	    lblRouterNameSuffix.setText("");
	    lblLinkCount.setText("");
	}
    }


    /**
     * Vrací vybraný router
     * @return router
     */
    private Router getSelectedRouter() {
	return routerListModel.getRouterByIndex(listOfRouters.getSelectedIndex());
    }
}
