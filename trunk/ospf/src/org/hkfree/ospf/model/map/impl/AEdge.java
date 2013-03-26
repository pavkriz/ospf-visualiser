package org.hkfree.ospf.model.map.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ResourceBundle;

import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.tools.Factory;

public class AEdge implements IEdge {

    protected ResourceBundle rb = Factory.getRb();
    protected boolean enabled = true;
    protected boolean hover = false;
    protected IVertex vertex1;
    protected IVertex vertex2;


    @Override
    public boolean isHover() {
	return this.hover;
    }


    public void setHover(boolean hover) {
	this.hover = hover;
    }


    @Override
    public boolean isEnabled() {
	return this.enabled;
    }


    @Override
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }


    public void setVertex1(IVertex vertex1) {
	this.vertex1 = vertex1;
    }


    @Override
    public IVertex getVertex1() {
	return this.vertex1;
    }


    public void setVertex2(IVertex vertex2) {
	this.vertex2 = vertex2;
    }


    @Override
    public IVertex getVertex2() {
	return this.vertex2;
    }


    @Override
    public String getLabel() {
	return null;
    }


    @Override
    public String getDescription() {
	if (!vertex1.isVisible() || !vertex2.isVisible()) {
	    return null;
	}
	return getLabel();
    }


    @Override
    public Paint getLineColor() {
	if (!vertex1.isVisible() || !vertex2.isVisible()) {
	    return null;
	}
	if (isHover()) {
	    return Color.ORANGE;
	}
	return new Color(0xcccccc);
    }


    @Override
    public Stroke getStroker() {
	if (!vertex1.isVisible() || !vertex2.isVisible()) {
	    return null;
	}
	if (isHover()) {
	    return new BasicStroke(3);
	}
	return new BasicStroke(1);
    }
}
