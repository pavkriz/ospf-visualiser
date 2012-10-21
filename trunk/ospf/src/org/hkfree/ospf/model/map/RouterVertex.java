package org.hkfree.ospf.model.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.hkfree.ospf.tools.geo.GPSPoint;

import edu.uci.ics.jung.visualization.RenderContext;

/**
 * Třída představující vrchol MapModelu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class RouterVertex {

    private String description = "";
    private String name = "";
    private boolean isMultilink = false;
    private boolean isEnabled = true;
    private boolean isLocked = false;
    private boolean isCenterOfShortestPathTree = false;
    private boolean isFullExpanded = false;
    private boolean isPermanentlyDisplayed = false;
    private double gpsLatitude = 0;
    private double gpsLongtitude = 0;
    private boolean isActuallyLive = false;
    private boolean isActuallyDead = false;
    private boolean isExtraAddedVertex = false;
    private boolean isPartOfNewAddedEdge = false;
    private boolean isFirstRVOfTwoRVShortestPath = false;
    private boolean isSecondRVOfTwoRVShortestPath = false;
    private boolean isFounded = false;
    private boolean isVisible = true;


    /**
     * Konstruktor - vytvoří instanci vrcholu
     */
    public RouterVertex() {}


    /**
     * Konstruktor - vytvoří instanci vrcholu a doplní mu název a popis
     * @param description
     * @param name
     */
    public RouterVertex(String description, String name) {
	this.description = description;
	this.name = name;
    }


    /**
     * Konstruktor - vytvoří instanci vrcholu a doplní mu název a popis a pozici
     * @param description
     * @param name
     * @param gpsPosition
     */
    public RouterVertex(String description, String name, GPSPoint gpsPosition) {
	this.description = description;
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
    public RouterVertex(String description, String name, GPSPoint gpsPosition, boolean isMultilinkCenter) {
	this.description = description;
	this.name = name;
	this.isMultilink = isMultilinkCenter;
	if (gpsPosition != null) {
	    this.gpsLatitude = gpsPosition.getLatitude();
	    this.gpsLongtitude = gpsPosition.getLongtitude();
	}
    }


    /**
     * Vrací barvu vrcholu
     * @return barva výplně
     */
    public Color getColorFill() {
	if (!isVisible) {
	    return null;
	}
	if (isEnabled()) {
	    if (isPartOfNewAddedEdge()) {
		return Color.BLACK;
	    }
	    if (!isMultilink()) {
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
		} else {
		    return new Color(255, 244, 178);
		}
	    } else
		return Color.WHITE;
	} else {
	    return Color.WHITE;
	}
    }


    /**
     * Vrací barvu ohraničení vrcholu - routeru
     * @return barva
     */
    public Color getColorStroke() {
	if (!isVisible) {
	    return null;
	}
	return isEnabled && isPermanentlyDisplayed() ? Color.DARK_GRAY : Color.LIGHT_GRAY;
    }


    /**
     * Vrací štětec, jeho sílu popřípadě zda je čárkovaný
     * @return štětec
     */
    public Stroke getStroker() {
	if (!isVisible) {
	    return null;
	}
	return isEnabled ? new BasicStroke(1) : RenderContext.DASHED;
    }


    /**
     * Metoda, která vrátí popis vrcholu (IP adresu)
     * @return s
     */
    public String getDescription() {
	return description;
    }


    /**
     * Metoda, vrtátí true pokud se jedná o bod multispoje
     * @return boolean
     */
    public boolean isMultilink() {
	return isMultilink;
    }


    /**
     * Metoda, vrtátí true pokud je router zapnutý
     * @return boolean
     */
    public boolean isEnabled() {
	return isEnabled;
    }


    /**
     * Nastavuje, že se jedná o vypnutý router
     * @param value
     */
    public void setEnabled(boolean value) {
	isEnabled = value;
    }


    /**
     * Metoda, která nastaví vlastnost,že se jedná o multispoj
     * @param isMultilink
     */
    public void setMultilink(boolean isMultilink) {
	this.isMultilink = isMultilink;
    }


    /**
     * Metoda, která nastaví vrcholu popis
     * @param description
     */
    public void setDescription(String description) {
	this.description = description;
    }


    /**
     * Metoda, která vrtátí název vrcholu
     * @return name
     */
    public String getName() {
	if (isVisible) {
	    return name;
	}
	return "";
    }


    /**
     * Metoda, která nastaví vrcholu název
     * @param name
     */
    public void setName(String name) {
	this.name = name;
    }


    /**
     * Metoda, která vrací příznak, zda je vrchol zamknutý
     * @return boolean
     */
    public boolean isLocked() {
	return isLocked;
    }


    /**
     * Metoda, která nastaví vrcholu příznak zda je zamknutý
     * @param value
     */
    public void setLocked(boolean value) {
	isLocked = value;
    }


    /**
     * Vrací příznak, zda se jedná o vrchol, jehož všichni sousedé jsou zobrazeni
     * @return boolean
     */
    public boolean isFullExpanded() {
	return isFullExpanded;
    }


    /**
     * Nastaví vrcholu příznak, zda se jedná o vrchol, jehož všichni sousedé jsou zobrazeni
     * @param isFullExpanded
     */
    public void setFullExpanded(boolean isFullExpanded) {
	this.isFullExpanded = isFullExpanded;
    }


    /**
     * Vrací příznak, zda se jedná o trvale zobrazený vrchol
     * @return boolean
     */
    public boolean isPermanentlyDisplayed() {
	return isPermanentlyDisplayed;
    }


    /**
     * Nastaví příznak, zda se jedná o trvale zobrazený vrchol
     * @param isPermanentlyDisplayed
     */
    public void setPermanentlyDisplayed(boolean isPermanentlyDisplayed) {
	this.isPermanentlyDisplayed = isPermanentlyDisplayed;
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
	return isActuallyLive;
    }


    /**
     * Nastaví, zda se jedná o aktuálně naběhlýr router
     * @param isActuallyLive
     */
    public void setActuallyLive(boolean isActuallyLive) {
	this.isActuallyLive = isActuallyLive;
    }


    /**
     * Vrací, zda se jedná o aktuálně spadlý router
     * @return boolean
     */
    public boolean isActuallyDead() {
	return isActuallyDead;
    }


    /**
     * Nastaví, zda se jedná o aktuálně spadlý router
     * @param isActuallyDead
     */
    public void setActuallyDead(boolean isActuallyDead) {
	this.isActuallyDead = isActuallyDead;
    }


    /**
     * Vrací příznak, zda se jedná o extra přidaný vrchol při tvorbě topologie
     * @return boolean
     */
    public boolean isExtraAddedVertex() {
	return isExtraAddedVertex;
    }


    /**
     * Nastavuje příznak, zda se jedná o extra přidaný vrchol při tvorbě topologie
     * @param isExtraAddedVertex
     */
    public void setExtraAddedVertex(boolean isExtraAddedVertex) {
	this.isExtraAddedVertex = isExtraAddedVertex;
    }


    /**
     * Vrací příznak, zda se jedná o kořen stromu nejkratších cest
     * @return boolean
     */
    public boolean isCenterOfShortestPathTree() {
	return isCenterOfShortestPathTree;
    }


    /**
     * Nastavuje příznak, zda se jedná o kořen stromu nejkratších cest
     * @param isCenterOfShortestPathTree
     */
    public void setCenterOfShortestPathTree(boolean isCenterOfShortestPathTree) {
	this.isCenterOfShortestPathTree = isCenterOfShortestPathTree;
    }


    /**
     * Vrací příznak, zda se jedná o vybraný vrchol nově tvořené hrany
     * @return boolean
     */
    public boolean isPartOfNewAddedEdge() {
	return isPartOfNewAddedEdge;
    }


    /**
     * Nastavuje příznak, zda se jedná o vybraný vrchol nově tvořené hrany
     * @param isPartOfNewAddedEdge
     */
    public void setPartOfNewAddedEdge(boolean isPartOfNewAddedEdge) {
	this.isPartOfNewAddedEdge = isPartOfNewAddedEdge;
    }


    /**
     * Vrací příznak, zda se jedná o první z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @return the isFirstRVOfTwoRVShortestPath
     */
    public boolean isFirstRVOfTwoRVShortestPath() {
	return isFirstRVOfTwoRVShortestPath;
    }


    /**
     * Nastavuje příznak, zda se jedná o první z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @param isFirstRVOfTwoRVShortestPath the isFirstRVOfTwoRVShortestPath to set
     */
    public void setFirstRVOfTwoRVShortestPath(boolean isFirstRVOfTwoRVShortestPath) {
	this.isFirstRVOfTwoRVShortestPath = isFirstRVOfTwoRVShortestPath;
    }


    /**
     * Vrací příznak, zda se jedná o druhý z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @return the isSecondRVOfTwoRVShortestPath
     */
    public boolean isSecondRVOfTwoRVShortestPath() {
	return isSecondRVOfTwoRVShortestPath;
    }


    /**
     * Nastavuje příznak, zda se jedná o druhý z vrcholů pro zobrazení nejkratších cest mezi dvěma vrcholy
     * @param isSecondRVOfTwoRVShortestPath the isSecondRVOfTwoRVShortestPath to set
     */
    public void setSecondRVOfTwoRVShortestPath(boolean isSecondRVOfTwoRVShortestPath) {
	this.isSecondRVOfTwoRVShortestPath = isSecondRVOfTwoRVShortestPath;
    }


    public boolean isFounded() {
	return isFounded;
    }


    public void setFounded(boolean isFounded) {
	this.isFounded = isFounded;
    }


    public void setVisible(boolean visible) {
	this.isVisible = visible;
    }
}
