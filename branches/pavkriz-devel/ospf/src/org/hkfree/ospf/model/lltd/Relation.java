package org.hkfree.ospf.model.lltd;

import java.io.Serializable;

/**
 * Prepravka reprezentující spojení (vztah) mezi dvěma zařízeními v LLTD protokolu
 * @author Jan Schovánek
 */
public class Relation implements Serializable {

    private static final long serialVersionUID = 1L;
    private Device from;
    private Device to;
    private String medium;


    public Relation() {}


    public Device getFrom() {
	return from;
    }


    public void setFrom(Device from) {
	this.from = from;
    }


    public Device getTo() {
	return to;
    }


    public void setTo(Device to) {
	this.to = to;
    }


    public String getMedium() {
	return medium;
    }


    public void setMedium(String medium) {
	this.medium = medium;
    }


    @Override
    public String toString() {
	return "Relation [from=" + from + ", to=" + to + ", medium=" + medium + "]";
    }
}
