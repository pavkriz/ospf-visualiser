package org.hkfree.ospf.model.map;

import java.util.ArrayList;
import java.util.List;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.IMapModel;
import org.hkfree.ospf.model.map.impl.DeviceVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RelationEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.tools.NeighbourCostAndLink;
import org.hkfree.ospf.tools.geo.GPSPoint;

/**
 * Třída představující MapModel (model grafu reprezentovaný seznamem vrcholů
 * a hran mezi nimi)
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapModel implements IMapModel {

    private List<IEdge> edges = new ArrayList<IEdge>();
    private List<IVertex> vertices = new ArrayList<IVertex>();


    /**
     * Konstruktor - vytvoří instanci třídy
     */
    public MapModel() {}


    /**
     * Metoda, která vytvoří instanci hrany dle na základě zadaných parametrů
     * @param id1
     * @param id2
     * @param name1
     * @param name2
     * @param cost1
     * @param cost2
     * @param gpsP1
     * @param gpsP2
     * @param linkIDv4
     * @param ospfLinksData
     */
    public void addLinkEdge(String id1, String id2, String name1, String name2, int cost1, int cost2, int cost1IPv6,
	    int cost2IPv6, GPSPoint gpsP1,
	    GPSPoint gpsP2, String linkIDv4, String linkIDv6, List<OspfLinkData> ospfLinksData) {
	RouterVertex rv1 = getRouterVertexById(id1);
	RouterVertex rv2 = getRouterVertexById(id2);
	if (rv1 == null) {
	    rv1 = new RouterVertex(id1, name1, gpsP1);
	    vertices.add(rv1);
	    if (id1.contains(Constants.MULTILINK)) {
		rv1.setMultilink(true);
	    }
	}
	if (rv2 == null) {
	    rv2 = new RouterVertex(id2, name2, gpsP2);
	    vertices.add(rv2);
	    if (id2.contains(Constants.MULTILINK)) {
		rv2.setMultilink(true);
	    }
	}
	LinkEdge le = new LinkEdge();
	le.setVertex1(rv1);
	le.setVertex2(rv2);
	le.setCost1v4(cost1);
	le.setCost2v4(cost2);
	le.setCost1v6(cost1IPv6);
	le.setCost2v6(cost2IPv6);
	le.setLinkIDv4(linkIDv4);
	le.setLinkIDv6(linkIDv6);
	edges.add(le);
    }


    /**
     * Přidá do MapModelu novou hranu a vrací odkaz na její instanci
     * @param rv1
     * @param rv2
     * @param cost1
     * @param cost2
     * @param linkID
     * @return linkEdge
     */
    public LinkEdge addLinkEdge(RouterVertex rv1, RouterVertex rv2, int cost1, int cost2, String linkID) {
	LinkEdge le = new LinkEdge();
	le.setLinkIDv4(linkID);
	// FIXME dodelat to!!!
	// add link edge dodelat
	/*
	 * if (rv1.isMultilink()) {
	 * linkEdges.add(new LinkEdge(rv2, cost2, rv1, 0, linkID));
	 * } else {
	 * if (rv2.isMultilink()) {
	 * linkEdges.add(new LinkEdge(rv1, cost1, rv2, 0, linkID));
	 * } else
	 * linkEdges.add(new LinkEdge(rv1, cost1, rv2, cost2, linkID));
	 * }
	 */
	return (LinkEdge) edges.get(edges.size() - 1);
    }


    /**
     * Nalezne routerVertex dle id a vrati ho
     * @param id
     * @return pokud nenalezne, vraci null
     */
    private RouterVertex getRouterVertexById(String id) {
	for (IVertex rv : vertices) {
	    if (rv instanceof RouterVertex) {
		if (((RouterVertex) rv).getInfo().equals(id)) {
		    return (RouterVertex) rv;
		}
	    }
	}
	return null;
    }


    @Override
    public List<IEdge> getEdges() {
	return this.edges;
    }


    @Override
    public List<IVertex> getVertices() {
	return this.vertices;
    }


    /**
     * Vraci vsechny hrany typu LinkEdge
     * @return
     */
    public List<LinkEdge> getLinkEdges() {
	List<LinkEdge> les = new ArrayList<LinkEdge>();
	for (IEdge e : this.edges) {
	    if (e instanceof LinkEdge) {
		les.add((LinkEdge) e);
	    }
	}
	return les;
    }


    /**
     * Vraci vsechny vrcholy typu RouterVertex
     * @return
     */
    public List<RouterVertex> getRouterVertices() {
	List<RouterVertex> rvs = new ArrayList<RouterVertex>();
	for (IVertex v : this.vertices) {
	    if (v instanceof RouterVertex) {
		rvs.add((RouterVertex) v);
	    }
	}
	return rvs;
    }


    /**
     * Vraci vsechny hrany typu RelationEdge
     * @return
     */
    public List<RelationEdge> getRelationEdges() {
	List<RelationEdge> res = new ArrayList<RelationEdge>();
	for (IEdge e : this.edges) {
	    if (e instanceof RelationEdge) {
		res.add((RelationEdge) e);
	    }
	}
	return res;
    }


    /**
     * Vraci vsechny vrcholy typu DeviceVertex
     * @return
     */
    public List<DeviceVertex> getDeviceVertices() {
	List<DeviceVertex> dvs = new ArrayList<DeviceVertex>();
	for (IVertex v : this.vertices) {
	    if (v instanceof DeviceVertex) {
		dvs.add((DeviceVertex) v);
	    }
	}
	return dvs;
    }


    /**
     * Metoda, která vrátí index zadaného vrcholu v Listu vrcholů
     * @return les
     */
    public List<LinkEdge> getIncidentEdges(RouterVertex v) {
	List<LinkEdge> incidentEdges = new ArrayList<LinkEdge>();
	for (LinkEdge le : getLinkEdges()) {
	    if (le.getVertex1().equals(v) || le.getVertex2().equals(v)) {
		incidentEdges.add(le);
	    }
	}
	return incidentEdges;
    }


    /**
     * Vrací maximální hodnotu zeměpisné šířky
     * @return double
     */
    public double getMaximumLatitude() {
	double max = 0.0d;
	for (RouterVertex rv : getRouterVertices()) {
	    if (rv.getGpsLatitude() > max) {
		max = rv.getGpsLatitude();
	    }
	}
	return max;
    }


    /**
     * Vrací minimální hodnotu zeměpisné šířky
     * @return double
     */
    public double getMinimumLatitude() {
	double min = 0.0d;
	for (RouterVertex rv : getRouterVertices()) {
	    if ((rv.getGpsLatitude() < min && rv.getGpsLatitude() != 0) || min == 0) {
		min = rv.getGpsLatitude();
	    }
	}
	return min;
    }


    /**
     * Vrací maximální hodnotu zeměpisné délky
     * @return double
     */
    public double getMaximumLongtitude() {
	double max = 0.0d;
	for (RouterVertex rv : getRouterVertices()) {
	    if (rv.getGpsLongtitude() > max) {
		max = rv.getGpsLongtitude();
	    }
	}
	return max;
    }


    /**
     * Vrací minimáln hodnotu zeměpisné délky
     * @return double
     */
    public double getMinimumLongtitude() {
	double min = 0.0d;
	for (RouterVertex rv : getRouterVertices()) {
	    if ((rv.getGpsLongtitude() < min && rv.getGpsLongtitude() != 0) || min == 0) {
		min = rv.getGpsLongtitude();
	    }
	}
	return min;
    }


    /**
     * Vrací příznak, zda má model dva a více routerů s definovanými zeměpisnými souřadnicemi
     * @return boolean
     */
    public boolean hasMoreRouterWithGPSPositions() {
	boolean twoAndMore = false;
	for (RouterVertex rv : getRouterVertices()) {
	    if (rv.getGpsLatitude() != 0 && rv.getGpsLongtitude() != 0) {
		if (twoAndMore) {
		    return true;
		} else {
		    twoAndMore = true;
		}
	    }
	}
	return false;
    }


    @Override
    public List<NeighbourCostAndLink> getNeighboursWithCosts(RouterVertex routerVertex) {
	List<NeighbourCostAndLink> neighbours = new ArrayList<NeighbourCostAndLink>();
	for (LinkEdge le : getLinkEdges()) {
	    if ((le.getVertex1().equals(routerVertex) || le.getVertex2().equals(routerVertex))
		    && le.isEnabled()) {
		if (!((LinkEdge) le).isEdgeOfMultilink()) {
		    if (le.getVertex1().equals(routerVertex)) {
			neighbours.add(new NeighbourCostAndLink((RouterVertex) le.getVertex2(), le.getCost1v4(), le));
		    } else {
			neighbours.add(new NeighbourCostAndLink((RouterVertex) le.getVertex1(), le.getCost2v4(), le));
		    }
		} else {
		    int mcost = le.getCost1v4();
		    for (LinkEdge mle : getIncidentEdges((RouterVertex) le.getVertex2())) {
			if (!mle.getVertex1().equals(routerVertex) && mle.isEnabled())
			    neighbours.add(new NeighbourCostAndLink((RouterVertex) mle.getVertex1(), mcost, le));
		    }
		}
	    }
	}
	return neighbours;
    }


    @Override
    public LinkEdge getMultilinkEdge(RouterVertex router, RouterVertex multilinkvertex) {
	for (LinkEdge le : getLinkEdges()) {
	    if (le.getVertex1().equals(router) && le.getVertex2().equals(multilinkvertex)) {
		return (LinkEdge) le;
	    }
	}
	return null;
    }
}
