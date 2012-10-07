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

    private String id = "";
    private String name = "";
    private String suffix = "";
    private GPSPoint gpsPosition = null;
    private List<StubLink> stubs = new ArrayList<StubLink>();


    public Router(String id) {
	this(id, "");
    }


    public Router(String id, String name) {
	this(id, name, null);
    }


    public Router(String id, String name, GPSPoint gpsPosition) {
	this.id = id;
	this.name = name;
	this.gpsPosition = gpsPosition;
    }


    public String getId() {
	return id;
    }


    public void setId(String id) {
	this.id = id;
    }


    public String getName() {
	return name;
    }


    public void setName(String name) {
	this.name = name;
    }


    public String getSuffix() {
	return suffix;
    }


    public void setSuffix(String suffix) {
	this.suffix = suffix;
    }


    public GPSPoint getGpsPosition() {
	return gpsPosition;
    }


    public void setGpsPosition(GPSPoint gpsPosition) {
	this.gpsPosition = gpsPosition;
    }


    public List<StubLink> getStubs() {
	return stubs;
    }


    public void setStubs(List<StubLink> stubs) {
	this.stubs = stubs;
    }
}
