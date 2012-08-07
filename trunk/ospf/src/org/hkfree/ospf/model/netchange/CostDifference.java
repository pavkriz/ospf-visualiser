package org.hkfree.ospf.model.netchange;

/**
 * Třída představující rozdíl v cenách
 * @author Jakub Menzel
 */
public class CostDifference {

    private String routerDescription = "";
    private String linkId = "";
    private int previousCost = 0;
    private int actualCost = 0;


    /**
     * Konstruktor
     */
    public CostDifference() {}


    /**
     * Konstruktor
     * @param routerDescription
     * @param linkId
     * @param previousCost
     * @param actualCost
     */
    public CostDifference(String routerDescription, String linkId, int previousCost, int actualCost) {
	this.routerDescription = routerDescription;
	this.linkId = linkId;
	this.previousCost = previousCost;
	this.actualCost = actualCost;
    }


    /**
     * Vrací popis routeru (router-id + název)
     * @return description
     */
    public String getRouterDescription() {
	return routerDescription;
    }


    /**
     * Nastaví popis routeru
     * @param routerDescription
     */
    public void setRouterDescription(String routerDescription) {
	this.routerDescription = routerDescription;
    }


    /**
     * Vrací popis spoje
     * @return linkId
     */
    public String getLinkId() {
	return linkId;
    }


    /**
     * Nastaví popis spoje
     * @param linkId
     */
    public void setLinkId(String linkId) {
	this.linkId = linkId;
    }


    /**
     * Vrací cenu v předchozím modelu
     * @return cost
     */
    public int getPreviousCost() {
	return previousCost;
    }


    /**
     * Nastaví cenu z předchozího modelu
     * @param previousCost
     */
    public void setPreviousCost(int previousCost) {
	this.previousCost = previousCost;
    }


    /**
     * Vrací cenu z aktuálního modelu
     * @return cost
     */
    public int getActualCost() {
	return actualCost;
    }


    /**
     * Nastaví cenu z aktuálního modelu
     * @param actualCost
     */
    public void setActualCost(int actualCost) {
	this.actualCost = actualCost;
    }
}
