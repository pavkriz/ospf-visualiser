package org.hkfree.ospf.gui.mappanel;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
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
	final VisualizationViewer<IVertex, IEdge> vv = (VisualizationViewer<IVertex, IEdge>) e.getSource();
	Point2D p = e.getPoint(); // vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
	GraphElementAccessor<IVertex, IEdge> pickSupport = vv.getPickSupport();
	if (pickSupport != null) {
	    // ---------------------------------------------------------------------------------
	    // H R A N Y
	    final IEdge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
	    if (edge != null) {
		if (edge instanceof LinkEdge) {
		    final LinkEdge linkEdge = (LinkEdge) edge;
		    JPopupMenu popup = new JPopupMenu();
		    if (linkEdge.isEnabled()) {
			// vypnout spoj
			popup.add(new AbstractAction(rb.getString("mdgcm.0")) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				linkEdge.setEnabled(false);
				if (linkEdge.getVertex2().isMultilink()) {
				    owner.checkMultilinkToDisable(linkEdge.getVertex2());
				}
				owner.tryRecomputeShortestPaths();
				vv.repaint();
			    }
			});
			// zmenit cenu
			popup.add(new AbstractAction(rb.getString("mdgcm.1")) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				ChangeLinkCostDialog dialog = new ChangeLinkCostDialog(linkEdge);
				if (dialog.costDataSaved()) {
				    linkEdge.setCost1v4(dialog.getChangedCost1());
				    linkEdge.setCost2v4(dialog.getChangedCost2());
				}
				owner.tryRecomputeShortestPaths();
				vv.repaint();
			    }
			});
		    } else {
			// zapnout spoj
			popup.add(new AbstractAction(rb.getString("mdgcm.2")) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				owner.enableLinkEdge(linkEdge);
				if (((RouterVertex) linkEdge.getVertex2()).isMultilink()
					&& !linkEdge.getVertex2().isEnabled()) {
				    owner.enableRouterVertex((RouterVertex) linkEdge.getVertex2());
				}
				owner.tryRecomputeShortestPaths();
				vv.repaint();
			    }
			});
		    }
		    if (linkEdge.isExtraAddedEdge()) {
			popup.addSeparator();
			// odstranit pridany spoj
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
	    }
	    // ---------------------------------------------------------------------------------
	    // V R C H O L Y
	    final IVertex vertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
	    if (vertex != null) {
		if (vertex instanceof RouterVertex) {
		    final RouterVertex routerVertex = (RouterVertex) vertex;
		    JPopupMenu popup = new JPopupMenu();
		    if (!routerVertex.isLocked()) {
			// zamknout pozici vrcholu
			popup.add(new AbstractAction(rb.getString("mdgcm.4")) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				owner.lockVertexPosition(routerVertex);
				vv.repaint();
			    }
			});
		    } else {
			// odemknout pozici vrcholu
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
			    // zobrazit strom nejkratsic cest
			    popup.add(new AbstractAction(rb.getString("mdgcm.6")) {

				private static final long serialVersionUID = 1L;


				public void actionPerformed(ActionEvent e) {
				    owner.showShortestPathTreeDialog(routerVertex);
				    vv.repaint();
				}
			    });
			}
			// vypnout router/multispoj
			String title = routerVertex.isMultilink() ? rb.getString("mdgcm.8") : rb.getString("mdgcm.7");
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
			// zapnout router / multispoj
			String title = routerVertex.isMultilink() ? rb.getString("mdgcm.10") : rb.getString("mdgcm.9");
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
			// zobrazit skryte sousedy
			String title = routerVertex.isFullExpanded() ? rb.getString("mdgcm.12") : rb.getString("mdgcm.11");
			popup.add(new AbstractAction(title) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				if (routerVertex.isFullExpanded()) {
				    owner.removeNonPermanentlyDisplayedNeighbours(routerVertex);
				} else {
				    owner.addNeighborsRouters(routerVertex);
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
		    // *************************************************
		    popup.addSeparator();
		    JMenu pm = new JMenu(rb.getString("mdgcm.16"));
		    Set<LLTDModel> lltds = owner.getLLTDmodels(routerVertex.getName());
		    for (LLTDModel model : lltds) {
			JCheckBoxMenuItem i = new JCheckBoxMenuItem(model.getPublicIP(), model.isShow());
			i.setAction(new AbstractAction(model.getPublicIP()) {

			    private static final long serialVersionUID = 1L;


			    public void actionPerformed(ActionEvent e) {
				owner.getOwner().getMapDesignWinManager()
					.showOrHideLLTDDialog(routerVertex.getName(), e.getActionCommand());
				vv.repaint();
			    }
			});
			pm.add(i);
		    }
		    pm.setEnabled(!lltds.isEmpty());
		    popup.add(pm);
		    popup.show(vv, e.getX(), e.getY());
		}
	    }
	}
    }
}
