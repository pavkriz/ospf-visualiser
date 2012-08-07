package org.hkfree.ospf.tools;

import java.util.Comparator;

import org.hkfree.ospf.model.netchange.NetState;

/**
 * Třída sloužící k porovnávání NetState z hledsika řazení
 * @author Jakub Menzel
 */
public class NetStateComparator implements Comparator<NetState> {

    /**
     * Konstruktor třídy
     */
    public NetStateComparator() {}


    /**
     * Metoda, která zjistí, zda se jedná o "vyssi" adresu...z důvodu řazení
     */
    public int compare(NetState ns1, NetState ns2) {
	if (ns1.getStateDate().before(ns2.getStateDate())) {
	    return -1;
	}
	if (ns2.getStateDate().before(ns1.getStateDate())) {
	    return 1;
	}
	return 0;
    }
}
