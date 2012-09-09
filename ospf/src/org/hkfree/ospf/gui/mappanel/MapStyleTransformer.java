package org.hkfree.ospf.gui.mappanel;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ResourceBundle;

import org.apache.commons.collections15.Transformer;
import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující objekt transformeru, který určuje podobu vrcholů a hran grafu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapStyleTransformer {

    private ResourceBundle rb = Factory.getRb();
    private Transformer<RouterVertex, Paint> vertexFillPainter = null;
    private Transformer<RouterVertex, Paint> vertexBorderPainter = null;
    private Transformer<RouterVertex, Stroke> vertexBorderStroker = null;
    private Transformer<RouterVertex, String> vertexLabeler = null;
    private Transformer<RouterVertex, String> vertexToolTiper = null;
    private Transformer<RouterVertex, Shape> vertexShaper = null;
    private Transformer<LinkEdge, String> edgeLabeler = null;
    private Transformer<LinkEdge, String> edgeToolTiper = null;
    private Transformer<LinkEdge, Paint> edgeLinePainter = null;
    private Transformer<LinkEdge, Stroke> edgeLineStroker = null;
    private MapGraphComponent mdwGraphComponent = null;


    /**
     * Konstruktor
     * @param mapGraphComponent
     */
    public MapStyleTransformer(MapGraphComponent mapGraphComponent) {
	this.mdwGraphComponent = mapGraphComponent;
	vertexFillPainter = new Transformer<RouterVertex, Paint>() {

	    public Paint transform(RouterVertex r) {
		return r.getColorFill();
	    }
	};
	vertexBorderPainter = new Transformer<RouterVertex, Paint>() {

	    public Paint transform(RouterVertex rv) {
		return rv.getColorStroke();
	    }
	};
	vertexBorderStroker = new Transformer<RouterVertex, Stroke>() {

	    public Stroke transform(RouterVertex rv) {
		return rv.getStroker();
	    }
	};
	vertexLabeler = new Transformer<RouterVertex, String>() {

	    public String transform(RouterVertex r) {
		if (!r.isMultilink())
		    return r.getName();
		else
		    return "";
	    }
	};
	vertexToolTiper = new Transformer<RouterVertex, String>() {

	    public String transform(RouterVertex r) {
		if (!r.isMultilink()) {
		    String tt = "<html>" + r.getName() + "<br><b>" + r.getDescription() + "</b><br><br>";
		    if (r.isPermanentlyDisplayed()) {
			tt += rb.getString("mst.0") + "<br>";
		    } else {
			tt += rb.getString("mst.1") + "<br>";
		    }
		    if (r.isFullExpanded()) {
			tt += rb.getString("mst.2") + "<br>";
		    } else {
			tt += rb.getString("mst.3") + "<br>";
		    }
		    tt += "</html>";
		    return tt;
		} else
		    return r.getName();
	    }
	};
	vertexShaper = new Transformer<RouterVertex, Shape>() {

	    public Shape transform(RouterVertex r) {
		if (!r.isMultilink())
		    return new Ellipse2D.Float(-10, -10, 20, 20);
		else
		    return new Rectangle2D.Float(-6, -6, 12, 12);
	    }
	};
	edgeLabeler = new Transformer<LinkEdge, String>() {

	    public String transform(LinkEdge e) {
		if (e.isEnabled()) {
		    if (e.getRVertex2() != null && e.getRVertex2().isMultilink())
			return Integer.toString(e.getCost1());
		    return e.getCost1() + " - " + e.getCost2();
		} else {
		    return "";
		}
	    }
	};
	edgeToolTiper = new Transformer<LinkEdge, String>() {

	    public String transform(LinkEdge e) {
		return e.getLinkDescription();
	    }
	};
	edgeLinePainter = new Transformer<LinkEdge, Paint>() {

	    public Paint transform(LinkEdge le) {
		return le.getLineColor(mdwGraphComponent.getMapGraphComponentMode());
	    }
	};
	edgeLineStroker = new Transformer<LinkEdge, Stroke>() {

	    public Stroke transform(LinkEdge le) {
		return le.getStroker();
	    }
	};
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<RouterVertex, Paint> getVertexFillPainter() {
	return vertexFillPainter;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<RouterVertex, Paint> getVertexBorderPainter() {
	return vertexBorderPainter;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<RouterVertex, Stroke> getVertexBorderStroker() {
	return vertexBorderStroker;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<RouterVertex, String> getVertexLabeler() {
	return vertexLabeler;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<RouterVertex, String> getVertexToolTiper() {
	return vertexToolTiper;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<RouterVertex, Shape> getVertexShaper() {
	return vertexShaper;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<LinkEdge, String> getEdgeLabeler() {
	return edgeLabeler;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<LinkEdge, String> getEdgeTooltiper() {
	return edgeToolTiper;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<LinkEdge, Paint> getEdgeLinePainter() {
	return edgeLinePainter;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<LinkEdge, Stroke> getEdgeLineStroker() {
	return edgeLineStroker;
    }
}
