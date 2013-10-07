package org.hkfree.ospf.tools.geo;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Třída sloužící pro přepočet geografických souřadnic bodu na souřadnice v mapě sítě
 * @author Jakub Menzel
 */
public class GPSPointOsmTestConverter extends GPSPointConverter implements Serializable {

    private static final long serialVersionUID = 5826793853415799232L;
    
    // zoom level=11 scale_denom=272989.386733
    private double centerLat = 50.18;
    private double centerLon = 15.74;
    
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
    public GPSPointOsmTestConverter() {}


    /**
     * Konstruktor
     * @param canvasWidth
     * @param canvasHeight
     */
    public GPSPointOsmTestConverter(double canvasWidth, double canvasHeight) {
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
	    centerGPSPoint = new GPSPoint(centerLat, centerLon);
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
	//double xRatio = (gpsPoint.getLongtitude() - (centerGPSPoint.getLongtitude() - longtitudeIntervalSize / 2))
	//	/ ((centerGPSPoint.getLongtitude() + longtitudeIntervalSize / 2) - (centerGPSPoint.getLongtitude() - longtitudeIntervalSize / 2));
	//double yRatio = (gpsPoint.getLatitude() - (centerGPSPoint.getLatitude() - latitudeIntervalSize / 2))
	//	/ ((centerGPSPoint.getLatitude() + latitudeIntervalSize / 2) - (centerGPSPoint.getLatitude() - latitudeIntervalSize / 2));
	//return new Point2D.Double(canvasWidth * xRatio + canvasWidth * borderSpace, canvasHeight * (1 - yRatio)
	//	+ canvasHeight * borderSpace);
    	Point2D center = WGS2Mercator(centerGPSPoint);
    	Point2D point = WGS2Mercator(gpsPoint);
    	int bitmapWidth = 1024;
    	return new Point2D.Double(point.getX() - center.getX() + bitmapWidth/2, -(point.getY() - center.getY())+bitmapWidth/2);
    }
    
    public Point2D WGS2Mercator(GPSPoint gpsPoint) {
    	double scale = 83000; //272989.386733; //6378137.0
    	double x = Math.toRadians(gpsPoint.getLongtitude())*scale;
    	double y = Math.log(Math.tan((Math.PI/4.0 + Math.toRadians(gpsPoint.getLatitude())/2.0))) *scale;
    	return new Point2D.Double(x,y);
    }
    
    // http://wiki.openstreetmap.org/wiki/Mercator
    // function lon2x($lon) { return deg2rad($lon) * 6378137.0; }
    // function lat2y($lat) { return log(tan(M_PI_4 + deg2rad($lat) / 2.0)) * 6378137.0; }
    // function x2lon($x) { return rad2deg($x / 6378137.0); }
    //function y2lat($y) { return rad2deg(2.0 * atan(exp($y / 6378137.0)) - M_PI_2); }
}
