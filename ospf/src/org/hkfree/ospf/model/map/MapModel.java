package org.hkfree.ospf.model.map;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hkfree.ospf.model.AbstractMapModel;
import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.NeighbourCostAndLink;
import org.hkfree.ospf.tools.geo.GPSPoint;

/**
 * Třída představující MapModel (model grafu reprezentovaný seznamem vrcholů
 * a hran mezi nimi)
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapModel implements AbstractMapModel {

    private ResourceBundle rb = Factory.getRb();
    private List<LinkEdge> linkEdges = new ArrayList<LinkEdge>();
    private List<RouterVertex> routerVertices = new ArrayList<RouterVertex>();


    // private boolean castSite = false;
    /**
     * Konstruktor - vytvoří instanci třídy
     */
    public MapModel() {}


    /**
     * Metoda, která vytvoří instanci hrany dle na základě zadaných parametrů
     * (IP, název obou vrcholů a ceny z nich, ID )
     * @param ip1
     * @param ip2
     * @param name1
     * @param name2
     * @param cena1
     * @param cena2
     * @param gpsP1
     * @param gpsP2
     * @param linkID
     */
    public void addLinkEdge(String ip1, String ip2, String name1, String name2, int cena1, int cena2, GPSPoint gpsP1,
	    GPSPoint gpsP2, String linkID) {
	boolean existV1 = false, existV2 = false;
	int positionV1 = 0, positionV2 = 0;
	// ZJISTENI EXISTENCE VRCHOLU
	for (int i = 0; i < routerVertices.size(); i++) {
	    if (routerVertices.get(i).getDescription().equals(ip1)) {
		existV1 = true;
		positionV1 = i;
	    }
	}
	for (int i = 0; i < routerVertices.size(); i++) {
	    if (routerVertices.get(i).getDescription().equals(ip2)) {
		existV2 = true;
		positionV2 = i;
	    }
	}
	// VYTVORENI HRAN
	if (!existV1 && !existV2) {
	    routerVertices.add(new RouterVertex(ip1, name1, gpsP1));
	    if (ip1.contains(rb.getString("mm.0"))) // TODO JS tohle neni jisty, bylo tu Multispoj, nevim jestli je to neco
						    // konstantniho nebo ne
		routerVertices.get(routerVertices.size() - 1).setMultilink(true);
	    routerVertices.add(new RouterVertex(ip2, name2, gpsP2));
	    if (ip2.contains(rb.getString("mm.0")))
		routerVertices.get(routerVertices.size() - 1).setMultilink(true);
	    linkEdges.add(new LinkEdge(routerVertices.get(routerVertices.size() - 2), cena1, routerVertices
		    .get(routerVertices.size() - 1), cena2, linkID));
	}
	if (existV1 && !existV2) {
	    routerVertices.add(new RouterVertex(ip2, name2, gpsP2));
	    if (ip2.contains(rb.getString("mm.0")))
		routerVertices.get(routerVertices.size() - 1).setMultilink(true);
	    linkEdges.add(new LinkEdge(routerVertices.get(positionV1), cena1, routerVertices.get(routerVertices.size() - 1),
		    cena2, linkID));
	}
	if (!existV1 && existV2) {
	    routerVertices.add(new RouterVertex(ip1, name1, gpsP1));
	    if (ip1.contains(rb.getString("mm.0")))
		routerVertices.get(routerVertices.size() - 1).setMultilink(true);
	    linkEdges.add(new LinkEdge(routerVertices.get(routerVertices.size() - 1), cena1, routerVertices.get(positionV2),
		    cena2, linkID));
	}
	if (existV1 && existV2) {
	    linkEdges
		    .add(new LinkEdge(routerVertices.get(positionV1), cena1, routerVertices.get(positionV2), cena2, linkID));
	}
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
	if (rv1.isMultilink()) {
	    linkEdges.add(new LinkEdge(rv2, cost2, rv1, 0, linkID));
	} else {
	    if (rv2.isMultilink()) {
		linkEdges.add(new LinkEdge(rv1, cost1, rv2, 0, linkID));
	    } else
		linkEdges.add(new LinkEdge(rv1, cost1, rv2, cost2, linkID));
	}
	return linkEdges.get(linkEdges.size() - 1);
    }


    /**
     * Metoda vytvoření vrcholu v seznamu vrcholů
     * @return rv
     */
    public RouterVertex addFirstRouterVertex(String ip, String name) {
	routerVertices.add(new RouterVertex(ip, name));
	return routerVertices.get(routerVertices.size() - 1);
    }


    /**
     * Vytvoří nový routerVertex modelu
     */
    public void addRouterVertex() {
	routerVertices.add(new RouterVertex());
    }


    /**
     * Metoda, která vrátí počet vrcholů MapaModelu
     * @return count
     */
    public int routerVertexCount() {
	return routerVertices.size();
    }


    /**
     * Metoda, která vrátí počet hran MapaModelu
     * @return count
     */
    public int linkCount() {
	return linkEdges.size();
    }


    /**
     * Metoda, která vrátí všechny linkEdges MapaModelu
     * @return linkEdge
     */
    public List<LinkEdge> getLinkEdges() {
	return linkEdges;
    }


    /**
     * Metoda, která vrátí všechny routerVertexes MapaModelu
     * @return rvs
     */
    public List<RouterVertex> getRouterVertices() {
	return routerVertices;
    }


    /**
     * Metoda, která vrátí index zadaného vrcholu v Listu vrcholů
     * @return index
     */
    public int getIndexOfRouterVertex(RouterVertex v) {
	return routerVertices.indexOf(v);
    }


    /**
     * Metoda, která vrátí index zadaného vrcholu v Listu vrcholů
     * @return les
     */
    public List<LinkEdge> getIncidentEdges(RouterVertex v) {
	List<LinkEdge> incidentEdges = new ArrayList<LinkEdge>();
	for (LinkEdge le : linkEdges) {
	    if (le.getRVertex1().equals(v) || le.getRVertex2().equals(v))
		incidentEdges.add(le);
	}
	return incidentEdges;
    }


    // /**
    // * Metoda, která vtátí true, pokud se jedná o model části sítě
    // * @return boolean
    // */
    // public boolean isCastSite() {
    // return castSite;
    // }
    //
    //
    // /**
    // * Metoda, která nastaví atribut určující zda se jedná o model části sítě
    // */
    // public void setCastSite(boolean castSite) {
    // this.castSite = castSite;
    // }
    /**
     * Vrací maximální hodnotu zeměpisné šířky
     * @return double
     */
    public double getMaximumLatitude() {
	double max = 0;
	for (RouterVertex r : routerVertices) {
	    if (r.getGpsLatitude() > max)
		max = r.getGpsLatitude();
	}
	return max;
    }


    /**
     * Vrací minimální hodnotu zeměpisné šířky
     * @return double
     */
    public double getMinimumLatitude() {
	double min = 0;
	for (RouterVertex r : routerVertices) {
	    if ((r.getGpsLatitude() < min && r.getGpsLatitude() != 0) || min == 0)
		min = r.getGpsLatitude();
	}
	return min;
    }


    /**
     * Vrací maximální hodnotu zeměpisné délky
     * @return double
     */
    public double getMaximumLongtitude() {
	double max = 0;
	for (RouterVertex r : routerVertices) {
	    if (r.getGpsLongtitude() > max)
		max = r.getGpsLongtitude();
	}
	return max;
    }


    /**
     * Vrací minimáln hodnotu zeměpisné délky
     * @return double
     */
    public double getMinimumLongtitude() {
	double min = 0;
	for (RouterVertex r : routerVertices) {
	    if ((r.getGpsLongtitude() < min && r.getGpsLongtitude() != 0) || min == 0)
		min = r.getGpsLongtitude();
	}
	return min;
    }


    /**
     * Vrací příznak, zda má model dva a více routerů s definovanými zeměpisnými souřadnicemi
     * @return boolean
     */
    public boolean hasMoreRouterWithGPSPositions() {
	int cnt = 0;
	for (RouterVertex r : routerVertices) {
	    if (r.getGpsLatitude() != 0 && r.getGpsLongtitude() != 0) {
		cnt++;
	    }
	}
	if (cnt > 1) {
	    return true;
	} else {
	    return false;
	}
    }


    /**
     * Vrací seznam sousedních routerů s cenami daného routeru
     * @param routerVertex
     * @return NeighbourCostAndLink
     */
    public List<NeighbourCostAndLink> getNeighboursWithCosts(RouterVertex routerVertex) {
	List<NeighbourCostAndLink> neighbours = new ArrayList<NeighbourCostAndLink>();
	for (LinkEdge le : this.getLinkEdges()) {
	    if ((le.getRVertex1().equals(routerVertex) || le.getRVertex2().equals(routerVertex)) && le.isEnabled()) {
		if (!le.isEdgeOfMultilink()) {
		    if (le.getRVertex1().equals(routerVertex)) {
			neighbours.add(new NeighbourCostAndLink(le.getRVertex2(), le.getCost1(), le));
		    } else {
			neighbours.add(new NeighbourCostAndLink(le.getRVertex1(), le.getCost2(), le));
		    }
		} else {
		    int mcost = le.getCost1();
		    for (LinkEdge mle : getIncidentEdges(le.getRVertex2())) {
			if (!mle.getRVertex1().equals(routerVertex) && mle.isEnabled())
			    neighbours.add(new NeighbourCostAndLink(mle.getRVertex1(), mcost, le));
		    }
		}
	    }
	}
	return neighbours;
    }


    /**
     * Vrací hranu multispoje mezi danými vrcholy
     * @param router
     * @param multilinkvertex
     * @return le
     */
    public LinkEdge getMultilinkEdge(RouterVertex router, RouterVertex multilinkvertex) {
	for (LinkEdge le : getLinkEdges()) {
	    if (le.getRVertex1().equals(router) && le.getRVertex2().equals(multilinkvertex)) {
		return le;
	    }
	}
	return null;
    }
}
