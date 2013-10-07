package org.hkfree.ospf.model.map.impl;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.hkfree.ospf.tools.geo.GPSPoint;

/**
 * Třída představující vrchol MapModelu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class RouterVertex extends AVertex implements Serializable {

    private static final long serialVersionUID = 7625919799208623090L;
    private String name;
    private String info;
    private boolean multilink = false;
    private boolean locked = false;
    private boolean centerOfShortestPathTree = false;
    private boolean fullExpanded = false;
    private boolean permanentlyDisplayed = false;
    private double gpsLatitude = 0.0d;
    private double gpsLongtitude = 0.0d;
    private boolean actuallyLive = false;
    private boolean actuallyDead = false;
    private boolean extraAddedVertex = false;
    private boolean partOfNewAddedEdge = false;
    private boolean firstRVOfTwoRVShortestPath = false;
    private boolean secondRVOfTwoRVShortestPath = false;
    private boolean founded = false;
    private Polygon hexagon;


    /**
     * Konstruktor - vytvoří instanci vrcholu
     */
    public RouterVertex() {}


    /**
     * Konstruktor - vytvoří instanci vrcholu a doplní mu název a popis
     * @param description
     * @param name
     */
    public RouterVertex(String info, String name) {
	this.info = info;
	this.name = name;
    }


    /**
     * Konstruktor - vytvoří instanci vrcholu a doplní mu název a popis a pozici
     * @param description
     * @param name
     * @param gpsPosition
     */
    public RouterVertex(String info, String name, GPSPoint gpsPosition) {
	this.info = info;
	this.name = name;
	if (gpsPosition != null) {
	    this.gpsLatitude = gpsPosition.getLatitude();
	    this.gpsLongtitude = gpsPosition.getLongtitude();
	}
    }


    /**
     * Konstruktor
     * @param description
     * @param name
     * @param gpsPosition
     * @param isMultilinkCenter
     */
    public RouterVertex(String info, String name, GPSPoint gpsPosition, boolean multilinkCenter) {
	this.info = info;
	this.name = name;
	this.multilink = multilinkCenter;
	if (gpsPosition != null) {
	    this.gpsLatitude = gpsPosition.getLatitude();
	    this.gpsLongtitude = gpsPosition.getLongtitude();
	}
    }


    @Override
    public Color getColorFill() {
	if (!isVisible()) {
	    return null;
	}
	if (!isEnabled()) {
	    return Color.WHITE;
	}
	if (isPartOfNewAddedEdge()) {
	    return Color.BLACK;
	}
	if (isMultilink()) {
	    return Color.WHITE;
	}
	if (isFounded()) {
	    return Color.GREEN;
	}
	if (isCenterOfShortestPathTree()) {
	    return Color.RED;
	}
	if (isFirstRVOfTwoRVShortestPath()) {
	    return new Color(0, 187, 227);
	}
	if (isSecondRVOfTwoRVShortestPath()) {
	    return new Color(255, 80, 80);
	}
	if (isFullExpanded()) {
	    return Color.ORANGE;
	}
	return new Color(255, 244, 178);
    }


    @Override
    public Color getColorStroke() {
	if (!isVisible()) {
	    return null;
	}
	return isEnabled() && isPermanentlyDisplayed() ? Color.DARK_GRAY : Color.LIGHT_GRAY;
    }


    @Override
    public Shape getShaper() {
	if (isMultilink()) {
	    return new Rectangle2D.Float(-6, -6, 12, 12);
	} else if (isLocked()) {
		if (hexagon == null) {
			Polygon p = new Polygon();
			int r = 11; // 10 vypada (a je) mensi nez kruh s r=10 pouzity normalne 
			for (int i = 0; i < 6; i++) {
				p.addPoint(
					(int)(r * Math.cos(i * 2 * Math.PI / 6)),
					(int)(r * Math.sin(i * 2 * Math.PI / 6)));
			}
			hexagon = p;
		}
		return hexagon;
	}
	return new Ellipse2D.Float(-10, -10, 20, 20);
    }


    @Override
    public String getLabel() {
	if (!isVisible()) {
	    return null;
	}
	if (isMultilink()) {
	    return null;
	}
	return getName();
    }


    @Override
    public String getDescription() {
	if (isMultilink()) {
	    return getInfo();
	}
	String result = "<html><body>";
	result += rb.getString("dv.1") + ": <b>" + getName() + "</b>";
	result += "<br>ID: " + getInfo() + "<br>";
	result += "<br>";
	if (isPermanentlyDisplayed()) {
	    result += rb.getString("mst.0") + "<br>";
	} else {
	    result += rb.getString("mst.1") + "<br>";
	}
	if (isFullExpanded()) {
	    result += rb.getString("mst.2") + "<br>";
	} else {
	    result += rb.getString("mst.3") + "<br>";
	}
	result += "</body></html>";
	return result;
    }


    /**
     * Vrací název vrcholu
     * @return name
     */
    public String getName() {
	return name;
    }


    /**
     * Nastaví název vrcholu
     * @param name
     */
    public void setName(String name) {
	this.name = name;
    }


    /**
     * Metoda, která vrátí popis vrcholu (IP adresu)
     * @return s
     */
    public String getInfo() {
	return info;
    }


    /**
     * Metoda, která nastaví vrcholu popis
     * @param description
     */
    public void setInfo(String info) {
	this.info = info;
    }


    /**
     * Metoda, vrtátí true pokud se jedná o bod multispoje
     * @return boolean
     */
    public boolean isMultilink() {
	return multilink;
    }


    /**
     * Metoda, která nastaví vlastnost,že se jedná o multispoj
     * @param isMultilink
     */
    public void setMultilink(boolean isMultilink) {
	this.multilink = isMultilink;
    }


    /**
     * Metoda, která vrací příznak, zda je vrchol zamknutý
     * @return boolean
     */
    public boolean isLocked() {
	return locked;
    }


    /**
     * Metoda, která nastaví vrcholu příznak zda je zamknutý
     * @param value
     */
    public void setLocked(boolean value) {
	locked = value;
    }


    /**
     * Vrací příznak, zda se jedná o vrchol, jehož všichni sousedé jsou zobrazeni
     * @return boolean
     */
    public boolean isFullExpanded() {
	return fullExpanded;
    }


    /**
     * Nastaví vrcholu příznak, zda se jedná o vrchol, jehož všichni sousedé jsou zobrazeni
     * @param isFullExpanded
     */
    public void setFullExpanded(boolean isFullExpanded) {
	this.fullExpanded = isFullExpanded;
    }


    /**
     * Vrací příznak, zda se jedná o trvale zobrazený vrchol
     * @return boolean
     */
    public boolean isPermanentlyDisplayed() {
	return permanentlyDisplayed;
    }


    /**
     * Nastaví příznak, zda se jedná o trvale zobrazený vrchol
     * @param isPermanentlyDisplayed
     */
    public void setPermanentlyDisplayed(boolean isPermanentlyDisplayed) {
	this.permanentlyDisplayed = isPermanentlyDisplayed;
    }


    /**
     * Vrací zeměpisnou šířku gps souřadnice routeru
     * @return lat
     */
    public double getGpsLatitude() {
	return gpsLatitude;
    }


    /**
     * Nastaví routeru zeměpisnou šířku souřadnice routeru
     * @param gpsLatitude
     */
    public void setGpsLatitude(double gpsLatitude) {
	this.gpsLatitude = gpsLatitude;
    }


    /**
     * Vrací zeměpisnou délku gps souřadnice routeru
     * @return lng
     */
    public double getGpsLongtitude() {
	return gpsLongtitude;
    }


    /**
     * Nastaví routeru zeměpisnou délku souřadnice routeru
     * @param gpsLongtitude
     */
    public void setGpsLongtitude(double gpsLongtitude) {
	this.gpsLongtitude = gpsLongtitude;
    }


    /**
     * Nastaví routeru gps souřadnice
     * @param gpsLatitude
     * @param gpsLongtitude
     */
    public void setGpsCoordinates(double gpsLatitude, double gpsLongtitude) {
	this.gpsLatitude = gpsLatitude;
	this.gpsLongtitude = gpsLongtitude;
    }


    /**
     * Vrací bod s gps souřadnicemi routeru
     * @return gpspoint
     */
    public GPSPoint getGPSCoordinates() {
	return new GPSPoint(gpsLatitude, gpsLongtitude);
    }


    /**
     * Vrací, zda se jedná o aktuálně naběhlý router
     * @return boolean
     */
    public boolean isActuallyLive() {
	return actuallyLive;
    }


    /**
     * Nastaví, zda se jedná o aktuálně naběhlýr router
     * @param isActuallyLive
     */
    public void setActuallyLive(boolean isActuallyLive) {
	this.actuallyLive = isActuallyLive;
    }


    /**
     * Vrací, zda se jedná o aktuálně spadlý router
     * @return boolean
     */
    public boolean isActuallyDead() {
	return actuallyDead;
    }


    /**
     * Nastaví, zda se jedná o aktuálně spadlý router
     * @param isActuallyDead
     */
    public void setActuallyDead(boolean isActuallyDead) {
	this.actuallyDead = isActuallyDead;
    }


    /**
     * Vrací příznak, zda se jedná o extra přidaný vrchol při tvorbě topologie
     * @return boolean
     */
    public boolean isExtraAddedVertex() {
	return extraAddedVertex;
    }


    /**
     * Nastavuje příznak, zda se jedná o extra přidaný vrchol při tvorbě topologie
     * @param isExtraAddedVertex
     */
    public void setExtraAddedVertex(boolean isExtraAddedVertex) {
	this.extraAddedVertex = isExtraAddedVertex;
    }


    /**
     * Vrací příznak, zda se jedná o kořen stromu nejkratších cest
     * @return boolean
     */
    public boolean isCenterOfShortestPathTree() {
	return centerOfShortestPathTree;
    }


    /**
     * Nastavuje příznak, zda se jedná o kořen stromu nejkratších cest
     * @param isCenterOfShortestPathTree
     */
    public void setCenterOfShortestPathTree(boolean isCenterOfShortestPathTree) {
	this.centerOfShortestPathTree = isCenterOfShortestPathTree;
    }


    /**
     * Vrací příznak, zda se jedná o vybraný vrchol nově tvořené hrany
     * @return boolean
     */
    public boolean isPartOfNewAddedEdge() {
	return partOfNewAddedEdge;
    }


    /**
     * Nastavuje příznak, zda se jedná o vybraný vrchol nově tvořené hrany
     * @param isPartOfNewAddedEdge
     */
    public void setPartOfNewAddedEdge(boolean isPartOfNewAddedEdge) {
	this.partOfNewAddedEdge = isPartOfNewAddedEdge;
    }


    /**
     * Vrací příznak, zda se jedná o první z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @return the isFirstRVOfTwoRVShortestPath
     */
    public boolean isFirstRVOfTwoRVShortestPath() {
	return firstRVOfTwoRVShortestPath;
    }


    /**
     * Nastavuje příznak, zda se jedná o první z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @param isFirstRVOfTwoRVShortestPath the isFirstRVOfTwoRVShortestPath to set
     */
    public void setFirstRVOfTwoRVShortestPath(boolean isFirstRVOfTwoRVShortestPath) {
	this.firstRVOfTwoRVShortestPath = isFirstRVOfTwoRVShortestPath;
    }


    /**
     * Vrací příznak, zda se jedná o druhý z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @return the isSecondRVOfTwoRVShortestPath
     */
    public boolean isSecondRVOfTwoRVShortestPath() {
	return secondRVOfTwoRVShortestPath;
    }


    /**
     * Nastavuje příznak, zda se jedná o druhý z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @param isSecondRVOfTwoRVShortestPath the isSecondRVOfTwoRVShortestPath to set
     */
    public void setSecondRVOfTwoRVShortestPath(boolean isSecondRVOfTwoRVShortestPath) {
	this.secondRVOfTwoRVShortestPath = isSecondRVOfTwoRVShortestPath;
    }


    public boolean isFounded() {
	return founded;
    }


    public void setFounded(boolean isFounded) {
	this.founded = isFounded;
    }
}
