package org.hkfree.ospf.gui.netstateswin;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;

import org.hkfree.ospf.gui.mappanel.OspfModalGraphMouse;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.EDGE_SHAPER;
import org.hkfree.ospf.model.Constants.LAYOUT;
import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.netchange.NetStateLinkEdge;
import org.hkfree.ospf.setting.MapGraphComponentMode;
import org.hkfree.ospf.tools.MapModelShortestPathFinder;
import org.hkfree.ospf.tools.geo.GPSPointConverter;
import org.hkfree.ospf.tools.geo.GPSPointOsmTestConverter;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

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
    private VisualizationViewer<IVertex, IEdge> vv = null;
    private Graph<IVertex, IEdge> graph = null;
    private Layout<IVertex, IEdge> layout = null;
    private OspfModalGraphMouse<IVertex, IEdge> graphMouse = null;
    private int actualNetStateIndex = 0;
    private ScalingControl scaler = null;
    private float zoomValue = 3.8f;
    private NetStatesWin owner = null;


    /**
     * Konstruktor
     */
    public NSWGraphComponent(EDGE_SHAPER edgeShaper) {
	initializeGraph(edgeShaper);
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
    public void setOwner(NetStatesWin owner) {
	this.owner = owner;
    }


    /**
     * Vrací vlastníka komponenty
     * @return JFrame
     */
    public NetStatesWin getOwner() {
	return owner;
    }


    /**
     * Nastavuje NetChangeModel
     * @param model
     */
    public void setNetChangeModel(NetChangeModel model) {
	this.netChangeModel = model;
	gpsPointConverter = new GPSPointOsmTestConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(netChangeModel.getMinimumLatitude(), netChangeModel.getMaximumLatitude(),
		netChangeModel.getMinimumLongtitude(), netChangeModel.getMaximumLongtitude());
    }


    /**
     * Vrací visualization viewer
     * @return vv
     */
    public VisualizationViewer<IVertex, IEdge> getVisualizationComponent() {
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
    @SuppressWarnings("rawtypes")
    public void initializeGraph(EDGE_SHAPER edgeShaper) {
	graph = new SparseMultigraph<IVertex, IEdge>();
	layout = new FRLayout<IVertex, IEdge>(graph);
	((FRLayout) layout).setAttractionMultiplier(Constants.LAYOUT_ATTRACTION);
	((FRLayout) layout).setRepulsionMultiplier(Constants.LAYOUT_REPULSION);
	((FRLayout) layout).setMaxIterations(Constants.LAYOUT_FR_MAX_ITERATIONS);
	layout.setSize(Constants.LAYOUT_SIZE);
	vv = new VisualizationViewer<IVertex, IEdge>(layout);
	vv.setBackground(Color.WHITE);
	scaler = new CrossoverScalingControl();
	graphMouse = new OspfModalGraphMouse<IVertex, IEdge>();
	graphMouse.setMode(Mode.TRANSFORMING);
	vv.setGraphMouse(graphMouse);
	graphMouse.add(new NSGraphMouseClickPlugin(this));
	NetStateMapStyleTransformer styleTransformer = new NetStateMapStyleTransformer(this);
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
	if (edgeShaper == EDGE_SHAPER.QUAD_CURVE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<IVertex, IEdge>());
	if (edgeShaper == EDGE_SHAPER.LINE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<IVertex, IEdge>());
	if (edgeShaper == EDGE_SHAPER.BENT_LINE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.BentLine<IVertex, IEdge>());
	if (edgeShaper == EDGE_SHAPER.CUBIC_CURVE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.CubicCurve<IVertex, IEdge>());
    }


    /**
     * Vytvoří graf
     */
    public void createGraph() {
	for (IVertex v : netChangeModel.getVertices()) {
	    if (v instanceof RouterVertex) {
		graph.addVertex(v);
	    }
	}
	for (IEdge e : netChangeModel.getEdges()) {
	    if (e instanceof LinkEdge) {
		graph.addEdge(e, e.getVertex1(), e.getVertex2());
	    }
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
	    for (IVertex v : netChangeModel.getVertices()) {
		v.setEnabled(false);
	    }
	    for (IVertex v : netChangeModel.getNetStates().get(netStateIndex).getActiveRouters()) {
		v.setEnabled(true);
	    }
	    for (IEdge e : netChangeModel.getEdges()) {
		e.setEnabled(false);
	    }
	    for (IEdge e : netChangeModel.getNetStates().get(netStateIndex).getActiveLinks()) {
		e.setEnabled(true);
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
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		setVertexPositionByGPS((RouterVertex) v);
	    }
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
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		RouterVertex rv = (RouterVertex) v;
		if (!rv.isLocked()) {
		    layout.lock(rv, false);
		}
	    }
	}
	layout.initialize();
	vv.getModel().getRelaxer().setSleepTime(50);
	vv.getModel().getRelaxer().relax();
    }


    /**
     * Zamkne pozice všech vrcholů
     */
    public void lockAllRouterVertexes() {
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		RouterVertex rv = (RouterVertex) v;
		rv.setLocked(true);
		layout.lock(rv, true);
	    }
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
	for (IEdge e : netChangeModel.getEdges()) {
	    if (e instanceof LinkEdge) {
		((LinkEdge) e).setEdgeOfShortestPath(false);
	    }
	}
	for (IVertex v : netChangeModel.getVertices()) {
	    if (v instanceof RouterVertex) {
		((RouterVertex) v).setCenterOfShortestPathTree(false);
	    }
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


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void layouting(LAYOUT lay) {
	try {
	    Object[] constructorArgs = { graph };
	    Class<? extends Layout<IVertex, IEdge>> layoutNew = null;
	    Constructor<? extends Layout<IVertex, IEdge>> constructor = null;
	    Object o = null;
	    Layout<IVertex, IEdge> l = null;
	    LayoutTransition<IVertex, IEdge> lt = null;
	    Animator animator = null;
	    switch (lay) {
		case LAYOUT_FR:
		    layoutNew = (Class<? extends Layout<IVertex, IEdge>>) FRLayout.class;
		    constructor = layoutNew.getConstructor(new Class[] { Graph.class });
		    o = constructor.newInstance(constructorArgs);
		    layout = (Layout<IVertex, IEdge>) o;
		    layout.setInitializer(vv.getGraphLayout());
		    ((FRLayout) layout).setAttractionMultiplier(Constants.LAYOUT_ATTRACTION);
		    ((FRLayout) layout).setRepulsionMultiplier(Constants.LAYOUT_REPULSION);
		    ((FRLayout) layout).setMaxIterations(Constants.LAYOUT_FR_MAX_ITERATIONS);
		    layout.setSize(Constants.LAYOUT_SIZE);
		    lt = new LayoutTransition<IVertex, IEdge>(vv, vv.getGraphLayout(), layout);
		    animator = new Animator(lt);
		    animator.start();
		    vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
		    vv.repaint();
		    break;
		case LAYOUT_SPRING_START:
		    if (layout instanceof SpringLayout<?, ?>) {
			vv.getModel().getRelaxer().relax();
		    } else {
			layoutNew = (Class<? extends Layout<IVertex, IEdge>>) SpringLayout.class;
			constructor = layoutNew.getConstructor(new Class[] { Graph.class });
			o = constructor.newInstance(constructorArgs);
			l = (Layout<IVertex, IEdge>) o;
			l.setInitializer(vv.getGraphLayout());
			((SpringLayout) l).setStretch(Constants.LAYOUT_STRETCH);
			((SpringLayout) l).setRepulsionRange(Constants.LAYOUT_REPULSION_RANGE);
			((SpringLayout) l).setForceMultiplier(Constants.LAYOUT_FORCE_MULTIPLIER);
			l.setSize(Constants.LAYOUT_SIZE);
			lt = new LayoutTransition<IVertex, IEdge>(vv, vv.getGraphLayout(), l);
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
		default:
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
}
