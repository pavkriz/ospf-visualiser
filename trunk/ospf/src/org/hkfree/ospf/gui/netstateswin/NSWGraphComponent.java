package org.hkfree.ospf.gui.netstateswin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.hkfree.ospf.gui.mappanel.OspfModalGraphMouse;
import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.netchange.NetStateLinkEdge;
import org.hkfree.ospf.setting.MapGraphComponentMode;
import org.hkfree.ospf.tools.MapModelShortestPathFinder;
import org.hkfree.ospf.tools.geo.GPSPointConverter;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ScalingControl;

/**
 * Třída představující komponentnu pro vykreslení grafu
 * @author Jakub Menzel
 */
public class NSWGraphComponent extends JComponent {

    private static final long serialVersionUID = 1L;
    private GPSPointConverter gpsPointConverter = null;
    private NetChangeModel netChangeModel = null;
    private int mapGraphCompMode = MapGraphComponentMode.NONE;
    private RouterVertex shortestTreeCenter = null;
    private VisualizationViewer<RouterVertex, LinkEdge> vv = null;
    private Graph<RouterVertex, LinkEdge> graph = null;
    private FRLayout<RouterVertex, LinkEdge> layout = null;
    private OspfModalGraphMouse<RouterVertex, LinkEdge> graphMouse = null;
    private NetStateMapStyleTransformer styleTransformer = null;
    private int actualNetStateIndex = 0;
    private ScalingControl scaler = null;
    private float zoomValue = 3.8f;
    private JFrame owner = null;


    /**
     * Konstruktor
     */
    public NSWGraphComponent() {
	initializeGraph();
    }


    /**
     * Vrací NetChangeModel
     * @return NetChangeModel
     */
    public NetChangeModel getNetChangeModel() {
	return this.netChangeModel;
    }


    /**
     * Nastaví vlastníka komponenty
     * @param owner
     */
    public void setOwner(JFrame owner) {
	this.owner = owner;
    }


    /**
     * Vrací vlastníka komponenty
     * @return JFrame
     */
    public JFrame getOwner() {
	return owner;
    }


    /**
     * Nastavuje NetChangeModel
     * @param model
     */
    public void setNetChangeModel(NetChangeModel model) {
	this.netChangeModel = model;
	gpsPointConverter = new GPSPointConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(netChangeModel.getMinimumLatitude(), netChangeModel.getMaximumLatitude(),
		netChangeModel.getMinimumLongtitude(), netChangeModel.getMaximumLongtitude());
    }


    /**
     * Vrací visualization viewer
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
    public void initializeGraph() {
	graph = new SparseMultigraph<RouterVertex, LinkEdge>();
	layout = new FRLayout<RouterVertex, LinkEdge>(graph);
	layout.setRepulsionMultiplier(0.55); // vzdalenosti vrcholu od sebe
	layout.setAttractionMultiplier(0.18); // vzdalenosti vrcholu na spoji k sobe
	layout.setSize(new Dimension(2300, 2300));
	layout.setMaxIterations(800); // default
	vv = new VisualizationViewer<RouterVertex, LinkEdge>(layout);
	vv.setBackground(Color.WHITE);
	vv.setSize(2000, 2000);
	scaler = new CrossoverScalingControl();
	graphMouse = new OspfModalGraphMouse<RouterVertex, LinkEdge>();
	graphMouse.setMode(Mode.TRANSFORMING);
	vv.setGraphMouse(graphMouse);
	graphMouse.add(new NSGraphMouseClickPlugin(this));
	styleTransformer = new NetStateMapStyleTransformer(this);
	vv.getRenderContext().setVertexFillPaintTransformer(styleTransformer.getVertexFillPainter());
	vv.getRenderContext().setVertexDrawPaintTransformer(styleTransformer.getVertexBorderPainter());
	vv.getRenderContext().setVertexLabelTransformer(styleTransformer.getVertexLabeler());
	vv.getRenderContext().setVertexShapeTransformer(styleTransformer.getVertexShaper());
	vv.getRenderContext().setVertexStrokeTransformer(styleTransformer.getVertexBorderStroker());
	vv.setVertexToolTipTransformer(styleTransformer.getVertexToolTiper());
	vv.getRenderContext().setEdgeLabelTransformer(styleTransformer.getEdgeLabeler());
	vv.setEdgeToolTipTransformer(styleTransformer.getEdgeTooltiper());
	vv.getRenderContext().setEdgeDrawPaintTransformer(styleTransformer.getEdgeLinePainter());
	vv.getRenderContext().setEdgeStrokeTransformer(styleTransformer.getEdgeLineStroker());
    }


    /**
     * Vytvoří graf
     */
    public void createGraph() {
	for (RouterVertex routerVertex : netChangeModel.getRouterVertices()) {
	    graph.addVertex(routerVertex);
	}
	for (LinkEdge linkEdge : netChangeModel.getLinkEdges()) {
	    graph.addEdge(linkEdge, linkEdge.getRVertex1(), linkEdge.getRVertex2());
	}
	vv.repaint();
	scaler.scale(vv, zoomValue, vv.getCenter());
	layout.initialize();
	vv.getModel().getRelaxer().setSleepTime(50);
	vv.getModel().getRelaxer().relax();
    }


    /**
     * Nastaví stavy prvků grafu dle aktuálního modelu
     * @param netStateIndex
     */
    public void applyActualNetStateView(int netStateIndex) {
	if (netStateIndex >= 0 && netStateIndex < netChangeModel.getNetStatesCount()) {
	    actualNetStateIndex = netStateIndex;
	    for (RouterVertex rv : netChangeModel.getRouterVertices()) {
		rv.setEnabled(false);
	    }
	    for (RouterVertex rv : netChangeModel.getNetStates().get(netStateIndex).getActiveRouters()) {
		rv.setEnabled(true);
	    }
	    for (LinkEdge le : netChangeModel.getLinkEdges()) {
		le.setEnabled(false);
	    }
	    for (LinkEdge le : netChangeModel.getNetStates().get(netStateIndex).getActiveLinks()) {
		le.setEnabled(true);
	    }
	}
    }


    /**
     * Nastavuje režim posunu vrcholů
     */
    public void setPickingMode() {
	graphMouse.setMode(Mode.PICKING);
    }


    /**
     * Nastavuje režim posunu celé mapy
     */
    public void setTransformingMode() {
	graphMouse.setMode(Mode.TRANSFORMING);
    }


    /**
     * Nastavuje režim zoomování
     */
    public void setZoomMode() {
	mapGraphCompMode = MapGraphComponentMode.ZOOM;
    }


    /**
     * Nastaví režim přesunu na pozice dle GPS
     */
    public void setGPSPositioningMode() {
	returnColorOfShortestPath();
	// returnColorOfNewEdgeFirstVertex();
	mapGraphCompMode = MapGraphComponentMode.GPS_POSITIONING;
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
     * Pokud má vrchol nadefinovánu gps pozici, bude na ni přemístěn
     * @param r
     */
    private void setVertexPositionByGPS(RouterVertex r) {
	if (r.getGpsLatitude() != 0 && r.getGpsLongtitude() != 0) {
	    layout.setLocation(r, gpsPointConverter.getPosition(r.getGPSCoordinates()));
	    lockVertexPosition(r);
	}
    }


    /**
     * Nastavuje režim zobrazení nejkratších cest
     */
    public void setShowShortestPathMode() {
	returnColorOfShortestPath();
	mapGraphCompMode = MapGraphComponentMode.SHOW_SHORTEST_PATH;
    }


    /**
     * Nastavuje režim zamykání pozic vrcholů
     */
    public void setLockVertexMode() {
	returnColorOfShortestPath();
	mapGraphCompMode = MapGraphComponentMode.VERTEX_LOCKING;
    }


    /**
     * Přepnutí do módu, kdy nejsou brány v úvahu žádné speciální operace
     */
    public void setNoneMode() {
	returnColorOfShortestPath();
	mapGraphCompMode = MapGraphComponentMode.NONE;
    }


    /**
     * Přepnutí do módu zobrazení výpadků spojů
     */
    public void setLinkFaultMode() {
	mapGraphCompMode = MapGraphComponentMode.LINK_FAULT;
    }


    /**
     * Zapne automatické rozvrhování mapy
     */
    public void startLayouting() {
	for (RouterVertex r : graph.getVertices()) {
	    if (!r.isLocked())
		layout.lock(r, false);
	}
	layout.initialize();
	vv.getModel().getRelaxer().setSleepTime(50);
	vv.getModel().getRelaxer().relax();
    }


    /**
     * Zamkne pozice všech vrcholů
     */
    public void lockAllRouterVertexes() {
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
     * Odemkne daný vrchol
     * @param r
     */
    public void unlockVertexPosition(RouterVertex r) {
	r.setLocked(false);
	layout.lock(r, false);
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
     * Odbarví nejkratší cesty
     */
    public void returnColorOfShortestPath() {
	for (LinkEdge le : netChangeModel.getLinkEdges()) {
	    le.setEdgeOfShortestPath(false);
	}
	for (RouterVertex rv : netChangeModel.getRouterVertices()) {
	    rv.setCenterOfShortestPathTree(false);
	}
	vv.repaint();
    }


    /**
     * Vrátí správný popisek (ceny) linkEdge z aktuálního pohledu na síť
     * @param linkEdge
     * @return description
     */
    public String getActualNetStateLinkEdgeLabel(LinkEdge linkEdge) {
	for (NetStateLinkEdge nlse : netChangeModel.getNetStates().get(actualNetStateIndex).getNetStateLinkEdges()) {
	    if (nlse.getLinkEdge().equals(linkEdge)) {
		return nlse.getNetStateLinkCostLabel();
	    }
	}
	// pokud se nenašly ceny v aktuálním modelu, vrací se ceny z posledního modelu, kde se spoj nalézal
	int index = actualNetStateIndex - 1;
	while (index >= 0) {
	    for (NetStateLinkEdge nlse : netChangeModel.getNetStates().get(index).getNetStateLinkEdges()) {
		if (nlse.getLinkEdge().equals(linkEdge)) {
		    return nlse.getNetStateLinkCostLabel();
		}
	    }
	    index--;
	}
	return "";
    }


    /**
     * Vrátí správný popis linkEdge z aktuálního pohledu na síť
     * @param linkEdge
     * @return description
     */
    public String getActualNetStateLinkEdgeDescription(LinkEdge linkEdge) {
	for (NetStateLinkEdge nlse : netChangeModel.getNetStates().get(actualNetStateIndex).getNetStateLinkEdges()) {
	    if (nlse.getLinkEdge().equals(linkEdge)) {
		return nlse.getNetStateLinkDescription();
	    }
	}
	// pokud se nenašel popisek v aktuálním modelu, vrací se popisek z posledního modelu, kde se spoj nalézal
	int index = actualNetStateIndex - 1;
	while (index >= 0) {
	    for (NetStateLinkEdge nlse : netChangeModel.getNetStates().get(index).getNetStateLinkEdges()) {
		if (nlse.getLinkEdge().equals(linkEdge)) {
		    return nlse.getNetStateLinkDescription();
		}
	    }
	    index--;
	}
	return "";
    }


    /**
     * Pokud je režim komponenty zobrazování nejkratších cest, zobrazí nejkratší cesty
     */
    public void tryShowShortestPathOnOtherState() {
	if (mapGraphCompMode == MapGraphComponentMode.SHOW_SHORTEST_PATH) {
	    showShortestPath();
	}
    }


    /**
     * Všechny linky "odbarví" a vypočítá nový strom nejkratších cest
     */
    public void showShortestPath() {
	returnColorOfShortestPath();
	if (shortestTreeCenter != null && shortestTreeCenter.isEnabled()) {
	    shortestTreeCenter.setCenterOfShortestPathTree(true);
	    MapModelShortestPathFinder shortestPathFinder = new MapModelShortestPathFinder();
	    shortestPathFinder.setMapModel(netChangeModel);
	    shortestPathFinder.findShortesPathTree(shortestTreeCenter);
	}
    }


    /**
     * Nastaví výchozí vrchol pro hledání nejkratších cest
     * @param routerVertex
     */
    public void setShortestTreeCenter(RouterVertex routerVertex) {
	shortestTreeCenter = routerVertex;
    }
}
