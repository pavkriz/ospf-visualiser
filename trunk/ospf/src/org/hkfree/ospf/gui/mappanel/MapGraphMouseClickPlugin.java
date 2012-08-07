package org.hkfree.ospf.gui.mappanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.setting.MapGraphComponentMode;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

/**
 * Třída představující plugin určující obsluhu událostí kliku do komponenty pro vykreslení grafu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapGraphMouseClickPlugin extends AbstractGraphMousePlugin implements MouseListener {

    MapGraphComponent owner = null;


    /**
     * Konstruktor
     * @param owner
     */
    public MapGraphMouseClickPlugin(MapGraphComponent owner) {
	this(MouseEvent.BUTTON1_MASK);
	this.owner = owner;
    }


    /**
     * Konstrutkor
     * @param modifiers
     */
    public MapGraphMouseClickPlugin(int modifiers) {
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
			case MapGraphComponentMode.SHOW_NEIGHBOURS:
			    if (!routerVertex.isMultilink()) {
				if (routerVertex.isPermanentlyDisplayed()) {
				    if (routerVertex.isFullExpanded()) {
					owner.removeNonPermanentlyDisplayedNeighbours(routerVertex);
				    } else {
					owner.addNeighborsRouterVertexes(routerVertex);
				    }
				} else {
				    routerVertex.setPermanentlyDisplayed(true);
				}
				vv.repaint();
			    }
			    break;
			case MapGraphComponentMode.SHOW_SHORTEST_PATH:
			    if (!routerVertex.isMultilink()) {
				owner.setShortestTreeCenter(routerVertex);
				owner.showShortestPath();
			    }
			    break;
			case MapGraphComponentMode.VERTEX_LOCKING:
			    owner.lockVertexPosition(routerVertex);
			    break;
			case MapGraphComponentMode.GPS_POSITIONING:
			    owner.setVertexPositionByGPS(routerVertex);
			    break;
			case MapGraphComponentMode.LEDGE_ADDING:
			    owner.addNewLinkEdge(routerVertex);
			    vv.repaint();
			    break;
			case MapGraphComponentMode.TWO_ROUTERS_SHORTEST_PATH:
			    if (routerVertex.isEnabled() && !routerVertex.isMultilink()) {
				owner.assignRouterToTwoRoutesShortesPath(routerVertex);
				vv.repaint();
			    }
			    break;
		    }
		}
		final LinkEdge linkEdge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
		if (linkEdge != null) {
		    switch (owner.getMapGraphComponentMode()) {
			case MapGraphComponentMode.COST_CHANGING:
			    ChangeLinkCostDialog dialog = new ChangeLinkCostDialog(linkEdge);
			    if (dialog.costDataSaved()) {
				linkEdge.setCost1(dialog.getChangedCost1());
				linkEdge.setCost2(dialog.getChangedCost2());
			    }
			    vv.repaint();
			    break;
		    }
		}
	    }
	    if (owner.getMapGraphComponentMode() == MapGraphComponentMode.RVERTEX_ADDING) {
		owner.addNewRouterVertex(p);
		vv.repaint();
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
