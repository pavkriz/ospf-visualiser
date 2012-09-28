package org.hkfree.ospf.gui.netstateswin;

import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JToolBar;

import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující toolbar okna zobrazení stavu sítě v čase
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetStatesWinToolBar extends JToolBar {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private ActionListener actionListener = null;


    /**
     * Konstruktor
     * @param actionListener
     */
    public NetStatesWinToolBar(ActionListener actionListener) {
	this.actionListener = actionListener;
	makeComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
	this.setName(rb.getString("nswtb.0"));
	this.setRollover(true);
	this.add(((NetStatesWinActionListener) actionListener).getActionTransformingMode());
	this.add(((NetStatesWinActionListener) actionListener).getActionPickingMode());
	this.add(((NetStatesWinActionListener) actionListener).getActionZoom());
	this.addSeparator();
	this.add(((NetStatesWinActionListener) actionListener).getActionNoneMode());
	this.add(((NetStatesWinActionListener) actionListener).getActionShortestPath());
	this.add(((NetStatesWinActionListener) actionListener).getActionLockMode());
	this.add(((NetStatesWinActionListener) actionListener).getActionGPS());
	this.add(((NetStatesWinActionListener) actionListener).getActionLockAll());
	this.add(((NetStatesWinActionListener) actionListener).getActionGPSAll());
	this.addSeparator();
	this.add(((NetStatesWinActionListener) actionListener).getActionStartLayouting());
	this.addSeparator();
	this.add(((NetStatesWinActionListener) actionListener).getActionShowCostDifferences());
	this.add(((NetStatesWinActionListener) actionListener).getActionLinkFaultMode());
    }
}
