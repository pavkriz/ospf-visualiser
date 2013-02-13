package org.hkfree.ospf.tools.geo;

import java.io.Serializable;

/**
 * Třída reprezentující geografickou souřadnici bodu
 * @author Jakub Menzel
 */
public class GPSPoint implements Serializable {

    private static final long serialVersionUID = -7865956655358961791L;
    
    private double latitude = 0;
    private double longtitude = 0;


    /**
     * Konstruktor
     */
    public GPSPoint() {}


    /**
     * Konstruktor
     * @param latitude
     * @param longtitude
     */
    public GPSPoint(double latitude, double longtitude) {
	this.latitude = latitude;
	this.longtitude = longtitude;
    }


    /**
     * Vrací souřadnici zeměpisné šířky
     * @return lat
     */
    public double getLatitude() {
	return latitude;
    }


    /**
     * Nastavuje souřadnici zeměpisné šířky
     * @param latitude
     */
    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }


    /**
     * Vrací souřadnici zeměpisné délky
     * @return lng
     */
    public double getLongtitude() {
	return longtitude;
    }


    /**
     * Nastavuje souřadnici zeměpisné délky
     * @param longtitude
     */
    public void setLongtitude(double longtitude) {
	this.longtitude = longtitude;
    }
}
