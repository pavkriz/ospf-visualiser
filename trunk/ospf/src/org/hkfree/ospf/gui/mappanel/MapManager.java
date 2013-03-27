package org.hkfree.ospf.gui.mappanel;

import java.awt.geom.Point2D;
import java.util.Map;

import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.lltd.Device;
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.lltd.Relation;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.impl.DeviceVertex;
import org.hkfree.ospf.model.map.impl.RelationEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.tools.ip.IpCalculator;

/**
 * Třída představující manažer okna návrhu sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapManager {

    private MapPanel owner = null;
    private MapModel mapModel;
    private OspfModel ospfModel = null;
    private MapGraphComponent graphComponent = null;
    private boolean processWholeModel = true;
    private Router centerRouter = null;
    private int neighboursDepth = 0;
    private Map<RouterVertex, Point2D> routerVertexPositions = null;


    /**
     * Konstruktor
     * @param owner
     */
    public MapManager(MapPanel owner) {
	this.owner = owner;
    }


    /**
     * Vrací předka - MapPanel
     */
    public MapPanel getOwner() {
	return owner;
    }


    /**
     * Nastaví OspfModel
     * @param ospfModel
     */
    public void setOspfModel(OspfModel ospfModel) {
	this.ospfModel = ospfModel;
    }


    /**
     * Nastaví MapModel
     * @param mapModel
     */
    public void setMapModel(MapModel mapModel) {
	this.mapModel = mapModel;
    }


    /**
     * Předá pozice routerů na plátně
     * @param routerVertexPositions
     */
    public void setRouterVertexPositions(Map<RouterVertex, Point2D> routerVertexPositions) {
	this.routerVertexPositions = routerVertexPositions;
    }


    /**
     * Nastaví komponentu vykreslení grafu
     * @param component
     */
    public void setGraphComponent(MapGraphComponent component) {
	this.graphComponent = component;
    }


    /**
     * Vrací komponentu vykreslení grafu
     * @return component
     */
    public MapGraphComponent getGraphComponent() {
	return this.graphComponent;
    }


    /**
     * Nastaví parametry otevření okna
     * @param processWholeModel
     * @param centerRouter
     * @param neighboursDepth
     */
    public void setLoadSettings(boolean processWholeModel, Router centerRouter, int neighboursDepth) {
	this.processWholeModel = processWholeModel;
	this.centerRouter = centerRouter;
	this.neighboursDepth = neighboursDepth;
    }


    /**
     * Načte MapModel
     */
    public void loadMapModel() {
	if (mapModel == null) {
	    mapModel = ospfModel.getConvertedWholeModelToMapaModel();
	}
	if (mapModel.hasMoreRouterWithGPSPositions()) {
	    // ((MapDesignWin) owner).getMDWActionListener().getActionGPS().setEnabled(true);
	    // ((MapDesignWin) owner).getMDWActionListener().getActionGPSAll().setEnabled(true);
	}
	graphComponent.setMapModel(mapModel);
	if (!processWholeModel && centerRouter != null) {
	    for (RouterVertex rv : mapModel.getRouterVertices()) {
		if (rv.getInfo().equals(centerRouter.getId())) {
		    graphComponent.createGraphFromCenter(rv, neighboursDepth);
		    break;
		}
	    }
	} else {
	    graphComponent.createWholeGraph();
	}
    }


    /**
     * Zpracuje a zobrazí mapModel načtený z xml
     */
    public void processMapModelFromXml() {
	if (mapModel.hasMoreRouterWithGPSPositions()) {
	    // ((MapDesignWin) owner).getMDWActionListener().getActionGPS().setEnabled(true);
	    // ((MapDesignWin) owner).getMDWActionListener().getActionGPSAll().setEnabled(true);
	}
	graphComponent.setMapModel(mapModel);
	graphComponent.createGraphByPositions(routerVertexPositions);
    }


    /**
     * Nalezne a zvyrazni routery obsahujici filtr
     * @param text
     */
    public void searchInMap(String text) {
	graphComponent.findByNameOrIP(text);
    }


    /**
     * Vrací ospf model
     * @return
     */
    public OspfModel getOspfModel() {
	return this.ospfModel;
    }


    /**
     * Vrací map model
     * @return
     */
    public MapModel getMapModel() {
	return this.mapModel;
    }


    /**
     * Nastaví mod komponenty s grafem
     */
    public void setMode(MODE mode) {
	switch (mode) {
	    case SHOW_NEIGHBORS:
		graphComponent.setShowNeighboursMode();
		break;
	    case COST_CHANGING:
		graphComponent.setCostChangingMode();
		break;
	    case PICKING:
		graphComponent.setPickingMode();
		break;
	    case TRANSFORMING:
		graphComponent.setTransformingMode();
		break;
	    case ZOOM:
		graphComponent.setZoomMode();
		break;
	    case LOCK_VERTEX:
		graphComponent.setLockVertexMode();
		break;
	    case LOCK_ALL:
		graphComponent.lockAllRouterVertices();
		break;
	    case SHORTEST_PATH:
		graphComponent.setShowShortestPathMode();
		break;
	    case GPS:
		graphComponent.setGPSPositioningMode();
		break;
	    case GPS_ALL:
		graphComponent.setAllVerticesToGPSPosition();
		break;
	    case ADD_VERTEXES:
		graphComponent.setAddRVertexMode();
		break;
	    case ADD_EDGES:
		graphComponent.setAddLEdgeMode();
		break;
	    case ASYMETRIC_LINKS:
		graphComponent.setAsymetricLinkMode();
		break;
	    case SHORTEST_PATH_TWO_ROUTERS:
		graphComponent.setTwoRoutersShortesPathMode();
		break;
	    case IPV6:
		graphComponent.setShowIPv6(!graphComponent.isShowIPv6());
		break;
	    default:
		break;
	}
    }


    /**
     * Zobrazí LLTD dialog
     * @param routerName název router v grafu
     * @param lltdName název lltd modelu k zobrazení
     */
    public void showOrHideLLTDDialog(String routerName, String lltdName) {
	Router r = ospfModel.getRouterByName(routerName);
	for (LLTDModel m : r.getLltdModels()) {
	    if (m.getPublicIP().equals(lltdName)) {
		getGraphComponent().showOrHideLltdModel(r, m);
		break;
	    }
	}
    }


    public void actualizeLltdVerticies() {
	// vymazani poslednich lltd prvku v grafu
	for (DeviceVertex dv : mapModel.getDeviceVertices()) {
	    graphComponent.removeVertex(dv);
	}
	for (RelationEdge re : mapModel.getRelationEdges()) {
	    graphComponent.removeEdge(re);
	}
	// prochazeni vsech routeru ospf modelu
	for (Router router : ospfModel.getRouters()) {
	    // nalezeni zakladniho vrcholu routeru, ktery propaguje lltd model
	    RouterVertex rvBase = graphComponent.findRouterVertex(router.getName());
	    // prochazeni lltd modelu daneho routeru
	    for (LLTDModel lltdModel : router.getLltdModels()) {
		IVertex vLast = null;
		// pridani routeru a spoju z traceroutu dokud se nenarazi na ten, ktery je propagovan
		for (int i = 0; i < lltdModel.getTraceroute().size(); i++) {
		    String ip = lltdModel.getTraceroute().get(i);
		    DeviceVertex dv = new DeviceVertex();
		    dv.setIpv4(ip);
		    dv.setIpv6(ip);
		    dv.setMachineName(ip);
		    dv.setMac(ip);
		    dv.setVisible(lltdModel.isShow());
		    graphComponent.addVertex(dv);
		    RelationEdge re = new RelationEdge();
		    re.setVertex1(i == 0 ? rvBase : graphComponent.findRouterVertex(lltdModel.getTraceroute().get(i - 1)));
		    re.setVertex2(dv);
		    graphComponent.addEdge(re);
		    if (IpCalculator.containsRouterSubnet(router, ip)) {
			vLast = dv;
			break;
		    }
		}
		for (Device d : lltdModel.getDevices()) {
		    DeviceVertex dv = new DeviceVertex();
		    dv.setMac(d.getSource());
		    dv.setIpv4(d.getIpv4());
		    dv.setIpv6(d.getIpv6());
		    dv.setMachineName(d.getMachineName());
		    dv.setVisible(lltdModel.isShow());
		    graphComponent.addVertex(dv);
		}
		for (Relation r : lltdModel.getRelations()) {
		    DeviceVertex dv1 = graphComponent.findLltdVertex(r.getFrom().getSource());
		    if (dv1 == null) {
			dv1 = new DeviceVertex();
			dv1.setMac(r.getFrom().getSource());
			dv1.setIpv4(r.getFrom().getIpv4());
			dv1.setIpv6(r.getFrom().getIpv6());
			dv1.setMachineName(r.getFrom().getMachineName());
			dv1.setVisible(lltdModel.isShow());
			graphComponent.addVertex(dv1);
		    }
		    DeviceVertex dv2 = graphComponent.findLltdVertex(r.getTo().getSource());
		    if (dv2 == null) {
			dv2 = new DeviceVertex();
			dv2.setMac(r.getTo().getSource());
			dv2.setIpv4(r.getTo().getIpv4());
			dv2.setIpv6(r.getTo().getIpv6());
			dv2.setMachineName(r.getTo().getMachineName());
			dv2.setVisible(lltdModel.isShow());
			graphComponent.addVertex(dv2);
		    }
		    RelationEdge re = new RelationEdge();
		    re.setVertex1(dv1);
		    re.setVertex2(dv2);
		    re.setMedium(r.getMedium());
		    graphComponent.addEdge(re);
		}
		// pridani spoje mezi routerem a poslednim prvkem v lltd modelu
		RelationEdge reBetween = new RelationEdge();
		reBetween.setVertex1(graphComponent.findLltdVertex(lltdModel.getRelations()
			.get(lltdModel.getRelations().size() - 1).getFrom().getSource()));
		reBetween.setVertex2(vLast);
		graphComponent.addEdge(reBetween);
	    }
	}
	graphComponent.getVisualizationComponent().repaint();
    }
}
