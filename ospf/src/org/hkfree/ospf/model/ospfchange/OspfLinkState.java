package org.hkfree.ospf.model.ospfchange;

import java.util.HashMap;
import java.util.Map;

import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.model.ospf.OspfLinkData;

/**
 * Třída představující stav spoje v určitém stavu sítě
 * @author Jakub Menzel
 */
public class OspfLinkState {

    private Link ospfLink = null;
    private Map<OspfLinkData, Integer> routers = new HashMap<OspfLinkData, Integer>();
    private String linkID = "";
    private int mask = 0;


    /**
     * Konstruktor
     * @param ospfLink
     * @param linkID
     * @param subnetMask
     */
    public OspfLinkState(Link ospfLink, String linkID, int subnetMask) {
	this.ospfLink = ospfLink;
	this.linkID = linkID;
	this.mask = subnetMask;
    }


    /**
     * Přidá záznam o účastníkovi spoje aktuálního State
     * @param ospfLinkData
     * @param actualCost
     */
    public void addOspfLinkStateData(OspfLinkData ospfLinkData, int actualCost) {
	routers.put(ospfLinkData, actualCost);
    }


    /**
     * Vrací spoj
     * @return ospflink
     */
    public Link getOspfLink() {
	return ospfLink;
    }


    /**
     * Nastavuje spoj
     * @param link
     */
    public void seOspfLink(Link link) {
	this.ospfLink = link;
    }


    /**
     * Vrací data o routerech spoje
     * @return link data
     */
    public Map<OspfLinkData, Integer> getRouters() {
	return routers;
    }


    /**
     * Nastavuje data o routerech spoje
     * @param routers
     */
    public void setRouters(Map<OspfLinkData, Integer> routers) {
	this.routers = routers;
    }


    /**
     * Vrací linkId
     * @return linkId
     */
    public String getLinkID() {
	return linkID;
    }


    /**
     * Nastavuje linkId
     * @param linkID
     */
    public void setLinkID(String linkID) {
	this.linkID = linkID;
    }


    /**
     * Vrací masku
     * @return mask
     */
    public int getMask() {
	return mask;
    }


    /**
     * Nastavuje masku
     * @param mask
     */
    public void setMask(int mask) {
	this.mask = mask;
    }


    /**
     * Vrací počet routerů linku
     * @return count
     */
    public int getRoutersCount() {
	return routers.size();
    }
}
