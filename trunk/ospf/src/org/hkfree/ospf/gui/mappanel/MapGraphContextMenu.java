package org.hkfree.ospf.gui.mappanel;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.tools.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

/**
 * Třída představující kontextové menu komponenty pro vykreslení grafu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapGraphContextMenu extends AbstractPopupGraphMousePlugin implements MouseListener {

    private ResourceBundle rb = Factory.getRb();
    private MapGraphComponent owner = null;


    /**
     * Konstruktor
     * @param owner
     */
    public MapGraphContextMenu(MapGraphComponent owner) {
	this(MouseEvent.BUTTON3_MASK);
	this.owner = owner;
    }


    /**
     * Konstruktor
     * @param modifiers
     */
    public MapGraphContextMenu(int modifiers) {
	super(modifiers);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void handlePopup(MouseEvent e) {
	final VisualizationViewer<RouterVertex, LinkEdge> vv = (VisualizationViewer<RouterVertex, LinkEdge>) e.getSource();
	Point2D p = e.getPoint(); // vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
	GraphElementAccessor<RouterVertex, LinkEdge> pickSupport = vv.getPickSupport();
	if (pickSupport != null) {
	    // H R A N Y - LinkEdge
	    final LinkEdge linkEdge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
	    if (linkEdge != null) {
		JPopupMenu popup = new JPopupMenu();
		if (linkEdge.isEnabled()) {
		    popup.add(new AbstractAction(rb.getString("mdgcm.0")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    linkEdge.setEnabled(false);
			    if (linkEdge.getRVertex2().isMultilink()) {
				owner.checkMultilinkToDisable(linkEdge.getRVertex2());
			    }
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		    popup.add(new AbstractAction(rb.getString("mdgcm.1")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    ChangeLinkCostDialog dialog = new ChangeLinkCostDialog(linkEdge);
			    if (dialog.costDataSaved()) {
				linkEdge.setCost1(dialog.getChangedCost1());
				linkEdge.setCost2(dialog.getChangedCost2());
			    }
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		} else {
		    popup.add(new AbstractAction(rb.getString("mdgcm.2")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.enableLinkEdge(linkEdge);
			    if (linkEdge.getRVertex2().isMultilink() && !linkEdge.getRVertex2().isEnabled()) {
				owner.enableRouterVertex(linkEdge.getRVertex2());
			    }
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		}
		if (linkEdge.isExtraAddedEdge()) {
		    popup.addSeparator();
		    popup.add(new AbstractAction(rb.getString("mdgcm.3")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.removeExtraAddedLEdge(linkEdge);
			    vv.repaint();
			}
		    });
		}
		popup.show(vv, e.getX(), e.getY());
	    }
	    // V R C H O L Y - RouterVertex
	    final RouterVertex routerVertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
	    if (routerVertex != null) {
		JPopupMenu popup = new JPopupMenu();
		if (!routerVertex.isLocked()) {
		    popup.add(new AbstractAction(rb.getString("mdgcm.4")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.lockVertexPosition(routerVertex);
			    vv.repaint();
			}
		    });
		} else {
		    popup.add(new AbstractAction(rb.getString("mdgcm.5")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.unlockVertexPosition(routerVertex);
			    vv.repaint();
			}
		    });
		}
		// *************************************************
		if (routerVertex.isEnabled()) {
		    if (!routerVertex.isMultilink()) {
			popup.add(new AbstractAction(rb.getString("mdgcm.6")) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				owner.showShortestPathTreeDialog(routerVertex);
				vv.repaint();
			    }
			});
		    }
		    String title = rb.getString("mdgcm.7");
		    if (routerVertex.isMultilink())
			title = rb.getString("mdgcm.8");
		    popup.add(new AbstractAction(title) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.disableRouterVertex(routerVertex);
			    owner.tryDisableIncidentMultilink(routerVertex);
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		} else {
		    String title = rb.getString("mdgcm.9");
		    if (routerVertex.isMultilink())
			title = rb.getString("mdgcm.10");
		    popup.add(new AbstractAction(title) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.enableRouterVertex(routerVertex);
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		}
		// *************************************************
		if (!routerVertex.isMultilink()) {
		    popup.addSeparator();
		    String title = rb.getString("mdgcm.11");
		    if (routerVertex.isFullExpanded()) {
			title = rb.getString("mdgcm.12");
		    }
		    popup.add(new AbstractAction(title) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    if (routerVertex.isFullExpanded()) {
				owner.removeNonPermanentlyDisplayedNeighbours(routerVertex);
			    } else {
				owner.addNeighborsRouterVertexes(routerVertex);
			    }
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		    // ---------------
		    String title2 = rb.getString("mdgcm.13");
		    if (routerVertex.isPermanentlyDisplayed()) {
			title2 = rb.getString("mdgcm.14");
		    }
		    popup.add(new AbstractAction(title2) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    if (routerVertex.isPermanentlyDisplayed()) {
				routerVertex.setPermanentlyDisplayed(false);
			    } else {
				routerVertex.setPermanentlyDisplayed(true);
			    }
			    owner.tryRecomputeShortestPaths();
			    vv.repaint();
			}
		    });
		}
		// *************************************************
		if (routerVertex.isExtraAddedVertex()) {
		    popup.addSeparator();
		    popup.add(new AbstractAction(rb.getString("mdgcm.15")) {

			private static final long serialVersionUID = 1L;


			public void actionPerformed(ActionEvent e) {
			    owner.removeExtraAddedRVertex(routerVertex);
			    vv.repaint();
			}
		    });
		}
		popup.show(vv, e.getX(), e.getY());
	    }
	}
    }
}
