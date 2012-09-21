package org.hkfree.ospf.gui.netstateswin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;
import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.setting.MapGraphComponentMode;

import edu.uci.ics.jung.visualization.RenderContext;

//import org.apache.commons.collections15.Predicate;
/**
 * Třída představující objekt transformeru, který určuje podobu vrcholů a hran grafu
 * @author Jakub Menzel
 */
public class NetStateMapStyleTransformer {

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
    private NSWGraphComponent owner = null;


    /**
     * Konstruktor
     */
    public NetStateMapStyleTransformer(final NSWGraphComponent ownerComponent) {
	this.owner = ownerComponent;
	vertexFillPainter = new Transformer<RouterVertex, Paint>() {

	    public Paint transform(RouterVertex r) {
		if (r.isEnabled()) {
		    if (!r.isMultilink()) {
			if (!r.isCenterOfShortestPathTree())
			    return Color.ORANGE;
			else
			    return Color.RED;
		    } else
			return Color.WHITE;
		} else {
		    return Color.WHITE;
		}
	    }
	};
	vertexBorderPainter = new Transformer<RouterVertex, Paint>() {

	    public Paint transform(RouterVertex r) {
		if (r.isEnabled()) {
		    if (r.isActuallyLive())
			return new Color(0, 153, 0);
		    else
			return Color.DARK_GRAY;
		} else {
		    if (r.isActuallyDead())
			return Color.RED;
		    else
			return Color.LIGHT_GRAY;
		}
	    }
	};
	vertexBorderStroker = new Transformer<RouterVertex, Stroke>() {

	    public Stroke transform(RouterVertex r) {
		if (r.isEnabled()) {
		    return new BasicStroke(1);
		} else {
		    return RenderContext.DASHED;
		}
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
		if (!r.isMultilink())
		    return "<html><b>" + r.getName() + "</b><br>" + r.getDescription() + "</html>";
		else
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
	/**
	 * visiblePredicate = new
	 * Predicate<Context<Graph<RouterVertex,String>,RouterVertex>>() {
	 * public boolean evaluate(Context<Graph<RouterVertex, String>,
	 * RouterVertex> argument) { //return argument.element.isMultispoj();
	 * return true; } };
	 */
	edgeLabeler = new Transformer<LinkEdge, String>() {

	    public String transform(LinkEdge e) {
//		if (e.isEnabled()) {
			//zmena, vzdy se zobrazuje cena spoje (pro vypadly se zobrazi posledni znama)
		    return owner.getActualNetStateLinkEdgeLabel(e);
//		} else {
//		    return "";
//		    
//		}
	    }
	};
	edgeToolTiper = new Transformer<LinkEdge, String>() {

	    public String transform(LinkEdge e) {
		if (owner.getMapGraphComponentMode() != MapGraphComponentMode.LINK_FAULT)
		    return owner.getActualNetStateLinkEdgeDescription(e);
		else
		    return e.getLinkFaultDescription();
	    }
	};
	edgeLinePainter = new Transformer<LinkEdge, Paint>() {

	    public Paint transform(LinkEdge le) {
		if (le.isHover()) {
		    return Color.ORANGE;
		}
		if (owner.getMapGraphComponentMode() != MapGraphComponentMode.LINK_FAULT) {
		    if (le.isEnabled()) {
			if (le.isEdgeOfShortestPath())
			    return Color.GREEN;
			else if (le.isActuallyLive())
			    return new Color(0, 153, 0);
			else
			    return Color.DARK_GRAY;
		    } else {
			if (le.isActuallyDead())
			    return Color.RED;
			else
			    return Color.LIGHT_GRAY;
		    }
		} else {
		    return new Color(255, 255 - (int) le.getFaultIntensity(), 255 - (int) le.getFaultIntensity());
		}
	    }
	};
	edgeLineStroker = new Transformer<LinkEdge, Stroke>() {

	    public Stroke transform(LinkEdge le) {
		if (le.isHover()) {
		    return new BasicStroke(3);
		}
		if (le.isEnabled()) {
		    if (le.isEdgeOfShortestPath())
			return new BasicStroke(3);
		    else
			return new BasicStroke(1);
		} else {
		    return RenderContext.DASHED;
		}
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
