package org.hkfree.ospf.tools.load;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.netchange.NetState;
import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospfchange.OspfChangeModel;
import org.hkfree.ospf.model.ospfchange.OspfLinkState;
import org.hkfree.ospf.model.ospfchange.OspfState;

/**
 * Třída sloužící k vytvoření NetChangeModelu na základe OspfChangeModelu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetChangeLoader {

    private NetChangeModel netChangeModel = null;


    /**
     * Konstruktor
     */
    public NetChangeLoader() {}


    /**
     * Nastavuje NetChangeModel
     * @param netChangeModel
     */
    public void setNetChangeModel(NetChangeModel netChangeModel) {
	this.netChangeModel = netChangeModel;
    }


    /**
     * Převede modely
     * @param ospfChangeModel
     */
    public void loadNetChanges(OspfChangeModel ospfChangeModel) {
	createAllRouterVertexesOfModel(ospfChangeModel);
	createAllMultilinkCenterVertexesOfModel(ospfChangeModel);
	createAllLinkEdgesOfModel(ospfChangeModel);
	for (OspfState ospfState : ospfChangeModel.getOspfStates()) {
	    netChangeModel.createNewNetState(ospfState.getStateDate(), ospfState.getStateName());
	    NetState actualNetState = netChangeModel.getLastNetState();
	    for (OspfLinkState ospfLinkState : ospfState.getStateLinks()) {
		// STANDARD LINK
		// celkově je to standardlink
		if (ospfLinkState.getOspfLink().getRoutersCount() == 2) {
		    // if (ospfLinkState.getRoutersCount()==2){
		    OspfLinkData routerData1 = null;
		    OspfLinkData routerData2 = null;
		    for (OspfLinkData old : ospfLinkState.getRouters().keySet()) {
			if (routerData1 == null)
			    routerData1 = old;
			else
			    routerData2 = old;
		    }
		    RouterVertex rv1 = netChangeModel.getRouterVertexByRouterID(routerData1.getRouter().getId());
		    RouterVertex rv2 = netChangeModel.getRouterVertexByRouterID(routerData2.getRouter().getId());
		    if (rv1 != null && rv2 != null) {
			LinkEdge actLinkEdge = netChangeModel.getStandardLinkEdge(rv1, rv2, ospfLinkState.getOspfLink()
				.getLinkID());
			if (actLinkEdge != null) {
			    actualNetState.addStateData(rv1, rv2, actLinkEdge, ospfLinkState.getLinkID(), ospfLinkState
				    .getRouters().get(routerData1), ospfLinkState.getRouters().get(routerData2));
			} else
			    System.err.println("NetChangeLoader - Nenalezena std LinkEdge hrana...");
		    } else {
			System.err.println("NetChangeLoader - Nenalezeny vrcholy");
		    }
		} else { // MULTILINK
			 // celkově je to multilink, ale v aktuálním modelu to může být jen spoj ze 2 routerů, ale to nás
			 // nezajímá - hledáme jako část multilinku
		    if (netChangeModel.findAndSetActualMultilinkCenter(ospfLinkState.getOspfLink().getLinkID())) {
			for (OspfLinkData old : ospfLinkState.getRouters().keySet()) {
			    RouterVertex rv = netChangeModel.getRouterVertexByRouterID(old.getRouter().getId());
			    if (rv != null) {
				LinkEdge actLinkEdge = netChangeModel.getMultilinkEdge(rv, ospfLinkState.getOspfLink()
					.getLinkID());
				if (actLinkEdge != null)
				    actualNetState.addStateData(rv, netChangeModel.getActualMultilinkCenter(), actLinkEdge,
					    ospfLinkState.getLinkID(), old.getCostIPv4(), 0);
			    } else
				System.err.println("NetChangeLoader - Nenalezen mlvrchol");
			}
		    } else
			System.err.println("NetChangeLoader - Nenalezen mlcenter");
		}
	    }
	}
	// pripadne pridani linkFaultModelu do netChangeModelu
	if (!ospfChangeModel.getOspfLinkFaultModel().isEmpty()) {
	    netChangeModel.setOspfLinkFaultModel(ospfChangeModel.getOspfLinkFaultModel());
	    netChangeModel.computeLinkFaultIntensity();
	}
    }


    /**
     * Vytvoreni vsech vrcholu routeru change-modelu
     * @param ospfChangeModel
     */
    public void createAllRouterVertexesOfModel(OspfChangeModel ospfChangeModel) {
	for (Router r : ospfChangeModel.getRouters()) {
	    String rName = r.getName();
	    if (r.getName().equals("")) {
		rName = r.getId();
	    }
	    netChangeModel.addRouterVertex(r.getId(), rName, r.getGpsPosition(), false);
	}
    }


    /**
     * Vytvoreni vsech vrcholu multilik-spoju change-modelu
     * @param ospfChangeModel
     */
    public void createAllMultilinkCenterVertexesOfModel(OspfChangeModel ospfChangeModel) {
	int multilinkCnt = 0;
	for (Link ol : ospfChangeModel.getLinks()) {
	    if (ol.getRoutersCount() > 2) {
		multilinkCnt++;
		netChangeModel.addRouterVertex(Constants.MULTILINK + multilinkCnt, ol.getLinkID(), null, true);
	    }
	}
    }


    /**
     * Vytvoří všechny LinkEdge hrany NetChangeModelu
     * @param ospfChangeModel
     */
    public void createAllLinkEdgesOfModel(OspfChangeModel ospfChangeModel) {
	for (Link ol : ospfChangeModel.getLinks()) {
	    // STANDARD LINK
	    if (ol.getRoutersCount() == 2) {
		OspfLinkData old1 = ol.getOspfLinkData().get(0);
		OspfLinkData old2 = ol.getOspfLinkData().get(1);
		netChangeModel.addStandardLinkEdge(old1.getRouter().getId(), old2.getRouter().getId(),
			old1.getCostIPv4(), old2.getCostIPv4(), ol.getLinkID());
	    } else {
		// MULTILINK
		if (netChangeModel.findAndSetActualMultilinkCenter(ol.getLinkID())) {
		    for (OspfLinkData old : ol.getOspfLinkData()) {
			netChangeModel.addMultilinkEdge(old.getRouter().getId(), old.getCostIPv4(), ol.getLinkID());
		    }
		}
	    }
	}
    }
}
