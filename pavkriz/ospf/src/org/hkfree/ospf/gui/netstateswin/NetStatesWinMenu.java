package org.hkfree.ospf.gui.netstateswin;

import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující menu okna zobrazení stavu sítě v čase
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetStatesWinMenu extends JMenuBar {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private ActionListener actionListener = null;


    /**
     * Konstruktor
     * @param actionListener
     */
    public NetStatesWinMenu(ActionListener actionListener) {
	this.actionListener = actionListener;
	makeComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
	// nabídka "Okno"
	JMenu mOkno = new JMenu(rb.getString("nswm.0"));
	mOkno.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionCloseWin()));
	// nabídka "Pracovní mód"
	JMenu mMod = new JMenu(rb.getString("nswm.1"));
	mMod.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionNoneMode()));
	mMod.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionShortestPath()));
	mMod.addSeparator();
	mMod.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLinkFaultMode()));
	mMod.addSeparator();
	mMod.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionTransformingMode()));
	mMod.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionPickingMode()));
	mMod.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionZoom()));
	// nabídka "Pozice vrcholů"
	JMenu mVertices = new JMenu(rb.getString("nswm.2"));
	// nabidka Pozice vrcholu
	JMenu mLayouts = new JMenu(rb.getString("menu.layout"));
	mLayouts.setToolTipText(rb.getString("menu.layout.title"));
	mLayouts.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLayoutStartFR()));
	mLayouts.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLayoutStartSpring()));
	mLayouts.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLayoutStopSpring()));
	mVertices.add(mLayouts);
	mVertices.addSeparator();
	mVertices.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionGPS()));
	mVertices.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionGPSAll()));
	mVertices.addSeparator();
	mVertices.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLockMode()));
	mVertices.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLockAll()));
	// nabídka "Změny cen"
	JMenu mCeny = new JMenu(rb.getString("nswm.3"));
	mCeny.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionShowCostDifferences()));
	this.add(mOkno);
	this.add(mMod);
	this.add(mVertices);
	this.add(mCeny);
    }
}
