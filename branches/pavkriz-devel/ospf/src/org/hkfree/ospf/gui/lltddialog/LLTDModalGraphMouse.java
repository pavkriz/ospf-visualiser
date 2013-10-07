package org.hkfree.ospf.gui.lltddialog;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;

import org.hkfree.ospf.model.lltd.Device;
import org.hkfree.ospf.model.lltd.Relation;

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

public class LLTDModalGraphMouse<V, E> extends AbstractModalGraphMouse implements ModalGraphMouse, ItemSelectable,
        Serializable {

    private static final long serialVersionUID = 1L;
    private LLTDMapDialog lltdDialog = null;


    public LLTDModalGraphMouse(LLTDMapDialog lltdDialog) {
	this(1.1f, 1 / 1.1f);
	this.lltdDialog = lltdDialog;
    }


    protected LLTDModalGraphMouse(float in, float out) {
	super(in, out);
	loadPlugins();
	setModeKeyListener(new ModeKeyAdapter(this));
    }


    @Override
    protected void loadPlugins() {
	pickingPlugin = new PickingGraphMousePlugin<V, E>();
	animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<V, E>();
	translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
	scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
	rotatingPlugin = new RotatingGraphMousePlugin();
	shearingPlugin = new ShearingGraphMousePlugin();
	add(scalingPlugin);
	setMode(Mode.TRANSFORMING);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void mouseClicked(MouseEvent e) {
	final VisualizationViewer<Device, Relation> vv = (VisualizationViewer<Device, Relation>) e.getSource();
	Point2D p = e.getPoint();
	GraphElementAccessor<Device, Relation> pickSupport = vv.getPickSupport();
	if (pickSupport != null) {
	    Relation rel = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
	    if (rel != null) {
		lltdDialog.actualizePropertyPanel(rel);
	    }
	    Device dev = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
	    if (dev != null) {
		lltdDialog.actualizePropertyPanel(dev);
	    }
	}
	super.mouseClicked(e);
    }

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
