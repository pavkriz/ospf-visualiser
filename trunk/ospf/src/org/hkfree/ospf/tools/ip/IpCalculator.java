package org.hkfree.ospf.tools.ip;

import org.hkfree.ospf.model.ospf.ExternalLSA;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.model.ospf.StubLink;

/**
 * Třída obsluhující operace s IP (výpočet adresy sítě, broadcastu, převod masky mezi tvary)
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class IpCalculator {

    private static String ZEROS = "00000000";
    private static String ONES = "11111111";


    /**
     * Vrací spočtenou adresu sítě
     * @return address
     */
    public static String getNetworkAddress(String ip, int subnetMask) {
	if (ip == null || ip.isEmpty()) {
	    return null;
	}
	String na;
	int ip1, ip2, ip3, ip4;
	String[] octets1 = ip.split("\\.");
	ip1 = Integer.valueOf(octets1[0]);
	ip2 = Integer.valueOf(octets1[1]);
	ip3 = Integer.valueOf(octets1[2]);
	ip4 = Integer.valueOf(octets1[3]);
	if (subnetMask <= 8) {
	    int maskedBits = subnetMask;
	    String ipB = (ZEROS + Integer.toBinaryString(ip1));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    na = Integer.valueOf((ipB.substring(0, maskedBits) + ZEROS).substring(0, 8), 2) + ".0.0.0";
	} else if (subnetMask <= 16) {
	    int maskedBits = subnetMask - 8;
	    String ipB = (ZEROS + Integer.toBinaryString(ip2));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    na = ip1 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + ZEROS).substring(0, 8), 2) + ".0.0";
	} else if (subnetMask <= 24) {
	    int maskedBits = subnetMask - 16;
	    String ipB = (ZEROS + Integer.toBinaryString(ip3));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    na = ip1 + "." + ip2 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + ZEROS).substring(0, 8), 2) + ".0";
	} else {
	    int maskedBits = subnetMask - 24;
	    String ipB = (ZEROS + Integer.toBinaryString(ip4));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    na = ip1 + "." + ip2 + "." + ip3 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + ZEROS).substring(0, 8), 2);
	}
	return na;
    }


    /**
     * Vrací spočtenou adresu broadcastu
     * @return address
     */
    public static String getBroadcastAddress(String ip, int subnetMask) {
	if (ip == null || ip.isEmpty()) {
	    return null;
	}
	String ba;
	int ip1, ip2, ip3, ip4;
	String[] octets1 = ip.split("\\.");
	ip1 = Integer.valueOf(octets1[0]);
	ip2 = Integer.valueOf(octets1[1]);
	ip3 = Integer.valueOf(octets1[2]);
	ip4 = Integer.valueOf(octets1[3]);
	if (subnetMask <= 8) {
	    int maskedBits = subnetMask;
	    String ipB = (ZEROS + Integer.toBinaryString(ip1));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    ba = Integer.valueOf((ipB.substring(0, maskedBits) + ONES).substring(0, 8), 2) + ".255.255.255";
	} else if (subnetMask <= 16) {
	    int maskedBits = subnetMask - 8;
	    String ipB = (ZEROS + Integer.toBinaryString(ip2));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    ba = ip1 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + ONES).substring(0, 8), 2) + ".255.255";
	} else if (subnetMask <= 24) {
	    int maskedBits = subnetMask - 16;
	    String ipB = (ZEROS + Integer.toBinaryString(ip3));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    ba = ip1 + "." + ip2 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + ONES).substring(0, 8), 2) + ".255";
	} else {
	    int maskedBits = subnetMask - 24;
	    String ipB = (ZEROS + Integer.toBinaryString(ip4));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    ba = ip1 + "." + ip2 + "." + ip3 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + ONES).substring(0, 8), 2);
	}
	return ba;
    }


    /**
     * Spočte a vrátí masku ve tvaru čísla
     * @param mask maska ve tvaru např. 255.255.255.255
     * @return masku ve tvaru např. 24
     */
    public static int getMask(String mask) {
	if (mask == null || mask.isEmpty()) {
	    return -1;
	}
	int m1, m2, m3, m4;
	String[] octets1 = mask.split("\\.");
	m1 = Integer.valueOf(octets1[0]);
	m2 = Integer.valueOf(octets1[1]);
	m3 = Integer.valueOf(octets1[2]);
	m4 = Integer.valueOf(octets1[3]);
	String s = Integer.toBinaryString(m1) + Integer.toBinaryString(m2) + Integer.toBinaryString(m3)
		+ Integer.toBinaryString(m4);
	s = s.replaceAll("0", "");
	return s.length(); // vraci pocet jednicek (masku site)
    }


    /**
     * Zjisti zda podsit s maskou obsahuje hledanou sit
     * @param network sit
     * @param mask maska site
     * @param search hledany retezec
     */
    public static boolean networkContains(String network, int mask, String search) {
	if (network.contains(":")) {
	    return networkContainsIPv6(network, mask, search);
	} else {
	    return networkContainsIPv4(network, mask, search);
	}
    }


    /**
     * Zjisti zda podsit IPv4 s maskou obsahuje hledanou sit
     * @param network sit
     * @param mask maska site
     * @param search hledany retezec
     * @return
     */
    private static boolean networkContainsIPv4(String network, int mask, String search) {
	// minimalni adresa - adresa site
	String na = getNetworkAddress(network, mask);
	// maximalni adresa - adresa broadcast
	String ba = getBroadcastAddress(network, mask);
	String[] sas = search.split("\\.");
	String[] nas = na.split("\\.");
	String[] bas = ba.split("\\.");
	if (sas.length != 4) {
	    return false;
	}
	for (int i = 0; i < 4; i++) {
	    try {
		if (!isBetweenInclude(Integer.valueOf(nas[i]), Integer.valueOf(bas[i]), Integer.valueOf(sas[i]))) {
		    return false;
		}
	    } catch (NumberFormatException ex) {}
	}
	return true;
    }


    /**
     * Zjisti zda podsit IPv6 s maskou obsahuje hledanou sit
     * @param network sit
     * @param mask maska site
     * @param search hledany retezec
     */
    private static boolean networkContainsIPv6(String network, int mask, String search) {
	// TODO zjistit zda by se to vubec pouzivalo, jakoze asi ne
	return false;
	// return true;
    }


    /**
     * Vrací příznak zda num je mezi min a max včetně
     * @param min min číslo
     * @param max max číslo
     * @param num číslo porovnávané
     * @return pokud num je mezi min/max - true, jinak false
     */
    private static boolean isBetweenInclude(int min, int max, int num) {
	if (num >= min && num <= max) {
	    return true;
	}
	return false;
    }


    /**
     * Vrací ip adresu routeru v klasickém tvaru
     * @param ipArpa ip adresa rotueru v převráceném tvaru končící ".in-addr.arpa."
     * @return klasický tvar ip adresy
     */
    public static String getIpFromIpArpa(String ipArpa) {
	String result = "";
	String[] arpas = ipArpa.substring(0, ipArpa.indexOf(".in-addr.arpa.")).split("\\.");
	for (int i = 0; i < arpas.length; i++) {
	    result += arpas[arpas.length - i - 1] + ".";
	}
	return result.substring(0, result.length() - 1);
    }


    /**
     * Vraci true, pokud router prograuje danou podsit
     * @param r router
     * @param ip podsit
     * @return
     */
    public static boolean containsRouterSubnet(Router r, String ip) {
	ip = ip.toUpperCase();
	for (StubLink sl : r.getStubs()) {
	    if (sl.getLinkID().toUpperCase().contains(ip) ||
		    IpCalculator.networkContains(sl.getLinkID(), sl.getMask(), ip)) {
		return true;
	    }
	}
	// vyhledavani v external lsa
	for (ExternalLSA el : r.getExternalLsa()) {
	    if (el.getNetwork().toUpperCase().contains(ip) ||
		    IpCalculator.networkContains(el.getNetwork(), el.getMask(), ip)) {
		return true;
	    }
	}
	return false;
    }
}
