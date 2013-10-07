package org.hkfree.ospf.gui.netstateswin;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.setting.MapGraphComponentMode;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

/**
 * Třída představujcí plugin pro obsluhu událostí po kliku do komponenty grafu
 * @author Jakub Menzel
 */
public class NSGraphMouseClickPlugin extends AbstractGraphMousePlugin implements MouseListener {

    NSWGraphComponent owner = null;


    /**
     * Konstruktor
     * @param owner
     */
    public NSGraphMouseClickPlugin(NSWGraphComponent owner) {
	this(MouseEvent.BUTTON1_MASK);
	this.owner = owner;
    }


    /**
     * Konstruktor
     * @param modifiers
     */
    public NSGraphMouseClickPlugin(int modifiers) {
	super(modifiers);
    }


    @SuppressWarnings("unchecked")
    public void mouseClicked(MouseEvent e) {
	final VisualizationViewer<RouterVertex, LinkEdge> vv = (VisualizationViewer<RouterVertex, LinkEdge>) e.getSource();
	Point2D p = e.getPoint();
	// LEFT MOUSE BUTTON
	if (e.getButton() == MouseEvent.BUTTON1) {
	    GraphElementAccessor<RouterVertex, LinkEdge> pickSupport = vv.getPickSupport();
	    if (pickSupport != null) {
		final RouterVertex routerVertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
		if (routerVertex != null) {
		    switch (owner.getMapGraphComponentMode()) {
			case MapGraphComponentMode.SHOW_SHORTEST_PATH:
			    if (!routerVertex.isMultilink()) {
				owner.setShortestTreeCenter(routerVertex);
				owner.showShortestPath();
			    }
			    break;
			case MapGraphComponentMode.VERTEX_LOCKING:
			    owner.lockVertexPosition(routerVertex);
			    break;
		    }
		}
		final LinkEdge linkEdge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
		if (linkEdge != null) {
		    if (owner.getMapGraphComponentMode() == MapGraphComponentMode.LINK_FAULT) {
			if (linkEdge.getFaultCount() > 0) {
			    owner.getOwner().getManager().showLinkFaultDialog(linkEdge.getLinkIDv4());
			}
		    }
		}
	    }
	    if (owner.getMapGraphComponentMode() == MapGraphComponentMode.ZOOM) {
		owner.zoomPlus(p);
	    }
	}
	// RIGHT MOUSE BUTTON
	if (e.getButton() == MouseEvent.BUTTON3) {
	    if (owner.getMapGraphComponentMode() == MapGraphComponentMode.ZOOM) {
		owner.zoomMinus(p);
	    }
	}
    }


    public void mouseEntered(MouseEvent e) {}


    public void mouseExited(MouseEvent arg0) {}


    public void mousePressed(MouseEvent arg0) {}


    public void mouseReleased(MouseEvent arg0) {}
}
