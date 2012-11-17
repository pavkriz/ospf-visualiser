package org.hkfree.ospf.tools.ip;

/**
 * Třída obsluhující operace s IP (výpočet adresy sítě, broadcastu, převod masky mezi tvary)
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class IpCalculator {

    private static String networkAddress = "0.0.0.0";
    private static String broadcastAddress = "0.0.0.0";


    /**
     * Vrací spočtenou adresu sítě
     * @return address
     */
    public static String getNetworkAddress(String ip, int subnetMask) {
	if (ip == null || ip.isEmpty()) {
	    return null;
	}
	int ip_1, ip_2, ip_3, ip_4;
	String[] octets1 = ip.split("\\.");
	ip_1 = Integer.valueOf(octets1[0]);
	ip_2 = Integer.valueOf(octets1[1]);
	ip_3 = Integer.valueOf(octets1[2]);
	ip_4 = Integer.valueOf(octets1[3]);
	if (subnetMask <= 8) {
	    int maskedBits = subnetMask;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_1));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2) + ".0.0.0";
	    broadcastAddress = Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2)
		    + ".255.255.255";
	} else if (subnetMask <= 16) {
	    int maskedBits = subnetMask - 8;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_2));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = ip_1 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2)
		    + ".0.0";
	    broadcastAddress = ip_1 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2)
		    + ".255.255";
	} else if (subnetMask <= 24) {
	    int maskedBits = subnetMask - 16;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_3));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = ip_1 + "." + ip_2 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2) + ".0";
	    broadcastAddress = ip_1 + "." + ip_2 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2) + ".255";
	} else {
	    int maskedBits = subnetMask - 24;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_4));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = ip_1 + "." + ip_2 + "." + ip_3 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2);
	    broadcastAddress = ip_1 + "." + ip_2 + "." + ip_3 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2);
	}
	return networkAddress;
    }


    /**
     * Vrací spočtenou adresu broadcastu
     * @return address
     */
    public static String getBroadcastAddress(String ip, int subnetMask) {
	if (ip == null || ip.isEmpty()) {
	    return null;
	}
	int ip_1, ip_2, ip_3, ip_4;
	String[] octets1 = ip.split("\\.");
	ip_1 = Integer.valueOf(octets1[0]);
	ip_2 = Integer.valueOf(octets1[1]);
	ip_3 = Integer.valueOf(octets1[2]);
	ip_4 = Integer.valueOf(octets1[3]);
	if (subnetMask <= 8) {
	    int maskedBits = subnetMask;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_1));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2) + ".0.0.0";
	    broadcastAddress = Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2)
		    + ".255.255.255";
	} else if (subnetMask <= 16) {
	    int maskedBits = subnetMask - 8;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_2));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = ip_1 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2)
		    + ".0.0";
	    broadcastAddress = ip_1 + "." + Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2)
		    + ".255.255";
	} else if (subnetMask <= 24) {
	    int maskedBits = subnetMask - 16;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_3));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = ip_1 + "." + ip_2 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2) + ".0";
	    broadcastAddress = ip_1 + "." + ip_2 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2) + ".255";
	} else {
	    int maskedBits = subnetMask - 24;
	    String ipB = ("00000000" + Integer.toBinaryString(ip_4));
	    ipB = ipB.substring(ipB.length() - 8, ipB.length());
	    networkAddress = ip_1 + "." + ip_2 + "." + ip_3 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "00000000").substring(0, 8), 2);
	    broadcastAddress = ip_1 + "." + ip_2 + "." + ip_3 + "."
		    + Integer.valueOf((ipB.substring(0, maskedBits) + "11111111").substring(0, 8), 2);
	}
	return broadcastAddress;
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
	int m_1, m_2, m_3, m_4;
	String[] octets1 = mask.split("\\.");
	m_1 = Integer.valueOf(octets1[0]);
	m_2 = Integer.valueOf(octets1[1]);
	m_3 = Integer.valueOf(octets1[2]);
	m_4 = Integer.valueOf(octets1[3]);
	String s = Integer.toBinaryString(m_1) + Integer.toBinaryString(m_2) + Integer.toBinaryString(m_3)
		+ Integer.toBinaryString(m_4);
	s = s.replaceAll("0", "");
	return s.length();
    }
}
