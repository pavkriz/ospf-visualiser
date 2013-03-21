package org.hkfree.ospf.gui.mappanel;

import java.awt.geom.Point2D;
import java.util.Map;

import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;

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
		if (rv.getDescription().equals(centerRouter.getId())) {
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
     * Vrací předka - MapPanel
     */
    public MapPanel getOwner() {
	return owner;
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
	    }
	}
    }
}
