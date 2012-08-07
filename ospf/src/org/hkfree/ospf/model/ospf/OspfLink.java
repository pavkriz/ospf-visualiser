package org.hkfree.ospf.model.ospf;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.ip.IpSubnetCalculator;

/**
 * Třída představující spoj OspfModelu.
 * Spoj tvoří 2 a více routerů jejich příslušné ceny do spoje.
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfLink {

    private ResourceBundle rb = Factory.getRb();
    private String linkID = "";
    private int subnetMask = 0;
    private List<OspfLinkData> routersOfLink = new ArrayList<OspfLinkData>();
    private List<String> subnetIps = new ArrayList<String>();
    private String networkAddress = "0.0.0.0";
    private String broadcastAddress = "0.0.0.0";


    /**
     * Konstruktor - vytvoří instanci třídy
     */
    public OspfLink(String linkID, int subnetMask) {
	this.linkID = linkID;
	this.subnetMask = subnetMask;
	computeSubnetIps();
    }


    /**
     * Spočítá adresu sítě a broadcastovou adresu spoje
     */
    public void computeSubnetIps() {
	IpSubnetCalculator ipSubnetCalc = new IpSubnetCalculator();
	ipSubnetCalc.computeAddresses(linkID, subnetMask);
	networkAddress = ipSubnetCalc.getNetworkAddress();
	broadcastAddress = ipSubnetCalc.getBroadcastAddress();
    }


    /**
     * Vrací příznak zda podsíť spoje obsahuje zadanou ip adresu
     * @param ip
     * @return boolean
     */
    public boolean subnetContainsIP(String ip) {
	if (subnetIps.contains(ip))
	    return true;
	return false;
    }


    /**
     * Metoda, která vrátí název spoje
     * @return linkId
     */
    public String getLinkID() {
	return linkID;
    }


    /**
     * Metoda, která přidá do spoje účastnický router
     */
    public void addRouter(Router router) {
	routersOfLink.add(new OspfLinkData(router));
    }


    /**
     * Metoda, která přidá do spoje záznam o účastníkovi
     * @param router
     * @param interfaceIp
     * @param cost
     */
    public void addRouter(Router router, String interfaceIp, int cost) {
	routersOfLink.add(new OspfLinkData(router, interfaceIp, cost));
    }


    /**
     * Vrátí OspfLinkData, které reprezentují zučastněný router @router
     * @param router
     * @return linkdata
     */
    public OspfLinkData getOspfLinkData(Router router) {
	for (OspfLinkData old : routersOfLink) {
	    if (old.getRouter().equals(router)) {
		return old;
	    }
	}
	return null;
    }


    /**
     * Metoda, která vrátí seznam routerů spoje s jejich cenami v textovém řetězci
     * @return text
     */
    public String routersToString() {
	String vzor = "%5$10s %1$-20s %2$-20s %5$10s %3$s: %4$d\n";
	String text = "";
	for (OspfLinkData old : routersOfLink) {
	    text += String.format(
		    vzor,  
		    old.getRouter().getRouterID(), 
		    "("+old.getInterfaceIp()+")",
		    rb.getString("ol.0"), 
		    old.getCost(),
		    ""
		 );
	}
	return text;
    }


    /**
     * Metoda, která zjistí zdali je zadaný router účastníkem spoje
     * @return boolean
     */
    public boolean containsRouter(Router router) {
	for (OspfLinkData old : routersOfLink) {
	    if (old.getRouter().equals(router)) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Metoda, která upraví cenu u zadaného routeru spoje
     */
    public void updateLinkData(Router router, String interfaceIp, int cost) {
	for (OspfLinkData old : routersOfLink) {
	    if (old.getRouter().equals(router)) {
		old.setCost(cost);
		old.setInterfaceIp(interfaceIp);
	    }
	}
    }


    /**
     * Metoda, která vrátí počet routerů, které jsou účastníky spoje
     * @return count
     */
    public int getRoutersCount() {
	return routersOfLink.size();
    }


    /**
     * Vrací data spoje
     * @return linkdata
     */
    public List<OspfLinkData> getOspfLinkData() {
	return routersOfLink;
    }


    /**
     * Vrací masku spoje
     * @return mask
     */
    public int getSubnetMask() {
	return subnetMask;
    }


    /**
     * Nastavuje masku spoje
     * @param subnetMask
     */
    public void setSubnetMask(int subnetMask) {
	this.subnetMask = subnetMask;
    }


    /**
     * Vrací adresu sítě spoje
     * @return address
     */
    public String getNetworkAddress() {
	return networkAddress;
    }


    /**
     * Vrací broadcastovou adresu spoje
     * @return address
     */
    public String getBroadcastAddress() {
	return broadcastAddress;
    }


    /**
     * Vrací příznak, zda se jedná o spoj, který není úspěšně načten
     * @return boolean
     */
    public boolean isNotCompletelyLoad() {
	List<OspfLinkData> actualMultilinkRemData = null;
	for (OspfLinkData old : routersOfLink) {
	    // standard link
	    if (routersOfLink.size() == 2) {
		if (old.getCost() == -1) {
		    return true; // smazat link
		}
	    } else { // multilink
		if (old.getCost() == -1) {
		    if (actualMultilinkRemData == null) {
			actualMultilinkRemData = new ArrayList<OspfLinkData>();
		    }
		    actualMultilinkRemData.add(old);
		}
	    }
	}
	if (actualMultilinkRemData != null && actualMultilinkRemData.size() > 0) {
	    for (OspfLinkData aold : actualMultilinkRemData) {
		routersOfLink.remove(aold);
	    }
	    if (routersOfLink.size() < 2) {
		return true; // smazat to co zbylo z multilinku
	    }
	}
	return false; // nechat link
    }
}