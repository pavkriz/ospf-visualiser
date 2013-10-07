package org.hkfree.ospf.model.netchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;

/**
 * Třída představující stav sítě v čase
 * @author Jakub Menzel
 */
public class NetState {

    private List<RouterVertex> activeRouters = new ArrayList<RouterVertex>();
    private List<NetStateLinkEdge> activeLinks = new ArrayList<NetStateLinkEdge>();
    private Date stateDate = new Date();
    private String stateName = "";


    /**
     * Konstruktor
     */
    public NetState() {}


    /**
     * Konstruktor
     * @param stateDate
     */
    public NetState(Date stateDate, String stateName) {
	this.stateDate = stateDate;
	this.stateName = stateName;
    }


    /**
     * Přidá aktuální data o spoji sítě
     * @param routerVertex1
     * @param routerVertex2
     * @param linkEdge
     * @param actualLinkID
     * @param cost1
     * @param cost2
     */
    public void addStateData(RouterVertex routerVertex1, RouterVertex routerVertex2, LinkEdge linkEdge, String actualLinkID,
	    int cost1, int cost2) {
	if (!activeRouters.contains(routerVertex1)) {
	    activeRouters.add(routerVertex1);
	}
	if (!activeRouters.contains(routerVertex2)) {
	    activeRouters.add(routerVertex2);
	}
	if (!activeLinks.contains(linkEdge)) {
	    // kontrola vytváření NetStateLinkEdge, aby nebyly prohozený ceny - v LinkEdge můžou být RouterVertexy prohozeny
	    if (linkEdge.getVertex1().equals(routerVertex1))
		activeLinks.add(new NetStateLinkEdge(linkEdge, actualLinkID, cost1, cost2));
	    else
		activeLinks.add(new NetStateLinkEdge(linkEdge, actualLinkID, cost2, cost1));
	}
    }


    /**
     * Vrací příznak zda stav obsahuje danou linkEdge
     * @param linkEdge
     * @return boolean
     */
    public boolean containsLinkEdge(LinkEdge linkEdge) {
	for (NetStateLinkEdge nsle : activeLinks) {
	    if (nsle.getLinkEdge().equals(linkEdge)) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Přidá routerVertex
     * @param routerVertex
     */
    public void addRouterVertex(RouterVertex routerVertex) {
	if (!activeRouters.contains(routerVertex))
	    activeRouters.add(routerVertex);
    }


    /**
     * Vrací datum stavu sítě
     * @return date
     */
    public Date getStateDate() {
	return stateDate;
    }


    /**
     * Vrací příznak, zda byl router v daném stavu sítě aktivní
     * @param routerVertex
     * @return boolean
     */
    public boolean isRouterVertexActive(RouterVertex routerVertex) {
	return activeRouters.contains(routerVertex);
    }


    /**
     * Vrací příznak, zda byl spoj v daném stavu sítě aktivní
     * @param linkEdge
     * @return boolean
     */
    public boolean isLinkEdgeActive(LinkEdge linkEdge) {
	return containsLinkEdge(linkEdge);
    }


    /**
     * Vrací seznam routerů aktivních v daném stavu
     * @return activeRouters
     */
    public List<RouterVertex> getActiveRouters() {
	return activeRouters;
    }


    /**
     * Vrátí odkazy na hrany NetChangeModelu reprezentující hrany aktuálního modelu
     * @return linkEdges
     */
    public List<LinkEdge> getActiveLinks() {
	List<LinkEdge> links = new ArrayList<LinkEdge>();
	for (NetStateLinkEdge nsle : activeLinks) {
	    links.add(nsle.getLinkEdge());
	}
	return links;
    }


    /**
     * Vrátí data o hranách aktuálního NetState
     * @return edges
     */
    public List<NetStateLinkEdge> getNetStateLinkEdges() {
	return activeLinks;
    }


    /**
     * Vrací název stavu sítě
     * @return stateName
     */
    public String getStateName() {
	return stateName;
    }


    /**
     * Nastaví název stavu sítě
     * @param stateName
     */
    public void setStateName(String stateName) {
	this.stateName = stateName;
    }


    /**
     * Vrací cenu danného routeru do danného spoje v tomto netStatu
     * @return cost
     */
    public int getRoutersCostOfLinkEdge(RouterVertex rv, LinkEdge le) {
	for (NetStateLinkEdge nsl : activeLinks) {
	    if (nsl.getLinkEdge().equals(le)) {
		if (nsl.getLinkEdge().getVertex1().equals(rv)) {
		    return nsl.getCost1();
		} else {
		    return nsl.getCost2();
		}
	    }
	}
	return 0;
    }
}
