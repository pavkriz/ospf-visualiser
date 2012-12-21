package org.hkfree.ospf.gui.mappanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.hkfree.ospf.gui.pathstreedialog.ShortestPathTreeDialog;
import org.hkfree.ospf.layout.JSLayout;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.map.EdgeOfSPT;
import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.ospf.ExternalLSA;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospf.StubLink;
import org.hkfree.ospf.setting.MapGraphComponentMode;
import org.hkfree.ospf.tools.MapModelShortestPathFinder;
import org.hkfree.ospf.tools.geo.GPSPointConverter;
import org.hkfree.ospf.tools.ip.IpCalculator;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * Třída představujíc koponentu grafu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapGraphComponent extends JComponent {

    private static final long serialVersionUID = 1L;
    private MapPanel owner = null;
    private MapModel mapModel = null;
    private int mapGraphCompMode;
    private RouterVertex shortestTreeCenter = null;
    private VisualizationViewer<RouterVertex, LinkEdge> vv = null;
    private Graph<RouterVertex, LinkEdge> graph = null;
    private Layout<RouterVertex, LinkEdge> layout = null;
    private OspfModalGraphMouse<RouterVertex, LinkEdge> graphMouse = null;
    private MapStyleTransformer styleTransformer = null;
    private GPSPointConverter gpsPointConverter = null;
    private ScalingControl scaler = null;
    private float zoomValue = 1.0f;
    private Forest<RouterVertex, EdgeOfSPT> shortestTreeGraph = new DelegateForest<RouterVertex, EdgeOfSPT>();
    private RouterVertex firstRVertexToMakeEdge = null;
    private RouterVertex firstShortestPathRV = null;
    private RouterVertex secondShortestPathRV = null;
    private boolean showIPv6 = false;


    /**
     * Konstruktor
     * @param mapDesignPanel
     */
    public MapGraphComponent(MapPanel mapDesignPanel) {
	this.owner = mapDesignPanel;
	initializeGraph();
    }


    /**
     * Nastavuje MapModel
     * @param model
     */
    public void setMapModel(MapModel model) {
	this.mapModel = model;
    }


    /**
     * Vrací komponentu vizualization vieweru
     * @return vv
     */
    public VisualizationViewer<RouterVertex, LinkEdge> getVisualizationComponent() {
	return this.vv;
    }


    /**
     * Vrací aktuální pracovní mód
     * @return int
     */
    public int getMapGraphComponentMode() {
	return mapGraphCompMode;
    }


    /**
     * Inicializuje graf
     */
    private void initializeGraph() {
	graph = new SparseMultigraph<RouterVertex, LinkEdge>();
	layout = new JSLayout<RouterVertex, LinkEdge>(graph);
	layout.setSize(new Dimension(2200, 2200));
	((JSLayout<RouterVertex, LinkEdge>) layout).setRepulsionMultiplier(Constants.LAYOUT_REPULSION);
	((JSLayout<RouterVertex, LinkEdge>) layout).setAttractionMultiplier(Constants.LAYOUT_ATTRACTION);
	((JSLayout<RouterVertex, LinkEdge>) layout).setMaxIterations(Constants.LAYOUT_FR_MAX_ITERATIONS);
	vv = new VisualizationViewer<RouterVertex, LinkEdge>(layout);
	vv.setBackground(Color.WHITE);
	vv.setSize(2200, 2200);
	scaler = new CrossoverScalingControl();
	graphMouse = new OspfModalGraphMouse<RouterVertex, LinkEdge>(this);
	vv.setGraphMouse(graphMouse);
	graphMouse.add(new MapGraphContextMenu(this));
	graphMouse.add(new MapGraphMouseClickPlugin(this));
	styleTransformer = new MapStyleTransformer(this);
	vv.getRenderContext().setVertexFillPaintTransformer(styleTransformer.getVertexFillPainter());
	vv.getRenderContext().setVertexDrawPaintTransformer(styleTransformer.getVertexBorderPainter());
	vv.getRenderContext().setVertexLabelTransformer(styleTransformer.getVertexLabeler());
	vv.getRenderContext().setVertexShapeTransformer(styleTransformer.getVertexShaper());
	vv.getRenderContext().setVertexStrokeTransformer(styleTransformer.getVertexBorderStroker());
	vv.setVertexToolTipTransformer(styleTransformer.getVertexToolTiper());
	// vv.getRenderContext().setVertexIncludePredicate(styleTransformer.getVisiblePredicate());
	vv.getRenderContext().setEdgeLabelTransformer(styleTransformer.getEdgeLabeler());
	vv.setEdgeToolTipTransformer(styleTransformer.getEdgeTooltiper());
	vv.getRenderContext().setEdgeDrawPaintTransformer(styleTransformer.getEdgeLinePainter());
	vv.getRenderContext().setEdgeStrokeTransformer(styleTransformer.getEdgeLineStroker());
    }


    /**
     * Vytvoří a zobrazí graf celé sítě
     */
    public void createWholeGraph() {
	for (RouterVertex routerVertex : mapModel.getRouterVertices()) {
	    graph.addVertex(routerVertex);
	    routerVertex.setFullExpanded(true);
	    routerVertex.setPermanentlyDisplayed(true);
	}
	for (LinkEdge linkEdge : mapModel.getLinkEdges()) {
	    linkEdge.setGraphComponent(this);
	    graph.addEdge(linkEdge, linkEdge.getRVertex1(), linkEdge.getRVertex2());
	}
	vv.repaint();
	scaler.scale(vv, zoomValue, vv.getCenter());
	gpsPointConverter = new GPSPointConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(mapModel.getMinimumLatitude(), mapModel.getMaximumLatitude(),
		mapModel.getMinimumLongtitude(), mapModel.getMaximumLongtitude());
	layout.initialize();
	vv.getModel().getRelaxer().setSleepTime(50);
	vv.getModel().getRelaxer().relax();
    }


    /**
     * Zobrazí graf pouze zadané části sítě
     * @param center
     * @param depth
     */
    public void createGraphFromCenter(RouterVertex center, int depth) {
	for (RouterVertex rv : mapModel.getRouterVertices()) {
	    graph.removeVertex(rv);
	}
	graph.addVertex(center);
	((JSLayout<RouterVertex, LinkEdge>) layout).setLocation(center, vv.getWidth() / 2, vv.getHeight() / 2);
	layout.lock(center, true);
	center.setPermanentlyDisplayed(true);
	List<RouterVertex> previousStepVertexes = new ArrayList<RouterVertex>();
	List<RouterVertex> newAddedVertexes = new ArrayList<RouterVertex>();
	previousStepVertexes.add(center);
	newAddedVertexes.add(center);
	for (int i = 1; i <= depth; i++) {
	    for (RouterVertex rv : newAddedVertexes) {
		addNeighborsRouterVertexes(rv);
	    }
	    newAddedVertexes.clear();
	    for (RouterVertex rv : graph.getVertices()) {
		if (!previousStepVertexes.contains(rv)) {
		    previousStepVertexes.add(rv);
		    newAddedVertexes.add(rv);
		}
	    }
	}
	vv.repaint();
	scaler.scale(vv, zoomValue, vv.getCenter());
	gpsPointConverter = new GPSPointConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(mapModel.getMinimumLatitude(), mapModel.getMaximumLatitude(),
		mapModel.getMinimumLongtitude(), mapModel.getMaximumLongtitude());
	layout.initialize();
	vv.getModel().getRelaxer().setSleepTime(50);
	vv.getModel().getRelaxer().relax();
    }


    /**
     * Vytvoří graf na základě inframací o pozicích z XML
     * @param rvPositions
     */
    public void createGraphByPositions(Map<RouterVertex, Point2D> rvPositions) {
	for (RouterVertex routerVertex : mapModel.getRouterVertices()) {
	    if (rvPositions.containsKey(routerVertex)) {
		graph.addVertex(routerVertex);
		layout.setLocation(routerVertex, rvPositions.get(routerVertex));
		if (routerVertex.isLocked()) {
		    lockVertexPosition(routerVertex);
		}
	    }
	}
	for (LinkEdge linkEdge : mapModel.getLinkEdges()) {
	    if (graph.containsVertex(linkEdge.getRVertex1()) && graph.containsVertex(linkEdge.getRVertex2())) {
		linkEdge.setGraphComponent(this);
		graph.addEdge(linkEdge, linkEdge.getRVertex1(), linkEdge.getRVertex2());
	    }
	}
	for (RouterVertex routerVertex : graph.getVertices()) {
	    checkRVertexExpandability(routerVertex);
	}
	vv.repaint();
	scaler.scale(vv, zoomValue, vv.getCenter());
	gpsPointConverter = new GPSPointConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(mapModel.getMinimumLatitude(), mapModel.getMaximumLatitude(),
		mapModel.getMinimumLongtitude(), mapModel.getMaximumLongtitude());
    }


    /**
     * Pokud graf ještě neobsahuje zadaný vrchol, metoda ho do grafu přidá
     * @param routerVertex
     */
    public void addRouterVertexToGraph(RouterVertex routerVertex) {
	if (!graph.containsVertex(routerVertex)) {
	    graph.addVertex(routerVertex);
	}
    }


    /**
     * Zobrazí sousedy zadaného vrcholu
     * @param routerVertex
     */
    public void addNeighborsRouterVertexes(RouterVertex routerVertex) {
	for (LinkEdge linkEdge : mapModel.getIncidentEdges(routerVertex)) {
	    linkEdge.setGraphComponent(this);
	    if (!routerVertex.equals(linkEdge.getRVertex1())) {
		addRouterVertexToGraph(linkEdge.getRVertex1());
		if (linkEdge.getRVertex1().isMultilink()) {
		    linkEdge.getRVertex1().setPermanentlyDisplayed(true);
		    this.addNeighborsRouterVertexes(linkEdge.getRVertex1());
		}
	    }
	    if (!routerVertex.equals(linkEdge.getRVertex2())) {
		addRouterVertexToGraph(linkEdge.getRVertex2());
		if (linkEdge.getRVertex2().isMultilink()) {
		    linkEdge.getRVertex2().setPermanentlyDisplayed(true);
		    this.addNeighborsRouterVertexes(linkEdge.getRVertex2());
		}
	    }
	    if (!graph.containsEdge(linkEdge)) {
		graph.addEdge(linkEdge, linkEdge.getRVertex1(), linkEdge.getRVertex2());
	    }
	}
	for (LinkEdge linkEdge : mapModel.getLinkEdges()) {
	    linkEdge.setGraphComponent(this);
	    if (!graph.containsEdge(linkEdge)) {
		if (graph.containsVertex(linkEdge.getRVertex1()) && graph.containsVertex(linkEdge.getRVertex2())) {
		    graph.addEdge(linkEdge, linkEdge.getRVertex1(), linkEdge.getRVertex2());
		    if (!linkEdge.getRVertex1().isFullExpanded())
			checkRVertexExpandability(linkEdge.getRVertex1());
		    if (!linkEdge.getRVertex2().isFullExpanded())
			checkRVertexExpandability(linkEdge.getRVertex2());
		}
	    }
	}
	for (RouterVertex rv : graph.getVertices()) {
	    if (!rv.isFullExpanded())
		checkRVertexExpandability(rv);
	}
    }


    /**
     * Zjistí, zda má zadaná router ještě nějaké nezobrazené sousedy
     * @param r
     */
    private void checkRVertexExpandability(RouterVertex r) {
	if (!r.isMultilink()) {
	    for (LinkEdge le : mapModel.getIncidentEdges(r)) {
		RouterVertex secondLeRVertex = null;
		if (le.getRVertex1().equals(r)) {
		    secondLeRVertex = le.getRVertex2();
		} else {
		    secondLeRVertex = le.getRVertex1();
		}
		// kouknout jestli je router na standardnim spoji zobrazen
		if (!secondLeRVertex.isMultilink()) {
		    if (!graph.containsVertex(secondLeRVertex)) {
			r.setFullExpanded(false);
			return;
		    }
		} else { // kouknout jestli ma incidentni multilink zobrazeny vsechny vrcholy
		    for (LinkEdge mle : mapModel.getIncidentEdges(secondLeRVertex)) {
			if (!graph.containsVertex(mle.getRVertex1())) {
			    r.setFullExpanded(false);
			    return;
			}
		    }
		}
	    }
	    r.setFullExpanded(true);
	}
    }


    /**
     * Odstraní z grafu všechny sousední vrcholy zadaného vrcholu, které nejsou permanentně zobrazeny
     * @param routerVertex
     */
    public void removeNonPermanentlyDisplayedNeighbours(RouterVertex routerVertex) {
	List<LinkEdge> edgesToRemove = new ArrayList<LinkEdge>();
	List<RouterVertex> rvertexesToRemove = new ArrayList<RouterVertex>();
	for (LinkEdge le : graph.getIncidentEdges(routerVertex)) {
	    RouterVertex secondRVertex = null;
	    if (le.getRVertex1().equals(routerVertex)) {
		secondRVertex = le.getRVertex2();
	    } else {
		secondRVertex = le.getRVertex1();
	    }
	    if (!secondRVertex.isPermanentlyDisplayed()) {
		for (LinkEdge sle : graph.getIncidentEdges(secondRVertex)) {
		    if (!edgesToRemove.contains(sle)) {
			edgesToRemove.add(sle);
		    }
		}
		rvertexesToRemove.add(secondRVertex);
	    }
	    if (secondRVertex.isMultilink()) {
		removeMultilikNonPermanentlyDRVertexes(secondRVertex, routerVertex, edgesToRemove, rvertexesToRemove);
	    }
	}
	for (LinkEdge rle : edgesToRemove) {
	    graph.removeEdge(rle);
	}
	for (RouterVertex rrv : rvertexesToRemove) {
	    graph.removeVertex(rrv);
	}
	for (RouterVertex rv : graph.getVertices()) {
	    checkRVertexExpandability(rv);
	}
    }


    /**
     * Přidá do seznamů vrcholů a hran ke smazání, ty které nejsou permanentně zobrazeny v zadaném multispoji
     * @param multilinkCenter
     * @param rvSender
     * @param edgesToRemove
     */
    private void removeMultilikNonPermanentlyDRVertexes(RouterVertex multilinkCenter, RouterVertex rvSender,
	    List<LinkEdge> edgesToRemove, List<RouterVertex> rvertexesToRemove) {
	int showedCnt = 0;
	for (LinkEdge le : graph.getIncidentEdges(multilinkCenter)) {
	    if (!le.getRVertex1().isPermanentlyDisplayed()) {
		for (LinkEdge sle : graph.getIncidentEdges(le.getRVertex1())) {
		    if (!edgesToRemove.contains(sle)) {
			edgesToRemove.add(sle);
		    }
		}
		if (!rvertexesToRemove.contains(le.getRVertex1())) {
		    rvertexesToRemove.add(le.getRVertex1());
		}
	    } else
		showedCnt++;
	}
	if (showedCnt < 2) {
	    rvertexesToRemove.add(multilinkCenter);
	    for (LinkEdge le : graph.getIncidentEdges(multilinkCenter)) {
		if (le.getRVertex1().equals(rvSender)) {
		    if (!edgesToRemove.contains(le)) {
			edgesToRemove.add(le);
		    }
		}
	    }
	}
    }


    /**
     * Nastaví režim změn pozic vrcholů
     */
    public void setPickingMode() {
	graphMouse.setMode(Mode.PICKING);
	mapGraphCompMode = MapGraphComponentMode.PICKING;
    }


    /**
     * Nastaví režim posunu celé mapy
     */
    public void setTransformingMode() {
	graphMouse.setMode(Mode.TRANSFORMING);
	mapGraphCompMode = MapGraphComponentMode.TRANSFORMING;
    }


    /**
     * Nastaví režim zobrazování sousedů
     */
    public void setShowNeighboursMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.SHOW_NEIGHBOURS;
    }


    /**
     * Nastaví režim zoomování
     */
    public void setZoomMode() {
	mapGraphCompMode = MapGraphComponentMode.ZOOM;
    }


    /**
     * Nastaví režim změn cen
     */
    public void setCostChangingMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.COST_CHANGING;
    }


    /**
     * Nastaví režim zobrazení stromu nejkratších cest
     */
    public void setShowShortestPathMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.SHOW_SHORTEST_PATH;
    }


    /**
     * Nastaví režim zamykání vrcholů
     */
    public void setLockVertexMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.VERTEX_LOCKING;
    }


    /**
     * Nastaví režim přesunu na pozice dle GPS
     */
    public void setGPSPositioningMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.GPS_POSITIONING;
    }


    /**
     * Naství mód přidávání vrcholů
     */
    public void setAddRVertexMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.RVERTEX_ADDING;
    }


    /**
     * Nastaví mód přidávání hran
     */
    public void setAddLEdgeMode() {
	returnColorOfShortestPath();
	mapGraphCompMode = MapGraphComponentMode.LEDGE_ADDING;
    }


    /**
     * Nastaví mód zobrazení asymetrických spojů
     */
    public void setAsymetricLinkMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.ASYMETRIC_LINK;
    }


    /**
     * Nastaví mód zobrazení nejkratšch cest mezi dvěma routery
     */
    public void setTwoRoutersShortesPathMode() {
	returnColorOfShortestPath();
	returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.TWO_ROUTERS_SHORTEST_PATH;
    }


    /**
     * Přiblíží mapu
     */
    public void zoomPlus() {
	zoomPlus(vv.getCenter());
    }


    /**
     * Přiblíží mapu
     */
    public void zoomPlus(Point2D clickPoint) {
	scaler.scale(vv, 1.2f, clickPoint);
    }


    /**
     * Oddálí mapu
     */
    public void zoomMinus() {
	zoomMinus(vv.getCenter());
    }


    /**
     * Oddálí mapu
     */
    public void zoomMinus(Point2D clickPoint) {
	scaler.scale(vv, 0.8f, clickPoint);
    }


    /**
     * Zamkne pozice všech vrcholů
     */
    public void lockAllRouterVertices() {
	for (RouterVertex r : graph.getVertices()) {
	    r.setLocked(true);
	    layout.lock(r, true);
	}
    }


    /**
     * Zamkne pozici daného vrcholu
     * @param r
     */
    public void lockVertexPosition(RouterVertex r) {
	r.setLocked(true);
	layout.lock(r, true);
    }


    /**
     * Odemkne pozici daného vrcholu
     * @param r
     */
    public void unlockVertexPosition(RouterVertex r) {
	r.setLocked(false);
	layout.lock(r, false);
    }


    /**
     * Vypne daný router
     * @param r
     */
    public void disableRouterVertex(RouterVertex r) {
	r.setEnabled(false);
	for (LinkEdge le : mapModel.getIncidentEdges(r)) {
	    le.setEnabled(false);
	}
    }


    /**
     * Zapne daný router
     * @param r
     */
    public void enableRouterVertex(RouterVertex r) {
	r.setEnabled(true);
	for (LinkEdge le : mapModel.getIncidentEdges(r)) {
	    enableLinkEdge(le);
	}
    }


    /**
     * Vypne daný spoj
     * @param le
     */
    public void enableLinkEdge(LinkEdge le) {
	if (le.getRVertex1().isEnabled() && le.getRVertex2().isEnabled()) {
	    le.setEnabled(true);
	}
    }


    /**
     * Zapne daný spoj
     * @param r
     */
    public void tryDisableIncidentMultilink(RouterVertex r) {
	for (LinkEdge le : mapModel.getIncidentEdges(r)) {
	    if (le.isEdgeOfMultilink()) {
		checkMultilinkToDisable(le.getRVertex2());
	    }
	}
    }


    /**
     * Zkontroluje a přípaně vypne multilink
     * @param r
     */
    public void checkMultilinkToDisable(RouterVertex r) {
	int enableCnt = 0;
	for (LinkEdge le : mapModel.getIncidentEdges(r)) {
	    if (le.isEnabled())
		enableCnt++;
	}
	if (enableCnt <= 1) {
	    disableRouterVertex(r);
	}
    }


    /**
     * Pokud má vrchol nadefinovánu gps pozici, bude na ni přemístěn
     * @param r
     */
    public void setVertexPositionByGPS(RouterVertex r) {
	if (r.getGpsLatitude() != 0 && r.getGpsLongtitude() != 0) {
	    layout.setLocation(r, gpsPointConverter.getPosition(r.getGPSCoordinates()));
	    lockVertexPosition(r);
	}
    }


    /**
     * Rozmístí všechny vrcholy dle jejich odpovídajících gps souřadnic
     */
    public void setAllVerticesToGPSPosition() {
	for (RouterVertex r : graph.getVertices()) {
	    setVertexPositionByGPS(r);
	}
	layout.initialize();
	vv.repaint();
    }


    /**
     * Nastaví všechny hrany jako neobarvené a všem vrcholům, že se nejedná o kořen stromu nejkratších cest
     */
    private void returnColorOfShortestPath() {
	for (LinkEdge le : mapModel.getLinkEdges()) {
	    le.setEdgeOfShortestPath(false);
	    le.setEdgeOfFirstPath(false);
	    le.setEdgeOfSecondPath(false);
	}
	for (RouterVertex rv : mapModel.getRouterVertices()) {
	    rv.setCenterOfShortestPathTree(false);
	    rv.setFirstRVOfTwoRVShortestPath(false);
	    rv.setSecondRVOfTwoRVShortestPath(false);
	}
	if (mapGraphCompMode != MapGraphComponentMode.TWO_ROUTERS_SHORTEST_PATH) {
	    firstShortestPathRV = null;
	    secondShortestPathRV = null;
	}
    }


    /**
     * Zruší první vybraný vrchol k vytvoření nové hrany
     */
    private void returnColorOfNewEdgeFirstVertex() {
	if (firstRVertexToMakeEdge != null) {
	    firstRVertexToMakeEdge.setPartOfNewAddedEdge(false);
	    firstRVertexToMakeEdge = null;
	}
    }


    /**
     * Zobrazí nejkratší cesty
     */
    public void showShortestPath() {
	returnColorOfShortestPath();
	shortestTreeCenter.setCenterOfShortestPathTree(true);
	MapModelShortestPathFinder shortestPathFinder = new MapModelShortestPathFinder();
	shortestPathFinder.setMapModel(mapModel);
	shortestPathFinder.findShortesPathTree(shortestTreeCenter);
    }


    /**
     * Zobrazí dialog se stromem nejkratších cest
     * @param routerVertex
     */
    public void showShortestPathTreeDialog(RouterVertex routerVertex) {
	returnColorOfShortestPath();
	MapModelShortestPathFinder shortestPathFinder = new MapModelShortestPathFinder();
	shortestPathFinder.setMapModel(mapModel);
	setShortestTreeCenter(routerVertex);
	routerVertex.setCenterOfShortestPathTree(true);
	shortestPathFinder.findShortesPathTree(routerVertex);
	shortestPathFinder.createShortestPathTreeGraph(shortestTreeGraph);
	ShortestPathTreeDialog treeDialog = new ShortestPathTreeDialog(shortestTreeGraph, routerVertex);
	treeDialog.setVisible(true);
    }


    /**
     * Nastaví routerVertex jako vrchol, ze kterého se budou počítat nejkratší cesty
     * @param routerVertex
     */
    public void setShortestTreeCenter(RouterVertex routerVertex) {
	this.shortestTreeCenter = routerVertex;
    }


    /**
     * Zjistí zda je komponenta v módu zobrazování nejkratších cest a je nastaven počáteční vrchol. Pokud ano, zobrazí
     * nejkratší cesty
     */
    public void tryRecomputeShortestPaths() {
	if (mapGraphCompMode == MapGraphComponentMode.SHOW_SHORTEST_PATH && shortestTreeCenter != null
		&& shortestTreeCenter.isEnabled()) {
	    showShortestPath();
	}
	if (mapGraphCompMode == MapGraphComponentMode.TWO_ROUTERS_SHORTEST_PATH && firstShortestPathRV != null
		&& secondShortestPathRV != null) {
	    showShortestPathsBetweenRV(firstShortestPathRV, secondShortestPathRV);
	}
    }


    /**
     * Přiřadí vrchol a případně zobrazí nejkratší cesty mezi zadanými vrcholy
     * @param rv
     */
    public void assignRouterToTwoRoutesShortesPath(RouterVertex rv) {
	if (firstShortestPathRV == null || (secondShortestPathRV != null && firstShortestPathRV != null)) {
	    returnColorOfShortestPath();
	    firstShortestPathRV = rv;
	    secondShortestPathRV = null;
	    rv.setFirstRVOfTwoRVShortestPath(true);
	    return;
	} else {
	    secondShortestPathRV = rv;
	    rv.setSecondRVOfTwoRVShortestPath(true);
	    showShortestPathsBetweenRV(firstShortestPathRV, secondShortestPathRV);
	    /**
	     * firstShortestPathRV = null;
	     * secondShortestPathRV = null;
	     */
	}
    }


    /**
     * Zobrazí vzájemné nejkratší cesty mezi zadanými routery
     * @param rv1
     * @param rv2
     */
    public void showShortestPathsBetweenRV(RouterVertex rv1, RouterVertex rv2) {
	MapModelShortestPathFinder shortestPathFinder = new MapModelShortestPathFinder();
	shortestPathFinder.setMapModel(mapModel);
	shortestPathFinder.findShortesPathTree(rv1);
	List<LinkEdge> pathToRV2 = shortestPathFinder.getShortestPathBetweenRouters(rv2);
	returnColorOfShortestPath();
	shortestPathFinder.findShortesPathTree(rv2);
	List<LinkEdge> pathToRV1 = shortestPathFinder.getShortestPathBetweenRouters(rv1);
	returnColorOfShortestPath();
	rv1.setFirstRVOfTwoRVShortestPath(true);
	rv2.setSecondRVOfTwoRVShortestPath(true);
	for (LinkEdge le : pathToRV2) {
	    le.setEdgeOfFirstPath(true);
	}
	for (LinkEdge le : pathToRV1) {
	    le.setEdgeOfSecondPath(true);
	}
    }


    /**
     * Vytvoří nový RouterVertex v modelu, přidá ho do grafu a přesune ho na zvolenou pozici
     */
    public void addNewRouterVertex(Point2D rvPoint) {
	AddRouterVertexDialog dialog = new AddRouterVertexDialog(mapModel.getRouterVertices());
	if (dialog.successfulyConfirmed()) {
	    RouterVertex rv = mapModel.addFirstRouterVertex(dialog.getEnteredName(), dialog.getEnteredName());
	    graph.addVertex(rv);
	    lockVertexPosition(rv);
	    rv.setFullExpanded(true);
	    rv.setPermanentlyDisplayed(true);
	    rv.setExtraAddedVertex(true);
	    rvPoint = transformClickedPointToGraphPoint(rvPoint);
	    ((JSLayout<RouterVertex, LinkEdge>) layout).setLocation(rv, rvPoint.getX(), rvPoint.getY());
	}
    }


    /**
     * Transformuje souřadnice kliku do komponenty na souřadnice v grafu
     * @param point
     * @return point2D
     */
    private Point2D transformClickedPointToGraphPoint(Point2D point) {
	point = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, point);
	point = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, point);
	return point;
    }


    /**
     * Vytvoří novou LinkEdge a přidá ji do grafu
     * @param rv
     */
    public void addNewLinkEdge(RouterVertex rv) {
	if (firstRVertexToMakeEdge == null) {
	    firstRVertexToMakeEdge = rv;
	    rv.setPartOfNewAddedEdge(true);
	} else {
	    if (!(firstRVertexToMakeEdge.isMultilink() && rv.isMultilink()) && !firstRVertexToMakeEdge.equals(rv)) {
		AddLinkEdgeDialog dialog = new AddLinkEdgeDialog(mapModel.getLinkEdges(), firstRVertexToMakeEdge, rv);
		if (dialog.successfulyConfirmed()) {
		    firstRVertexToMakeEdge.setPartOfNewAddedEdge(false);
		    LinkEdge le = mapModel.addLinkEdge(dialog.getRouterV1(), dialog.getRouterV2(), dialog.getCost1(),
			    dialog.getCost2(), dialog.getEnteredName());
		    le.setExtraAddedEdge(true);
		    le.setGraphComponent(this);
		    graph.addEdge(le, le.getRVertex1(), le.getRVertex2());
		    firstRVertexToMakeEdge = null;
		}
	    }
	}
    }


    /**
     * Vrátí pozice routerů na plátně, které se v grafu nacházejí
     * @return map
     */
    public Map<RouterVertex, Point2D> getRouterVertexPositions() {
	Map<RouterVertex, Point2D> positions = new HashMap<RouterVertex, Point2D>();
	for (RouterVertex rv : graph.getVertices()) {
	    positions.put(rv, new Point2D.Double(((JSLayout<RouterVertex, LinkEdge>) layout).getX(rv),
		    ((JSLayout<RouterVertex, LinkEdge>) layout).getY(rv)));
	}
	return positions;
    }


    /**
     * Odstraní externě přidaný routerVertex z grafu a mapModelu
     * @param routerVertex
     */
    public void removeExtraAddedRVertex(RouterVertex routerVertex) {
	if (routerVertex.isExtraAddedVertex()) {
	    List<LinkEdge> removeEdges = new ArrayList<LinkEdge>();
	    for (LinkEdge le : mapModel.getIncidentEdges(routerVertex)) {
		graph.removeEdge(le);
		removeEdges.add(le);
	    }
	    for (LinkEdge le : removeEdges) {
		mapModel.getLinkEdges().remove(le);
	    }
	    graph.removeVertex(routerVertex);
	    mapModel.getRouterVertices().remove(routerVertex);
	}
    }


    /**
     * Odstraní externě přidanou linkEdge z grafu a mapModelu
     * @param linkEdge
     */
    public void removeExtraAddedLEdge(LinkEdge linkEdge) {
	if (linkEdge.isExtraAddedEdge()) {
	    graph.removeEdge(linkEdge);
	    mapModel.getLinkEdges().remove(linkEdge);
	}
    }


    /**
     * Nalezne a oznaci routery obsahujici v nazvu text, take prohledava stuby a external LSA
     * text muze byt oddeleny | pro vyhledavani vice retezcu
     * @param text filtr pro nazev routeru
     */
    public void findByNameOrIP(String text) {
	boolean b;
	Set<RouterVertex> rvs = new HashSet<RouterVertex>(graph.getVertices());
	Router r = null;
	String[] foundedNames = text.split("\\|");
	for (RouterVertex rv : rvs) {
	    // multilink se preskakuje, v nem se nehleda
	    if (rv.isMultilink()) {
		continue;
	    }
	    b = false;
	    // prochazeni a hledani kazdeho retezce ktere byly oddeleny '|'
	    for (String name : foundedNames) {
		if (name.isEmpty()) {
		    continue;
		}
		if (rv.getName().toUpperCase().contains(name.toUpperCase())
			|| rv.getDescription().toUpperCase().contains(name.toUpperCase())) {
		    b = true;
		}
		// pokud je model null, hledat pouze v mapModelu
		if (owner.getMapDesignWinManager().getOspfModel() != null) {
		    r = owner.getMapDesignWinManager().getOspfModel().getRouterByIp(rv.getDescription());
		    // vyhledavani ve stubs
		    for (StubLink sl : r.getStubs()) {
			if (sl.getLinkID().toUpperCase().contains(name.toUpperCase()) ||
				IpCalculator.networkContains(sl.getLinkID(), sl.getMask(), name)) {
			    b = true;
			}
		    }
		    // vyhledavani v external lsa
		    for (ExternalLSA el : r.getExternalLsa()) {
			if (el.getNetwork().toUpperCase().contains(name.toUpperCase()) ||
				IpCalculator.networkContains(el.getNetwork(), el.getMask(), name)) {
			    b = true;
			}
		    }
		}
	    }
	    rv.setFounded(b);
	    vv.getPickedVertexState().pick(rv, b);
	}
    }


    public MapPanel getOwner() {
	return owner;
    }


    public MapModel getMapModel() {
	return mapModel;
    }


    @SuppressWarnings("unchecked")
    public void layouting(MODE mode) {
	try {
	    Object[] constructorArgs = { graph };
	    Class<? extends Layout<RouterVertex, LinkEdge>> layoutNew = null;
	    Constructor<? extends Layout<RouterVertex, LinkEdge>> constructor = null;
	    Object o = null;
	    Layout<RouterVertex, LinkEdge> l = null;
	    LayoutTransition<RouterVertex, LinkEdge> lt = null;
	    Animator animator = null;
	    switch (mode) {
		case LAYOUT_FR_START:
		    // if (layout instanceof JSLayout<?, ?>) {
		    // vv.getModel().getRelaxer().prerelax();
		    // vv.getModel().getRelaxer().relax();
		    // vv.repaint();
		    // } else {
		    layoutNew = (Class<? extends Layout<RouterVertex, LinkEdge>>) JSLayout.class;
		    constructor = layoutNew.getConstructor(new Class[] { Graph.class });
		    o = constructor.newInstance(constructorArgs);
		    l = (Layout<RouterVertex, LinkEdge>) o;
		    l.setInitializer(vv.getGraphLayout());
		    ((JSLayout<RouterVertex, LinkEdge>) l).setRepulsionMultiplier(Constants.LAYOUT_REPULSION);
		    ((JSLayout<RouterVertex, LinkEdge>) l).setAttractionMultiplier(Constants.LAYOUT_ATTRACTION);
		    ((JSLayout<RouterVertex, LinkEdge>) l).setMaxIterations(Constants.LAYOUT_FR_MAX_ITERATIONS);
		    l.setSize(new Dimension(2200, 2200));
		    lt = new LayoutTransition<RouterVertex, LinkEdge>(vv, vv.getGraphLayout(), l);
		    animator = new Animator(lt);
		    animator.start();
		    vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
		    vv.repaint();
		    layout = l;
		    // }
		    break;
		case LAYOUT_SPRING_START:
		    if (layout instanceof SpringLayout<?, ?>) {
			vv.getModel().getRelaxer().relax();
		    } else {
			layoutNew = (Class<? extends Layout<RouterVertex, LinkEdge>>) SpringLayout.class;
			constructor = layoutNew.getConstructor(new Class[] { Graph.class });
			o = constructor.newInstance(constructorArgs);
			l = (Layout<RouterVertex, LinkEdge>) o;
			l.setInitializer(vv.getGraphLayout());
			((SpringLayout<RouterVertex, LinkEdge>) l).setStretch(0.7);
			((SpringLayout<RouterVertex, LinkEdge>) l).setRepulsionRange(120);
			((SpringLayout<RouterVertex, LinkEdge>) l).setForceMultiplier(0.85);
			l.setSize(new Dimension(2000, 2000));
			lt = new LayoutTransition<RouterVertex, LinkEdge>(vv, vv.getGraphLayout(), l);
			animator = new Animator(lt);
			animator.start();
			vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
			vv.repaint();
			layout = l;
		    }
		    break;
		case LAYOUT_SPRING_STOP:
		    // pokud je aktivni rozvrhovani pomoci SpringLayoutu, pauza
		    if (layout instanceof SpringLayout<?, ?>) {
			vv.getModel().getRelaxer().pause();
		    }
		    break;
	    }
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	}
    }


    public boolean isShowIPv6() {
	return showIPv6;
    }


    /**
     * Nastavi zda v modelu zobrazovat IPv6
     * @param b
     */
    public void setShowIPv6(boolean showIPv6) {
	this.showIPv6 = showIPv6;
	if (showIPv6) {
	    for (RouterVertex rv : mapModel.getRouterVertices()) {
		rv.setVisible(true);
	    }
	} else {
	    for (RouterVertex rv : mapModel.getRouterVertices()) {
		boolean bi = false;
		for (LinkEdge le : mapModel.getLinkEdges()) {
		    if ((rv == le.getRVertex1() || rv == le.getRVertex2()) && le.isIPv4()) {
			bi = true;
		    }
		}
		rv.setVisible(bi);
	    }
	}
	vv.repaint();
    }
}
