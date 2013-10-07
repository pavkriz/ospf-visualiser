package org.hkfree.ospf.gui.mappanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.apache.commons.collections15.Transformer;
import org.hkfree.ospf.gui.pathstreedialog.ShortestPathTreeDialog;
import org.hkfree.ospf.layout.JSLayout;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.EDGE_SHAPER;
import org.hkfree.ospf.model.Constants.LAYOUT;
import org.hkfree.ospf.model.lltd.Device;
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.map.EdgeOfSPT;
import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.impl.DeviceVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.model.ospf.ExternalLSA;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospf.StubLink;
import org.hkfree.ospf.setting.MapGraphComponentMode;
import org.hkfree.ospf.tools.MapModelShortestPathFinder;
import org.hkfree.ospf.tools.geo.GPSPointConverter;
import org.hkfree.ospf.tools.geo.GPSPointOsmTestConverter;
import org.hkfree.ospf.tools.ip.IpCalculator;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
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
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;
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
    private VisualizationViewer<IVertex, IEdge> vv = null;
    private Graph<IVertex, IEdge> graph = null;
    private Layout<IVertex, IEdge> layout = null;
    private OspfModalGraphMouse<IVertex, IEdge> graphMouse = null;
    private MapStyleTransformer styleTransformer = null;
    private GPSPointConverter gpsPointConverter = null;
    private ScalingControl scaler = null;
    private float zoomValue = 1.0f;
    private Forest<RouterVertex, EdgeOfSPT> shortestTreeGraph = new DelegateForest<RouterVertex, EdgeOfSPT>();
    private RouterVertex firstRouterVertexToMakeEdge = null;
    private RouterVertex firstShortestPathRV = null;
    private RouterVertex secondShortestPathRV = null;
    private boolean showIPv6 = false;


    /**
     * Konstruktor
     * @param mapDesignPanel
     */
    public MapGraphComponent(MapPanel mapDesignPanel, EDGE_SHAPER edgeShaper) {
	this.owner = mapDesignPanel;
	initializeGraph(edgeShaper);
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
    private void initializeGraph(EDGE_SHAPER edgeShaper) {
	graph = new SparseMultigraph<IVertex, IEdge>();
	layout = new FRLayout<IVertex, IEdge>(graph);
	layout.setSize(Constants.LAYOUT_SIZE);
	vv = new VisualizationViewer<IVertex, IEdge>(layout);
	((FRLayout) layout).setAttractionMultiplier(Constants.LAYOUT_ATTRACTION);
	((FRLayout) layout).setRepulsionMultiplier(Constants.LAYOUT_REPULSION);
	((FRLayout) layout).setMaxIterations(Constants.LAYOUT_FR_MAX_ITERATIONS);
	scaler = new CrossoverScalingControl();
	graphMouse = new OspfModalGraphMouse<IVertex, IEdge>(this);
	graphMouse.add(new MapGraphContextMenu(this));
	graphMouse.add(new MapGraphMouseClickPlugin(this));
	styleTransformer = new MapStyleTransformer(this);
	vv.setGraphMouse(graphMouse);
	vv.setBackground(Color.WHITE);
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
	// kriz: bitmapove pozadi mapy
	vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {
		
			@Override
			public boolean useTransform() {
				return true;
			}
			
			@Override
			public void paint(Graphics g) {
				ImageIcon mapIcon = null;
                String imageLocation = "x/skola/postgradual/subprojekty/wifilokace/mapa/staticmap-5018-1574-bright.png";
                try {
                    mapIcon =
                            new ImageIcon(imageLocation);
                } catch(Exception ex)
                {
                    System.err.println("Cant load background map "+ex);
                }
                 final ImageIcon icon = mapIcon;

                Graphics2D g2d = (Graphics2D)g;
                AffineTransform oldXform = g2d.getTransform();
                AffineTransform lat = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getTransform();
                AffineTransform vat = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
                AffineTransform at = new AffineTransform();
                //at.concatenate(g2d.getTransform());
                at.concatenate(vat);
                at.concatenate(lat);
                g2d.setTransform(at);
                g.drawImage(icon.getImage(), 0, 0,icon.getIconWidth(),icon.getIconHeight(),vv);
                g2d.setTransform(oldXform);
				
			}
		});
    }


    /**
     * Aktualizuje tvar spoje dle nastaveni
     * @param edgeShaper
     */
    public void setEdgeShaper(EDGE_SHAPER edgeShaper) {
	if (edgeShaper == EDGE_SHAPER.QUAD_CURVE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<IVertex, IEdge>());
	if (edgeShaper == EDGE_SHAPER.LINE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<IVertex, IEdge>());
	if (edgeShaper == EDGE_SHAPER.BENT_LINE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.BentLine<IVertex, IEdge>());
	if (edgeShaper == EDGE_SHAPER.CUBIC_CURVE)
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.CubicCurve<IVertex, IEdge>());
	vv.repaint();
    }


    /**
     * Vytvoří a zobrazí graf celé sítě
     */
    public void createWholeGraph() {
	for (RouterVertex rv : mapModel.getRouterVertices()) {
	    rv.setFullExpanded(true);
	    rv.setPermanentlyDisplayed(true);
	    graph.addVertex(rv);
	}
	for (LinkEdge le : mapModel.getLinkEdges()) {
	    le.setGraphComponent(this);
	    graph.addEdge(le, le.getVertex1(), le.getVertex2());
	}
	vv.repaint();
	scaler.scale(vv, zoomValue, vv.getCenter());
	gpsPointConverter = new GPSPointOsmTestConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(mapModel.getMinimumLatitude(), mapModel.getMaximumLatitude(),
		mapModel.getMinimumLongtitude(), mapModel.getMaximumLongtitude());
	
	layout.initialize();
	vv.getModel().getRelaxer().relax();

	//pro testovani JS layout
//	layouting(LAYOUT.LAYOUT_JS_START);
    }


    /**
     * Zobrazí graf pouze zadané části sítě
     * prakticky pouze odebere rotuery krome centralniho
     * @param centerVertex
     * @param depth
     */
    public void createGraphFromCenter(IVertex centerVertex, int depth) {
	for (IVertex v : mapModel.getVertices()) {
	    graph.removeVertex(v);
	}
	
	List<IVertex> previousStepVertexes = new ArrayList<IVertex>();
	List<IVertex> newAddedVertexes = new ArrayList<IVertex>();
	previousStepVertexes.add(centerVertex);
	newAddedVertexes.add(centerVertex);
	for (int i = 1; i <= depth; i++) {
	    for (IVertex rv : newAddedVertexes) {
		addNeighborsRouters((RouterVertex) rv);
	    }
	    newAddedVertexes.clear();
	    for (IVertex rv : graph.getVertices()) {
		if (!previousStepVertexes.contains(rv)) {
		    previousStepVertexes.add(rv);
		    newAddedVertexes.add(rv);
		}
	    }
	}
	graph.addVertex(centerVertex);
    }


    /**
     * Vytvoří graf na základě informací o pozicích z XML
     * @param rvPositions
     */
    public void createGraphByPositions(Map<RouterVertex, Point2D> rvPositions) {
	for (RouterVertex rv : mapModel.getRouterVertices()) {
	    if (rvPositions.containsKey(rv)) {
		graph.addVertex(rv);
		layout.setLocation(rv, rvPositions.get(rv));
		if (rv.isLocked()) {
		    lockVertexPosition(rv);
		}
	    }
	}
	for (LinkEdge le : mapModel.getLinkEdges()) {
	    if (graph.containsVertex(le.getVertex1()) && graph.containsVertex(le.getVertex2())) {
		le.setGraphComponent(this);
		graph.addEdge(le, le.getVertex1(), le.getVertex2());
	    }
	}
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		checkRouterVertexExpandability((RouterVertex) v);
	    }
	}
	vv.repaint();
	scaler.scale(vv, zoomValue, vv.getCenter());
	gpsPointConverter = new GPSPointOsmTestConverter(layout.getSize().getWidth(), layout.getSize().getHeight());
	gpsPointConverter.setGPSMaxsAndMins(mapModel.getMinimumLatitude(), mapModel.getMaximumLatitude(),
		mapModel.getMinimumLongtitude(), mapModel.getMaximumLongtitude());
    }


    /**
     * Pokud graf ještě neobsahuje zadaný vrchol, metoda ho do grafu přidá
     * @param vertex
     */
    public void addVertexToGraph(IVertex vertex) {
	if (!graph.containsVertex(vertex)) {
	    graph.addVertex(vertex);
	}
    }


    /**
     * Zobrazí sousedy zadaného vrcholu
     * @param routerVertex
     */
    public void addNeighborsRouters(RouterVertex routerVertex) {
	for (LinkEdge linkEdge : mapModel.getIncidentEdges(routerVertex)) {
	    linkEdge.setGraphComponent(this);
	    if (!routerVertex.equals(linkEdge.getVertex1())) {
		addVertexToGraph(linkEdge.getVertex1());
		if (linkEdge.getVertex1().isMultilink()) {
		    linkEdge.getVertex1().setPermanentlyDisplayed(true);
		    addNeighborsRouters(linkEdge.getVertex1());
		}
	    }
	    if (!routerVertex.equals(linkEdge.getVertex2())) {
		addVertexToGraph(linkEdge.getVertex2());
		if (linkEdge.getVertex2().isMultilink()) {
		    linkEdge.getVertex2().setPermanentlyDisplayed(true);
		    addNeighborsRouters(linkEdge.getVertex2());
		}
	    }
	    if (!graph.containsEdge(linkEdge)) {
		graph.addEdge(linkEdge, linkEdge.getVertex1(), linkEdge.getVertex2());
	    }
	}
	for (LinkEdge le : mapModel.getLinkEdges()) {
	    le.setGraphComponent(this);
	    if (!graph.containsEdge(le)) {
		if (graph.containsVertex(le.getVertex1()) && graph.containsVertex(le.getVertex2())) {
		    graph.addEdge(le, le.getVertex1(), le.getVertex2());
		    if (!le.getVertex1().isFullExpanded())
			checkRouterVertexExpandability(le.getVertex1());
		    if (!le.getVertex2().isFullExpanded())
			checkRouterVertexExpandability(le.getVertex2());
		}
	    }
	}
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		if (!((RouterVertex) v).isFullExpanded()) {
		    checkRouterVertexExpandability((RouterVertex) v);
		}
	    }
	}
    }


    /**
     * Zjistí, zda má zadaná router ještě nějaké nezobrazené sousedy
     * @param r
     */
    private void checkRouterVertexExpandability(RouterVertex r) {
	if (!r.isMultilink()) {
	    for (LinkEdge le : mapModel.getIncidentEdges(r)) {
		RouterVertex secondLeRouterVertex = null;
		if (le.getVertex1().equals(r)) {
		    secondLeRouterVertex = le.getVertex2();
		} else {
		    secondLeRouterVertex = le.getVertex1();
		}
		// kouknout jestli je router na standardnim spoji zobrazen
		if (!secondLeRouterVertex.isMultilink()) {
		    if (!graph.containsVertex(secondLeRouterVertex)) {
			r.setFullExpanded(false);
			return;
		    }
		} else { // kouknout jestli ma incidentni multilink zobrazeny vsechny vrcholy
		    for (LinkEdge mle : mapModel.getIncidentEdges(secondLeRouterVertex)) {
			if (!graph.containsVertex(mle.getVertex1())) {
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
	List<RouterVertex> vertexesToRemove = new ArrayList<RouterVertex>();
	for (IEdge e : graph.getIncidentEdges(routerVertex)) {
	    if (e instanceof LinkEdge) {
		LinkEdge le = (LinkEdge) e;
		RouterVertex secondRVertex = null;
		if (le.getVertex1().equals(routerVertex)) {
		    secondRVertex = le.getVertex2();
		} else {
		    secondRVertex = le.getVertex1();
		}
		if (!secondRVertex.isPermanentlyDisplayed()) {
		    for (IEdge se : graph.getIncidentEdges(secondRVertex)) {
			if (se instanceof LinkEdge) {
			    LinkEdge sle = (LinkEdge) se;
			    if (!edgesToRemove.contains(sle)) {
				edgesToRemove.add(sle);
			    }
			}
		    }
		    vertexesToRemove.add(secondRVertex);
		}
		if (secondRVertex.isMultilink()) {
		    removeMultilikNonPermanentlyDRVertexes(secondRVertex, routerVertex, edgesToRemove, vertexesToRemove);
		}
	    }
	}
	for (LinkEdge le : edgesToRemove) {
	    graph.removeEdge(le);
	}
	for (RouterVertex rv : vertexesToRemove) {
	    graph.removeVertex(rv);
	}
	for (IVertex rv : graph.getVertices()) {
	    if (rv instanceof RouterVertex) {
		checkRouterVertexExpandability((RouterVertex) rv);
	    }
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
	for (IEdge e : graph.getIncidentEdges(multilinkCenter)) {
	    if (e instanceof LinkEdge) {
		LinkEdge le = (LinkEdge) e;
		if (!le.getVertex1().isPermanentlyDisplayed()) {
		    for (IEdge se : graph.getIncidentEdges(le.getVertex1())) {
			if (se instanceof LinkEdge) {
			    LinkEdge sle = (LinkEdge) se;
			    if (!edgesToRemove.contains(sle)) {
				edgesToRemove.add(sle);
			    }
			}
		    }
		    if (!rvertexesToRemove.contains(le.getVertex1())) {
			rvertexesToRemove.add(le.getVertex1());
		    }
		} else
		    showedCnt++;
	    }
	}
	if (showedCnt < 2) {
	    rvertexesToRemove.add(multilinkCenter);
	    for (IEdge e : graph.getIncidentEdges(multilinkCenter)) {
		if (e instanceof LinkEdge) {
		    LinkEdge le = (LinkEdge) e;
		    if (le.getVertex1().equals(rvSender)) {
			if (!edgesToRemove.contains(le)) {
			    edgesToRemove.add(le);
			}
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
     * Zamkne pozice všech routeru v mapě
     */
    public void lockAllRouterVertices() {
	for (IVertex r : graph.getVertices()) {
	    if (r instanceof RouterVertex) {
		((RouterVertex) r).setLocked(true);
		layout.lock(r, true);
	    }
	}
    }


    public void unlockAllRouterVertices() {
	for (IVertex r : graph.getVertices()) {
	    if (r instanceof RouterVertex) {
		((RouterVertex) r).setLocked(false);
		layout.lock(r, false);
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
	if (le.getVertex1().isEnabled() && le.getVertex2().isEnabled()) {
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
		checkMultilinkToDisable(le.getVertex2());
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
     * @param rv
     */
    public void setVertexPositionByGPS(RouterVertex rv) {
	if (rv.getGpsLatitude() != 0 && rv.getGpsLongtitude() != 0) {
	    layout.setLocation(rv, gpsPointConverter.getPosition(rv.getGPSCoordinates()));
	    lockVertexPosition(rv);
	}
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
     * Nastaví všechny hrany jako neobarvené a všem vrcholům, že se nejedná o kořen stromu nejkratších cest
     */
    private void returnColorOfShortestPath() {
	for (IEdge e : mapModel.getEdges()) {
	    if (e instanceof LinkEdge) {
		LinkEdge le = (LinkEdge) e;
		le.setEdgeOfShortestPath(false);
		le.setEdgeOfFirstPath(false);
		le.setEdgeOfSecondPath(false);
	    }
	}
	for (IVertex v : mapModel.getVertices()) {
	    if (v instanceof RouterVertex) {
		RouterVertex rv = (RouterVertex) v;
		rv.setCenterOfShortestPathTree(false);
		rv.setFirstRVOfTwoRVShortestPath(false);
		rv.setSecondRVOfTwoRVShortestPath(false);
	    }
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
	if (firstRouterVertexToMakeEdge != null) {
	    firstRouterVertexToMakeEdge.setPartOfNewAddedEdge(false);
	    firstRouterVertexToMakeEdge = null;
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
     * Vytvoří nový IVertex v modelu, přidá ho do grafu a přesune ho na zvolenou pozici
     */
    public void addNewVertex(Point2D rvPoint) {
	AddRouterVertexDialog dialog = new AddRouterVertexDialog(mapModel.getRouterVertices());
	if (dialog.successfulyConfirmed()) {
	    RouterVertex rv = new RouterVertex();
	    rv.setName(dialog.getEnteredName());
	    rv.setInfo(dialog.getEnteredName());
	    mapModel.getVertices().add(rv);
	    graph.addVertex(rv);
	    lockVertexPosition(rv);
	    rv.setFullExpanded(true);
	    rv.setPermanentlyDisplayed(true);
	    rv.setExtraAddedVertex(true);
	    rvPoint = transformClickedPointToGraphPoint(rvPoint);
	    layout.setLocation(rv, rvPoint);
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
	if (firstRouterVertexToMakeEdge == null) {
	    firstRouterVertexToMakeEdge = rv;
	    rv.setPartOfNewAddedEdge(true);
	} else {
	    if (!(firstRouterVertexToMakeEdge.isMultilink() && rv.isMultilink()) && !firstRouterVertexToMakeEdge.equals(rv)) {
		AddLinkEdgeDialog dialog = new AddLinkEdgeDialog(mapModel.getLinkEdges(), firstRouterVertexToMakeEdge, rv);
		if (dialog.successfulyConfirmed()) {
		    firstRouterVertexToMakeEdge.setPartOfNewAddedEdge(false);
		    LinkEdge le = mapModel.addLinkEdge(dialog.getRouterV1(), dialog.getRouterV2(), dialog.getCost1(),
			    dialog.getCost2(), dialog.getEnteredName());
		    le.setExtraAddedEdge(true);
		    le.setGraphComponent(this);
		    graph.addEdge(le, le.getVertex1(), le.getVertex2());
		    firstRouterVertexToMakeEdge = null;
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
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		RouterVertex rv = (RouterVertex) v;
		positions.put(rv, new Point2D.Double(((AbstractLayout<IVertex, IEdge>) layout).getX(rv),
			((AbstractLayout<IVertex, IEdge>) layout).getY(rv)));
	    }
	}
	return positions;
    }


    /**
     * Odstraní externě přidaný routerVertex z grafu a mapModelu
     * @param routerVertex
     */
    public void removeExtraAddedRVertex(RouterVertex routerVertex) {
	if (routerVertex.isExtraAddedVertex()) {
	    for (LinkEdge le : mapModel.getIncidentEdges(routerVertex)) {
		removeEdge(le);
	    }
	    removeVertex(routerVertex);
	}
    }


    /**
     * Odstraní externě přidanou linkEdge z grafu a mapModelu
     * @param linkEdge
     */
    public void removeExtraAddedLEdge(LinkEdge linkEdge) {
	if (linkEdge.isExtraAddedEdge()) {
	    removeEdge(linkEdge);
	}
    }


    /**
     * Prida vrchol do grafu a MapModelu
     * @param vertex
     */
    public void addVertex(IVertex vertex) {
	graph.addVertex(vertex);
	mapModel.getVertices().add(vertex);
    }


    /**
     * Prida hranu do grafu a MapModelu
     * @param edge
     */
    public void addEdge(IEdge edge) {
	graph.addEdge(edge, edge.getVertex1(), edge.getVertex2());
	mapModel.getEdges().add(edge);
    }


    /**
     * Odebere vrchol z grafu a MapModelu
     * @param vertex
     */
    public void removeVertex(IVertex vertex) {
	graph.removeVertex(vertex);
	mapModel.getVertices().remove(vertex);
    }


    /**
     * Odebere hranu z grafu a MapModelu
     * @param edge
     */
    public void removeEdge(IEdge edge) {
	graph.removeEdge(edge);
	mapModel.getEdges().remove(edge);
    }


    /**
     * Nalezne a oznaci routery obsahujici v nazvu text, take prohledava stuby a external LSA
     * text muze byt oddeleny | pro vyhledavani vice retezcu
     * @param text filtr pro nazev routeru
     */
    public void findByNameOrIP(String text) {
	boolean b;
	Router r = null;
	String[] foundedNames = text.split("\\|");
	for (RouterVertex rv : mapModel.getRouterVertices()) {
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
			|| rv.getInfo().toUpperCase().contains(name.toUpperCase())) {
		    b = true;
		}
		// pokud je model null, hledat pouze v mapModelu
		if (owner.getMapDesignWinManager().getOspfModel() != null) {
		    r = owner.getMapDesignWinManager().getOspfModel().getRouterByIp(rv.getInfo());
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


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void layouting(LAYOUT mode) {
	//nejprve se zastavi aktualni rozmistovani
	vv.getModel().getRelaxer().stop();
	try {
	    Object[] constructorArgs = { graph };
	    Class<? extends Layout<IVertex, IEdge>> layoutNew = null;
	    Constructor<? extends Layout<IVertex, IEdge>> constructor = null;
	    Object o = null;
	    Layout<IVertex, IEdge> l = null;
	    LayoutTransition<IVertex, IEdge> lt = null;
	    switch (mode) {
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
		    new Animator(lt).start();
		    vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
		    vv.repaint();
		    break;
		case LAYOUT_SPRING_START:
		    if (layout instanceof SpringLayout<?, ?>) {
			vv.getModel().getRelaxer().relax();
		    } else {
			layoutNew = (Class<? extends Layout<IVertex, IEdge>>) SpringLayout.class;
			constructor = layoutNew.getConstructor(new Class[] { Graph.class });
			constructor = layoutNew.getConstructor(new Class[] { Graph.class, Transformer.class });
			Transformer<IEdge, Integer> weightTransformer = new Transformer<IEdge,Integer>() {
				 public Integer transform(IEdge link) {
					 return link.getWeight();
					 //return 30;
				 }
			};
		    //Object[] constructorArgs2 = { graph, weightTransformer };
			o = constructor.newInstance(graph, weightTransformer);
			//o = constructor.newInstance(constructorArgs);
			l = (Layout<IVertex, IEdge>) o;
			l.setInitializer(vv.getGraphLayout());
			((SpringLayout) l).setStretch(Constants.LAYOUT_STRETCH);
			((SpringLayout) l).setRepulsionRange(Constants.LAYOUT_REPULSION_RANGE);
			((SpringLayout) l).setForceMultiplier(Constants.LAYOUT_FORCE_MULTIPLIER);
			l.setSize(Constants.LAYOUT_SIZE);
			lt = new LayoutTransition<IVertex, IEdge>(vv, vv.getGraphLayout(), l);
			new Animator(lt).start();
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
		case LAYOUT_JS_START:
		    layoutNew = (Class<? extends Layout<IVertex, IEdge>>) JSLayout.class;
		    constructor = layoutNew.getConstructor(new Class[] { Graph.class });
		    o = constructor.newInstance(constructorArgs);
		    layout = (Layout<IVertex, IEdge>) o;
		    layout.setInitializer(vv.getGraphLayout());
		    layout.setSize(Constants.LAYOUT_SIZE);
		    lt = new LayoutTransition<IVertex, IEdge>(vv, vv.getGraphLayout(), layout);
		    new Animator(lt).start();
		    vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
		    vv.repaint();
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
	    for (IVertex v : mapModel.getVertices()) {
		if (v instanceof RouterVertex) {
		    ((RouterVertex) v).setVisible(true);
		}
	    }
	} else {
	    for (IVertex v : mapModel.getVertices()) {
		if (v instanceof RouterVertex) {
		    RouterVertex rv = (RouterVertex) v;
		    boolean bi = false;
		    for (IEdge e : mapModel.getEdges()) {
			if (e instanceof LinkEdge) {
			    LinkEdge le = (LinkEdge) e;
			    if ((rv == le.getVertex1() || rv == le.getVertex2()) && le.isIPv4()) {
				bi = true;
			    }
			}
		    }
		    rv.setVisible(bi);
		}
	    }
	}
	vv.repaint();
    }


    /**
     * Zobrazí nebo skryje LLTD model u routeru
     * @param model
     */
    public void showOrHideLltdModel(Router router, LLTDModel model) {
	// nastaveni viditelnosti/neviditelnosti lltd modelu
	model.setShow(!model.isShow());
	// nastaveni viditelnosti/neviditelnosti u prvku modelu
	for (Device d : model.getDevices()) {
	    DeviceVertex dv = findLltdVertex(d.getSource());
	    if (dv != null) {
		dv.setVisible(model.isShow());
	    }
	}
	for (String ip : model.getTraceroute()) {
	    DeviceVertex dv = findLltdVertex(ip);
	    if (dv != null) {
		dv.setVisible(model.isShow());
	    }
	}
    }


    /**
     * Vraci vrchol LLTD zarizeni v grafu dle mac adresy (v model znaceno "source")
     * @param mac
     * @return
     */
    public DeviceVertex findLltdVertex(String mac) {
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof DeviceVertex) {
		if (((DeviceVertex) v).getMac().equals(mac)) {
		    return (DeviceVertex) v;
		}
	    }
	}
	return null;
    }


    /**
     * Vraci vrcholu Router v grafu dle jeho id
     * @param idRouter
     * @return
     */
    public RouterVertex findRouterVertex(String idRouter) {
	for (IVertex v : graph.getVertices()) {
	    if (v instanceof RouterVertex) {
		if (((RouterVertex) v).getName().equals(idRouter)) {
		    return (RouterVertex) v;
		}
	    }
	}
	return null;
    }


    /**
     * Vrací LLTD modely pro router dle jeho názvu (názvu v mapě)
     * @param routerName
     * @return
     */
    public Set<LLTDModel> getLLTDmodels(String routerName) {
	return owner.getMapDesignWinManager().getOspfModel().getRouterByName(routerName).getLltdModels();
    }
}
