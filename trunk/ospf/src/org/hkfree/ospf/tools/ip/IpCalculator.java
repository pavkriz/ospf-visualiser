package org.hkfree.ospf.tools.ip;

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
	    return false;
	}
	// minimalni adresa - adresa site
	String na = getNetworkAddress(network, mask);
	// maximalni adresa - adresa broadcast
	String ba = getBroadcastAddress(network, mask);
	String[] sas = search.split("\\.");
	String[] nas = na.split("\\.");
	String[] bas = ba.split("\\.");
	for (int i = 0; i < 4; i++) {
	    if (!isBetweenInclude(Integer.valueOf(nas[i]), Integer.valueOf(bas[i]), Integer.valueOf(sas[i]))) {
		return false;
	    }
	}
	return true;
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
}
