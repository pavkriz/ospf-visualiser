package org.hkfree.ospf.gui.mappanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
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
	final VisualizationViewer<IVertex, IEdge> vv = (VisualizationViewer<IVertex, IEdge>) e.getSource();
	Point2D p = e.getPoint();
	// LEFT MOUSE BUTTON
	if (e.getButton() == MouseEvent.BUTTON1) {
	    GraphElementAccessor<IVertex, IEdge> pickSupport = vv.getPickSupport();
	    if (pickSupport != null) {
		final IVertex vertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
		if (vertex != null) {
		    if (vertex instanceof RouterVertex) {
			RouterVertex routerVertex = (RouterVertex) vertex;
			switch (owner.getMapGraphComponentMode()) {
			    case MapGraphComponentMode.SHOW_NEIGHBOURS:
				if (!routerVertex.isMultilink()) {
				    if (routerVertex.isPermanentlyDisplayed()) {
					if (routerVertex.isFullExpanded()) {
					    owner.removeNonPermanentlyDisplayedNeighbours(routerVertex);
					} else {
					    owner.addNeighborsRouters(routerVertex);
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
		}
		final IEdge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
		if (edge != null) {
		    if (edge instanceof LinkEdge) {
			LinkEdge linkEdge = (LinkEdge) edge;
			switch (owner.getMapGraphComponentMode()) {
			    case MapGraphComponentMode.COST_CHANGING:
				ChangeLinkCostDialog dialog = new ChangeLinkCostDialog(linkEdge);
				if (dialog.costDataSaved()) {
				    linkEdge.setCost1v4(dialog.getChangedCost1());
				    linkEdge.setCost2v4(dialog.getChangedCost2());
				}
				vv.repaint();
				break;
			}
		    }
		}
	    }
	    if (owner.getMapGraphComponentMode() == MapGraphComponentMode.RVERTEX_ADDING) {
		owner.addNewVertex(p);
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
