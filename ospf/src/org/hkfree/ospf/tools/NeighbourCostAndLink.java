package org.hkfree.ospf.tools;

import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;

/**
 * Třída představující sousední router s cenou a spojem, kterým je možné se do něho dostat
 * @author Jakub Menzel
 */
public class NeighbourCostAndLink {

    private RouterVertex neighbour = null;
    private int cost = 0;
    private LinkEdge linkEdge = null;


    /**
     * Konstruktor
     * @param neighbour
     * @param cost
     * @param linkEdge
     */
    public NeighbourCostAndLink(RouterVertex neighbour, int cost, LinkEdge linkEdge) {
	this.neighbour = neighbour;
	this.cost = cost;
	this.linkEdge = linkEdge;
    }


    /**
     * Vrací sousední router
     * @return rv
     */
    public RouterVertex getNeighbour() {
	return neighbour;
    }


    /**
     * Nastavuje sousední router
     * @param neighbour
     */
    public void setNeighbour(RouterVertex neighbour) {
	this.neighbour = neighbour;
    }


    /**
     * Vrací cenu
     * @return cost
     */
    public int getCost() {
	return cost;
    }


    /**
     * Nastavuje cenu
     * @param cost
     */
    public void setCost(int cost) {
	this.cost = cost;
    }


    /**
     * Vrací spoj
     * @return le
     */
    public LinkEdge getLinkEdge() {
	return linkEdge;
    }


    /**
     * Nastavuje spoj
     * @param linkEdge
     */
    public void setLinkEdge(LinkEdge linkEdge) {
	this.linkEdge = linkEdge;
    }
}
