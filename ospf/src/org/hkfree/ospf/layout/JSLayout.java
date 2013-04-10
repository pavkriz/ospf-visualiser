package org.hkfree.ospf.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

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
public class JSLayout<V, E> extends AbstractLayout<V, E> implements IterativeContext {

    private double temperature;
    private int currentIteration;
    private int maxIterations = 700;
    private Map<V, JSVertexData> jsVertexData =
	    LazyMap.decorate(new HashMap<V, JSVertexData>(), new Factory<JSVertexData>() {

		public JSVertexData create() {
		    return new JSVertexData();
		}
	    });
    private Rectangle2D innerBounds = new Rectangle2D.Double();
    private static double KONSTANTA = 10d;


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
	    temperature = d.getWidth() / 10;
	}
    }


    @Override
    public synchronized void step() {
	currentIteration++;
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
		    calcPositions(v);
		}
		break;
	    } catch (ConcurrentModificationException cme) {}
	}
	cool();
    }


    protected synchronized void calcPositions(V v) {
	double velocity_maximum = 0.05 * KONSTANTA; // rychlost
	double friction = 0.0005 * KONSTANTA; // treni
	JSVertexData fvd = jsVertexData.get(v);
	if (fvd == null)
	    return;
	fvd.forceVelocityX += getVelocity(fvd.forceCoulombX, fvd.forceHarmonicX);
	fvd.forceVelocityY += getVelocity(fvd.forceCoulombY, fvd.forceHarmonicY);
	// velocity
	if (fvd.forceVelocityX > velocity_maximum) {
	    fvd.forceVelocityX = velocity_maximum;
	}
	if (fvd.forceVelocityY > velocity_maximum) {
	    fvd.forceVelocityY = velocity_maximum;
	}
	if (fvd.forceVelocityX < (-velocity_maximum)) {
	    fvd.forceVelocityX = -velocity_maximum;
	}
	if (fvd.forceVelocityY < (-velocity_maximum)) {
	    fvd.forceVelocityY = -velocity_maximum;
	}
	// friction
	if (fvd.forceVelocityX > friction) {
	    fvd.forceVelocityX -= friction;
	}
	if (fvd.forceVelocityX < (-friction)) {
	    fvd.forceVelocityX += friction;
	}
	if (fvd.forceVelocityY > friction) {
	    fvd.forceVelocityY -= friction;
	}
	if (fvd.forceVelocityY < (-friction)) {
	    fvd.forceVelocityY += friction;
	}
	Point2D p = transform(v);
	double newX = getCoord(p.getX(), fvd.forceVelocityX);
	double newY = getCoord(p.getY(), fvd.forceVelocityY);
	// zajisti ze vrchol nebude mimo platno layoutu
	newX = Math.max(innerBounds.getMinX(), Math.min(newX, innerBounds.getMaxX()));
	newY = Math.max(innerBounds.getMinY(), Math.min(newY, innerBounds.getMaxY()));
	p.setLocation(newX, newY);
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
		    fvd1.forceHarmonicX += getForceHarmonic(xDelta, radius);
		    fvd1.forceHarmonicY += getForceHarmonic(yDelta, radius);
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
		    Point2D p1 = transform(v1);
		    Point2D p2 = transform(v2);
		    if (p1 == null || p2 == null)
			continue;
		    double xDelta = p1.getX() - p2.getX();
		    double yDelta = p1.getY() - p2.getY();
		    double radius = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
		    if (radius != 0) {
			fvd1.forceCoulombX += getForceCoulomb(xDelta, radius);
			fvd1.forceCoulombY += getForceCoulomb(yDelta, radius);
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


    private static int K2 = 100;
    private static int K3 = 100;
    private double getCoord(double coord, double fv) {
	return coord + Math.max(-K2 * KONSTANTA, Math.min(K2 * KONSTANTA, fv * KONSTANTA));
    }


    private double getVelocity(double fc, double fh) {
	return fc + fh / KONSTANTA;
    }


    private double getForceHarmonic(double delta, double radius) {
	double vec = -delta / radius;
	return vec * radius * radius / K3;
    }


    private double getForceCoulomb(double delta, double radius) {
	double vec = delta / radius;
	return vec / radius / K3;
    }


    private double getRandom() {
	return Math.random() - 0.5;
    }


    private void cool() {
	temperature *= (1.0 - currentIteration / (double) maxIterations);
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


	protected void offset(double x, double y) {
	    this.x += x;
	    this.y += y;
	}


	protected double norm() {
	    return Math.sqrt(x * x + y * y);
	}
    }
}
