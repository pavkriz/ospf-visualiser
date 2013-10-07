package org.hkfree.ospf.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hkfree.ospf.model.IMapModel;
import org.hkfree.ospf.model.map.EdgeOfSPT;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;

import edu.uci.ics.jung.graph.Graph;

/**
 * Třída sloužící k hledání nejkratších cest pomocí Dijkstrova algoritmu
 * @author Jakub Menzel
 */
public class MapModelShortestPathFinder {

    private IMapModel mapModel = null;
    private RouterVertex startVertex = null;
    private Map<RouterVertex, RouterCostPredecessorBox> routerDistances = new HashMap<RouterVertex, RouterCostPredecessorBox>();


    /**
     * Konstruktor
     */
    public MapModelShortestPathFinder() {}


    /**
     * Nastaví mapModel, ve kterém je cesta počítána
     * @param mapModel
     */
    public void setMapModel(IMapModel mapModel) {
	this.mapModel = mapModel;
    }


    /**
     * Nastaví kořenový vrchol pro hledání stromu nejkratších cest
     * @param startVertex the startVertex to set
     */
    public void setStartVertex(RouterVertex startVertex) {
	this.startVertex = startVertex;
    }


    /**
     * Vyhledá nejkratší cestu
     * @param startVertex
     */
    public void findShortesPathTree(RouterVertex startVertex) {
	this.startVertex = startVertex;
	List<RouterVertex> waitingVertices = new ArrayList<RouterVertex>();
	waitingVertices.add(startVertex);
	List<RouterVertex> closedVertices = new ArrayList<RouterVertex>();
	for (IVertex v : mapModel.getVertices()) {
	    if (v instanceof RouterVertex) {
		RouterVertex rv = (RouterVertex) v;
		if (!rv.isMultilink() && rv.isEnabled())
		    routerDistances.put(rv, new RouterCostPredecessorBox(Integer.MAX_VALUE));
	    }
	}
	routerDistances.get(startVertex).setCost(0);
	while (!waitingVertices.isEmpty()) {
	    int minCost = Integer.MAX_VALUE;
	    RouterVertex actRVertex = null;
	    for (RouterVertex rv : waitingVertices) {
		if (routerDistances.get(rv).getCost() < minCost) {
		    minCost = routerDistances.get(rv).getCost();
		    actRVertex = rv;
		}
	    }
	    waitingVertices.remove(actRVertex);
	    closedVertices.add(actRVertex);
	    int actCost = routerDistances.get(actRVertex).getCost();
	    for (NeighbourCostAndLink n : mapModel.getNeighboursWithCosts(actRVertex)) {
		if (!closedVertices.contains(n.getNeighbour())) {
		    if (actCost + n.getCost() < routerDistances.get(n.getNeighbour()).getCost()) {
			routerDistances.get(n.getNeighbour()).setCost(actCost + n.getCost());
			routerDistances.get(n.getNeighbour()).setPredecessor(actRVertex);
			routerDistances.get(n.getNeighbour()).setLinkEdge(n.getLinkEdge());
			if (!waitingVertices.contains(n.getNeighbour())) {
			    waitingVertices.add(n.getNeighbour());
			}
		    }
		}
	    }
	}
	for (Map.Entry<RouterVertex, RouterCostPredecessorBox> rcpb : routerDistances.entrySet()) {
	    if (!rcpb.getKey().equals(startVertex)) {
		if (rcpb.getValue().getLinkEdge() != null) {
		    rcpb.getValue().getLinkEdge().setEdgeOfShortestPath(true);
		    if (rcpb.getValue().getLinkEdge().isEdgeOfMultilink()) {
			mapModel.getMultilinkEdge(rcpb.getKey(), (RouterVertex) rcpb.getValue().getLinkEdge().getVertex2())
				.setEdgeOfShortestPath(true);
		    }
		} else {
		    // System.out.println(rcpb.getKey().getName());
		}
	    }
	}
    }


    /**
     * Z nalezených nejkratších cest vytvoří stromový graf
     * @param treeGraph
     */
    public void createShortestPathTreeGraph(Graph<RouterVertex, EdgeOfSPT> treeGraph) {
	List<EdgeOfSPT> toRemoveE = new ArrayList<EdgeOfSPT>();
	for (EdgeOfSPT e : treeGraph.getEdges()) {
	    toRemoveE.add(e);
	}
	for (EdgeOfSPT e : toRemoveE) {
	    treeGraph.removeEdge(e);
	}
	List<RouterVertex> toRemoveV = new ArrayList<RouterVertex>();
	for (RouterVertex v : treeGraph.getVertices()) {
	    toRemoveV.add(v);
	}
	for (RouterVertex v : toRemoveV) {
	    treeGraph.removeVertex(v);
	}
	treeGraph.addVertex(startVertex);
	for (Map.Entry<RouterVertex, RouterCostPredecessorBox> rcpb : routerDistances.entrySet()) {
	    if (!rcpb.getKey().equals(startVertex) && rcpb.getValue().getPredecessor() != null) {
		if (!treeGraph.containsVertex(rcpb.getKey())) {
		    treeGraph.addVertex(rcpb.getKey());
		}
		if (!treeGraph.containsVertex(rcpb.getValue().getPredecessor())) {
		    treeGraph.addVertex(rcpb.getValue().getPredecessor());
		}
		treeGraph.addEdge(new EdgeOfSPT(rcpb.getValue().getCost(), rcpb.getValue().getLinkEdge().getLinkIDv4()),
			rcpb
				.getValue().getPredecessor(), rcpb.getKey());
	    }
	}
    }


    /**
     * Vrací nejkratší cestu kořene stromu do destinationVertex v mapModelu s hranami označenýmy jako nejkratší cesty stromu
     * @param destinationVertex
     * @return les
     */
    public List<LinkEdge> getShortestPathBetweenRouters(RouterVertex destinationVertex) {
	List<LinkEdge> pathEdges = new ArrayList<LinkEdge>();
	if (routerDistances.get(destinationVertex).getCost() != Integer.MAX_VALUE) {
	    backwardMarkPathEdges(destinationVertex, pathEdges);
	}
	return pathEdges;
    }


    /**
     * dokud nedojde ke startovnímu vrcholu, prochází strom nejkratších cest a přidává do seznamu hran na cestě ty
     * @param routerVertex
     * @param liste
     */
    public void backwardMarkPathEdges(RouterVertex routerVertex, List<LinkEdge> liste) {
	if (routerVertex.equals(startVertex)) { // pokud se došlo ke kořeni, zastavit
	    return;
	}
	liste.add(routerDistances.get(routerVertex).getLinkEdge()); // přidání hrany do seznamu
	if (routerDistances.get(routerVertex).getLinkEdge().isEdgeOfMultilink()) { // pokud je to multilink, přidat i druhou
										   // hranu reprezentující multilink
	    liste.add(mapModel.getMultilinkEdge(routerVertex, (RouterVertex) routerDistances.get(routerVertex).getLinkEdge()
		    .getVertex2()));
	}
	backwardMarkPathEdges(routerDistances.get(routerVertex).getPredecessor(), liste);
    }
}
