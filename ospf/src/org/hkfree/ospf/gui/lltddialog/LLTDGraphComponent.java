package org.hkfree.ospf.gui.lltddialog;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.collections15.functors.ConstantTransformer;
import org.hkfree.ospf.model.lltd.Device;
import org.hkfree.ospf.model.lltd.Relation;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * Komponenta grafu pro LLTD
 * @author Jan Schovánek
 */
public class LLTDGraphComponent extends JComponent {

    private static final long serialVersionUID = 1L;
    private Graph<Device, Relation> graph = null;
    private Layout<Device, Relation> layout = null;
    private VisualizationViewer<Device, Relation> vv = null;


    public LLTDGraphComponent(List<Device> devices, List<Relation> relations) {
	graph = new SparseMultigraph<Device, Relation>();
	for (Device dev : devices) {
	    graph.addVertex(dev);
	}
	for (Relation rel : relations) {
	    graph.addEdge(rel, rel.getFrom(), rel.getTo());
	}
	layout = new FRLayout<Device, Relation>(graph);
	vv = new VisualizationViewer<Device, Relation>(layout, new Dimension(400, 400));
	GraphZoomScrollPane graphScroll = new GraphZoomScrollPane(vv);
	DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
	graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
	vv.setGraphMouse(graphMouse);
	vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
	vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	vv.setVertexToolTipTransformer(new ToStringLabeller());
	vv.setEdgeToolTipTransformer(new ToStringLabeller());
	vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
	layout.setSize(new Dimension(400, 400));
	LayoutTransition lt = new LayoutTransition<Device, Relation>(vv, vv.getGraphLayout(), layout);
	Animator animator = new Animator(lt);
	animator.start();
	vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
	vv.repaint();
    }


    public VisualizationViewer<Device, Relation> getVisualizationComponent() {
	return this.vv;
    }
}
