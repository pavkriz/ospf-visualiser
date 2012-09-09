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
	// nabídka "Pozice vrcholů"
	JMenu mPozice = new JMenu(rb.getString("nswm.2"));
	mPozice.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionStartLayouting()));
	mPozice.addSeparator();
	mPozice.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLockMode()));
	mPozice.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionLockAll()));
	// nabídka "Změny cen"
	JMenu mCeny = new JMenu(rb.getString("nswm.4"));
	mCeny.add(new JMenuItem(((NetStatesWinActionListener) actionListener).getActionShowCostDifferences()));
	this.add(mOkno);
	this.add(mMod);
	this.add(mPozice);
	this.add(mCeny);
    }
}
