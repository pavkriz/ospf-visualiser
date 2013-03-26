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
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.hkfree.ospf.gui.about.AboutApplicationDialog;
import org.hkfree.ospf.gui.about.TipsDialog;
import org.hkfree.ospf.gui.importancedialog.LinkImportanceDialog;
import org.hkfree.ospf.gui.importancedialog.RouterImportanceDialog;
import org.hkfree.ospf.gui.lltddialog.LLTDSummaryDialog;
import org.hkfree.ospf.gui.loadedlogslistdialog.LoadedLogsListDialog;
import org.hkfree.ospf.gui.mappanel.MapManager;
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
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospfchange.OspfChangeModel;
import org.hkfree.ospf.model.ospfcomponent.OspfGraphComponentModel;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.setting.SettingsManager;
import org.hkfree.ospf.tools.Exporter;
import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.ip.IpCalculator;
import org.hkfree.ospf.tools.load.LLTDLoader;
import org.hkfree.ospf.tools.load.MapXMLLoader;
import org.hkfree.ospf.tools.load.NetChangeLoader;
import org.hkfree.ospf.tools.load.OspfChangeLoader;
import org.hkfree.ospf.tools.load.OspfDataLoadInitiator;
import org.hkfree.ospf.tools.save.MapXMLSaver;
import org.xml.sax.SAXException;

/**
 * Třída představující manažer okna návrhu sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWinManager {

    private ResourceBundle rb = Factory.getRb();
    private OspfWin ospfWin = null;
    private List<OspfModel> ospfModels = new ArrayList<OspfModel>();
    private List<LLTDModel> lltdModels = new ArrayList<LLTDModel>();
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
    public OspfWinManager(OspfWin owner) {
	this.ospfWin = owner;
	try {
	    settingsManager = new SettingsManager(settings);
	    // načte nastavení aplikace a nastavení načítání dat
	    settingsManager.loadSettings();
	    // jazyk/země
	    Locale locale = new Locale(settings.language.toString().substring(0, 2), settings.language.toString().substring(
		    3, 5));
	    rb = ResourceBundle.getBundle("org.hkfree.ospf.lng.ospf", locale);
	} catch (Exception e) {
	    owner.showErrorMessage(rb == null ? "Error" : rb.getString("error"), e.getMessage());
	}
    }


    /**
     * Aktualizuje mody u všech komponent grafu a popisky ve statusbaru
     */
    protected void actualizeModesAndStatusBarAndBySettings() {
	if (getAllMDManager().isEmpty()) {
	    // pokud není žádná mapa, vymazat status bar
	    ospfWin.getStatusBar().clear();
	    return;
	}
	// aktualizuje režimy všech grafů
	for (MapManager man : getAllMDManager()) {
	    man.setMode(this.mode0);
	    man.setMode(this.mode1);
	}
	// aktualizace statusbaru
	ospfWin.getStatusBar().setStatus(0, rb.getString("mode." + mode0));
	ospfWin.getStatusBar().setStatus(1, rb.getString("mode." + mode1));
	ospfWin.getStatusBar().setToolTip(1, rb.getString("mode." + mode1 + ".toolTip"));
	// aktualizace dle nastaveni
	actualizeBySettings();
    }


    /**
     * Nastaví en/dis able akce dle naečtených modelů
     */
    protected void checkActionsEnable() {
	boolean modelExist = !getAllMDManager().isEmpty();
	// boolean modelExist = ospfModely.isEmpty() ? false : true;
	// if (!modelExist && !owner.getAllMDManager().isEmpty()) {
	// modelExist = true;
	// }
	ospfWin.getOspfWinActListener().getActionCenterRouter().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionCloseActualModel().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionShowInfoTable().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionExportModelToXGMML().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionExportModelToSVG().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionComputeRouterImportance().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionComputeLinkImportance().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionSaveToXML().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionSearchRouter().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionAddEdges().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionAddVertexes().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionAddEdges().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionAsymetricLinksMode().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionTwoRoutersShortesPathMode().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionLayoutStartJS().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionLayoutStartFR().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionLayoutStartSpring().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionLayoutStopSpring().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionTransformingMode().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionPickingMode().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionLockMode().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionLockAll().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionZoom().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionShortestPath().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionCostChangingMode().setEnabled(modelExist);
	ospfWin.getOspfWinActListener().getActionShowNeighboursMode().setEnabled(modelExist);
	// if (modelExist && getActualMDManager().getOspfModel() != null) {
	if (modelExist && getActualMDManager() != null && getActualMDManager().getOspfModel() != null) {
	    // GPS - kontrola zda jsou souřadnice pro daný model načteny
	    // IPv6 - kontrola zda jsou data načtena
	    boolean gpsLoaded = getActualMDManager().getOspfModel().isGpsLoaded();
	    ospfWin.getOspfWinActListener().getActionGPS().setEnabled(gpsLoaded);
	    ospfWin.getOspfWinActListener().getActionGPSAll().setEnabled(gpsLoaded);
	    boolean ipv6Loaded = getActualMDManager().getOspfModel().isIpv6Loaded();
	    ospfWin.getOspfWinActListener().getActionIPv6Toggle().setEnabled(ipv6Loaded);
	} else {
	    ospfWin.getOspfWinActListener().getActionGPS().setEnabled(modelExist);
	    ospfWin.getOspfWinActListener().getActionGPSAll().setEnabled(modelExist);
	    ospfWin.getOspfWinActListener().getActionIPv6Toggle().setEnabled(modelExist);
	}
	ospfWin.getOspfWinActListener().getActionShowNetStates().setEnabled(ospfModels.size() < 2 ? false : true);
	if (!modelExist) {
	    ospfWin.getStatusBar().clear();
	}
    }


    /**
     * Otevře dialog pro výběr vstupních dat a pokud byl dialog potvrzen, data
     * načte
     */
    protected void openLoadDataSelection() {
	ModelSourceDialog selectSourceDialog = new ModelSourceDialog(ospfWin, settings);
	selectSourceDialog.setLoadDialogConfirmed(false);
	selectSourceDialog.setModal(true);
	selectSourceDialog.setVisible(true);
	if (selectSourceDialog.loadDialogConfirmed()) {
	    ospfWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    try {
		settingsManager.saveSettings();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    loadData();
	    ospfWin.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }


    /**
     * Načte vstupní data dle nastavení
     */
    private void loadData() {
	OspfDataLoadInitiator loadInitiator = new OspfDataLoadInitiator(settings, this);
	List<OspfModel> newModels = new ArrayList<OspfModel>();
	// inicializace logu nacteni dat
	getOwner().getStateDialog().init();
	for (String sourcePath : settings.getFilePaths()) {
	    newModels.add(new OspfModel());
	    OspfModel actualModel = newModels.get(newModels.size() - 1);
	    try {
		loadInitiator.loadData(actualModel, sourcePath);
		actualModel.removeNonCompletelyLoadedLinksAndRouters();
	    } catch (Exception e) {
		getOwner().showErrorMessage(rb.getString("error"), e.getMessage());
		getOwner().getStateDialog().operationFailed();
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
	getOwner().getStateDialog().addText(rb.getString("stated.5"));
	// pridani novych modelu do zalozek
	if (settings.fromDateToDateLoadTo.equals(AppSettings.MAP_PANEL)) {
	    for (OspfModel om : newModels) {
		ospfWin.addAndFillModelTabbedPane(om.getModelName(), om);
	    }
	    ospfModels.addAll(newModels);
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
		    ospfWin.showInfoMessage(rb.getString("info"), rb.getString("owm.3"));
		}
	    } else {
		ospfWin.showInfoMessage(rb.getString("info"), rb.getString("owm.4"));
	    }
	}
	getOwner().getStateDialog().operationSucceeded();
	// zarazeni LLTD dat do nove nacteneho modelu
	if (!lltdModels.isEmpty()) {
	    getOwner().getStateDialog().addText(rb.getString("stated.12"));
	    addLLTDtoOspfModels();
	    getOwner().getStateDialog().operationSucceeded();
	}
	// zavreni dialogu s vypisem logu o nacteni dat
	getOwner().getStateDialog().closeIfCloseable();
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
	    ospfWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    try {
		settingsManager.saveSettings();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    loadLogs();
	    ospfWin.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
	    }
	}
	if (success) {
	    ospfWin.getOspfWinActListener().getActionShowLoadedLogs().setEnabled(true);
	    ospfWin.showInfoMessage(rb.getString("info"), rb.getString("owm.0"));
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
	if (fchooser.showOpenDialog(this.ospfWin) == JFileChooser.APPROVE_OPTION) {
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
	    ospfWin.addAndFillModelTabbedPane(inputFile.getName(), mapXmlLoader.getMapModel(),
		    mapXmlLoader.getRVertexPositions());
	    checkActionsEnable();
	}
    }


    /**
     * Otevře okno zobrazní stavu sítě v čase
     */
    protected void showNetStatesWin() {
	if (ospfModels.size() > 1) {
	    NSModelChooseDialog modelChooseDialog = new NSModelChooseDialog(ospfModels);
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
		    ospfWin.showInfoMessage(rb.getString("info"), rb.getString("owm.3"));
		}
	    }
	} else {
	    ospfWin.showInfoMessage(rb.getString("info"), rb.getString("owm.4"));
	}
    }


    /**
     * Zavře aktuálně vybranou záložku a odstraní model, který v ní je zobrazen
     */
    protected void closeActualModel() {
	if (getAllMDManager().isEmpty()) {
	    // pokud je seznam s modely prazdny, neni co zavirat
	    ospfWin.getStatusBar().clear();
	} else {
	    // jinak je nacten model site
	    OspfModel modelToRemove = getActualMDManager().getOspfModel();
	    if (modelToRemove != null) {
		// pokud model site obsahuje ospfModel, smaze se
		ospfModels.remove(modelToRemove);
	    }
	    ospfWin.closeActiveModelTabbedPane();
	}
    }


    /**
     * Otevře dialog s routery, jejichž výpadek způsobí rozpad sítě na více
     * částí
     */
    protected void showComputedRouterImportancesDialog() {
	if (getActualMDManager().getOspfModel() == null) {
	    ospfWin.showErrorMessage(rb.getString("error"), rb.getString("err.noData"));
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
	    ospfWin.showErrorMessage(rb.getString("error"), rb.getString("err.noData"));
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
	AboutApplicationDialog dialog = new AboutApplicationDialog(ospfWin);
	dialog.setVisible(true);
    }


    /**
     * Zobrazí dialog "Tipy"
     */
    protected void showTipsDialog() {
	TipsDialog dialog = new TipsDialog(ospfWin, settings.language);
	dialog.setVisible(true);
    }


    /**
     * Otevře dialog s nastavením aplikace
     */
    protected void showSettingsDialog() {
	SettingsDialog dialog = new SettingsDialog(ospfWin, this, settings);
	dialog.setVisible(true);
    }


    /**
     * Zobrazí okno s informacemi o modelu ve dvou záložkách
     */
    protected void showOspfModelSummaryDialog() {
	OspfModel model = getActualMDManager().getOspfModel();
	OspfModelSummaryDialog dialog = new OspfModelSummaryDialog(ospfWin, new OspfWinModelTabbedPane(model), model);
	dialog.setVisible(true);
    }


    /**
     * Zobrazí okno s přehledem načtených LLTD dat a práce s nimi
     */
    protected void showLLTDDialog() {
	LLTDSummaryDialog dialog = new LLTDSummaryDialog(ospfWin, this, lltdModels);
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
	    ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
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
	if (fchooser.showSaveDialog(this.ospfWin) == JFileChooser.APPROVE_OPTION) {
	    try {
		Exporter.exportModelToXGMML(fchooser.getSelectedFile(), model);
	    } catch (IOException e) {
		ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
	    }
	}
    }


    /**
     * Vrací manažery všech načtených modelů
     * @return
     */
    protected Set<MapManager> getAllMDManager() {
	return ospfWin.getAllMDManager();
    }


    /**
     * Vrací manažera aktuálního modelu
     * @return
     */
    protected MapManager getActualMDManager() {
	return ospfWin.getActualMDManager();
    }


    /**
     * Vrací vlastníka
     */
    public OspfWin getOwner() {
	return ospfWin;
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
	mapXMLSaver.setVertexPositions(getActualMDManager().getGraphComponent().getRouterVertexPositions());
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
	if (fchooser.showSaveDialog(this.ospfWin) == JFileChooser.APPROVE_OPTION) {
	    saveModelToXML(fchooser.getSelectedFile());
	}
    }


    /**
     * Nastaví text status infa dle indesu (číslováno od 0)
     * @param text
     */
    protected void setStatusText(int index, String text) {
	ospfWin.getStatusBar().setStatus(index, text);
    }


    /**
     * Nastaví text tooltipu status infa dle indexu (číslováno od 0)
     * @param text
     */
    protected void setStatusTextToolTip(int index, String text) {
	ospfWin.getStatusBar().setToolTip(index, text);
    }


    /**
     * Vrací řetězec k vyhledání
     * @return
     */
    protected String getSearchString() {
	return ospfWin.getSearhString();
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
		getActualMDManager().getOwner().processModelsAfterStart(true, null, 0);
	    } else {
		getActualMDManager().getOwner().processModelsAfterStart(false, dialog.getSelectedRouter(),
			dialog.getNeighboursDepth());
	    }
	    ospfWin.repaint();
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
	if (fchooser.showSaveDialog(this.ospfWin) == JFileChooser.APPROVE_OPTION) {
	    try {
		Exporter.exportModelToSVG(fchooser.getSelectedFile(), model, getActualMDManager().getGraphComponent());
	    } catch (IOException e) {
		ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
	    }
	}
    }


    public void actualizeIPv6mode() {
	ospfWin.actualizeIPv6mode();
    }


    /**
     * Aktualizace po změně v nastavení
     */
    public void actualizeBySettings() {
	for (MapManager mm : getAllMDManager()) {
	    mm.getGraphComponent().setEdgeShaper(settings.edgeShaper);
	}
    }


    /**
     * Načte LLTD data
     */
    public void loadLLTDData() {
	try {
	    LLTDLoader.loadLLTDData(lltdModels);
	} catch (IOException e) {
	    e.printStackTrace();
	    ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	    ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
	} catch (SAXException e) {
	    e.printStackTrace();
	    ospfWin.showErrorMessage(rb.getString("error"), e.getMessage());
	}
    }


    /**
     * Zařazení LLTD modelů do OSPF modelů (k routerům) dle toho,
     * zda dany routeru propaguje IP adresu
     */
    public void addLLTDtoOspfModels() {
	// prochazeni vsech OSPF modelu
	for (OspfModel ospf : ospfModels) {
	    // prochazeni vsech routeru daneho ospf modelu
	    for (Router r : ospf.getRouters()) {
		// prochazeni vsech nactenych lltd modelu
		for (LLTDModel m : this.lltdModels) {
		    // prochazeni ip adres z traceroutu
		    for (String ip : m.getTraceroute()) {
			// pokud ip z traceroutu je propagovana routerem, lltd model se prida
			if (IpCalculator.containsRouterSubnet(r, ip)) {
			    r.getLltdModels().add(m);
			}
		    }
		}
	    }
	}
	bla();
    }


    public void bla() {
	for (MapManager mm : getAllMDManager()) {
	    mm.actualizeLltdVericies();
	}
    }
}
