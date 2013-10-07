package org.hkfree.ospf.model.ospffault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.model.ospf.Link;
import org.hkfree.ospf.setting.LinkFaultMode;

/**
 * Třída představující model výpadků konkrétních spojů OspfModelu
 * @author Jakub Menzel
 */
public class OspfLinkFaultModel {

    private int faultMode = LinkFaultMode.DAY_INTERVAL;
    private List<OspfLinkFault> ospfLinkFaults = new ArrayList<OspfLinkFault>();
    private int maxFaults = -1;
    private int minFaults = -1;


    /**
     * Vrací mód
     * @return mode
     */
    public int getFaultMode() {
	return faultMode;
    }


    /**
     * Nastavuje mód
     * @param faultMode
     */
    public void setFaultMode(int faultMode) {
	this.faultMode = faultMode;
    }


    /**
     * Vrací seznam OspfLinkFault výpadků
     * @return linkfaults
     */
    public List<OspfLinkFault> getOspfLinkFaults() {
	return ospfLinkFaults;
    }


    /**
     * Nastavuje seznam OspfLinkFault výpadků
     * @param ospfLinkFaults
     */
    public void setOspfLinkFaults(List<OspfLinkFault> ospfLinkFaults) {
	this.ospfLinkFaults = ospfLinkFaults;
    }


    /**
     * Přidá záznam s výpadky ke konkrétnímu spoji
     * @param ospfLink
     * @param date
     * @param faultCount
     */
    public void addOspfLinkFault(Link ospfLink, Date date, int faultCount) {
	OspfLinkFault actualLinkFault = null;
	for (OspfLinkFault olf : ospfLinkFaults) {
	    if (olf.getOspfLink().equals(ospfLink)) {
		actualLinkFault = olf;
		break;
	    }
	}
	if (actualLinkFault == null) {
	    ospfLinkFaults.add(new OspfLinkFault(ospfLink));
	    actualLinkFault = ospfLinkFaults.get(ospfLinkFaults.size() - 1);
	}
	switch (faultMode) {
	    case LinkFaultMode.DAY_INTERVAL:
		actualLinkFault.addDayIntervalData(date, faultCount);
		break;
	}
    }


    /**
     * Vrátí objekt výpadků zadaného spoje
     * @param ospfLink
     * @return ospflinkfaults
     */
    public OspfLinkFault getOspfLinkFaultOfLink(Link ospfLink) {
	for (OspfLinkFault olf : ospfLinkFaults) {
	    if (olf.getOspfLink().equals(ospfLink)) {
		return olf;
	    }
	}
	return null;
    }


    /**
     * Vrací příznak, zada je ospfLinkFaultModel prázdný
     * @return boolean
     */
    public boolean isEmpty() {
	if (ospfLinkFaults.size() > 0)
	    return false;
	return true;
    }


    /**
     * Nalezne minimální a maximální počet výpadků
     */
    public void computeMaxAndMinFaults() {
	int faultCount = 0;
	for (OspfLinkFault olf : ospfLinkFaults) {
	    faultCount = olf.getTotalFaultCount();
	    if (faultCount > maxFaults) {
		maxFaults = faultCount;
	    }
	    if (faultCount < minFaults || minFaults == -1) {
		minFaults = faultCount;
	    }
	}
    }


    /**
     * Vrací maximální počet výpadků linku v modelu
     * @return count
     */
    public int getMaxFaults() {
	return maxFaults;
    }


    /**
     * Vrací minimální počet výpadků linku v modelu
     * @return count
     */
    public int getMinFaults() {
	return minFaults;
    }
}
