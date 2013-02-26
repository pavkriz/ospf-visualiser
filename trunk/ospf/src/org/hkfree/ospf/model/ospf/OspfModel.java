package org.hkfree.ospf.model.ospf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.geo.GPSPoint;
import org.hkfree.ospf.tools.ip.IpCalculator;
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
    private boolean ipv6Loaded = false;
    private boolean gpsLoaded = false;


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
     * Vrací router dle jména (popisku v mapě)
     * @param routerName
     * @return
     */
    public Router getRouterByName(String routerName) {
	for (Router u : routers) {
	    if (u.getName().equals(routerName) || u.getId().equals(routerName))
		return u;
	}
	return null;
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
    }


    /**
     * Přidá stub network ke konkrétnímu routeru
     * @param routerId
     * @param linkId
     * @param mask
     * @param cost
     */
    public void addStubNetwork(String routerId, String linkId, int mask, int cost) {
	Router r = getRouterByIp(routerId);
	if (r == null) {
	    return; // TODO opravit, router by se mel nalezt
	}
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


    public void setIpv6Loaded(boolean ipv6Loaded) {
	this.ipv6Loaded = ipv6Loaded;
    }


    public boolean isIpv6Loaded() {
	return ipv6Loaded;
    }


    public boolean isGpsLoaded() {
	return gpsLoaded;
    }


    public void setGpsLoaded(boolean gpsLoaded) {
	this.gpsLoaded = gpsLoaded;
    }


    /**
     * Převede celý OspfModel na MapaModel a vrátí jej
     */
    public MapModel getConvertedWholeModelToMapaModel() {
	ResourceBundle rb = Factory.getRb();
	MapModel mapModel = new MapModel();
	int multilinkCount = 0;
	int cost1 = 0;
	int cost2 = 0;
	int cost1IPv6 = 0;
	int cost2IPv6 = 0;
	String descr1 = "";
	String descr2 = ""; // pouzito take pro popisek multilinku
	String id1 = "";
	String id2 = "";
	for (Link link : this.getLinks()) {
	    if (link.getRoutersCount() == 2) {
		// id routeru
		id1 = link.getOspfLinkData().get(0).getRouter().getId();
		id2 = link.getOspfLinkData().get(1).getRouter().getId();
		// popisky
		if (link.getOspfLinkData().get(0).getRouter().getName().equals("")) {
		    descr1 = link.getOspfLinkData().get(0).getRouter().getId();
		} else {
		    descr1 = link.getOspfLinkData().get(0).getRouter().getName();
		}
		if (link.getOspfLinkData().get(1).getRouter().getName().equals("")) {
		    descr2 = link.getOspfLinkData().get(1).getRouter().getId();
		} else {
		    descr2 = link.getOspfLinkData().get(1).getRouter().getName();
		}
		// ceny
		cost1 = link.getOspfLinkData().get(0).getCostIPv4();
		cost2 = link.getOspfLinkData().get(1).getCostIPv4();
		cost1IPv6 = link.getOspfLinkData().get(0).getCostIPv6();
		cost2IPv6 = link.getOspfLinkData().get(1).getCostIPv6();
		// gps souradnice
		GPSPoint gp1 = link.getOspfLinkData().get(0).getRouter().getGpsPosition();
		GPSPoint gp2 = link.getOspfLinkData().get(1).getRouter().getGpsPosition();
		// pridani spoje v mapModelu
		// System.out.println(cost1IPv6 + " " + cost2IPv6);
		mapModel.addLinkEdge(id1, id2, descr1, descr2, cost1, cost2, cost1IPv6, cost2IPv6, gp1, gp2,
			link.getLinkIDv4(), link.getLinkIDv6(),
			link.getOspfLinkData());
	    } else {
		multilinkCount++;
		descr2 = "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + link.getLinkIDv4() + "</b></body></html>";
		for (OspfLinkData old : link.getOspfLinkData()) {
		    id1 = old.getRouter().getId();
		    GPSPoint gp = old.getRouter().getGpsPosition();
		    if (!old.getRouter().getName().equals("")) {
			descr1 = old.getRouter().getName();
		    } else {
			descr1 = old.getRouter().getId();
		    }
		    mapModel.addLinkEdge(id1, Constants.MULTILINK + Integer.toString(multilinkCount), descr1, descr2,
			    old.getCostIPv4(), -1, old.getCostIPv6(), -1, gp, null, link.getLinkIDv4(), link.getLinkIDv6(),
			    link.getOspfLinkData());
		}
	    }
	}
	return mapModel;
    }


    /**
     * Zařazení LLTD modelů k jednotlivým routerům,
     * dle toho jestli dany router propaguje danou sit
     * @param models
     */
    public void addLLTD(Set<LLTDModel> models) {
	for (Router r : routers) {
	    Set<LLTDModel> modelsAdd = new HashSet<LLTDModel>();
	    for (LLTDModel m : models) {
		if (containsRouterSubnet(r, m.getPublicIP())) {
		    modelsAdd.add(m);
		}
		r.setLltdModels(modelsAdd);
	    }
	}
    }


    /**
     * Vraci true, pokud router prograuje danou podsit
     * @param r router
     * @param publicIP podsit
     * @return
     */
    private boolean containsRouterSubnet(Router r, String publicIP) {
	publicIP = publicIP.toUpperCase();
	for (StubLink sl : r.getStubs()) {
	    if (sl.getLinkID().toUpperCase().contains(publicIP) ||
		    IpCalculator.networkContains(sl.getLinkID(), sl.getMask(), publicIP)) {
		return true;
	    }
	}
	// vyhledavani v external lsa
	for (ExternalLSA el : r.getExternalLsa()) {
	    if (el.getNetwork().toUpperCase().contains(publicIP) ||
		    IpCalculator.networkContains(el.getNetwork(), el.getMask(), publicIP)) {
		return true;
	    }
	}
	return false;
    }
}
