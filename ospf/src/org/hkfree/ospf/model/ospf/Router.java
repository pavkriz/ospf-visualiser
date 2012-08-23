package org.hkfree.ospf.model.ospf;

import java.util.ArrayList;
import java.util.List;

import org.hkfree.ospf.tools.geo.GPSPoint;

/**
 * Třída reprezentující router v síti
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class Router {

    private String routerID = "";
    private String routerName = "";
    private String nameSuffix = "";
    private GPSPoint gpsPosition = null;
    private List<StubLink> stubs = new ArrayList<StubLink>();


    /**
     * Konstruktor - vytvoří instanci třídy
     */
    public Router(String routerID) {
	this.routerID = routerID;
    }


    public List<StubLink> getStubs() {
	return stubs;
    }


    public void setStubs(List<StubLink> stubs) {
	this.stubs = stubs;
    }


    /**
     * Konstruktor - vytvoří instanci třídy
     * @param routerID
     * @param routerName
     */
    public Router(String routerID, String routerName) {
	this.routerID = routerID;
	this.routerName = routerName;
    }


    /**
     * Konstruktor - vytvoří instanci třídy
     * @param routerID
     * @param routerName
     * @param gpsPosition
     */
    public Router(String routerID, String routerName, GPSPoint gpsPosition) {
	this.routerID = routerID;
	this.routerName = routerName;
	this.gpsPosition = gpsPosition;
    }


    /**
     * Metoda, která přiřadí uzlu (routeru) určité ID
     */
    public void setRouterID(String routerID) {
	this.routerID = routerID;
    }


    /**
     * Metoda, která vrátí ID uzlu
     */
    public String getRouterID() {
	return routerID;
    }


    /**
     * Metoda, která nastaví uzlu nějaký název (jméno routeru)
     */
    public void setRouterName(String routerName) {
	if (routerName != null) {
	    this.routerName = routerName;
	}
    }


    /**
     * Metoda, která vrátí název uzlu (jméno routeru)
     */
    public String getRouterName() {
	return routerName;
    }


    /**
     * Nastaví routeru GPS souřadnice
     * @return gpspoint
     */
    public GPSPoint getGpsPosition() {
	return gpsPosition;
    }


    /**
     * Vrátí GPS souřadnice routeru
     * @param gpsPosition
     */
    public void setGpsPosition(GPSPoint gpsPosition) {
	this.gpsPosition = gpsPosition;
    }


    /**
     * Nastavi suffix nazvu routeru
     * @return
     */
    public String getNameSuffix() {
	return nameSuffix;
    }


    /**
     * Vraci suffixu nazvu routeru
     * @param routersSuffix
     */
    public void setNameSuffix(String routersSuffix) {
	this.nameSuffix = routersSuffix;
    }
}
