package org.hkfree.ospf.gui.summarydialog;

import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující tabbedpan s dvojici záložek s daty o sítí načteného modelu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWinModelTabbedPane extends JTabbedPane {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JPanel ospfDataPanel = null;
    private JPanel ospfRoutersPanel = null;
    private OspfModel ospfModel = null;


    /**
     * Konstruktor
     * @param model
     * @param actionListener
     */
    public OspfWinModelTabbedPane(OspfModel model) {
	this.ospfModel = model;
	makeComponents();
	fillComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
	ospfDataPanel = new OspfDataPanel(ospfModel);
	ospfRoutersPanel = new OspfRoutersPanel(ospfModel);
	this.add(rb.getString("owmtp.0"), ospfDataPanel);
	this.add(rb.getString("owmtp.1"), ospfRoutersPanel);
    }


    /**
     * Naplní komponenty načtenými daty
     */
    public void fillComponents() {
	((OspfDataPanel) ospfDataPanel).fillOspfDataText();
	((OspfRoutersPanel) ospfRoutersPanel).fillOspfRoutersList();
    }
}
