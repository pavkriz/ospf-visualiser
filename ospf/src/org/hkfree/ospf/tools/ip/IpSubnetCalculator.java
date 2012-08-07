package org.hkfree.ospf.tools.ip;

/**
 * Třída sloužící k výpočtu adresy sítě a broadcastové adresy
 * @author Jakub Menzel
 */
public class IpSubnetCalculator {

    private String networkAddress = "0.0.0.0";
    private String broadcastAddress = "0.0.0.0";


    /**
     * Spočítá adresu sítě a broadcast pro zadanou IP a masku
     * @param ip
     * @param subnetMask
     */
    public void computeAddresses(String ip, int subnetMask) {
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
    }


    /**
     * Vrací naposledy spočtenou adresu sítě
     * @return address
     */
    public String getNetworkAddress() {
	return networkAddress;
    }


    /**
     * Vrací neposledy spočtenou adresu broadcastu
     * @return address
     */
    public String getBroadcastAddress() {
	return broadcastAddress;
    }
}
