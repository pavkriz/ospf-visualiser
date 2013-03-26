package org.hkfree.ospf.gui.mappanel;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;
import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;

/**
 * Třída představující objekt transformeru, který určuje podobu vrcholů a hran grafu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapStyleTransformer {

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


    /**
     * Konstruktor
     * @param mapGraphComponent
     */
    public MapStyleTransformer(MapGraphComponent mapGraphComponent) {
	vertexFillPainter = new Transformer<IVertex, Paint>() {

	    public Paint transform(IVertex v) {
		return v.getColorFill();
	    }
	};
	vertexBorderPainter = new Transformer<IVertex, Paint>() {

	    public Paint transform(IVertex v) {
		return v.getColorStroke();
	    }
	};
	vertexBorderStroker = new Transformer<IVertex, Stroke>() {

	    public Stroke transform(IVertex v) {
		return v.getStroker();
	    }
	};
	vertexLabeler = new Transformer<IVertex, String>() {

	    public String transform(IVertex v) {
		return v.getLabel();
	    }
	};
	vertexToolTiper = new Transformer<IVertex, String>() {

	    public String transform(IVertex v) {
		return v.getDescription();
	    }
	};
	vertexShaper = new Transformer<IVertex, Shape>() {

	    public Shape transform(IVertex v) {
		return v.getShaper();
	    }
	};
	edgeLabeler = new Transformer<IEdge, String>() {

	    public String transform(IEdge e) {
		return e.getLabel();
	    }
	};
	edgeToolTiper = new Transformer<IEdge, String>() {

	    public String transform(IEdge e) {
		return e.getDescription();
	    }
	};
	edgeLinePainter = new Transformer<IEdge, Paint>() {

	    public Paint transform(IEdge e) {
		return e.getLineColor();
	    }
	};
	edgeLineStroker = new Transformer<IEdge, Stroke>() {

	    public Stroke transform(IEdge e) {
		return e.getStroker();
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
