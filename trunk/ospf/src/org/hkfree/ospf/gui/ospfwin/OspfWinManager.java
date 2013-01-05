package org.hkfree.ospf.gui.ospfwin;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.hkfree.ospf.gui.about.AboutApplicationDialog;
import org.hkfree.ospf.gui.about.TipsDialog;
import org.hkfree.ospf.gui.importancedialog.LinkImportanceDialog;
import org.hkfree.ospf.gui.importancedialog.RouterImportanceDialog;
import org.hkfree.ospf.gui.loadedlogslistdialog.LoadedLogsListDialog;
import org.hkfree.ospf.gui.mappanel.MapManager;
import org.hkfree.ospf.gui.mappanel.MapPanel;
import org.hkfree.ospf.gui.mappanel.MapStartStateDialog;
import org.hkfree.ospf.gui.netstateswin.NSModelChooseDialog;
import org.hkfree.ospf.gui.netstateswin.NetStatesWin;
import org.hkfree.ospf.gui.settingsdialog.SettingsDialog;
import org.hkfree.ospf.gui.sourcedialog.LogSourceDialog;
import org.hkfree.ospf.gui.sourcedialog.ModelSourceDialog;
import org.hkfree.ospf.gui.summarydialog.OspfModelSummaryDialog;
import org.hkfree.ospf.gui.summarydialog.OspfWinModelTabbedPane;
import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.linkfault.LinkFaultModel;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospfchange.OspfChangeModel;
import org.hkfree.ospf.model.ospfcomponent.OspfGraphComponentModel;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.setting.SettingsManager;
import org.hkfree.ospf.tools.Exporter;
import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.load.MapXMLLoader;
import org.hkfree.ospf.tools.load.NetChangeLoader;
import org.hkfree.ospf.tools.load.OspfChangeLoader;
import org.hkfree.ospf.tools.load.OspfDataLoadInitiator;
import org.hkfree.ospf.tools.save.MapXMLSaver;

/**
 * Třída představující manažer okna návrhu sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWinManager {

    private ResourceBundle rb = Factory.getRb();
    private JFrame owner = null;
    private List<OspfModel> ospfModely = new ArrayList<OspfModel>();
    private LinkFaultModel linkFaultModel = new LinkFaultModel();
    private NetChangeModel netChangeModel = null;
    private AppSettings settings = new AppSettings();
    private SettingsManager settingsManager;
    private List<String> loadedLogs = new ArrayList<String>();
    private MODE mode0 = MODE.TRANSFORMING; // aktualni mody mapy
    private MODE mode1 = MODE.SHOW_NEIGHBORS; // jsou dva


    /**
     * Konstruktor
     * z puvodniho
     * @param owner
     */
    public OspfWinManager(JFrame owner) {
	this.owner = owner;
	try {
	    settingsManager = new SettingsManager(settings);
	    // načte nastavení aplikace a nastavení načítání dat
	    settingsManager.loadSettings();
	    // jazyk/země
	    Locale locale = new Locale(settings.language.toString().substring(0, 2), settings.language.toString().substring(
		    3, 5));
	    rb = ResourceBundle.getBundle("org.hkfree.ospf.lng.ospf", locale);
	} catch (Exception e) {
	    ((OspfWin) owner).showErrorMessage(rb == null ? "Error" : rb.getString("error"), e.getMessage());
	}
    }


    /**
     * Aktualizuje mody u všech komponent grafu a popisky ve statusbaru
     */
    protected void actualizeModesAndStatusBarAndBySettings() {
	if (getAllMDManager().isEmpty()) {
	    // pokud není žádná mapa, vymazat status bar
	    ((OspfWin) owner).getStatusBar().clear();
	    return;
	}
	// aktualizuje režimy všech grafů
	for (MapManager man : getAllMDManager()) {
	    man.setMode(this.mode0);
	    man.setMode(this.mode1);
	}
	// aktualizace statusbaru
	((OspfWin) owner).getStatusBar().setStatus(0, rb.getString("mode." + mode0));
	((OspfWin) owner).getStatusBar().setStatus(1, rb.getString("mode." + mode1));
	((OspfWin) owner).getStatusBar().setToolTip(1, rb.getString("mode." + mode1 + ".toolTip"));
	// aktualizace dle nastaveni
	actualizeBySettings();
    }


    /**
     * Nastaví en/dis able akce dle naečtených modelů
     */
    protected void checkActionsEnable() {
	boolean modelExist = !getAllMDManager().isEmpty();
	// boolean modelExist = ospfModely.isEmpty() ? false : true;
	// if (!modelExist && !((OspfWin) owner).getAllMDManager().isEmpty()) {
	// modelExist = true;
	// }
	((OspfWin) owner).getOspfWinActListener().getActionCenterRouter().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionCloseActualModel().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionShowInfoTable().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionExportModelToXGMML().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionExportModelToSVG().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionComputeRouterImportance().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionComputeLinkImportance().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionSaveToXML().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionSearchRouter().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionAddEdges().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionAddVertexes().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionAddEdges().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionAsymetricLinksMode().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionTwoRoutersShortesPathMode().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionLayoutStartJS().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionLayoutStartFR().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionLayoutStartSpring().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionLayoutStopSpring().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionTransformingMode().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionPickingMode().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionLockMode().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionLockAll().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionZoom().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionShortestPath().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionCostChangingMode().setEnabled(modelExist);
	((OspfWin) owner).getOspfWinActListener().getActionShowNeighboursMode().setEnabled(modelExist);
	// if (modelExist && getActualMDManager().getOspfModel() != null) {
	if (modelExist && getActualMDManager() != null && getActualMDManager().getOspfModel() != null) {
	    // GPS - kontrola zda jsou souřadnice pro daný model načteny
	    // IPv6 - kontrola zda jsou data načtena
	    boolean gpsLoaded = getActualMDManager().getOspfModel().isGpsLoaded();
	    ((OspfWin) owner).getOspfWinActListener().getActionGPS().setEnabled(gpsLoaded);
	    ((OspfWin) owner).getOspfWinActListener().getActionGPSAll().setEnabled(gpsLoaded);
	    boolean ipv6Loaded = getActualMDManager().getOspfModel().isIpv6Loaded();
	    ((OspfWin) owner).getOspfWinActListener().getActionIPv6Toggle().setEnabled(ipv6Loaded);
	} else {
	    ((OspfWin) owner).getOspfWinActListener().getActionGPS().setEnabled(modelExist);
	    ((OspfWin) owner).getOspfWinActListener().getActionGPSAll().setEnabled(modelExist);
	    ((OspfWin) owner).getOspfWinActListener().getActionIPv6Toggle().setEnabled(modelExist);
	}
	((OspfWin) owner).getOspfWinActListener().getActionShowNetStates().setEnabled(ospfModely.size() < 2 ? false : true);
	if (!modelExist) {
	    ((OspfWin) owner).getStatusBar().clear();
	}
    }


    /**
     * Otevře dialog pro výběr vstupních dat a pokud byl dialog potvrzen, data
     * načte
     */
    protected void openLoadDataSelection() {
	ModelSourceDialog selectSourceDialog = new ModelSourceDialog(owner, settings);
	selectSourceDialog.setLoadDialogConfirmed(false);
	selectSourceDialog.setModal(true);
	selectSourceDialog.setVisible(true);
	if (selectSourceDialog.loadDialogConfirmed()) {
	    owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    try {
		settingsManager.saveSettings();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    loadData();
	    owner.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }


    /**
     * Načte vstupní data dle nastavení
     */
    private void loadData() {
	OspfDataLoadInitiator loadInitiator = new OspfDataLoadInitiator(settings, this);
	List<OspfModel> newModels = new ArrayList<OspfModel>();
	// inicializace logu nacteni dat
	((OspfWin) getOwner()).getStateDialog().init();
	for (String sourcePath : settings.getFilePaths()) {
	    newModels.add(new OspfModel());
	    OspfModel actualModel = newModels.get(newModels.size() - 1);
	    try {
		loadInitiator.loadData(actualModel, sourcePath);
		actualModel.removeNonCompletelyLoadedLinksAndRouters();
	    } catch (Exception e) {
		((OspfWin) owner).showErrorMessage(rb.getString("error"), e.getMessage());
		((OspfWin) getOwner()).getStateDialog().operationFailed();
	    }
	    // pokud model obsahuje spoje, provede v nem upravy
	    if (!actualModel.getLinks().isEmpty()) {
		actualModel.sortRoutersByIP();
		actualModel.computeNetAddressesOfLinks();
	    }
	    // jinak ho odstrani
	    else {
		newModels.remove(actualModel);
	    }
	}
	((OspfWin) getOwner()).getStateDialog().addText(rb.getString("stated.5"));
	// pridani novych modelu do zalozek
	if (settings.fromDateToDateLoadTo.equals(AppSettings.MAP_PANEL)) {
	    for (OspfModel om : newModels) {
		((OspfWin) owner).addAndFillModelTabbedPane(om.getModelName(), om);
	    }
	    ospfModely.addAll(newModels);
	    checkActionsEnable();
	    actualizeModesAndStatusBarAndBySettings();
	}
	// otevreni okna stavu site s nactenymi modely
	else if (settings.fromDateToDateLoadTo.equals(AppSettings.NET_STATE_WINDOW)) {
	    if (newModels.size() > 1) {
		if (newModels.size() > 1) {
		    OspfChangeModel ospfChangeModel = new OspfChangeModel();
		    OspfChangeLoader loader = new OspfChangeLoader();
		    loader.setOspfChangeModel(ospfChangeModel);
		    try {
			if (!linkFaultModel.isEmpty())
			    ospfChangeModel.setLinkFaultModel(linkFaultModel);
			loader.loadOspfChangeModel(newModels);
			NetChangeLoader netChangeLoader = new NetChangeLoader();
			netChangeModel = new NetChangeModel();
			netChangeLoader.setNetChangeModel(netChangeModel);
			netChangeLoader.loadNetChanges(ospfChangeModel);
			netChangeModel.sortNetStatesByDate();
			NetStatesWin netStatesWin = new NetStatesWin(settings.edgeShaper);
			netStatesWin.setNetChangeModel(netChangeModel);
			netStatesWin.setVisible(true);
			netStatesWin.processModelsAfterStart();
			netStatesWin.getManager().checkLinkFaultModeToEnable();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		} else {
		    ((OspfWin) owner).showInfoMessage(rb.getString("info"), rb.getString("owm.3"));
		}
	    } else {
		((OspfWin) owner).showInfoMessage(rb.getString("info"), rb.getString("owm.4"));
	    }
	}
	((OspfWin) getOwner()).getStateDialog().operationSucceeded();
	// zavreni dialogu s vypisem logu o nacteni dat
	if (settings.closeLogDialog) {
	    ((OspfWin) getOwner()).getStateDialog().close();
	}
    }


    /**
     * Otevře dialog pro výběr logů k načtení
     */
    protected void openLoadLogsSelection() {
	LogSourceDialog logSourceDialog = new LogSourceDialog();
	logSourceDialog.setLogSourceDialogConfirmed(false);
	logSourceDialog.setDataLoadSettings(settings);
	logSourceDialog.setModal(true);
	logSourceDialog.setVisible(true);
	if (logSourceDialog.logSourceDialogConfirmed()) {
	    owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    try {
		settingsManager.saveSettings();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    loadLogs();
	    owner.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }


    /**
     * Načte logy dle nastavení
     */
    private void loadLogs() {
	OspfDataLoadInitiator loadInitiator = new OspfDataLoadInitiator(settings, this);
	boolean success = true;
	for (String path : settings.getFilePaths()) {
	    try {
		if (!loadedLogs.contains(path)) {
		    loadInitiator.loadLogsFromRemoteServerFiles(linkFaultModel, path);
		    loadedLogs.add(path);
		}
	    } catch (Exception e) {
		success = false;
		((OspfWin) owner).showErrorMessage(rb.getString("error"), e.getMessage());
	    }
	}
	if (success) {
	    ((OspfWin) owner).getOspfWinActListener().getActionShowLoadedLogs().setEnabled(true);
	    ((OspfWin) owner).showInfoMessage(rb.getString("info"), rb.getString("owm.0"));
	}
    }


    /**
     * Otevře dialog pro výběr NML (XML) souboru k načtení
     */
    protected void openLoadXMLFileDialog() {
	JFileChooser fchooser = new JFileChooser();
	fchooser.setAcceptAllFileFilterUsed(false);
	fchooser.setFileFilter(new FileFilter() {

	    public String getDescription() {
		return "NML";
	    }


	    public boolean accept(File f) {
		if (f.isDirectory())
		    return true;
		if (f.getName().endsWith(".nml"))
		    return true;
		return false;
	    }
	});
	fchooser.setDialogTitle(rb.getString("owm.1"));
	fchooser.setSelectedFile(new File("map-model.nml"));
	if (fchooser.showOpenDialog(this.owner) == JFileChooser.APPROVE_OPTION) {
	    loadMapModelFromXML(fchooser.getSelectedFile());
	    checkActionsEnable();
	}
    }


    /**
     * Načte mapModel z xml souboru
     * @param inputFile
     */
    private void loadMapModelFromXML(File inputFile) {
	if (inputFile.exists()) {
	    // MapModel mapModel = new MapModel();
	    MapXMLLoader mapXmlLoader = new MapXMLLoader();
	    mapXmlLoader.setInputFile(inputFile);
	    mapXmlLoader.loadModelFromDocument();
	    // mapModel = mapXmlLoader.getMapModel();
	    ((OspfWin) owner).addAndFillModelTabbedPane(inputFile.getName(), mapXmlLoader.getMapModel(),
		    mapXmlLoader.getRVertexPositions());
	    checkActionsEnable();
	}
    }


    /**
     * Otevře okno zobrazní stavu sítě v čase
     */
    protected void showNetStatesWin() {
	if (ospfModely.size() > 1) {
	    NSModelChooseDialog modelChooseDialog = new NSModelChooseDialog(ospfModely);
	    if (modelChooseDialog.selectionConfirmed()) {
		if (modelChooseDialog.getSelectedOspfModels().size() > 1) {
		    OspfChangeModel ospfChangeModel = new OspfChangeModel();
		    OspfChangeLoader loader = new OspfChangeLoader();
		    loader.setOspfChangeModel(ospfChangeModel);
		    try {
			if (!linkFaultModel.isEmpty())
			    ospfChangeModel.setLinkFaultModel(linkFaultModel);
			loader.loadOspfChangeModel(modelChooseDialog.getSelectedOspfModels());
			NetChangeLoader netChangeLoader = new NetChangeLoader();
			netChangeModel = new NetChangeModel();
			netChangeLoader.setNetChangeModel(netChangeModel);
			netChangeLoader.loadNetChanges(ospfChangeModel);
			netChangeModel.sortNetStatesByDate();
			NetStatesWin netStatesWin = new NetStatesWin(settings.edgeShaper);
			netStatesWin.setNetChangeModel(netChangeModel);
			netStatesWin.setVisible(true);
			netStatesWin.processModelsAfterStart();
			netStatesWin.getManager().checkLinkFaultModeToEnable();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		} else {
		    ((OspfWin) owner).showInfoMessage(rb.getString("info"), rb.getString("owm.3"));
		}
	    }
	} else {
	    ((OspfWin) owner).showInfoMessage(rb.getString("info"), rb.getString("owm.4"));
	}
    }


    /**
     * Zavře aktuálně vybranou záložku a odstraní model, který v ní je zobrazen
     */
    protected void closeActualModel() {
	if (getAllMDManager().isEmpty()) {
	    // pokud je seznam s modely prazdny, neni co zavirat
	    ((OspfWin) owner).getStatusBar().clear();
	} else {
	    // jinak je nacten model site
	    OspfModel modelToRemove = getActualMDManager().getOspfModel();
	    if (modelToRemove != null) {
		// pokud model site obsahuje ospfModel, smaze se
		ospfModely.remove(modelToRemove);
	    }
	    ((OspfWin) owner).closeActiveModelTabbedPane();
	}
    }


    /**
     * Otevře dialog s routery, jejichž výpadek způsobí rozpad sítě na více
     * částí
     */
    protected void showComputedRouterImportancesDialog() {
	if (getActualMDManager().getOspfModel() == null) {
	    ((OspfWin) owner).showErrorMessage(rb.getString("error"), rb.getString("err.noData"));
	} else {
	    OspfGraphComponentModel ogcm = new OspfGraphComponentModel();
	    ogcm.setOspfModel(getActualMDManager().getOspfModel());
	    ogcm.createGraphModel();
	    ogcm.computeVertexImportanceForConnectedGraph();
	    RouterImportanceDialog dialog = new RouterImportanceDialog(ogcm.getRouterImportances());
	    dialog.setVisible(true);
	}
    }


    /**
     * Otevře dialog se spoji, jejichž výpadek způsobí rozpad sítě na více částí
     */
    protected void showComputedLinkImportancesDialog() {
	if (getActualMDManager().getOspfModel() == null) {
	    ((OspfWin) owner).showErrorMessage(rb.getString("error"), rb.getString("err.noData"));
	} else {
	    OspfGraphComponentModel ogcm = new OspfGraphComponentModel();
	    ogcm.setOspfModel(getActualMDManager().getOspfModel());
	    ogcm.createGraphModel();
	    ogcm.computeEdgeImportanceForConnectedGraph();
	    LinkImportanceDialog dialog = new LinkImportanceDialog(ogcm.getLinkImportances());
	    dialog.setVisible(true);
	}
    }


    /**
     * Otevře dialog se seznamem již načtených logů
     */
    protected void showLoadedLogsListDialog() {
	LoadedLogsListDialog dialog = new LoadedLogsListDialog(loadedLogs);
	dialog.setVisible(true);
    }


    /**
     * Zobrazí dialog "O aplikaci"
     */
    protected void showAboutDialog() {
	AboutApplicationDialog dialog = new AboutApplicationDialog(owner);
	dialog.setVisible(true);
    }


    /**
     * Zobrazí dialog "Tipy"
     */
    public void showTipsDialog() {
	TipsDialog dialog = new TipsDialog(owner, settings.language);
	dialog.setVisible(true);
    }


    /**
     * Otevře dialog s nastavením aplikace
     */
    protected void showSettingsDialog() {
	SettingsDialog dialog = new SettingsDialog(owner, this, settings);
	dialog.setVisible(true);
    }


    /**
     * Zobrazí okno s informacemi o modelu ve dvou záložkách
     */
    protected void showOspfModelSummaryDialog() {
	OspfModel model = getActualMDManager().getOspfModel();
	OspfModelSummaryDialog dialog = new OspfModelSummaryDialog(owner, new OspfWinModelTabbedPane(model), model);
	dialog.setVisible(true);
    }


    /**
     * Uloží nastavení aplikace a nastavení načítání dat do properties souboru
     */
    public void saveSettings() {
	try {
	    SettingsManager setMan = new SettingsManager(settings);
	    setMan.saveSettings();
	} catch (Exception e) {
	    ((OspfWin) owner).showErrorMessage(rb.getString("error"), e.getMessage());
	}
    }


    /**
     * Provede export do XGMML souboru vhodneho pro import do programu Cytoscape
     */
    protected void openExportModelToXGMMLDialog() {
	OspfModel model = getActualMDManager().getOspfModel();
	JFileChooser fchooser = new JFileChooser();
	fchooser.setAcceptAllFileFilterUsed(false);
	fchooser.setFileFilter(new FileFilter() {

	    public String getDescription() {
		return rb.getString("owm.5");
	    }


	    public boolean accept(File f) {
		if (f.isDirectory())
		    return true;
		if (f.getName().endsWith(".xgmml"))
		    return true;
		return false;
	    }
	});
	fchooser.setDialogTitle(rb.getString("owm.6"));
	fchooser.setSelectedFile(new File(model.getModelName() + ".xgmml"));
	if (fchooser.showSaveDialog(this.owner) == JFileChooser.APPROVE_OPTION) {
	    try {
		Exporter.exportModelToXGMML(fchooser.getSelectedFile(), model);
	    } catch (IOException e) {
		((OspfWin) owner).showErrorMessage(rb.getString("error"), e.getMessage());
	    }
	}
    }


    /**
     * Vrací manažery všech načtených modelů
     * @return
     */
    protected Set<MapManager> getAllMDManager() {
	return ((OspfWin) owner).getAllMDManager();
    }


    /**
     * Vrací manažera aktuálního modelu
     * @return
     */
    protected MapManager getActualMDManager() {
	return ((OspfWin) owner).getActualMDManager();
    }


    /**
     * Vrací vlastníka
     * @return JFrame
     */
    public JFrame getOwner() {
	return owner;
    }


    /**
     * Nastavuje vlastníka
     * @param owner
     */
    public void setOwner(JFrame owner) {
	this.owner = owner;
    }


    /**
     * Uloží vybraný model do NML (XML) souboru pro pozdější otevření
     * @param outputFile
     */
    private void saveModelToXML(File outputFile) {
	MapXMLSaver mapXMLSaver = new MapXMLSaver();
	mapXMLSaver.setMapModel(getActualMDManager().getMapModel());
	mapXMLSaver.setOutputFile(outputFile);
	mapXMLSaver.createRVertexIdentificators();
	mapXMLSaver.setRVertexPositions(getActualMDManager().getGraphComponent().getRouterVertexPositions());
	mapXMLSaver.createDocumentFromModel();
    }


    /**
     * Otevře dialog pro uložení do xml souboru
     */
    protected void openSaveXMLFileDialog() {
	JFileChooser fchooser = new JFileChooser();
	fchooser.setAcceptAllFileFilterUsed(false);
	fchooser.setFileFilter(new FileFilter() {

	    public String getDescription() {
		return rb.getString("mdwm.0");
	    }


	    public boolean accept(File f) {
		if (f.isDirectory())
		    return true;
		if (f.getName().endsWith(".nml"))
		    return true;
		return false;
	    }
	});
	fchooser.setDialogTitle(rb.getString("mdwm.1"));
	fchooser.setSelectedFile(new File("map-model.nml"));
	if (fchooser.showSaveDialog(this.owner) == JFileChooser.APPROVE_OPTION) {
	    saveModelToXML(fchooser.getSelectedFile());
	}
    }


    /**
     * Nastaví text status infa dle indesu (číslováno od 0)
     * @param text
     */
    protected void setStatusText(int index, String text) {
	((OspfWin) owner).getStatusBar().setStatus(index, text);
    }


    /**
     * Nastaví text tooltipu status infa dle indexu (číslováno od 0)
     * @param text
     */
    protected void setStatusTextToolTip(int index, String text) {
	((OspfWin) owner).getStatusBar().setToolTip(index, text);
    }


    /**
     * Vrací řetězec k vyhledání
     * @return
     */
    protected String getSearchString() {
	return ((OspfWin) owner).getSearhString();
    }


    /**
     * Nastaví režim
     * @param mode
     */
    public void setMode(int index, MODE mode) {
	switch (index) {
	    case 0:
		this.mode0 = mode;
		break;
	    case 1:
		this.mode1 = mode;
		break;
	    default:
		break;
	}
    }


    /**
     * Vrací resource bundle s popisky v nastaveném jazyce
     */
    protected ResourceBundle getRb() {
	return rb;
    }


    /**
     * Vrací nastavení aplikace
     */
    public AppSettings getSettings() {
	return this.settings;
    }


    /**
     * Vytvoří instanci okna návrhu sítě
     */
    public void openShowMapDialog() {
	MapStartStateDialog dialog = new MapStartStateDialog(getActualMDManager().getOspfModel().getRouters(), null);
	dialog.setVisible(true);
	if (dialog.selectionConfirmed()) {
	    if (dialog.wholeModelIsSelected()) {
		((MapPanel) getActualMDManager().getOwner()).processModelsAfterStart(true, null, 0);
	    } else {
		((MapPanel) getActualMDManager().getOwner()).processModelsAfterStart(false, dialog.getSelectedRouter(),
		        dialog.getNeighboursDepth());
	    }
	    owner.repaint();
	}
    }


    public void openExportModelToSVGDialog() {
	String name = getActualMDManager().getOspfModel().getModelName();
	MapModel model = getActualMDManager().getMapModel();
	JFileChooser fchooser = new JFileChooser();
	fchooser.setAcceptAllFileFilterUsed(false);
	fchooser.setFileFilter(new FileFilter() {

	    public String getDescription() {
		return rb.getString("owm.7");
	    }


	    public boolean accept(File f) {
		if (f.isDirectory())
		    return true;
		if (f.getName().endsWith(".svg"))
		    return true;
		return false;
	    }
	});
	fchooser.setDialogTitle(rb.getString("owm.8"));
	fchooser.setSelectedFile(new File(name + ".svg"));
	if (fchooser.showSaveDialog(this.owner) == JFileChooser.APPROVE_OPTION) {
	    try {
		Exporter.exportModelToSVG(fchooser.getSelectedFile(), model, getActualMDManager().getGraphComponent());
	    } catch (IOException e) {
		((OspfWin) owner).showErrorMessage(rb.getString("error"), e.getMessage());
	    }
	}
    }


    public void actualizeIPv6mode() {
	((OspfWin) owner).actualizeIPv6mode();
    }


    /**
     * Aktualizace po změně v nastavení
     */
    public void actualizeBySettings() {
	for (MapManager mm : getAllMDManager()) {
	    mm.getGraphComponent().setEdgeShaper(settings.edgeShaper);
	}
    }
}
