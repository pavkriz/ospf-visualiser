package org.hkfree.ospf.model.map.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ResourceBundle;

import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.tools.Factory;

import edu.uci.ics.jung.visualization.RenderContext;

abstract public class AVertex implements IVertex {

    protected ResourceBundle rb = Factory.getRb();
    private boolean enabled = true;
    private boolean visible = true;


    @Override
    public boolean isEnabled() {
	return enabled;
    }


    @Override
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }


    @Override
    public boolean isVisible() {
	return this.visible;
    }


    @Override
    public void setVisible(boolean visible) {
	this.visible = visible;
    }


    @Override
    public String getLabel() {
	return null;
    }


    @Override
    public String getDescription() {
	return null;
    }


    @Override
    public Color getColorFill() {
	if (!isVisible()) {
	    return null;
	}
	if (!isEnabled()) {
	    return Color.WHITE;
	}
	return Color.BLACK;
    }


    @Override
    public Color getColorStroke() {
	if (!isVisible()) {
	    return null;
	}
	return Color.DARK_GRAY;
    }


    @Override
    public Stroke getStroker() {
	if (!isVisible()) {
	    return null;
	}
	return isEnabled() ? new BasicStroke(1) : RenderContext.DASHED;
    }


    @Override
    public Shape getShaper() {
	return new Ellipse2D.Float(-10, -10, 20, 20);
    }
}
