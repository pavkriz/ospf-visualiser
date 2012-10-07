package org.hkfree.ospf.tools.ip;

import java.util.Comparator;

import org.hkfree.ospf.model.ospf.Router;

/**
 * Třída sloužící k porovnávání IP adres z hledsika řazení
 * @author Jakub Menzel
 */
public class IpComparator implements Comparator<Router> {

    /**
     * Konstruktor třídy
     */
    public IpComparator() {}


    /**
     * Metoda, která zjistí, zda se jedná o "vyssi" adresu...z důvodu řazení
     * @return int
     */
    public int compare(Router u1, Router u2) {
	String ip1 = u1.getId();
	String ip2 = u2.getId();
	return compareIps(ip1, ip2);
    }


    /**
     * Porovnává dvě zadané ip adresy
     * @param ip1
     * @param ip2
     * @return int
     *         -1 ... ip1 < ip2
     *         0 ... ip1 == ip2
     *         1 ... ip1 > ip2
     */
    public int compareIps(String ip1, String ip2) {
	// rozklad IP do jednotlivych oktetu
	int ip1_1, ip1_2, ip1_3, ip1_4, ip2_1, ip2_2, ip2_3, ip2_4;
	String[] octets1 = ip1.split("\\.");
	ip1_1 = Integer.valueOf(octets1[0]);
	ip1_2 = Integer.valueOf(octets1[1]);
	ip1_3 = Integer.valueOf(octets1[2]);
	ip1_4 = Integer.valueOf(octets1[3]);
	String[] octets2 = ip2.split("\\.");
	ip2_1 = Integer.valueOf(octets2[0]);
	ip2_2 = Integer.valueOf(octets2[1]);
	ip2_3 = Integer.valueOf(octets2[2]);
	ip2_4 = Integer.valueOf(octets2[3]);
	// porovnavani
	// -1 : neprohodit 1 : prohodit
	// default: 0 (pro pripad, ze by byly stejne)
	if (ip1_1 < ip2_1)
	    return -1;
	if (ip1_1 > ip2_1)
	    return 1;
	if (ip1_1 == ip2_1 && ip1_2 < ip2_2)
	    return -1;
	if (ip1_1 == ip2_1 && ip1_2 > ip2_2)
	    return 1;
	if (ip1_1 == ip2_1 && ip1_2 == ip2_2 && ip1_3 < ip2_3)
	    return -1;
	if (ip1_1 == ip2_1 && ip1_2 == ip2_2 && ip1_3 > ip2_3)
	    return 1;
	if (ip1_1 == ip2_1 && ip1_2 == ip2_2 && ip1_3 == ip2_3 && ip1_4 < ip2_4)
	    return -1;
	if (ip1_1 == ip2_1 && ip1_2 == ip2_2 && ip1_3 == ip2_3 && ip1_4 > ip2_4)
	    return 1;
	return 0;
    }


    /**
     * Metoda zjistí, zda je zadaná IP nižší nebo stejná jako zadaná hraniční IP
     * @param ip
     * @param minIpValue
     * @return boolean
     */
    public boolean ipIsHigherOrEqualToMinIpValue(String ip, String minIpValue) {
	return (compareIps(ip, minIpValue) != -1);
    }


    /**
     * Metoda zjistí, zda je zadaná IP vyšší nebo stejná jako zadaná hraniční IP
     * @param ip
     * @param maxIpValue
     * @return boolean
     */
    public boolean ipIsLowerOrEqualToMaxIpValue(String ip, String maxIpValue) {
	return (compareIps(ip, maxIpValue) != 1);
    }
}
