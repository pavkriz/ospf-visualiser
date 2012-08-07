package org.hkfree.ospf.model.netchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.model.AbstractMapModel;
import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.ospffault.OspfLinkFault;
import org.hkfree.ospf.model.ospffault.OspfLinkFaultModel;
import org.hkfree.ospf.tools.FaultIntensityCalculator;
import org.hkfree.ospf.tools.NeighbourCostAndLink;
import org.hkfree.ospf.tools.NetStateComparator;
import org.hkfree.ospf.tools.geo.GPSPoint;

/**
 * Model grafu (mapy) sítě postihující stavy sítě v několika časových okamžicích
 * @author Jakub Menzel
 */
public class NetChangeModel implements AbstractMapModel {

    public static final int LINK = 1;
    public static final int MULTILINK = 2;
    public static final int NONEXISTENT = 3;
    private List<LinkEdge> linkEdges = new ArrayList<LinkEdge>();
    private List<RouterVertex> routerVertices = new ArrayList<RouterVertex>();
    private List<NetState> netStates = new ArrayList<NetState>();
    private int actualNetStateIndex = 0;
    private RouterVertex actualMultilinkCenter = null;
    private List<CostDifference> costDifferences = new ArrayList<CostDifference>();
    private OspfLinkFaultModel ospfLinkFaultModel = null;


    /**
     * Konstruktor
     */
    public NetChangeModel() {}


    /**
     * Vrací seznam hran grafu
     * @return linkEdges
     */
    public List<LinkEdge> getLinkEdges() {
	return linkEdges;
    }


    /**
     * Nastavuje seznam hran grafu
     * @param linkEdges
     */
    public void setLinkEdges(List<LinkEdge> linkEdges) {
	this.linkEdges = linkEdges;
    }


    /**
     * Vrací seznam vrcholů grafu
     * @return routerVertexes
     */
    public List<RouterVertex> getRouterVertices() {
	return routerVertices;
    }


    /**
     * Nastavuje seznam vrcholů grafu
     * @param routerVertexes
     */
    public void setRouterVertices(List<RouterVertex> routerVertexes) {
	this.routerVertices = routerVertexes;
    }


    /**
     * Vrací seznam stavů sítě
     * @return netStates
     */
    public List<NetState> getNetStates() {
	return netStates;
    }


    /**
     * Nastavuje seznam stavů sítě
     * @param netStates
     */
    public void setNetStates(List<NetState> netStates) {
	this.netStates = netStates;
    }


    /**
     * Vytváří nový stav sítě
     * @param netStateDate
     * @param netStateName
     */
    public void createNewNetState(Date netStateDate, String netStateName) {
	this.netStates.add(new NetState(netStateDate, netStateName));
    }


    /**
     * Přidává nový routerVertex
     * @param id
     * @param name
     */
    public void addRouterVertex(String id, String name, GPSPoint gpsPoint, boolean isMultilinkCenter) {
	routerVertices.add(new RouterVertex(id, name, gpsPoint, isMultilinkCenter));
    }


    /**
     * Vrátí RouterVertex reprezentující centrum aktuálně zpracovávaného multilinku
     * @return routerVertex
     */
    public RouterVertex getActualMultilinkCenter() {
	return actualMultilinkCenter;
    }


    /**
     * Vrací RouterVertex dle zadaného id
     * @param routerID
     * @return routerVertex
     */
    public RouterVertex getRouterVertexByRouterID(String routerID) {
	for (RouterVertex rv : routerVertices) {
	    if (rv.getDescription().equals(routerID) && !rv.isMultilink()) {
		return rv;
	    }
	}
	return null;
    }


    /**
     * Přidá do NetChangeModelu novou hranu mezi dvěma RouterVertexy
     * @param routerId1
     * @param routerId2
     * @param cost1
     * @param cost2
     * @param linkID
     */
    public void addStandardLinkEdge(String routerId1, String routerId2, int cost1, int cost2, String linkID) {
	RouterVertex rv1 = getRouterVertexByRouterID(routerId1);
	RouterVertex rv2 = getRouterVertexByRouterID(routerId2);
	if (rv1 != null && rv2 != null) {
	    linkEdges.add(new LinkEdge(rv1, cost1, rv2, cost2, linkID));
	} else {
	    System.err.println("Chyba NetChangeModel -> addStandardLinkEdge" + rv1 + " " + rv2);
	}
    }


    /**
     * Přidá hranu mutlispoje
     * @param routerId1
     * @param linkID
     * @param cost1
     */
    public void addMultilinkEdge(String routerId1, int cost1, String linkID) {
	RouterVertex rv1 = getRouterVertexByRouterID(routerId1);
	if (rv1 != null) {
	    linkEdges.add(new LinkEdge(rv1, cost1, actualMultilinkCenter, 0, linkID));
	} else {
	    System.err.println("Chyba NetChangeModel -> addMultilinkEdge" + rv1);
	}
    }


    /**
     * Najde vrchol odpovídající multilink-vrcholu spoje s linkID a nastaví ho jako aktuální multilinkCenter vrchol
     * @param linkID
     * @return true - zdařilo se, false - nezdařilo se
     */
    public boolean findAndSetActualMultilinkCenter(String linkID) {
	for (RouterVertex rv : routerVertices) {
	    if (rv.getName().equals(linkID) && rv.isMultilink()) {
		actualMultilinkCenter = rv;
		return true;
	    }
	}
	return false;
    }


    /**
     * Vytvoří centrum multispoje
     * @param linkID
     */
    public void createMultilinkCenter(String linkID) {
	routerVertices.add(new RouterVertex(linkID, ""));
	actualMultilinkCenter = routerVertices.get(routerVertices.size() - 1);
	actualMultilinkCenter.setMultilink(true);
    }


    /**
     * Vrátí existující hranu mezi routervertexy rv1 a rv2, mající popis linkID
     * @param rv1
     * @param rv2
     * @param linkID
     * @return linkEdge
     */
    public LinkEdge getStandardLinkEdge(RouterVertex rv1, RouterVertex rv2, String linkID) {
	for (LinkEdge le : linkEdges) {
	    if (le.getLinkID().equals(linkID)) {
		if ((le.getRVertex1().equals(rv1) && le.getRVertex2().equals(rv2))
			|| (le.getRVertex1().equals(rv2) && le.getRVertex2().equals(rv1))) {
		    return le;
		}
	    }
	}
	return null;
    }


    /**
     * Vrátí existujicí hranu s linkID tvorenou mezi routerem rv a aktuálně nastaveným actualMultilinkCenterem
     * @param rv
     * @return linkEdge
     */
    public LinkEdge getMultilinkEdge(RouterVertex rv, String linkID) {
	for (LinkEdge le : linkEdges) {
	    if (le.getLinkID().equals(linkID)) {
		if ((le.getRVertex1().equals(rv) && le.getRVertex2().equals(actualMultilinkCenter))
			|| (le.getRVertex1().equals(actualMultilinkCenter) && le.getRVertex2().equals(rv))) {
		    return le;
		}
	    }
	}
	return null;
    }


    /**
     * Vrací počet NetState modelů
     * @return count
     */
    public int getNetStatesCount() {
	return netStates.size();
    }


    /**
     * Vrací celkový počet routerVertex vrcholů
     * @return count
     */
    public int getRouterVertexCount() {
	return routerVertices.size();
    }


    /**
     * Vrátí poslední NetState ze seznamu
     * @return netState
     */
    public NetState getLastNetState() {
	if (netStates.size() > 0) {
	    return netStates.get(netStates.size() - 1);
	} else
	    return null;
    }


    /**
     * Vrací seznam aktuálně naběhlých linků a označí je za aktuálně naběhlé
     * @param actualStateIndex
     */
    public String getActualLivedLinksLogAndMarkThem(int actualStateIndex) {
	String log = "";
	List<String> linkIDs = new ArrayList<String>();
	if (actualStateIndex != 0) {
	    NetState previousState = netStates.get(actualStateIndex - 1);
	    NetState actualState = netStates.get(actualStateIndex);
	    for (LinkEdge le : linkEdges) {
		if (!previousState.isLinkEdgeActive(le) && actualState.isLinkEdgeActive(le)) {
		    if (!linkIDs.contains(le.getLinkID())) {
			log += " ^   linkID: " + le.getLinkID() + "\n";
			linkIDs.add(le.getLinkID());
		    }
		    le.setActuallyLive(true);
		}
	    }
	}
	return log;
    }


    /**
     * Vrací seznam aktuálně spadlých linků a označí je za aktuálně spadlé
     * @param actualStateIndex
     */
    public String getActualDeadLinksLogAndMarkThem(int actualStateIndex) {
	String log = "";
	List<String> linkIDs = new ArrayList<String>();
	if (actualStateIndex != 0) {
	    NetState previousState = netStates.get(actualStateIndex - 1);
	    NetState actualState = netStates.get(actualStateIndex);
	    for (LinkEdge le : linkEdges) {
		if (previousState.isLinkEdgeActive(le) && !actualState.isLinkEdgeActive(le)) {
		    if (!linkIDs.contains(le.getLinkID())) {
			log += " †   linkID: " + le.getLinkID() + "\n";
			linkIDs.add(le.getLinkID());
		    }
		    le.setActuallyDead(true);
		}
	    }
	}
	return log;
    }


    /**
     * Vrací seznam aktuálně naběhlých routerů a označí je za aktuálně naběhlé
     * @param actualStateIndex
     */
    public String getActualLivedRoutersLogAndMarkThem(int actualStateIndex) {
	String log = "";
	if (actualStateIndex != 0) {
	    NetState previousState = netStates.get(actualStateIndex - 1);
	    NetState actualState = netStates.get(actualStateIndex);
	    for (RouterVertex rv : routerVertices) {
		if (!previousState.isRouterVertexActive(rv) && actualState.isRouterVertexActive(rv)) {
		    if (!rv.isMultilink()) {
			log += " ^   router: " + rv.getDescription() + "\n";
		    }
		    rv.setActuallyLive(true);
		}
	    }
	}
	return log;
    }


    /**
     * Vrací seznam aktuálně spadlých linků a označí je za aktuálně spadlé
     * @param actualStateIndex
     */
    public String getActualDeadRoutersLogAndMarkThem(int actualStateIndex) {
	String log = "";
	if (actualStateIndex != 0) {
	    NetState previousState = netStates.get(actualStateIndex - 1);
	    NetState actualState = netStates.get(actualStateIndex);
	    for (RouterVertex rv : routerVertices) {
		if (previousState.isRouterVertexActive(rv) && !actualState.isRouterVertexActive(rv)) {
		    if (!rv.isMultilink()) {
			log += " †   router: " + rv.getDescription() + "\n";
		    }
		    rv.setActuallyDead(true);
		}
	    }
	}
	return log;
    }


    /**
     * Vrací seznam sousedních routerů zadaného routeru s linkem a cenou, se kterou do něj zadaný router vstupuje
     * @param routerVertex
     * @return neighbours
     */
    public List<NeighbourCostAndLink> getNeighboursWithCosts(RouterVertex routerVertex) {
	List<NeighbourCostAndLink> neighbours = new ArrayList<NeighbourCostAndLink>();
	for (LinkEdge le : this.getLinkEdges()) {
	    if ((le.getRVertex1().equals(routerVertex) || le.getRVertex2().equals(routerVertex)) && le.isEnabled()) {
		if (!le.isEdgeOfMultilink()) {
		    if (le.getRVertex1().equals(routerVertex)) {
			neighbours.add(new NeighbourCostAndLink(le.getRVertex2(), netStates.get(actualNetStateIndex)
				.getRoutersCostOfLinkEdge(le.getRVertex2(), le), le));
		    } else {
			neighbours.add(new NeighbourCostAndLink(le.getRVertex1(), netStates.get(actualNetStateIndex)
				.getRoutersCostOfLinkEdge(le.getRVertex1(), le), le));
		    }
		} else {
		    int mcost = netStates.get(actualNetStateIndex).getRoutersCostOfLinkEdge(le.getRVertex1(), le);
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
     * Vrací seznam incidentních hran se zadaným routerVertexem
     * @param routerVertex
     * @return edges
     */
    public List<LinkEdge> getIncidentEdges(RouterVertex routerVertex) {
	List<LinkEdge> incidentEdges = new ArrayList<LinkEdge>();
	for (LinkEdge le : linkEdges) {
	    if (le.getRVertex1().equals(routerVertex) || le.getRVertex2().equals(routerVertex))
		incidentEdges.add(le);
	}
	return incidentEdges;
    }


    /**
     * Vrací instanci LinkEdge, která je hranou multispoje mezi zadaným routerVertexem a multilik routerVertexem
     * @return linkEdge
     */
    public LinkEdge getMultilinkEdge(RouterVertex router, RouterVertex multilinkVertex) {
	for (LinkEdge le : getLinkEdges()) {
	    if (le.isEdgeOfMultilink()
		    && ((le.getRVertex1().equals(router) && le.getRVertex2().equals(multilinkVertex)) || (le.getRVertex2()
			    .equals(router) && le.getRVertex1().equals(multilinkVertex)))) {
		return le;
	    }
	}
	return null;
    }


    /**
     * Nastaví všem vrcholům a hranám, že se nejedná o aktuálně spadlé, nebo naběhlé
     */
    public void resetActuallyLivedAndDeads() {
	for (LinkEdge le : linkEdges) {
	    le.setActuallyDead(false);
	    le.setActuallyLive(false);
	}
	for (RouterVertex rv : routerVertices) {
	    rv.setActuallyDead(false);
	    rv.setActuallyLive(false);
	}
    }


    /**
     * Seřadí list NetStatů dle jejich data vzestupně
     */
    public void sortNetStatesByDate() {
	Collections.sort(netStates, new NetStateComparator());
    }


    /**
     * Zjistí, zda rozdíly mezi cenami spojů v aktuálním a předešlém modelu
     */
    public void checkExistenceOfCostDifferences() {
	costDifferences.clear();
	if (actualNetStateIndex > 0) {
	    NetState actualNetState = netStates.get(actualNetStateIndex);
	    NetState previousNetState = netStates.get(actualNetStateIndex - 1);
	    for (NetStateLinkEdge act_nsle : actualNetState.getNetStateLinkEdges()) {
		for (NetStateLinkEdge prev_nsle : previousNetState.getNetStateLinkEdges()) {
		    if (act_nsle.getLinkEdge().equals(prev_nsle.getLinkEdge())) {
			if (act_nsle.getCost1() != prev_nsle.getCost1()) {
			    costDifferences.add(new CostDifference(act_nsle.getLinkEdge().getRVertex1().getName() + " ("
				    + act_nsle.getLinkEdge().getRVertex1().getDescription() + ")",
				    act_nsle.getStateLinkID(), act_nsle.getCost1(), prev_nsle.getCost1()));
			}
			if (act_nsle.getCost2() != prev_nsle.getCost2()) {
			    costDifferences.add(new CostDifference(act_nsle.getLinkEdge().getRVertex2().getName() + " ("
				    + act_nsle.getLinkEdge().getRVertex2().getDescription() + ")",
				    act_nsle.getStateLinkID(), act_nsle.getCost2(), prev_nsle.getCost2()));
			}
		    }
		}
	    }
	}
    }


    /**
     * Vrací seznam rozdílů cen v aktuálním a předchozím modelu
     * @return differences
     */
    public List<CostDifference> getCostDifferences() {
	return costDifferences;
    }


    /**
     * Vrací příznak, zda existují rozdíly mezi cenami spojů v aktuálním a předešlém modelu
     * @return boolean
     */
    public boolean existCostDifferences() {
	return (costDifferences.size() > 0);
    }


    /**
     * Nastavuje index aktuálně prohlíženého stavu sítě
     * @param actualNetStateIndex
     */
    public void setActualNetStateIndex(int actualNetStateIndex) {
	this.actualNetStateIndex = actualNetStateIndex;
    }


    /**
     * Nastaví ospfLinkFaultModel
     * @return model
     */
    public OspfLinkFaultModel getOspfLinkFaultModel() {
	return ospfLinkFaultModel;
    }


    /**
     * Vrací ospfLinkFaultModel
     * @param ospfLinkFaultModel
     */
    public void setOspfLinkFaultModel(OspfLinkFaultModel ospfLinkFaultModel) {
	this.ospfLinkFaultModel = ospfLinkFaultModel;
    }


    /**
     * Spočítá intenzitu výpadků spojů
     */
    public void computeLinkFaultIntensity() {
	if (ospfLinkFaultModel != null) {
	    ospfLinkFaultModel.computeMaxAndMinFaults();
	    FaultIntensityCalculator fic = new FaultIntensityCalculator();
	    int max = 0;
	    int min = -1;
	    for (LinkEdge le : linkEdges) {
		for (OspfLinkFault olf : ospfLinkFaultModel.getOspfLinkFaults()) {
		    if (olf.getOspfLink().getLinkID().equals(le.getLinkID())) {
			int cnt = olf.getTotalFaultCount();
			le.setFaultCount(cnt);
			if (cnt > max) {
			    max = cnt;
			}
			if (cnt < min || min == -1) {
			    min = cnt;
			}
			break;
		    }
		}
	    }
	    fic.setMaximalFaults(max);
	    fic.setMinimalFaults(min);
	    for (LinkEdge le : linkEdges) {
		le.setFaultIntensity(fic.getMyIntensity((int) le.getFaultCount()));
	    }
	}
    }
}
