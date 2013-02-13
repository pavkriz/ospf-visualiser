package org.hkfree.ospf.tools.geo;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Třída sloužící pro přepočet geografických souřadnic bodu na souřadnice v mapě sítě
 * @author Jakub Menzel
 */
public class GPSPointConverter implements Serializable {

    private static final long serialVersionUID = 5826793853415799232L;
    
    private double canvasWidth = 0;
    private double canvasHeight = 0;
    private GPSPoint centerGPSPoint = null;
    private double latitudeIntervalSize = 0;
    private double longtitudeIntervalSize = 0;
    // mezera od kraje plátna v procentech
    private double borderSpace = 0.05;


    /**
     * Kostruktor
     */
    public GPSPointConverter() {}


    /**
     * Konstruktor
     * @param canvasWidth
     * @param canvasHeight
     */
    public GPSPointConverter(double canvasWidth, double canvasHeight) {
	this.canvasWidth = canvasWidth - 2 * borderSpace * canvasWidth;
	this.canvasHeight = canvasHeight - 2 * borderSpace * canvasHeight;
	// korekce, aby nebyla mapa příliš rozházená
	this.canvasWidth = this.canvasWidth - this.canvasWidth/4;
	this.canvasHeight = this.canvasHeight - this.canvasHeight/4;
    }


    /**
     * Nastavení maximálních a minimálních hodnot zěměpisné šířky a délky
     * @param minLatitude
     * @param maxLatitude
     * @param minLongtitude
     * @param maxLongtitude
     */
    public void setGPSMaxsAndMins(double minLatitude, double maxLatitude, double minLongtitude, double maxLongtitude) {
	if (minLatitude != maxLatitude) {
	    centerGPSPoint = new GPSPoint(((maxLatitude - minLatitude) / 2) + minLatitude,
		    ((maxLongtitude - minLongtitude) / 2) + minLongtitude);
	    latitudeIntervalSize = maxLatitude - minLatitude;
	    longtitudeIntervalSize = maxLongtitude - minLongtitude;
	    double WaHRatio = canvasWidth / canvasHeight;
	    if (latitudeIntervalSize * 2 / WaHRatio < longtitudeIntervalSize) {
		latitudeIntervalSize = longtitudeIntervalSize / 2 / WaHRatio;
	    } else {
		longtitudeIntervalSize = latitudeIntervalSize * 2 * WaHRatio;
	    }
	}
    }


    /**
     * Přepočet z geografických souřadnic do souřadnic grafu
     * @param gpsPoint
     * @return point2d
     */
    public Point2D getPosition(GPSPoint gpsPoint) {
	double xRatio = (gpsPoint.getLongtitude() - (centerGPSPoint.getLongtitude() - longtitudeIntervalSize / 2))
		/ ((centerGPSPoint.getLongtitude() + longtitudeIntervalSize / 2) - (centerGPSPoint.getLongtitude() - longtitudeIntervalSize / 2));
	double yRatio = (gpsPoint.getLatitude() - (centerGPSPoint.getLatitude() - latitudeIntervalSize / 2))
		/ ((centerGPSPoint.getLatitude() + latitudeIntervalSize / 2) - (centerGPSPoint.getLatitude() - latitudeIntervalSize / 2));
	return new Point2D.Double(canvasWidth * xRatio + canvasWidth * borderSpace, canvasHeight * (1 - yRatio)
		+ canvasHeight * borderSpace);
    }
}
