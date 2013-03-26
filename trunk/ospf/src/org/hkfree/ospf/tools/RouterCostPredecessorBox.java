package org.hkfree.ospf.tools;

import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;

/**
 * Tříd představující předchůdce s cenou a použitým spojem při hledání nejkratších cest
 * @author Jakub Menzel
 */
public class RouterCostPredecessorBox {

    private int cost = 0;
    private RouterVertex predecessor = null;
    private LinkEdge linkEdge = null;


    /**
     * Konstruktor
     * @param cost
     */
    public RouterCostPredecessorBox(int cost) {
	this.cost = cost;
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
     * Vrací předchůdce
     * @return rv
     */
    public RouterVertex getPredecessor() {
	return predecessor;
    }


    /**
     * Nastavuje předchůdce
     * @param predecessor
     */
    public void setPredecessor(RouterVertex predecessor) {
	this.predecessor = predecessor;
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
