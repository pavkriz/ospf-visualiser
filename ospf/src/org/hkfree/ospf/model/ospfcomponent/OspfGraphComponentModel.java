package org.hkfree.ospf.model.ospfcomponent;

import java.util.ArrayList;
import java.util.List;

import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;

/**
 * Třída představující model pro zjištění počtu komponent sítě při simulaci výpadků
 * @author Jakub Menzel
 */
public class OspfGraphComponentModel {

    public static final int FRESH = 0;
    public static final int OPEN = 1;
    public static final int CLOSED = 2;
    private OspfModel ospfModel = null;
    private List<OspfGraphVertex> vertices = new ArrayList<OspfGraphVertex>();
    private List<OspfGraphEdge> edges = new ArrayList<OspfGraphEdge>();


    /**
     * Konstruktor
     */
    public OspfGraphComponentModel() {}


    /**
     * Vrací ospfModel
     * @return model
     */
    public OspfModel getOspfModel() {
	return ospfModel;
    }


    /**
     * Nastavuje ospfModel
     * @param ospfModel
     */
    public void setOspfModel(OspfModel ospfModel) {
	this.ospfModel = ospfModel;
    }


    /**
     * Vrací vrcholy
     * @return vertices
     */
    public List<OspfGraphVertex> getVertices() {
	return vertices;
    }


    /**
     * Nastavuje vrcholy
     * @param vertices
     */
    public void setVertices(List<OspfGraphVertex> vertices) {
	this.vertices = vertices;
    }


    /**
     * Vrací hrany
     * @return edges
     */
    public List<OspfGraphEdge> getEdges() {
	return edges;
    }


    /**
     * Nastavuje hrany
     * @param edges
     */
    public void setEdges(List<OspfGraphEdge> edges) {
	this.edges = edges;
    }


    /**
     * Vytvoří model grafu z ospfModelu
     */
    public void createGraphModel() {
	for (Router r : ospfModel.getRouters()) {
	    vertices.add(new OspfGraphVertex(r));
	}
	for (Link ol : ospfModel.getLinks()) {
	    if (ol.getRoutersCount() == 2) {
		edges.add(new OspfGraphEdge(ol, getVertex(ol.getOspfLinkData().get(0).getRouter()), getVertex(ol
			.getOspfLinkData().get(1).getRouter())));
	    } else { // multilink - vytvořit úplný podgraf - každý s každým
		for (OspfLinkData old1 : ol.getOspfLinkData()) {
		    for (OspfLinkData old2 : ol.getOspfLinkData()) {
			if (!old1.equals(old2)) {
			    edges.add(new OspfGraphEdge(ol, getVertex(old1.getRouter()), getVertex(old2.getRouter())));
			}
		    }
		}
	    }
	}
    }


    /**
     * Vrací vertex, který zastupuje zadaný router
     * @param router
     * @return vertex
     */
    public OspfGraphVertex getVertex(Router router) {
	for (OspfGraphVertex ogv : vertices) {
	    if (ogv.getRouter().equals(router)) {
		return ogv;
	    }
	}
	return null;
    }


    /**
     * Každému vrcholu spočítá počet komponent, na který se graf rozpadne při neúčasti daného vrcholu
     */
    public void computeVertexImportanceForConnectedGraph() {
	for (OspfGraphVertex ogv : vertices) {
	    int graphComponentCount = 0;
	    ogv.disable();
	    disableIncidentEdges(ogv);
	    refreshAllVertices();
	    for (OspfGraphVertex vertex : vertices) {
		if (vertex.getState() == OspfGraphComponentModel.FRESH && !vertex.equals(ogv)) {
		    graphComponentCount++;
		    makeDepthFirstSearchOpeningAndClosing(vertex);
		}
	    }
	    ogv.setGraphComponentCountAfterDisable(graphComponentCount);
	    ogv.enable();
	    enableIncidentEdges(ogv);
	}
    }


    /**
     * Každé hraně spočítá počet komponent, na který se graf rozpadne při neúčasti dané hrany
     */
    public void computeEdgeImportanceForConnectedGraph() {
	for (OspfGraphEdge oge : edges) {
	    if (oge.getGraphComponentCountAfterDisable() <= 1) { // kdyby to byla hrana multispoje, tak už to může mít
								 // spočítaný
		int graphComponentCount = 0;
		oge.disable();
		for (OspfGraphEdge mlde : edges) { // celý multispoj deaktivovat
		    if (mlde.getOspfLink().equals(oge.getOspfLink())) {
			mlde.disable();
		    }
		}
		refreshAllVertices();
		for (OspfGraphVertex vertex : vertices) {
		    if (vertex.getState() == OspfGraphComponentModel.FRESH) {
			graphComponentCount++;
			makeDepthFirstSearchOpeningAndClosing(vertex);
		    }
		}
		oge.setGraphComponentCountAfterDisable(graphComponentCount);
		for (OspfGraphEdge mlde : edges) { // celý multispoj aktivovat
		    if (mlde.getOspfLink().equals(oge.getOspfLink())) {
			mlde.enable();
			mlde.setGraphComponentCountAfterDisable(graphComponentCount);
		    }
		}
		oge.enable();
	    }
	}
    }


    /**
     * Hledáním do hloubky projde (otevře a zavře) všechny propojené vrcholy
     * @param vertex
     */
    public void makeDepthFirstSearchOpeningAndClosing(OspfGraphVertex vertex) {
	vertex.setOpened();
	for (OspfGraphVertex ogv : getVertexEnabledNeighbours(vertex)) {
	    if (ogv.getState() == OspfGraphComponentModel.FRESH) {
		makeDepthFirstSearchOpeningAndClosing(ogv);
	    }
	}
	vertex.setClosed();
    }


    /**
     * Vrací aktivní sousedy zadaného vrcholu
     * @param vertex
     * @return vertices
     */
    public List<OspfGraphVertex> getVertexEnabledNeighbours(OspfGraphVertex vertex) {
	List<OspfGraphVertex> enabledNeighbours = new ArrayList<OspfGraphVertex>();
	for (OspfGraphEdge oge : edges) {
	    if (oge.isEnabled() && oge.cointainsEnabledVertex(vertex)) {
		enabledNeighbours.add(oge.getNeighbour(vertex));
	    }
	}
	return enabledNeighbours;
    }


    /**
     * Nastaví hrany incidentní s vrcholem jako neaktivní - nezúčastněné v grafu
     */
    public void disableIncidentEdges(OspfGraphVertex ogv) {
	for (OspfGraphEdge oge : edges) {
	    if (oge.cointainsVertex(ogv)) {
		ogv.disable();
	    }
	}
    }


    /**
     * Nastaví hrany incidentní s vrcholem jako aktivní - účastněné v grafu
     */
    public void enableIncidentEdges(OspfGraphVertex ogv) {
	for (OspfGraphEdge oge : edges) {
	    if (oge.cointainsVertex(ogv)) {
		ogv.enable();
	    }
	}
    }


    /**
     * Nastaví všechny vrcholy jako ještě neotevřené - pro nový průchod
     */
    public void refreshAllVertices() {
	for (OspfGraphVertex ogv : vertices) {
	    ogv.setFresh();
	}
    }


    /**
     * Vrací seznam routerů a počet komponent, na který jejich výpadek rozpadne síť takových, kde je počet komponent větší
     * než 1
     * @return importances
     */
    public List<RouterImportance> getRouterImportances() {
	List<RouterImportance> routerImportances = new ArrayList<RouterImportance>();
	for (OspfGraphVertex v : vertices) {
	    if (v.getGraphComponentCountAfterDisable() > 1) {
		routerImportances.add(new RouterImportance(v.getRouter(), v.getGraphComponentCountAfterDisable()));
	    }
	}
	return routerImportances;
    }


    /**
     * Vrací seznam spojů a počet komponent, na který jejich výpadek rozpadne síť takových, kde je počet komponent větší než
     * 1
     * @return importances
     */
    public List<LinkImportance> getLinkImportances() {
	List<Link> addedOLinks = new ArrayList<Link>();
	List<LinkImportance> linkImportances = new ArrayList<LinkImportance>();
	for (OspfGraphEdge e : edges) {
	    if (e.getGraphComponentCountAfterDisable() > 1) {
		if (!addedOLinks.contains(e.getOspfLink())) {
		    linkImportances.add(new LinkImportance(e.getOspfLink(), e.getGraphComponentCountAfterDisable()));
		    addedOLinks.add(e.getOspfLink());
		}
	    }
	}
	return linkImportances;
    }
}
