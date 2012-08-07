package org.hkfree.ospf.model.linkfault;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hkfree.ospf.tools.DateUtil;

/**
 * Třída představující výpadek dle linkId
 * @author Jakub Menzel
 */
public class LinkFault {

    private String linkID = "";
    private Date linkFaultDate = null;
    private int faultCount = 1;


    /**
     * Konstruktor
     */
    public LinkFault() {}


    /**
     * Konstruktor
     */
    public LinkFault(String linkID, Date faultDate) {
	linkFaultDate = faultDate;
	this.linkID = linkID;
    }


    /**
     * Vrací linkId
     * @return linkID
     */
    public String getLinkID() {
	return linkID;
    }


    /**
     * Nastavuje linkId
     * @param linkID
     */
    public void setLinkID(String linkID) {
	this.linkID = linkID;
    }


    /**
     * Vrací počáteční datum intervalu výpadků
     * @return date
     */
    public Date getLinkFaultDate() {
	return linkFaultDate;
    }


    /**
     * Nastavuje počáteční datum intervalu výpadků
     * @param linkFaultDate
     */
    public void setLinkFaultDate(Date linkFaultDate) {
	this.linkFaultDate = linkFaultDate;
    }


    /**
     * Navýší počet spadnutí
     */
    public void increaseLinkFaultCount() {
	this.faultCount++;
    }


    /**
     * Vrací počet výpadku spoje v danném intervalu
     * @return count
     */
    public int getFaultCount() {
	return faultCount;
    }


    /**
     * Vrací datum ve definovaném formátu
     * @return string
     */
    public String getStringDate() {
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
	return formatter.format(linkFaultDate);
    }


    /**
     * Určuje, zda se jedná o stejné linkId a stejný hodinový interval
     * @param linkID
     * @param date
     * @return boolean
     */
    @SuppressWarnings("static-access")
    public boolean isSameHourIntevalOnLink(String linkID, Date date) {
	if (this.linkID.equals(linkID)) {
	    Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();
	    calendar1.setTime(this.linkFaultDate);
	    calendar2.setTime(date);
	    if (calendar1.HOUR_OF_DAY == calendar2.HOUR_OF_DAY && calendar1.DAY_OF_MONTH == calendar2.DAY_OF_MONTH
		    && calendar1.MONTH == calendar2.MONTH && calendar1.YEAR == calendar2.YEAR) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Určuje, zda se jedná o stejné linkId a stejný denní interval
     * @param linkID
     * @param date
     * @return boolean
     */
    public boolean isSameDayIntevalOnLink(String linkID, Date date) {
	if (this.linkID.equals(linkID)) {
	    return DateUtil.isItSameDay(date, linkFaultDate);
	}
	return false;
    }


    /**
     * Určuje, zda se jedná o stejné linkId a stejný týdenní interval
     * @param linkID
     * @param date
     * @return boolean
     */
    @SuppressWarnings("static-access")
    public boolean isSameWeekIntevalOnLink(String linkID, Date date) {
	if (this.linkID.equals(linkID)) {
	    Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();
	    calendar1.setTime(this.linkFaultDate);
	    calendar2.setTime(date);
	    if (calendar1.WEEK_OF_YEAR == calendar2.WEEK_OF_YEAR && calendar1.MONTH == calendar2.MONTH
		    && calendar1.YEAR == calendar2.YEAR) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Určuje, zda se jedná o stejné linkId a stejný měsíční interval
     * @param linkID
     * @param date
     * @return boolean
     */
    @SuppressWarnings("static-access")
    public boolean isSameMonthIntevalOnLink(String linkID, Date date) {
	if (this.linkID.equals(linkID)) {
	    Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();
	    calendar1.setTime(this.linkFaultDate);
	    calendar2.setTime(date);
	    if (calendar1.MONTH == calendar2.MONTH && calendar1.YEAR == calendar2.YEAR) {
		return true;
	    }
	}
	return false;
    }
}
