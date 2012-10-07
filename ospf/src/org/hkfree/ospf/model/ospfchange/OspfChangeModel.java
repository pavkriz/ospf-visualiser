package org.hkfree.ospf.model.ospfchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.model.linkfault.LinkFault;
import org.hkfree.ospf.model.linkfault.LinkFaultModel;
import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospffault.OspfLinkFaultModel;
import org.hkfree.ospf.tools.ip.IpComparator;

/**
 * Třída představující model změn v sítí mezi zadanými OspfModely
 * @author Jakub Menzel
 */
public class OspfChangeModel {

    private List<Router> routers = new ArrayList<Router>();
    private List<Link> links = new ArrayList<Link>();
    private List<OspfState> ospfStates = new ArrayList<OspfState>();
    private OspfLinkFaultModel ospfLinkFaultModel = new OspfLinkFaultModel();
    private LinkFaultModel linkFaultModel = null;
    private OspfState actualOspfState = null;


    /**
     * Vytvoří nový stav
     * @param modelDate
     * @param modelName
     */
    public void createNewOspfState(Date modelDate, String modelName) {
	ospfStates.add(new OspfState(modelDate, modelName));
	actualOspfState = ospfStates.get(ospfStates.size() - 1);
    }


    /**
     * Přidá spoj do celého modelu a aktuálního spoje
     * @param ospfLink
     */
    public void addOspfLink(Link ospfLink) {
	Link actualLink = addOrUseOsfpLink(ospfLink);
	// pridani linku take do aktuálního stavu sítě
	actualOspfState.addOspfLinkState(actualLink, ospfLink.getLinkID(), ospfLink.getSubnetMask());
	// zpracování routerů linku
	for (OspfLinkData linkData : ospfLink.getOspfLinkData()) {
	    Router actRouter = addOrUseRouter(linkData.getRouter());
	    // přidání routeru do konkrétního linku celého change modelu
	    if (!actualLink.containsRouter(actRouter)) {
		actualLink.addRouter(actRouter, linkData.getInterfaceIp(), linkData.getCostIPv4());
	    }
	    // přidání routeru do linku aktuálního ospfState
	    actualOspfState.addRouter(actRouter);
	    actualOspfState.getActualOspfLinkState().addOspfLinkStateData(actualLink.getOspfLinkData(actRouter),
		    linkData.getCostIPv4());
	}
	// vytvoření záznamu o výpadku
	addFaultsToOspfLink(actualLink);
    }


    /**
     * Přidá nebo použije již vytvořenou instanci spoje
     * @param ospfLink
     * @return ospfLink
     */
    public Link addOrUseOsfpLink(Link ospfLink) {
	for (Link chMLink : links) {
	    // jestliže se je shodné linkID nebo link tvoří stejnou podsíť
	    if (chMLink.getLinkID().equals(ospfLink.getLinkID())
		    || (chMLink.getSubnetMask() == ospfLink.getSubnetMask()
			    && chMLink.getNetworkAddress().equals(ospfLink.getNetworkAddress()) && chMLink
			    .getBroadcastAddress().equals(ospfLink.getBroadcastAddress()))) {
		return chMLink;
	    }
	}
	// jestli nebyl už nalezen, tak vytvořit
	links.add(new Link(ospfLink.getLinkID(), ospfLink.getSubnetMask()));
	return links.get(links.size() - 1);
    }


    /**
     * Přidá nebo použije již vytvořenou instanci routeru
     * @param router
     * @return router
     */
    public Router addOrUseRouter(Router router) {
	for (Router chMRouter : routers) {
	    if (chMRouter.getId().equals(router.getId())) {
		return chMRouter;
	    }
	}
	routers.add(new Router(router.getId(), router.getName(), router.getGpsPosition()));
	return routers.get(routers.size() - 1);
    }


    /**
     * Přidá k linku všechny záznamy o výpadku
     */
    public void addFaultsToOspfLink(Link ospfLink) {
	if (linkFaultModel != null) {
	    IpComparator ipComparator = new IpComparator();
	    for (LinkFault lf : linkFaultModel.getLinkFaults()) {
		if (lf.getLinkID().equals(ospfLink.getLinkID())
			|| (ipComparator.ipIsHigherOrEqualToMinIpValue(lf.getLinkID(), ospfLink.getNetworkAddress()) && ipComparator
				.ipIsLowerOrEqualToMaxIpValue(lf.getLinkID(), ospfLink.getBroadcastAddress()))) {
		    ospfLinkFaultModel.addOspfLinkFault(ospfLink, lf.getLinkFaultDate(), lf.getFaultCount());
		}
	    }
	}
    }


    /**
     * Nastaví link fault model
     * @param linkFaultModel
     */
    public void setLinkFaultModel(LinkFaultModel linkFaultModel) {
	this.ospfLinkFaultModel.setFaultMode(linkFaultModel.getLinkFaultMode());
	this.linkFaultModel = linkFaultModel;
    }


    /**
     * Vrací routery modelu
     * @return router
     */
    public List<Router> getRouters() {
	return routers;
    }


    /**
     * Nastavuje routery modelu
     * @param routers
     */
    public void setRouters(List<Router> routers) {
	this.routers = routers;
    }


    /**
     * Vrací spoje modelu
     * @return ospflink
     */
    public List<Link> getLinks() {
	return links;
    }


    /**
     * Nastavuje spoje modelu
     * @param links
     */
    public void setLinks(List<Link> links) {
	this.links = links;
    }


    /**
     * Vrací stavy modelu
     * @return ospfstate
     */
    public List<OspfState> getOspfStates() {
	return ospfStates;
    }


    /**
     * Nastavuje stavy modelu
     * @param ospfStates
     */
    public void setOspfStates(List<OspfState> ospfStates) {
	this.ospfStates = ospfStates;
    }


    /**
     * Vrací ospfLinkFaultModel
     * @return model
     */
    public OspfLinkFaultModel getOspfLinkFaultModel() {
	return ospfLinkFaultModel;
    }


    /**
     * Nastavuje ospfLinkFaultModel
     * @param ospfLinkFaultModel
     */
    public void setOspfLinkFaultModel(OspfLinkFaultModel ospfLinkFaultModel) {
	this.ospfLinkFaultModel = ospfLinkFaultModel;
    }
}
