package org.hkfree.ospf.model.map.impl;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;

public class RelationEdge extends AEdge implements Serializable {

    private static final long serialVersionUID = 1L;
    private String medium;


    @Override
    public String getLabel() {
	if (!vertex1.isVisible() || !vertex2.isVisible()) {
	    return null;
	}
	if (getMedium() == null) {
	    return "-";
	}
	if (getMedium().equals("00")
		|| getMedium().equals("02")) {
	    return rb.getString("lltd.medium." + getMedium());
	}
	return getMedium();
    }


    @Override
    public String getDescription() {
	if (!vertex1.isVisible() || !vertex2.isVisible()) {
	    return null;
	}
	String result = "<html><body>";
	result += "<b>" + rb.getString("re.1") + "</b>";
	result += "<br>" + rb.getString("re.0") + ": ";
	if (getMedium() == null) {
	    result += "<b>-</b>";
	} else if (getMedium().equals("00") || getMedium().equals("02")) {
	    result += "<b>" + rb.getString("lltd.medium." + getMedium()) + "</b>";
	} else {
	    result += "<b>" + getMedium() + "</b>";
	}
	result += "</body></html>";
	return result;
    }


    @Override
    public Paint getLineColor() {
	if (!vertex1.isVisible() || !vertex2.isVisible()) {
	    return null;
	}
	if (isHover()) {
	    return Color.ORANGE;
	}
	return Color.MAGENTA;
    }


    public String getMedium() {
	return medium;
    }


    public void setMedium(String medium) {
	this.medium = medium;
    }
}
