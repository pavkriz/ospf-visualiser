package org.hkfree.ospf.gui.ospfwin;

import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující menu hlavního okna aplikace
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWinMenu extends JMenuBar {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private ActionListener actionListener = null;


    /**
     * Konstruktor
     * @param actionListener
     */
    public OspfWinMenu(ActionListener actionListener) {
	this.actionListener = actionListener;
	makeComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
	JMenu mProgram = new JMenu(rb.getString("menu.program"));
	JMenu mData = new JMenu(rb.getString("menu.data"));
	JMenu mMapaInfo = new JMenu(rb.getString("menu.mapInfo"));
	JMenu mMode = new JMenu(rb.getString("menu.mode"));
	JMenu mVertices = new JMenu(rb.getString("menu.vertices"));
	JMenu mHelp = new JMenu(rb.getString("menu.help"));
	// nabídka "Program"
	mProgram.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionSettings()));
	mProgram.addSeparator();
	mProgram.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionCloseActualModel()));
	mProgram.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionCloseWin()));
	// nabídka Data
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionOpenXMLModel()));
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionLoadData()));
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionLoadLog()));
	mData.addSeparator();
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionShowLoadedLogs()));
	mData.addSeparator();
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionSaveToXML()));
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionExportModelToXGMML()));
	mData.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionExportModelToSVG()));
	// nabídka Mapa info
	mMapaInfo.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionShowInfoTable()));
	mMapaInfo.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionShowNetStates()));
	mMapaInfo.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionComputeRouterImportance()));
	mMapaInfo.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionComputeLinkImportance()));
	// nabidka Mod
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionCenterRouter()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionShowNeighboursMode()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionCostChangingMode()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionShortestPath()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionTwoRoutersShortesPathMode()));
	mMode.addSeparator();
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionAsymetricLinksMode()));
	mMode.addSeparator();
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionAddVertexes()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionAddEdges()));
	mMode.addSeparator();
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionTransformingMode()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionPickingMode()));
	mMode.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionZoom()));
	// nabidka Pozice vrcholu
	mVertices.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionStartLayouting()));
	mVertices.addSeparator();
	mVertices.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionGPS()));
	mVertices.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionGPSAll()));
	mVertices.addSeparator();
	mVertices.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionLockMode()));
	mVertices.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionLockAll()));
	// nabídka nápověda
	mHelp.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionTips()));
	mHelp.add(new JMenuItem(((OspfWinActionListener) actionListener).getActionAbout()));
	this.add(mProgram);
	this.add(mData);
	this.add(mMapaInfo);
	this.add(mMode);
	this.add(mVertices);
	this.add(mHelp);
    }
}
