package org.hkfree.ospf.model.linkfault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hkfree.ospf.setting.LinkFaultMode;

/**
 * Třída představující model výpadků jednotlivých linkId
 * @author Jakub Menzel
 */
public class LinkFaultModel {

    private List<LinkFault> linkFaults = new ArrayList<LinkFault>();
    private int linkFaultMode = LinkFaultMode.DAY_INTERVAL;
    private List<String> linkIDs = new ArrayList<String>();


    /**
     * Konstruktor
     */
    public LinkFaultModel() {}


    /**
     * Přidá záznam o výpadku
     * @param datetime
     * @param linkID
     */
    public void addLinkFault(Date datetime, String linkID) {
	if (!linkIDs.contains(linkID)) {
	    linkIDs.add(linkID);
	}
	for (LinkFault lf : linkFaults) {
	    switch (linkFaultMode) {
		case LinkFaultMode.HOUR_INTERVAL:
		    if (lf.isSameHourIntevalOnLink(linkID, datetime)) {
			lf.increaseLinkFaultCount();
			return;
		    }
		    break;
		case LinkFaultMode.DAY_INTERVAL:
		    if (lf.isSameDayIntevalOnLink(linkID, datetime)) {
			lf.increaseLinkFaultCount();
			return;
		    }
		    break;
		case LinkFaultMode.WEEK_INTERVAL:
		    if (lf.isSameWeekIntevalOnLink(linkID, datetime)) {
			lf.increaseLinkFaultCount();
			return;
		    }
		    break;
		case LinkFaultMode.MONTH_INTERVAL:
		    if (lf.isSameMonthIntevalOnLink(linkID, datetime)) {
			lf.increaseLinkFaultCount();
			return;
		    }
		    break;
	    }
	}
	linkFaults.add(new LinkFault(linkID, datetime));
    }


    /**
     * Vrací seznam záznamů o výpadcích
     * @return linkFaults
     */
    public List<LinkFault> getLinkFaults() {
	return linkFaults;
    }


    /**
     * Nastavuje seznam záznamů o výpadcích
     * @param linkFaults
     */
    public void setLinkFaults(List<LinkFault> linkFaults) {
	this.linkFaults = linkFaults;
    }


    /**
     * Vrací mód
     * @return mode
     */
    public int getLinkFaultMode() {
	return linkFaultMode;
    }


    /**
     * Nastavuje mód
     * @param linkFaultMode
     */
    public void setLinkFaultMode(int linkFaultMode) {
	this.linkFaultMode = linkFaultMode;
    }


    /**
     * Vrací příznak, zda je model prázdný
     * @return boolean
     */
    public boolean isEmpty() {
	return linkFaults.size() == 0;
    }
}
