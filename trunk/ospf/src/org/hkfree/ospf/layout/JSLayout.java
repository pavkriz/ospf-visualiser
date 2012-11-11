package org.hkfree.ospf.layout;

import java.awt.Dimension;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * Implementace Force Directed Layoutu
 * @author Jan Schovánek
 *
 * @param <V> router
 * @param <E> edge
 */
public class JSLayout<V, E> extends AbstractLayout<V, E> implements IterativeContext {

    private int currentIteration;
    private int maxIterations = 700;
    private boolean firstTime = true;


    protected JSLayout(Graph<V, E> graph) {
	super(graph);
    }


    @Override
    public void initialize() {
	doInit();
    }


    @Override
    public void reset() {
	doInit();
    }


    private void doInit() {
	currentIteration = 0;
	
    }


    @Override
    public synchronized void step() {
	currentIteration++;
    }


    @Override
    public boolean done() {
	if (currentIteration > maxIterations)
	{
	    return true;
	}
	return false;
    }


    public void setMaxIterations(int maxIterations) {
	this.maxIterations = maxIterations;
    }
}
