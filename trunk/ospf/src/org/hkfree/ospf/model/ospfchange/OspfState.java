package org.hkfree.ospf.model.ospfchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.model.ospf.Router;

/**
 * Třída představující stav modelu vytvořený na základě OspfModelu
 * @author Jakub Menzel
 */
public class OspfState {

    private List<Router> stateRouters = new ArrayList<Router>();
    private List<OspfLinkState> stateLinks = new ArrayList<OspfLinkState>();
    private String stateName = "";
    private Date stateDate = new Date();


    /**
     * Konstruktor
     * @param stateDate
     * @param stateName
     */
    public OspfState(Date stateDate, String stateName) {
	this.stateDate = stateDate;
	this.stateName = stateName;
    }


    /**
     * Přidá spoj
     * @param ospfLink
     * @param linkID
     * @param subnetMask
     */
    public void addOspfLinkState(Link ospfLink, String linkID, int subnetMask) {
	stateLinks.add(new OspfLinkState(ospfLink, linkID, subnetMask));
    }


    /**
     * Přidá do tohoto State router, pokud již není v seznamu obsažen
     * @param router
     */
    public void addRouter(Router router) {
	for (Router r : stateRouters) {
	    if (r.equals(router)) {
		return;
	    }
	}
	stateRouters.add(router);
    }


    /**
     * Vrací poslední používaný spoj
     * @return ospflinkstate
     */
    public OspfLinkState getActualOspfLinkState() {
	return stateLinks.get(stateLinks.size() - 1);
    }


    /**
     * Vrací routery
     * @return routers
     */
    public List<Router> getStateRouters() {
	return stateRouters;
    }


    /**
     * Nastavuje routery
     * @param stateRouters
     */
    public void setStateRouters(List<Router> stateRouters) {
	this.stateRouters = stateRouters;
    }


    /**
     * Vrací spoje
     * @return ospflinkstates
     */
    public List<OspfLinkState> getStateLinks() {
	return stateLinks;
    }


    /**
     * Nastavuje spoje
     * @param stateLinks
     */
    public void setStateLinks(List<OspfLinkState> stateLinks) {
	this.stateLinks = stateLinks;
    }


    /**
     * Vrací název stavu
     * @return name
     */
    public String getStateName() {
	return stateName;
    }


    /**
     * Nastavuje název stavu
     * @param stateName
     */
    public void setStateName(String stateName) {
	this.stateName = stateName;
    }


    /**
     * Vrací datum stavu
     * @return date
     */
    public Date getStateDate() {
	return stateDate;
    }


    /**
     * Nastavuje datum stavu
     * @param stateDate
     */
    public void setStateDate(Date stateDate) {
	this.stateDate = stateDate;
    }
}
