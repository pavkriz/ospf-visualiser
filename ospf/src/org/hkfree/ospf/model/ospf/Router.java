package org.hkfree.ospf.model.ospf;

import java.util.HashSet;
import java.util.Set;

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
    private Set<ExternalLSA> externalLsa = new HashSet<ExternalLSA>();
    private Set<StubLink> stubs = new HashSet<StubLink>();


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


    public Set<StubLink> getStubs() {
	return stubs;
    }


    public void setStubs(Set<StubLink> stubs) {
	this.stubs = stubs;
    }


    public Set<ExternalLSA> getExternalLsa() {
	return externalLsa;
    }


    public void setExternalLsa(Set<ExternalLSA> externalLsa) {
	this.externalLsa = externalLsa;
    }
}
