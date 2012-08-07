package org.hkfree.ospf.model.netchange;

import java.util.ResourceBundle;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující aktuální data o spoji modelu v některém ze stavů
 * @author Jakub Menzel
 */
public class NetStateLinkEdge {

    private ResourceBundle rb = Factory.getRb();
    private LinkEdge linkEdge = null;
    private int cost1 = 0;
    private int cost2 = 0;
    private String stateLinkID = "";


    /**
     * Konstruktor
     * @param linkEdge
     * @param actualLinkID
     * @param cost1
     * @param cost2
     */
    public NetStateLinkEdge(LinkEdge linkEdge, String actualLinkID, int cost1, int cost2) {
	this.linkEdge = linkEdge;
	this.stateLinkID = actualLinkID;
	this.cost1 = cost1;
	this.cost2 = cost2;
    }


    /**
     * Vrací instanci spoje
     * @return linkEdge
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


    /**
     * Vrací aktuální cenu1
     * @return cost
     */
    public int getCost1() {
	return cost1;
    }


    /**
     * Nastavuje aktuální cenu1
     * @param cost1
     */
    public void setCost1(int cost1) {
	this.cost1 = cost1;
    }


    /**
     * Vrací aktuální cenu2
     * @return cost
     */
    public int getCost2() {
	return cost2;
    }


    /**
     * Nastavuje aktuální cenu2
     * @param cost2
     */
    public void setCost2(int cost2) {
	this.cost2 = cost2;
    }


    /**
     * Vrací aktuální linkId
     * @return linkId
     */
    public String getStateLinkID() {
	return stateLinkID;
    }


    /**
     * Nastavuje aktuální linkId
     * @param stateLinkID
     */
    public void setStateLinkID(String stateLinkID) {
	this.stateLinkID = stateLinkID;
    }


    /**
     * Vrací popisek hrany (ceny)
     * @return description
     */
    public String getNetStateLinkCostLabel() {
	if (!linkEdge.isEdgeOfMultilink()) {
	    return cost1 + " - " + cost2;
	} else {
	    return cost1 + "";
	}
    }


    /**
     * Metoda, která vrátí popis hrany reprezentující spoj
     * @return description
     */
    public String getNetStateLinkDescription() {
	if (!linkEdge.getRVertex2().isMultilink()) {
	    return "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + stateLinkID + "</b><br><br>"
		    + rb.getString("le.0") + ":<br><b>" + linkEdge.getRVertex1().getName() + "</b>" + "("
		    + linkEdge.getRVertex1().getDescription() + "): <b>" + Integer.toString(cost1) + "</b><br><b>"
		    + linkEdge.getRVertex2().getName() + "</b>" + "(" + linkEdge.getRVertex2().getDescription() + "): <b>"
		    + Integer.toString(cost2) + "</b></body></html>";
	} else {
	    return "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + stateLinkID + "</b><br><br>"
		    + rb.getString("clcd.1") + ":<br> <b>" + linkEdge.getRVertex1().getName() + "</b>" + "("
		    + linkEdge.getRVertex1().getDescription() + "): <b>" + Integer.toString(cost1) + "</b></body></html>";
	}
    }
}
