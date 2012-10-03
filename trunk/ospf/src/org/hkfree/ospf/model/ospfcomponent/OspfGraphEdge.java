package org.hkfree.ospf.model.ospfcomponent;

import org.hkfree.ospf.model.ospf.OspfLink;

/**
 * Třída představující spoj jako hranu modelu, kde je počítán počet komponent
 * @author Jakub Menzel
 */
public class OspfGraphEdge {

    private OspfLink ospfLink = null;
    private boolean enabled = true;
    private int graphComponentCountAfterDisable = 1;
    private OspfGraphVertex vertex1 = null;
    private OspfGraphVertex vertex2 = null;


    /**
     * Konstruktor
     * @param ospfLink
     */
    public OspfGraphEdge(OspfLink ospfLink, OspfGraphVertex v1, OspfGraphVertex v2) {
	this.ospfLink = ospfLink;
	this.vertex1 = v1;
	this.vertex2 = v2;
    }


    /**
     * Vrací ospfLink
     * @return link
     */
    public OspfLink getOspfLink() {
	return ospfLink;
    }


    /**
     * Nastavuje ospfLink
     * @param ospfLink
     */
    public void setOspfLink(OspfLink ospfLink) {
	this.ospfLink = ospfLink;
    }


    /**
     * Vrací příznak, zda je hrana aktivní (počítaná jako součást grafu)
     * @return boolean
     */
    public boolean isEnabled() {
	return enabled;
    }


    /**
     * Nastavuje příznak, zda je hrana aktivní (počítaná jako součást grafu)
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }


    /**
     * Vrací vrchol1 hrany
     * @return vertex
     */
    public OspfGraphVertex getVertex1() {
	return vertex1;
    }


    /**
     * Nastavuje vrchol1 hrany
     * @param vertex1
     */
    public void setVertex1(OspfGraphVertex vertex1) {
	this.vertex1 = vertex1;
    }


    /**
     * Vrací vrchol2 hrany
     * @return vertex
     */
    public OspfGraphVertex getVertex2() {
	return vertex2;
    }


    /**
     * Nastavuje vrchol2 hrany
     * @param vertex2
     */
    public void setVertex2(OspfGraphVertex vertex2) {
	this.vertex2 = vertex2;
    }


    /**
     * Nastavuje hranu jako aktivní
     */
    public void enable() {
	this.enabled = true;
    }


    /**
     * Nastavuje hranu jako neaktivní
     */
    public void disable() {
	this.enabled = false;
    }


    /**
     * Vrací příznak, zda hrana obsahuje zadaný vrchol
     * @param v
     * @return boolean
     */
    public boolean cointainsVertex(OspfGraphVertex v) {
	return (vertex1.equals(v) || vertex2.equals(v));
    }


    /**
     * Vrací příznak, zda hrana obsahuje zadaný aktivní vrchol
     * @param v
     * @return boolean
     */
    public boolean cointainsEnabledVertex(OspfGraphVertex v) {
	return ((vertex1.equals(v) && vertex1.isEnabled()) || (vertex2.equals(v) && vertex2.isEnabled()));
    }


    /**
     * Vrací souseda zadaného vrcholu
     * @param v
     * @return vertex
     */
    public OspfGraphVertex getNeighbour(OspfGraphVertex v) {
	if (vertex1.equals(v))
	    return vertex2;
	if (vertex2.equals(v))
	    return vertex1;
	return null;
    }


    /**
     * Vrací počet komponent, na který se graf rozpadne při neúčasti této hrany
     * @return count
     */
    public int getGraphComponentCountAfterDisable() {
	return graphComponentCountAfterDisable;
    }


    /**
     * Nastavuje počet komponent, na který se graf rozpadne při neúčasti této hrany
     * @param graphComponentCountAfterDisable
     */
    public void setGraphComponentCountAfterDisable(int graphComponentCountAfterDisable) {
	this.graphComponentCountAfterDisable = graphComponentCountAfterDisable;
    }
}
