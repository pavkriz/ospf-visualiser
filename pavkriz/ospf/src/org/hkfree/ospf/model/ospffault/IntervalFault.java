package org.hkfree.ospf.model.ospffault;

import java.util.Date;

/**
 * Třída představující počet výpadků v daném intervalu, začínájícím zadaným datem
 * @author Jakub Menzel
 */
public class IntervalFault {

    private Date faultDate = new Date();
    private int faultCount = 1;


    /**
     * Konstruktor
     */
    public IntervalFault() {}


    /**
     * Konstruktor
     * @param date
     * @param count
     */
    public IntervalFault(Date date, int count) {
	this.faultDate = date;
	this.faultCount = count;
    }


    /**
     * Nastavuje datum
     * @return date
     */
    public Date getFaultDate() {
	return faultDate;
    }


    /**
     * Vrací datum
     * @param faultDate
     */
    public void setFaultDate(Date faultDate) {
	this.faultDate = faultDate;
    }


    /**
     * Nastavuje počet výpadků
     * @return count
     */
    public int getFaultCount() {
	return faultCount;
    }


    /**
     * Vrací počet výpadků
     * @param faultCount
     */
    public void setFaultCount(int faultCount) {
	this.faultCount = faultCount;
    }


    /**
     * Navyšuje počet výpadků o zadaný počet
     * @param increaseCount
     */
    public void increaseIntervalFaultCount(int increaseCount) {
	this.faultCount += increaseCount;
    }
}
