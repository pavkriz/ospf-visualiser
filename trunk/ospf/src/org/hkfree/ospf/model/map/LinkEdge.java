package org.hkfree.ospf.model.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ResourceBundle;

import org.hkfree.ospf.gui.mappanel.MapGraphComponent;
import org.hkfree.ospf.setting.MapGraphComponentMode;
import org.hkfree.ospf.tools.Factory;

import edu.uci.ics.jung.visualization.RenderContext;

/**
 * Třída představující hranu MapaModelu reprezentovanou 2 vrcholy a cenami
 * z jednotlivých vrcholů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LinkEdge {

    private ResourceBundle rb = Factory.getRb();
    private MapGraphComponent graphComponent = null;
    private RouterVertex routerVertex1 = new RouterVertex();
    private int cost1 = 0;
    private RouterVertex routerVertex2 = new RouterVertex();
    private int cost2 = 0;
    private String linkEdgeID = "";
    private boolean enabled = true;
    private boolean edgeOfShortestPath = false;
    private boolean actuallyLive = false;
    private boolean actuallyDead = false;
    private boolean extraAddedEdge = false;
    private int faultCount = 0;
    private float faultIntensity = 0.0f;
    private boolean edgeOfFirstPath = false;
    private boolean edgeOfSecondPath = false;
    private boolean hover = false;


    /**
     * Konstruktor
     */
    public LinkEdge() {}


    /**
     * Konstruktor třídy
     * - vytvoří instanci hrany dle zadaných parametrů (2 vrcholy + příslušné ceny)
     * @param routerVertex1
     * @param cost1
     * @param routerVertex2
     * @param cost2
     * @param linkID
     * @param ospfLinksData
     */
    public LinkEdge(RouterVertex routerVertex1, int cost1, RouterVertex routerVertex2, int cost2, String linkID) {
	this.routerVertex1 = routerVertex1;
	this.cost1 = cost1;
	this.routerVertex2 = routerVertex2;
	this.cost2 = cost2;
	this.linkEdgeID = linkID;
    }


    public void setGraphComponent(MapGraphComponent graphComponent) {
	this.graphComponent = graphComponent;
    }


    public MapGraphComponent getGraphComponent() {
	return graphComponent;
    }


    /**
     * Metoda, která vrátí cenu z prvního vrcholu
     * @return cost
     */
    public int getCost1() {
	return cost1;
    }


    /**
     * Metoda, která vrátí cenu z druhého vrcholu
     * @return cost
     */
    public int getCost2() {
	return cost2;
    }


    /**
     * Metoda, která nastaví cenu z prvního vrcholu
     * @param cost
     */
    public void setCost1(int cost) {
	cost1 = cost;
    }


    /**
     * Metoda, která nastaví cenu z druhého vrcholu
     * @param cost
     */
    public void setCost2(int cost) {
	cost2 = cost;
    }


    /**
     * Metoda, která vrátí instanci prvního vrcholu
     * @return rv
     */
    public RouterVertex getRVertex1() {
	return routerVertex1;
    }


    /**
     * Metoda, která vrátí instanci druhého vrcholu
     * @return rv
     */
    public RouterVertex getRVertex2() {
	return routerVertex2;
    }


    /**
     * Metoda, která nastaví instanci prvního vrcholu
     * @param routerVertex1
     */
    public void setRouterVertex1(RouterVertex routerVertex1) {
	this.routerVertex1 = routerVertex1;
    }


    /**
     * Metoda, která nastaví instanci druhého vrcholu
     * @param routerVertex2
     */
    public void setRouterVertex2(RouterVertex routerVertex2) {
	this.routerVertex2 = routerVertex2;
    }


    /**
     * Metoda, která vrátí příznak, zda je hrana součástí multispoje
     * @return boolean
     */
    public boolean isEdgeOfMultilink() {
	if (routerVertex2 != null)
	    return routerVertex2.isMultilink();
	else
	    return false;
    }


    /**
     * Vrací sílu štětce - jeho tlouštku.<br>
     * Používat pouze při pro MapPanel (ne v netstatepanel)
     * @return síla štětce
     */
    public Stroke getStroker() {
	if (isHover()) {
	    return new BasicStroke(3);
	}
	if (isEnabled()) {
	    if (isEdgeOfShortestPath() || isEdgeOfFirstPath() || isEdgeOfSecondPath())
		return new BasicStroke(3);
	    else
		return new BasicStroke(1);
	} else {
	    return RenderContext.DASHED;
	}
    }


    /**
     * Metoda, která vrátí příznak, zda je spoj zapnut
     * @return boolean
     */
    public boolean isEnabled() {
	return enabled;
    }


    /**
     * Metoda, která nastaví příznak, zda je spoj zapnut
     * @param value
     */
    public void setEnabled(boolean value) {
	enabled = value;
    }


    /**
     * Metoda, která vrátí název spoje
     * @return linkId
     */
    public String getLinkID() {
	return linkEdgeID;
    }


    /**
     * Metoda, která nastaví název spoje
     * @param linkEdgeID
     */
    public void setLinkID(String linkEdgeID) {
	this.linkEdgeID = linkEdgeID;
    }


    /**
     * Nastavuje příznak, zda se jedná o hranu stromu nejkratších cest
     * @param isEdgeOfShortestPath
     */
    public void setEdgeOfShortestPath(boolean isEdgeOfShortestPath) {
	this.edgeOfShortestPath = isEdgeOfShortestPath;
    }


    /**
     * Vrací příznak, zda se jedná o hranu stromu nejkratších cest
     * @return boolean
     */
    public boolean isEdgeOfShortestPath() {
	return edgeOfShortestPath;
    }


    /**
     * Vrací, zda se jedná o aktuálně naběhlý spoj
     * @return boolean
     */
    public boolean isActuallyLive() {
	return actuallyLive;
    }


    /**
     * Nastavuje, zda se jedná o aktuálně naběhlý spoj
     * @param isActuallyLive
     */
    public void setActuallyLive(boolean isActuallyLive) {
	this.actuallyLive = isActuallyLive;
    }


    /**
     * Vrací, zda se jedná o aktuálně spadlý spoj
     * @return boolean
     */
    public boolean isActuallyDead() {
	return actuallyDead;
    }


    /**
     * Nastavuje, zda se jedná o aktuálně spadlý spoj
     * @param isActuallyDead
     */
    public void setActuallyDead(boolean isActuallyDead) {
	this.actuallyDead = isActuallyDead;
    }


    /**
     * Metoda, která vrátí popis hrany reprezentující spoj
     * @return string
     */
    public String getLinkDescription() {
	if (!routerVertex2.isMultilink()) {
	    return "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + linkEdgeID + "</b><br><br>" + rb.getString("le.0")
		    + ":<br> <b>" + routerVertex1.getName() + "</b>" + "(" + routerVertex1.getDescription() + "): <b>"
		    + Integer.toString(cost1) + "</b><br><b>" + routerVertex2.getName() + "</b>" + "("
		    + routerVertex2.getDescription() + "): <b>" + Integer.toString(cost2) + "</b></body></html>";
	} else {
	    return "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + linkEdgeID + "</b><br><br>"
		    + rb.getString("clcd.1") + ":<br> <b>" + routerVertex1.getName() + "</b>" + "("
		    + routerVertex1.getDescription() + "): <b>" + Integer.toString(cost1) + "</b></body></html>";
	}
    }


    /**
     * Vrací popisek s počtem výpadků spoje
     * @return description
     */
    public String getLinkFaultDescription() {
	return "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + linkEdgeID + "</b><br><br>"
		+ rb.getString("lftm.col1") + ":<br> <b>" + faultCount + "</b></body></html>";
    }


    /**
     * Vrací barvu spoje.<br>
     * Používat pouze pro MapPanel (ne v netstatepanel)
     * @param mode
     * @return
     */
    public Paint getLineColor(int mode) {
	if (isHover()) {
	    return Color.ORANGE;
	}
	if (isEnabled()) {
	    if (mode == MapGraphComponentMode.ASYMETRIC_LINK && !isSymetric()) {
		return new Color(0, 150, 255);
	    }
	    if (isEdgeOfShortestPath() || (isEdgeOfFirstPath() && isEdgeOfSecondPath())) {
		return Color.GREEN;
	    }
	    if (isEdgeOfFirstPath()) {
		return new Color(0, 187, 227);
	    }
	    if (isEdgeOfSecondPath()) {
		return new Color(255, 80, 80);
	    }
	    return Color.GRAY;
	} else {
	    return Color.LIGHT_GRAY;
	}
    }


    /**
     * Vrací příznak, zda se jedná o extra přidanou hranu při tvorbě topologie
     * @return boolean
     */
    public boolean isExtraAddedEdge() {
	return extraAddedEdge;
    }


    /**
     * Nastavuje příznak, zda se jedná o extra přidanou hranu při tvorbě topologie
     * @param isExtraAddedEdge
     */
    public void setExtraAddedEdge(boolean isExtraAddedEdge) {
	this.extraAddedEdge = isExtraAddedEdge;
    }


    /**
     * Vrací intenzitu výpadků pro obarvení hrany (max je 255)
     * @return intensity
     */
    public float getFaultIntensity() {
	return faultIntensity;
    }


    /**
     * Nastavuje intenzitu výpadků pro obarvení hrany
     * @param faultIntensity
     */
    public void setFaultIntensity(float faultIntensity) {
	this.faultIntensity = faultIntensity;
    }


    /**
     * Vrací počet výpadků spoje
     * @return count
     */
    public int getFaultCount() {
	return faultCount;
    }


    /**
     * Nastavuje počet výpadků spoje
     * @param faultCount
     */
    public void setFaultCount(int faultCount) {
	this.faultCount = faultCount;
    }


    /**
     * Vrací příznak, že se jedná o součást první nejkratší cesty mezi dvěma routery
     * @return the isEdgeOfFirstPath
     */
    public boolean isEdgeOfFirstPath() {
	return edgeOfFirstPath;
    }


    /**
     * Nastavuje příznak, že se jedná o součást první nejkratší cesty mezi dvěma routery
     * @param isEdgeOfFirstPath the isEdgeOfFirstPath to set
     */
    public void setEdgeOfFirstPath(boolean isEdgeOfFirstPath) {
	this.edgeOfFirstPath = isEdgeOfFirstPath;
    }


    /**
     * Vrací příznak, že se jedná o součást druhé nejkratší cesty mezi dvěma routery
     * @return the isEdgeOfSecondPath
     */
    public boolean isEdgeOfSecondPath() {
	return edgeOfSecondPath;
    }


    /**
     * Nastavuje příznak, že se jedná o součást druhé nejkratší cesty mezi dvěma routery
     * @param isEdgeOfSecondPath the isEdgeOfSecondPath to set
     */
    public void setEdgeOfSecondPath(boolean isEdgeOfSecondPath) {
	this.edgeOfSecondPath = isEdgeOfSecondPath;
    }


    /**
     * Nastavuje příznak, že nad spojem je kurzor
     * @param b
     */
    public void setHover(boolean b) {
	this.hover = b;
    }


    /**
     * Vrací příznak, zda je nad spojem kurzor
     * @return
     */
    public boolean isHover() {
	return hover;
    }


    /**
     * Vrací příznak zda se jedná o symetrický spoj
     * @return
     */
    public boolean isSymetric() {
	if (isEdgeOfMultilink()) {
	    for (LinkEdge le : graphComponent.getMapModel().getLinkEdges()) {
		if (le.getRVertex2() == routerVertex2) {
		    if (le.getCost1() != cost1) {
			return false;
		    }
		}
	    }
	    return true;
	}
	return cost1 == cost2;
    }
}
