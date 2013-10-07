package org.hkfree.ospf.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;
import org.hkfree.ospf.model.map.IEdge;
import org.hkfree.ospf.model.map.IVertex;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * Implementace Force Directed Layoutu
 * @author Jan Schovánek
 * @param <V> router
 * @param <E> edge
 */
public class JSLayout<V extends IVertex, E extends IEdge> extends AbstractLayout<V, E> implements IterativeContext {

    private int currentIteration;
    private int maxIterations = 400;
    private Rectangle2D innerBounds = new Rectangle2D.Double();
    private Map<V, JSVertexData> jsVertexData =
	    LazyMap.decorate(new HashMap<V, JSVertexData>(), new Factory<JSVertexData>() {

		public JSVertexData create() {
		    return new JSVertexData();
		}
	    });


    // private boolean checked = false;
    public JSLayout(Graph<V, E> g) {
	super(g);
    }


    public JSLayout(Graph<V, E> g, Dimension d) {
	super(g, new RandomLocationTransformer<V>(d), d);
	initialize();
    }


    @Override
    public void setSize(Dimension size) {
	if (!initialized)
	    setInitializer(new RandomLocationTransformer<V>(size));
	super.setSize(size);
	double t = size.width / 50.0;
	innerBounds.setFrameFromDiagonal(t, t, size.width - t, size.height - t);
    }


    @Override
    public void reset() {
	doInit();
    }


    @Override
    public void initialize() {
	doInit();
    }


    private void doInit() {
	Graph<V, E> graph = getGraph();
	Dimension d = getSize();
	if (graph != null && d != null) {
	    currentIteration = 0;
	}
    }

    boolean show = false;


    @Override
    public synchronized void step() {
	currentIteration++;
	show = true;
	// vypocet odporu
	while (true) {
	    try {
		for (V v1 : getGraph().getVertices()) {
		    calcRepulsion(v1);
		}
		break;
	    } catch (ConcurrentModificationException cme) {}
	}
	// vypocet pritazlivosti
	while (true) {
	    try {
		for (V v1 : getGraph().getVertices()) {
		    calcAttraction(v1);
		}
		break;
	    } catch (ConcurrentModificationException cme) {}
	}
	// vypocet pozic vrcholu
	while (true) {
	    try {
		for (V v : getGraph().getVertices()) {
		    if (isLocked(v))
			continue;
		    calcPosition(v);
		    show = false;
		}
		break;
	    } catch (ConcurrentModificationException cme) {}
	}
    }

    /** maximalni rychlost pohybu prvku */
    private static final double velocity_maximum = 5;
    /** treni (zpomaleni) prvku */
    private static final double friction = velocity_maximum / 100;


    protected synchronized void calcPosition(V v) {
	JSVertexData jsvd = jsVertexData.get(v);
	if (jsvd == null)
	    return;
	jsvd.forceVelocityX += (jsvd.forceCoulombX + jsvd.forceHarmonicX / 10) / 10;
	jsvd.forceVelocityY += (jsvd.forceCoulombY + jsvd.forceHarmonicY / 10) / 10;
	// velocity
	if (jsvd.forceVelocityX > velocity_maximum) {
	    jsvd.forceVelocityX = velocity_maximum;
	}
	if (jsvd.forceVelocityY > velocity_maximum) {
	    jsvd.forceVelocityY = velocity_maximum;
	}
	if (jsvd.forceVelocityX < (-velocity_maximum)) {
	    jsvd.forceVelocityX = -velocity_maximum;
	}
	if (jsvd.forceVelocityY < (-velocity_maximum)) {
	    jsvd.forceVelocityY = -velocity_maximum;
	}
	// friction
	if (jsvd.forceVelocityX > friction) {
	    jsvd.forceVelocityX -= friction;
	}
	if (jsvd.forceVelocityX < (-friction)) {
	    jsvd.forceVelocityX += friction;
	}
	if (jsvd.forceVelocityY > friction) {
	    jsvd.forceVelocityY -= friction;
	}
	if (jsvd.forceVelocityY < (-friction)) {
	    jsvd.forceVelocityY += friction;
	}
	Point2D xyd = transform(v);
	double newX = xyd.getX() + jsvd.forceVelocityX;
	double newY = xyd.getY() + jsvd.forceVelocityY;
	// osetreni aby vrchol nebyl mimo platno
	newX = Math.max(innerBounds.getMinX(), Math.min(newX, innerBounds.getMaxX()));
	newY = Math.max(innerBounds.getMinY(), Math.min(newY, innerBounds.getMaxY()));
	xyd.setLocation(newX, newY);
    }


    protected void calcAttraction(V v1) {
	JSVertexData fvd1 = jsVertexData.get(v1);
	if (fvd1 == null)
	    return;
	fvd1.forceHarmonicX = 0;
	fvd1.forceHarmonicY = 0;
	Point2D p1 = transform(v1);
	boolean v1_locked = isLocked(v1);
	for (V v2 : getGraph().getNeighbors(v1)) {
	    boolean v2_locked = isLocked(v2);
	    if (v1_locked && v2_locked)
		continue;
	    if (v1 != v2) {
		Point2D p2 = transform(v2);
		if (p1 == null || p2 == null)
		    continue;
		double xDelta = p1.getX() - p2.getX();
		double yDelta = p1.getY() - p2.getY();
		double radius = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
		if (radius != 0) {
		    fvd1.forceHarmonicX += -xDelta * radius;
		    fvd1.forceHarmonicY += -yDelta * radius;
		} else {
		    fvd1.forceHarmonicX += getRandom();
		    fvd1.forceHarmonicY += getRandom();
		}
	    }
	}
    }


    protected void calcRepulsion(V v1) {
	JSVertexData fvd1 = jsVertexData.get(v1);
	if (fvd1 == null)
	    return;
	fvd1.forceCoulombX = 0;
	fvd1.forceCoulombY = 0;
	boolean v1_locked = isLocked(v1);
	try {
	    for (V v2 : getGraph().getVertices()) {
		boolean v2_locked = isLocked(v2);
		if (v1_locked && v2_locked)
		    continue;
		if (v1 != v2) {
		    Point2D p1 = transform(v1); // transform - vraci souradnice vrcholu
		    Point2D p2 = transform(v2);
		    if (p1 == null || p2 == null)
			continue;
		    double xDelta = p1.getX() - p2.getX();
		    double yDelta = p1.getY() - p2.getY();
		    double radius = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
		    if (radius != 0) {
			fvd1.forceCoulombX += xDelta / radius / radius;
			fvd1.forceCoulombY += yDelta / radius / radius;
		    } else {
			fvd1.forceCoulombX += getRandom();
			fvd1.forceCoulombY += getRandom();
		    }
		}
	    }
	} catch (ConcurrentModificationException cme) {
	    calcRepulsion(v1);
	}
    }


    private double getRandom() {
	return (Math.random() - 0.5) * 100;
    }


    @Override
    public boolean done() {
	if (currentIteration > maxIterations) {// || temperature < 1.0 / max_dimension) {
	    // if (!checked)
	    // {
	    // // System.out.println("current iteration: " + currentIteration);
	    // // System.out.println("temperature: " + temperature);
	    // checked = true;
	    // }
	    return true;
	}
	return false;
    }

    protected static class JSVertexData extends Point2D.Double {

	private static final long serialVersionUID = 1L;
	protected double forceCoulombX = 0.0d;
	protected double forceCoulombY = 0.0d;
	protected double forceHarmonicX = 0.0d;
	protected double forceHarmonicY = 0.0d;
	protected double forceVelocityX = 0.0d;
	protected double forceVelocityY = 0.0d;
    }
}
