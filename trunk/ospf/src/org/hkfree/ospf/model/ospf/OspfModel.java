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
    private List<OspfLink> ospfLinks = new ArrayList<OspfLink>();
    private List<Router> routers = new ArrayList<Router>();
    private String modelName = "";


    public String getModelName() {
	return modelName;
    }


    public void setModelName(String modelName) {
	this.modelName = modelName;
    }


    /**
     * Konstruktor - vytvoří instanci třídy
     */
    public OspfModel() {}


    /**
     * Metoda, která vytvoří záznam o novém spoji
     * @param name
     * @param subnetMask
     */
    public void addOspfLink(String name, int subnetMask) {
	ospfLinks.add(new OspfLink(name, subnetMask));
    }


    /**
     * Metoda, která přidá router reprezentovaný určitou IP do posledně vytvořeného ospfLinks
     * @param ip
     */
    public void addRouter(String ip) {
	boolean existuje = false;
	int pozice = 0;
	for (int i = 0; i < routers.size(); i++) {
	    if (routers.get(i).getRouterID().equals(ip)) {
		existuje = true;
		pozice = i;
	    }
	}
	if (existuje) {
	    ospfLinks.get(ospfLinks.size() - 1).addRouter(routers.get(pozice));
	} else {
	    routers.add(new Router(ip));
	    ospfLinks.get(ospfLinks.size() - 1).addRouter(routers.get(routers.size() - 1));
	}
    }


    /**
     * Metoda, která v určitém spoji změní cenu u určitého routeru
     * @param linkName
     * @param router
     * @param intervaceIp
     * @param cost
     */
    public void updateCost(String linkName, Router router, String intervaceIp, int cost) {
	for (OspfLink s : ospfLinks) {
	    if (s.getLinkID().equals(linkName)) {
		s.updateLinkData(router, intervaceIp, cost);
	    }
	}
    }


    /**
     * Spočítá u všech spojů adresu sítě a broadcast adresu
     */
    public void computeNetAddressesOfLinks() {
	for (OspfLink ospfLink : ospfLinks) {
	    ospfLink.computeSubnetIps();
	}
    }


    /**
     * Metoda, která vrací seznam routerů
     */
    public List<Router> getRouters() {
	return routers;
    }


    /**
     * Metoda, která vrací seznam spojů
     */
    public List<OspfLink> getOspfLinks() {
	return ospfLinks;
    }


    /**
     * Metoda, která vrací v textovém řetězci Ospf data
     */
    public String modelToString() {
	String text = "";
	for (OspfLink s : ospfLinks) {
	    text += String.format(
		    "%1$s: %2$s\t %3$s %4$d\n%5$s\n",
		    new Object[] { rb.getString("lfd.1"), s.getLinkID(), rb.getString("om.0"), s.getSubnetMask(),
			    s.routersToString() });
	}
	return text;
    }


    /**
     * Metoda, která vrací počet routerů modelu
     */
    public int routersCount() {
	return routers.size();
    }


    /**
     * Metoda, která vrací počet spojů modelu
     */
    public int ospfLinksCount() {
	return ospfLinks.size();
    }


    /**
     * Metoda, která vrací instanci Uzlu dle zadané IP
     */
    public Router getRouterByIp(String ip) {
	for (Router u : routers) {
	    if (u.getRouterID().equals(ip))
		return u;
	}
	return null;
    }


    /**
     * Metoda, která vrací počet spojů, ve kterých je router účastníkem
     */
    public int getRouterLinksCount(Router r) {
	int x = 0;
	for (OspfLink s : ospfLinks) {
	    if (s.containsRouter(r))
		x++;
	}
	return x;
    }


    /**
     * Metoda, která seřadí seznam routrů dle IP adres
     */
    public void sortRoutersByIP() {
	Collections.sort(routers, new IpComparator());
    }


    /**
     * Vrací příznak, zda má model 1 a více spojů
     * @return boolean
     */
    public boolean modelHasSomeLinks() {
	return this.ospfLinks.size() > 0;
    }


    /**
     * Smaže z modelu linky, které nebyly zcela načtené
     */
    public void removeNonCompletelyLoadLinksAndRouters() {
	List<OspfLink> linksToRemove = new ArrayList<OspfLink>();
	List<Router> routersToRemove = new ArrayList<Router>();
	for (OspfLink ol : ospfLinks) {
	    if (ol.isNotCompletelyLoad()) {
		linksToRemove.add(ol);
	    }
	}
	for (OspfLink rl : linksToRemove) {
	    ospfLinks.remove(rl);
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
     * Zjistí zda je router obsažen v nějakém ze zcela načtených spojů
     * @param router
     * @return boolean
     */
    public boolean someLinkContainsRouter(Router router) {
	for (OspfLink ol : ospfLinks) {
	    if (ol.containsRouter(router)) {
		return true;
	    }
	}
	return false;
    }
}
