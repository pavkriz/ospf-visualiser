package org.hkfree.ospf.gui.netstateswin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;
import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.setting.MapGraphComponentMode;

import edu.uci.ics.jung.visualization.RenderContext;

/**
 * Třída představující objekt transformeru, který určuje podobu vrcholů a hran grafu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetStateMapStyleTransformer {

    private Transformer<IVertex, Paint> vertexFillPainter = null;
    private Transformer<IVertex, Paint> vertexBorderPainter = null;
    private Transformer<IVertex, Stroke> vertexBorderStroker = null;
    private Transformer<IVertex, String> vertexLabeler = null;
    private Transformer<IVertex, String> vertexToolTiper = null;
    private Transformer<IVertex, Shape> vertexShaper = null;
    private Transformer<IEdge, String> edgeLabeler = null;
    private Transformer<IEdge, String> edgeToolTiper = null;
    private Transformer<IEdge, Paint> edgeLinePainter = null;
    private Transformer<IEdge, Stroke> edgeLineStroker = null;
    private NSWGraphComponent owner = null;


    /**
     * Konstruktor
     */
    public NetStateMapStyleTransformer(final NSWGraphComponent ownerComponent) {
	this.owner = ownerComponent;
	vertexFillPainter = new Transformer<IVertex, Paint>() {

	    @Override
	    public Paint transform(IVertex v) {
		if (v.isEnabled()) {
		    if (v instanceof RouterVertex) {
			RouterVertex rv = (RouterVertex) v;
			if (!rv.isMultilink()) {
			    if (!rv.isCenterOfShortestPathTree())
				return Color.ORANGE;
			    else
				return Color.RED;
			}
		    }
		}
		return Color.WHITE;
	    }
	};
	vertexBorderPainter = new Transformer<IVertex, Paint>() {

	    @Override
	    public Paint transform(IVertex v) {
		if (v instanceof RouterVertex) {
		    RouterVertex rv = (RouterVertex) v;
		    if (v.isEnabled()) {
			if (rv.isActuallyLive())
			    return new Color(0, 153, 0);
			else
			    return Color.DARK_GRAY;
		    } else {
			if (rv.isActuallyDead())
			    return Color.RED;
			else
			    return Color.LIGHT_GRAY;
		    }
		}
		return Color.WHITE;
	    }
	};
	vertexBorderStroker = new Transformer<IVertex, Stroke>() {

	    @Override
	    public Stroke transform(IVertex v) {
		if (v.isEnabled()) {
		    return new BasicStroke(1);
		} else {
		    return RenderContext.DASHED;
		}
	    }
	};
	vertexLabeler = new Transformer<IVertex, String>() {

	    @Override
	    public String transform(IVertex v) {
		if (v instanceof RouterVertex) {
		    if (!((RouterVertex) v).isMultilink())
			return v.getLabel();
		}
		return "";
	    }
	};
	vertexToolTiper = new Transformer<IVertex, String>() {

	    @Override
	    public String transform(IVertex v) {
		if (v instanceof RouterVertex) {
		    if (!((RouterVertex) v).isMultilink()) {
			return "<html><b>" + v.getLabel() + "</b><br>" + v.getDescription() + "</html>";
		    }
		}
		return v.getLabel();
	    }
	};
	vertexShaper = new Transformer<IVertex, Shape>() {

	    @Override
	    public Shape transform(IVertex v) {
		if (v instanceof RouterVertex) {
		    if (((RouterVertex) v).isMultilink()) {
			return new Rectangle2D.Float(-6, -6, 12, 12);
		    }
		}
		return new Ellipse2D.Float(-10, -10, 20, 20);
	    }
	};
	edgeLabeler = new Transformer<IEdge, String>() {

	    @Override
	    public String transform(IEdge e) {
		// zmena, vzdy se zobrazuje cena spoje (pro vypadly se zobrazi posledni znama)
		if (e instanceof LinkEdge) {
		    return owner.getActualNetStateLinkEdgeLabel((LinkEdge) e);
		}
		return "";
	    }
	};
	edgeToolTiper = new Transformer<IEdge, String>() {

	    @Override
	    public String transform(IEdge e) {
		if (e instanceof LinkEdge) {
		    if (owner.getMapGraphComponentMode() != MapGraphComponentMode.LINK_FAULT)
			return owner.getActualNetStateLinkEdgeDescription((LinkEdge) e);
		    else
			return ((LinkEdge) e).getLinkFaultDescription();
		}
		return "";
	    }
	};
	edgeLinePainter = new Transformer<IEdge, Paint>() {

	    @Override
	    public Paint transform(IEdge e) {
		if (e.isHover()) {
		    return Color.ORANGE;
		}
		if (e instanceof LinkEdge) {
		    LinkEdge le = (LinkEdge) e;
		    if (owner.getMapGraphComponentMode() != MapGraphComponentMode.LINK_FAULT) {
			if (e.isEnabled()) {
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
		return Color.BLACK;
	    }
	};
	edgeLineStroker = new Transformer<IEdge, Stroke>() {

	    @Override
	    public Stroke transform(IEdge e) {
		if (e.isHover()) {
		    return new BasicStroke(3);
		}
		if (e.isEnabled()) {
		    if (e instanceof LinkEdge) {
			if (((LinkEdge) e).isEdgeOfShortestPath())
			    return new BasicStroke(3);
			else
			    return new BasicStroke(1);
		    }
		}
		return RenderContext.DASHED;
	    }
	};
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IVertex, Paint> getVertexFillPainter() {
	return vertexFillPainter;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IVertex, Paint> getVertexBorderPainter() {
	return vertexBorderPainter;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IVertex, Stroke> getVertexBorderStroker() {
	return vertexBorderStroker;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IVertex, String> getVertexLabeler() {
	return vertexLabeler;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IVertex, String> getVertexToolTiper() {
	return vertexToolTiper;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IVertex, Shape> getVertexShaper() {
	return vertexShaper;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IEdge, String> getEdgeLabeler() {
	return edgeLabeler;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IEdge, String> getEdgeTooltiper() {
	return edgeToolTiper;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IEdge, Paint> getEdgeLinePainter() {
	return edgeLinePainter;
    }


    /**
     * Vrací transformer
     * @return transformer
     */
    public Transformer<IEdge, Stroke> getEdgeLineStroker() {
	return edgeLineStroker;
    }
}
