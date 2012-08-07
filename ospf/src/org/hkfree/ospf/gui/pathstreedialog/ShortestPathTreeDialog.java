package org.hkfree.ospf.gui.pathstreedialog;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ResourceBundle;

import javax.swing.JDialog;

import org.apache.commons.collections15.Transformer;
import org.hkfree.ospf.gui.mappanel.OspfModalGraphMouse;
import org.hkfree.ospf.model.map.EdgeOfSPT;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.tools.Factory;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;

/**
 * Třída představující dilaog pro zobrazení stromu nejkratších cest
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class ShortestPathTreeDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private VisualizationViewer<RouterVertex, EdgeOfSPT> vv = null;
    private Forest<RouterVertex, EdgeOfSPT> graph = null;
    private TreeLayout<RouterVertex, EdgeOfSPT> layout = null;


    /**
     * Konstruktor
     * @param graph
     * @param rootVertex
     */
    public ShortestPathTreeDialog(Forest<RouterVertex, EdgeOfSPT> graph, final RouterVertex rootVertex) {
	this.graph = graph;
	layout = new TreeLayout<RouterVertex, EdgeOfSPT>((Forest<RouterVertex, EdgeOfSPT>) this.graph);
	vv = new VisualizationViewer<RouterVertex, EdgeOfSPT>(layout);
	OspfModalGraphMouse<RouterVertex, EdgeOfSPT> graphMouse = new OspfModalGraphMouse<RouterVertex, EdgeOfSPT>();
	graphMouse.setMode(Mode.TRANSFORMING);
	vv.setGraphMouse(graphMouse);
	vv.setBackground(Color.WHITE);
	vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<RouterVertex, Paint>() {

	    public Paint transform(RouterVertex rv) {
		if (!rv.equals(rootVertex))
		    return Color.ORANGE;
		else
		    return Color.RED;
	    }
	});
	vv.getRenderContext().setVertexLabelTransformer(new Transformer<RouterVertex, String>() {

	    public String transform(RouterVertex r) {
		return r.getName();
	    }
	});
	vv.setVertexToolTipTransformer(new Transformer<RouterVertex, String>() {

	    public String transform(RouterVertex rv) {
		return rv.getDescription();
	    }
	});
	vv.getRenderContext().setEdgeLabelTransformer(new Transformer<EdgeOfSPT, String>() {

	    public String transform(EdgeOfSPT e) {
		return Integer.toString(e.getCost());
	    }
	});
	vv.setEdgeToolTipTransformer(new Transformer<EdgeOfSPT, String>() {

	    public String transform(EdgeOfSPT e) {
		return rb.getString("sptd.0") + ": " + e.getLinkID();
	    }
	});
	AbstractEdgeShapeTransformer<RouterVertex, EdgeOfSPT> aesf = (AbstractEdgeShapeTransformer<RouterVertex, EdgeOfSPT>) vv
		.getRenderContext().getEdgeShapeTransformer();
	aesf.setControlOffsetIncrement(0);
	ScalingControl scaler = new CrossoverScalingControl();
	scaler.scale(vv, 2.5f, new Point2D.Double(layout.transform(rootVertex).getX() * 1.5, 50));
	this.setSize(800, 500);
	this.setLocationRelativeTo(null);
	this.getContentPane().add(new GraphZoomScrollPane(vv));
	this.setTitle(rb.getString("sptd.title"));
	this.setModal(true);
    }
}
