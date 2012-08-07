package org.hkfree.ospf.model.ospffault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.model.ospf.OspfLink;
import org.hkfree.ospf.tools.DateUtil;

/**
 * Třída představující počty výpadků spoje v určených intervalech
 * @author Jakub Menzel
 */
public class OspfLinkFault {

    private OspfLink ospfLink = null;
    private List<IntervalFault> intervalFaults = new ArrayList<IntervalFault>();


    public OspfLinkFault(OspfLink ospfLink) {
	this.ospfLink = ospfLink;
    }


    /**
     * Vrací ospf link
     * @return ospfLink
     */
    public OspfLink getOspfLink() {
	return ospfLink;
    }


    /**
     * Nastaví ospf link
     * @param ospfLink
     */
    public void setOspfLink(OspfLink ospfLink) {
	this.ospfLink = ospfLink;
    }


    /**
     * Vrací seznam výpadků ospf linku
     * @return intervalFaults
     */
    public List<IntervalFault> getIntervalFaults() {
	return intervalFaults;
    }


    /**
     * Nastavuje seznam výpadků ospf linku
     * @param intervalFaults
     */
    public void setIntervalFaults(List<IntervalFault> intervalFaults) {
	this.intervalFaults = intervalFaults;
    }


    /**
     * Navýší denní počet výpadků
     * @param date
     * @param faultCount
     */
    public void addDayIntervalData(Date date, int faultCount) {
	for (IntervalFault inf : intervalFaults) {
	    if (DateUtil.isItSameDay(date, inf.getFaultDate())) {
		inf.increaseIntervalFaultCount(faultCount);
		return;
	    }
	}
	intervalFaults.add(new IntervalFault(date, faultCount));
    }


    /**
     * Vrací celkový počet výpadků spoje
     * @return count
     */
    public int getTotalFaultCount() {
	int count = 0;
	for (IntervalFault inf : intervalFaults) {
	    count += inf.getFaultCount();
	}
	return count;
    }
}
