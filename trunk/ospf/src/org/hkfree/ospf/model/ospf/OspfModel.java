package org.hkfree.ospf.model.ospf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.ip.IpComparator;

/**
 * Třída představující OspfModel v podobě uzlů (routerů) a spojů do kterých tyto
 * ospfLinks vstupují včetně příslušných cen(costů)
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfModel {

    private ResourceBundle rb = Factory.getRb();
    private List<Link> links = new ArrayList<Link>();
    private List<Router> routers = new ArrayList<Router>();
    private String modelName = "";


    public OspfModel() {}


    /**
     * Metoda, která vytvoří záznam o novém spoji
     * @param name
     * @param subnetMask
     */
    public void addOspfLink(String name, int subnetMask) {
	links.add(new Link(name, subnetMask));
    }


    /**
     * Metoda, která vytvoří záznam o novém spoji
     * @param name
     * @param subnetMask
     */
    // public void addOspfLink(String name) {
    // Link l = new Link();
    // l.setLinkIDIPv4(name);
    // links.add(l);
    // }
    /**
     * Metoda, která přidá router reprezentovaný určitou IP do posledně vytvořeného ospfLinks
     * @param ip
     */
    public void addRouter(String ip) {
	boolean existuje = false;
	int pozice = 0;
	for (int i = 0; i < routers.size(); i++) {
	    if (routers.get(i).getId().equals(ip)) {
		existuje = true;
		pozice = i;
	    }
	}
	if (existuje) {
	    links.get(links.size() - 1).addRouter(routers.get(pozice));
	} else {
	    routers.add(new Router(ip));
	    links.get(links.size() - 1).addRouter(routers.get(routers.size() - 1));
	}
    }


    /**
     * Spočítá u všech spojů adresu sítě a broadcast adresu
     */
    public void computeNetAddressesOfLinks() {
	for (Link ospfLink : links) {
	    ospfLink.computeSubnetIps();
	}
    }


    /**
     * Metoda, která vrací v textovém řetězci Ospf data
     */
    public String modelToString() {
	String text = "";
	for (Link s : links) {
	    text += String.format(
		    "%1$s: %2$s\t %3$s %4$d\n%5$s\n",
		    new Object[] { rb.getString("lfd.1"), s.getLinkIDv4(), rb.getString("om.0"), s.getSubnetMask(),
			    s.routersToString() });
	}
	return text;
    }


    /**
     * Metoda, která vrací instanci Uzlu dle zadané IP
     */
    public Router getRouterByIp(String ip) {
	for (Router u : routers) {
	    if (u.getId().equals(ip))
		return u;
	}
	return null;
    }


    /**
     * Metoda, která seřadí seznam routerů dle IP adres
     */
    public void sortRoutersByIP() {
	Collections.sort(routers, new IpComparator());
    }


    /**
     * Smaže z modelu linky, které nebyly zcela načtené
     */
    public void removeNonCompletelyLoadedLinksAndRouters() {
	List<Link> linksToRemove = new ArrayList<Link>();
	List<Router> routersToRemove = new ArrayList<Router>();
	for (Link ol : links) {
	    if (ol.isNotCompletelyLoad()) {
		linksToRemove.add(ol);
	    }
	}
	for (Link rl : linksToRemove) {
	    links.remove(rl);
	}
	for (Router r : routers) {
	    if (!someLinkContainsRouter(r)) {
		routersToRemove.add(r);
	    }
	}
	for (Router rr : routersToRemove) {
	    routers.remove(rr);
	}
    }


    /**
     * Metoda, která vrací počet spojů, ve kterých je router účastníkem
     */
    public int getCountOfLinksContainingRouter(Router r) {
	int x = 0;
	for (Link s : links) {
	    if (s.containsRouter(r))
		x++;
	}
	return x;
    }


    /**
     * Zjistí zda je router obsažen v nějakém ze zcela načtených spojů
     * @param router
     * @return boolean
     */
    public boolean someLinkContainsRouter(Router router) {
	for (Link ol : links) {
	    if (ol.containsRouter(router)) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Metoda, která v určitém spoji změní cenu u určitého routeru
     * @param linkName
     * @param router
     * @param intervaceIp
     * @param cost
     */
    public void updateCost(String linkName, Router router, String intervaceIp, int cost) {
	for (Link s : links) {
	    if (s.getLinkIDv4().equals(linkName)) {
		s.updateLinkData(router, intervaceIp, cost);
	    }
	}
    }


    public void updateCostIPv6(String linkName, Router router, String intervaceIp, int cost) {
	for (Link s : links) {
	    if (s.getLinkIDv6().equals(linkName)) {
		s.updateLinkDataIPv6(router, intervaceIp, cost);
	    }
	}
    }


    /**
     * Změní cenu spoje
     * @param linkId ID spoje
     * @param linkStateId ID routeru
     * @param linkData adresa rozhraní
     * @param cost cena
     */
    public void updateCost(String linkId, String linkStateId, String linkData, int cost) {
	for (Router r : routers) {
	    if (r.getId().equals(linkStateId)) {
		updateCost(linkId, r, linkData, cost);
	    }
	}
    }


    /**
     * Nastaví cenu spoji mezi dvěma routerama
     * @param router id prvního routeru
     * @param neighborRouter id druhého routeru
     * @param cost cena
     */
    public void updateCostIPv6(String linkId, String router, String neighborRouter, int cost) {
	for (Router r : routers) {
	    if (r.getId().equals(router)) {
		updateCostIPv6(linkId, r, neighborRouter, cost);
	    }
	}
	// for (Link s : links) {
	// if (s.getLinkID().equals(linkId)) {
	// s.updateLinkDataIPv6(router, neighborRouter, cost);
	// }
	// }
	// for (Link l : links) {
	// if (l.getOspfLinkData().get(0).getRouter().getId().equals(router) &&
	// l.getOspfLinkData().get(1).getRouter().getId().equals(neighborRouter)) {
	// l.getOspfLinkData().get(0).setCostIPv6(cost);
	// } else if (l.getOspfLinkData().get(0).getRouter().getId().equals(neighborRouter) &&
	// l.getOspfLinkData().get(1).getRouter().getId().equals(router)) {
	// l.getOspfLinkData().get(1).setCostIPv6(cost);
	// }
	// }
    }


    /**
     * Přidá stub network ke konkrétnímu routeru
     * @param routerId
     * @param linkId
     * @param mask
     * @param cost
     */
    public void addStubNetwork(String routerId, String linkId, String mask, int cost) {
	Router r = getRouterByIp(routerId);
	StubLink sl = new StubLink();
	sl.setLinkID(linkId);
	sl.setMask(mask);
	sl.setCost(cost);
	r.getStubs().add(sl);
    }


    public List<Router> getRouters() {
	return routers;
    }


    public List<Link> getLinks() {
	return links;
    }


    public String getModelName() {
	return modelName;
    }


    public void setModelName(String modelName) {
	this.modelName = modelName;
    }
}
