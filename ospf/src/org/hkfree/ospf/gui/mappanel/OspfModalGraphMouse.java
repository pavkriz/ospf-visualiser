package org.hkfree.ospf.gui.mappanel;

/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Mar 8, 2005
 *
 */
import java.awt.Component;
import java.awt.Cursor;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

/**
 * Třída představující graphMouse
 * metody jsou přepsány za účelem invertování ovládání zoomování
 * a por zvyrazneni spoje nad kterym se nachazi kurzor mysi
 * @author Jakub Menzel
 * @author Jan Schovánek
 * @param <V>
 * @param <E>
 */
public class OspfModalGraphMouse<V, E> extends AbstractModalGraphMouse implements ModalGraphMouse, ItemSelectable, Serializable {

    private static final long serialVersionUID = -6553269221133983493L;
    
    MapGraphComponent owner;
    LinkEdge edgeHovered;


    /**
     * Konstruktor
     */
    public OspfModalGraphMouse(MapGraphComponent owner) {
	this(1.1f, 1 / 1.1f);
	this.owner = owner;
    }


    /**
     * Konstruktor
     */
    public OspfModalGraphMouse() {
	this(1.1f, 1 / 1.1f);
    }


    /**
     * Konstrutkor
     * @param in
     * @param out
     */
    public OspfModalGraphMouse(float in, float out) {
	super(in, out);
	loadPlugins();
	setModeKeyListener(new ModeKeyAdapter(this));
    }


    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mouseMoved(MouseEvent e) {
	boolean isrepaint = false;
	if (edgeHovered != null) {
	    isrepaint = true;
	    edgeHovered.setHover(false);
	}
	final VisualizationViewer<RouterVertex, LinkEdge> vv = (VisualizationViewer<RouterVertex, LinkEdge>) e.getSource();
	Point2D p = e.getPoint();
	GraphElementAccessor<RouterVertex, LinkEdge> pickSupport = vv.getPickSupport();
	if (pickSupport != null) {
	    edgeHovered = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
	    if (edgeHovered != null) {
		edgeHovered.setHover(true);
		isrepaint = true;
	    }
	}
	if (isrepaint) {
	    vv.repaint();
	}
	super.mouseMoved(e);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void mouseClicked(MouseEvent e) {
	if (owner != null) {	//pokud owner neni null, tak je instanci MapPanelu
	    final VisualizationViewer<RouterVertex, LinkEdge> vv = (VisualizationViewer<RouterVertex, LinkEdge>) e.getSource();
	    Point2D p = e.getPoint();
	    GraphElementAccessor<RouterVertex, LinkEdge> pickSupport = vv.getPickSupport();
	    if (pickSupport != null) {
		LinkEdge le = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
		if (le != null) {
		    owner.getOwner().setPropertiesPanel(le);
		}
		RouterVertex rv = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
		if (rv != null) {
		    owner.getOwner().setPropertiesPanel(rv);
		}
	    }
	}
	super.mouseClicked(e);
    }


    /**
     * Načte pluginy
     */
    @Override
    protected void loadPlugins() {
	pickingPlugin = new PickingGraphMousePlugin<V, E>();
	animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<V, E>();
	translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
	scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, out, in);
	rotatingPlugin = new RotatingGraphMousePlugin();
	shearingPlugin = new ShearingGraphMousePlugin();
	add(scalingPlugin);
	setMode(Mode.TRANSFORMING);
    }

    /**
     * 
     */
    public static class ModeKeyAdapter extends KeyAdapter {

	private char t = 't';
	private char p = 'p';
	protected ModalGraphMouse graphMouse;


	public ModeKeyAdapter(ModalGraphMouse graphMouse) {
	    this.graphMouse = graphMouse;
	}


	public ModeKeyAdapter(char t, char p, ModalGraphMouse graphMouse) {
	    this.t = t;
	    this.p = p;
	    this.graphMouse = graphMouse;
	}


	@Override
	public void keyTyped(KeyEvent event) {
	    char keyChar = event.getKeyChar();
	    if (keyChar == t) {
		((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		graphMouse.setMode(Mode.TRANSFORMING);
	    } else if (keyChar == p) {
		((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		graphMouse.setMode(Mode.PICKING);
	    }
	}
    }
}