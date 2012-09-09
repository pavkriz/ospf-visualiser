package org.hkfree.ospf.gui.ospfwin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.tools.Factory;

/**
 * ActionListener okna návrhu sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWinActionListener implements ActionListener {

    private ResourceBundle rb = Factory.getRb();
    private OspfWinManager winManager = null;
    private Action actionShowNeighboursMode = null;
    private Action actionCostChangingMode = null;
    private Action actionPickingMode = null;
    private Action actionTransformingMode = null;
    private Action actionStartLayouting = null;
    private Action actionZoom = null;
    private Action actionLockMode = null;
    private Action actionLockAll = null;
    private Action actionShortestPath = null;
    private Action actionGPS = null;
    private Action actionGPSAll = null;
    private Action actionAddVertexes = null;
    private Action actionAddEdges = null;
    private Action actionSaveToXML = null;
    private Action actionAsymetricLinksMode = null;
    private Action actionTwoRoutersShortesPathMode = null;
    private Action actionSearchRouter = null;
    private Action actionCloseWin = null;
    private Action actionAbout = null;
    private Action actionLoadData = null;
    private Action actionLoadLog = null;
    private Action actionShowNetStates = null;
    private Action actionCenterRouter = null;
    private Action actionSettings = null;
    private Action actionOpenXMLModel = null;
    private Action actionComputeRouterImportance = null;
    private Action actionComputeLinkImportance = null;
    private Action actionCloseActualModel = null;
    private Action actionShowLoadedLogs = null;
    private Action actionExportModelToXGMML = null;
    private Action actionExportModelToSVG = null;
    private Action actionShowInfoTable = null;
    private Action actionTips = null;


    /**
     * Konstruktor
     */
    public OspfWinActionListener(OspfWinManager winManager) {
	this.winManager = winManager;
	createActions();
    }


    /**
     * Vytvoří akce
     */
    public void createActions() {
	actionShowNeighboursMode = new AbstractAction(rb.getString("mode." + MODE.SHOW_NEIGHBORS), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "show_neighbours.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.SHOW_NEIGHBORS);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionShowNeighboursMode.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("mode." + MODE.SHOW_NEIGHBORS + ".title"));
	actionShowNeighboursMode.setEnabled(false);
	actionCostChangingMode = new AbstractAction(rb.getString("mode." + MODE.COST_CHANGING), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "cost_change.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.COST_CHANGING);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionCostChangingMode.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("mode." + MODE.COST_CHANGING + ".title"));
	actionCostChangingMode.setEnabled(false);
	actionShortestPath = new AbstractAction(rb.getString("mode." + MODE.SHORTEST_PATH), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "shortest_path.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.SHORTEST_PATH);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionShortestPath.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.SHORTEST_PATH + ".title"));
	actionShortestPath.setEnabled(false);
	actionGPS = new AbstractAction(rb.getString("mode." + MODE.GPS), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "gps.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.GPS);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionGPS.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.GPS + ".title"));
	actionGPS.setEnabled(false);
	actionGPSAll = new AbstractAction(rb.getString("mode." + MODE.GPS_ALL), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "gps_all.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.getActualMDManager().setMode(MODE.GPS_ALL);
	    }
	};
	actionGPSAll.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.GPS_ALL + ".title"));
	actionGPSAll.setEnabled(false);
	actionZoom = new AbstractAction(rb.getString("mode." + MODE.ZOOM), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "zoom.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.ZOOM);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionZoom.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.ZOOM + ".title"));
	actionZoom.setEnabled(false);
	actionLockAll = new AbstractAction(rb.getString("mode." + MODE.LOCK_ALL), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "lock_all.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.getActualMDManager().setMode(MODE.LOCK_ALL);
	    }
	};
	actionLockAll.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.LOCK_ALL + ".title"));
	actionLockAll.setEnabled(false);
	actionLockMode = new AbstractAction(rb.getString("mode." + MODE.LOCK_VERTEX), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "lock.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.LOCK_VERTEX);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionLockMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.LOCK_VERTEX + ".title"));
	actionLockMode.setEnabled(false);
	actionPickingMode = new AbstractAction(rb.getString("mode." + MODE.PICKING), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "picking.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(0, MODE.PICKING);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionPickingMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.PICKING + ".title"));
	actionPickingMode.setEnabled(false);
	actionTransformingMode = new AbstractAction(rb.getString("mode." + MODE.TRANSFORMING), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "transforming.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(0, MODE.TRANSFORMING);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionTransformingMode.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("mode." + MODE.TRANSFORMING + ".title"));
	actionTransformingMode.setEnabled(false);
	actionStartLayouting = new AbstractAction(rb.getString("mode." + MODE.LAYOUTING), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "startlayout.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.getActualMDManager().setMode(MODE.LAYOUTING);
	    }
	};
	actionStartLayouting.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.LAYOUTING + ".title"));
	actionStartLayouting.setEnabled(false);
	actionAddVertexes = new AbstractAction(rb.getString("mode." + MODE.ADD_VERTEXES), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "router_add.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.ADD_VERTEXES);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionAddVertexes.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.ADD_VERTEXES + ".title"));
	actionAddVertexes.setEnabled(false);
	actionAddEdges = new AbstractAction(rb.getString("mode." + MODE.ADD_EDGES), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "link_add.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.ADD_EDGES);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionAddEdges.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mode." + MODE.ADD_EDGES + ".title"));
	actionAddEdges.setEnabled(false);
	actionAsymetricLinksMode = new AbstractAction(rb.getString("mode." + MODE.ASYMETRIC_LINKS), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "asymetric_link.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.ASYMETRIC_LINKS);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionAsymetricLinksMode.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("mode." + MODE.ASYMETRIC_LINKS + ".title"));
	actionAsymetricLinksMode.setEnabled(false);
	actionTwoRoutersShortesPathMode = new AbstractAction(rb.getString("mode." + MODE.SHORTEST_PATH_TWO_ROUTERS),
		new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "two_r_shortest_path.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.setMode(1, MODE.SHORTEST_PATH_TWO_ROUTERS);
		winManager.actualizeModesAndStatusBar();
	    }
	};
	actionTwoRoutersShortesPathMode.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("mode." + MODE.SHORTEST_PATH_TWO_ROUTERS + ".title"));
	actionTwoRoutersShortesPathMode.setEnabled(false);
	actionCloseWin = new AbstractAction(rb.getString("menu.program.close"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "exit.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	};
	actionCloseWin.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.program.close.title"));
	actionSaveToXML = new AbstractAction(rb.getString("mdwal.14"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "save_xml.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openSaveXMLFileDialog();
	    }
	};
	actionSaveToXML.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mdwal.14.title"));
	actionSaveToXML.setEnabled(false);
	actionAbout = new AbstractAction(rb.getString("menu.help.about")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showAboutDialog();
	    }
	};
	actionAbout.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.help.about.title"));
	actionTips = new AbstractAction(rb.getString("menu.help.tips")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showTipsDialog();
	    }
	};
	actionTips.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.help.tips.title"));
	actionLoadData = new AbstractAction(rb.getString("menu.map.loadData"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "nactenidat.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openLoadDataSelection();
	    }
	};
	actionLoadData.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.loadData.title"));
	actionLoadLog = new AbstractAction(rb.getString("menu.map.loadDataFromLog"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "load_log.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openLoadLogsSelection();
	    }
	};
	actionLoadLog.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.loadDataFromLog.title"));
	actionShowNetStates = new AbstractAction(rb.getString("menu.map.changesInNetwork"), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "netstates.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showNetStatesWin();
	    }
	};
	actionShowNetStates.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.changesInNetwork.title"));
	actionShowNetStates.setEnabled(false);
	actionCloseActualModel = new AbstractAction(rb.getString("menu.map.closeActModel"), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "closemodel.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.closeActualModel();
	    }
	};
	actionCloseActualModel.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.closeActModel.title"));
	actionCloseActualModel.setEnabled(false);
	actionOpenXMLModel = new AbstractAction(rb.getString("menu.map.loadDataFromNML"), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "open_xml.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openLoadXMLFileDialog();
	    }
	};
	actionOpenXMLModel.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.loadDataFromNML.title"));
	actionComputeRouterImportance = new AbstractAction(rb.getString("menu.failureResults.routers"), new ImageIcon(
		getClass().getResource(Constants.URL_IMG_GUI + "router_importance.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showComputedRouterImportancesDialog();
	    }
	};
	actionComputeRouterImportance.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("menu.failureResults.routers.title"));
	actionComputeRouterImportance.setEnabled(false);
	actionComputeLinkImportance = new AbstractAction(rb.getString("menu.failureResults.links"), new ImageIcon(getClass()
		.getResource(Constants.URL_IMG_GUI + "link_importance.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showComputedLinkImportancesDialog();
	    }
	};
	actionComputeLinkImportance.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("menu.failureResults.links.title"));
	actionComputeLinkImportance.setEnabled(false);
	actionShowLoadedLogs = new AbstractAction(rb.getString("menu.failureResults.loadLogsList")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showLoadedLogsListDialog();
	    }
	};
	actionShowLoadedLogs.putValue(AbstractAction.SHORT_DESCRIPTION,
		rb.getString("menu.failureResults.loadLogsList.title"));
	actionShowLoadedLogs.setEnabled(false);
	actionSettings = new AbstractAction(rb.getString("menu.program.settings"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "nastaveni.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showSettingsDialog();
	    }
	};
	actionSettings.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.program.settings.title"));
	actionExportModelToXGMML = new AbstractAction(rb.getString("menu.map.exportModel")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openExportModelToXGMMLDialog();
	    }
	};
	actionExportModelToXGMML.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.exportModel.title"));
	actionExportModelToXGMML.setEnabled(false);
	actionSearchRouter = new AbstractAction(null, new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "search.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.getActualMDManager().searchInMap(winManager.getSearchString());
	    }
	};
	actionSearchRouter.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mdwtb.1"));
	actionSearchRouter.setEnabled(false);
	actionShowInfoTable = new AbstractAction(rb.getString("menu.data.summary"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "summary.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.showOspfModelSummaryDialog();
	    }
	};
	actionShowInfoTable.setEnabled(false);
	actionCenterRouter = new AbstractAction(rb.getString("mdwal.17"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "mapa_center_router.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openShowMapDialog();
	    }
	};
	actionCenterRouter.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("mdwal.17.title"));
	actionCenterRouter.setEnabled(false);
	actionExportModelToSVG = new AbstractAction(rb.getString("menu.map.exportSVG")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		winManager.openExportModelToSVGDialog();
	    }
	};
	actionExportModelToSVG.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("menu.map.exportSVG.title"));
	actionExportModelToSVG.setEnabled(false);
    }


    /**
     * Vrací akci nastavení režimu zobrazování sousedů
     * @return action
     */
    public Action getActionShowNeighboursMode() {
	return actionShowNeighboursMode;
    }


    /**
     * Vrací akci nastavení režimu změny cen
     * @return action
     */
    public Action getActionCostChangingMode() {
	return actionCostChangingMode;
    }


    /**
     * Vrací akci nastavení režimu posunu mapy
     * @return action
     */
    public Action getActionTransformingMode() {
	return actionTransformingMode;
    }


    /**
     * Vrací akci nastavení režimu změny pozic vrcholů
     * @return action
     */
    public Action getActionPickingMode() {
	return actionPickingMode;
    }


    /**
     * Vrací akci spuštění automatického rozvrhování
     * @return action
     */
    public Action getActionStartLayouting() {
	return actionStartLayouting;
    }


    /**
     * Vrací akci nastavení režimu zobrazení nejkratších cest
     * @return action
     */
    public Action getActionShortestPath() {
	return actionShortestPath;
    }


    /**
     * Vrací akci nastavení režimu zobrazení dle GPS
     * @return action
     */
    public Action getActionGPS() {
	return actionGPS;
    }


    /**
     * Vrací akci zobrazení pozic všech vrcholů dle GPS
     * @return action
     */
    public Action getActionGPSAll() {
	return actionGPSAll;
    }


    /**
     * Vrací akci nastavení režimu zoomování
     * @return action
     */
    public Action getActionZoom() {
	return actionZoom;
    }


    /**
     * Vrací akci nastavení režimu zamykání sousedů
     * @return action
     */
    public Action getActionLockMode() {
	return actionLockMode;
    }


    /**
     * Vrací akci zamknutí všech vrcholů
     * @return action
     */
    public Action getActionLockAll() {
	return actionLockAll;
    }


    /**
     * Vrací akci přidání nového vrcholu
     * @return action
     */
    public Action getActionAddVertexes() {
	return actionAddVertexes;
    }


    /**
     * Vrací akci přidáni nového spoje
     * @return action
     */
    public Action getActionAddEdges() {
	return actionAddEdges;
    }


    /**
     * Vrací akci uložení do XML
     * @return action
     */
    public Action getActionSaveToXML() {
	return actionSaveToXML;
    }


    /**
     * Vrací akci nastavení režimu zobrazení asymetrických spojů
     * @return action
     */
    public Action getActionAsymetricLinksMode() {
	return actionAsymetricLinksMode;
    }


    /**
     * Vrací akci nastavení režimu zobrazení nejkratších cest mezi 2 vrcholy
     * @return action
     */
    public Action getActionTwoRoutersShortesPathMode() {
	return actionTwoRoutersShortesPathMode;
    }


    public void actionPerformed(ActionEvent action) {}


    /**
     * Vrací akci zavření okna
     * @return action
     */
    public Action getActionCloseWin() {
	return actionCloseWin;
    }


    /**
     * Vrací akci zobrazní informací o aplikaci
     * @return action
     */
    public Action getActionAbout() {
	return actionAbout;
    }


    /**
     * Vrací akci načtení dat
     * @return action
     */
    public Action getActionLoadData() {
	return actionLoadData;
    }


    /**
     * Vrací akci načtení logů
     * @return action
     */
    public Action getActionLoadLog() {
	return actionLoadLog;
    }


    /**
     * Vrací akci otevření okna zobrazení stavu sítě v čase
     * @return action
     */
    public Action getActionShowNetStates() {
	return actionShowNetStates;
    }


    /**
     * Vrací akci zavření aktuálního modelu
     * @return action
     */
    public Action getActionCloseActualModel() {
	return actionCloseActualModel;
    }


    /**
     * Vrací akci otevření XML modelu
     * @return action
     */
    public Action getActionOpenXMLModel() {
	return actionOpenXMLModel;
    }


    /**
     * Vrací akci výpočtu důležitosti routerů
     * @return action
     */
    public Action getActionComputeRouterImportance() {
	return actionComputeRouterImportance;
    }


    /**
     * Vrací akci vypočtu důležitosti routerů
     * @return action
     */
    public Action getActionComputeLinkImportance() {
	return actionComputeLinkImportance;
    }


    /**
     * Vrací akci zobrazení načtených logů
     * @return action
     */
    public Action getActionShowLoadedLogs() {
	return actionShowLoadedLogs;
    }


    /**
     * Vrací akci zobrazení dialogu s nastavením aplikace
     * @return
     */
    public Action getActionSettings() {
	return actionSettings;
    }


    /**
     * Vrací akci export do souboru xgmml
     * @return
     */
    public Action getActionExportModelToXGMML() {
	return actionExportModelToXGMML;
    }


    /**
     * Vrací akci vyhledání routeru dle IP nebo názvu
     * @return
     */
    public Action getActionSearchRouter() {
	return actionSearchRouter;
    }


    /**
     * Vrací akci zobrazení dialogu s infem o ospf modelu
     * @return
     */
    public Action getActionShowInfoTable() {
	return actionShowInfoTable;
    }


    /**
     * Vrací akci zobrazení mapy dle centrálního routeru
     * @return
     */
    public Action getActionCenterRouter() {
	return actionCenterRouter;
    }


    /**
     * Vrací akci exportu mapy do SVG souboru (vektorová grafika)
     * @return
     */
    public Action getActionExportModelToSVG() {
	return actionExportModelToSVG;
    }


    /**
     * Vrací akci zobrazení okna s tipama pro aplikaci
     * @return
     */
    public Action getActionTips() {
	return actionTips;
    }
}
