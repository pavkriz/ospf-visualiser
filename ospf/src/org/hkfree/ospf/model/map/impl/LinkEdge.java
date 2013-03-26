package org.hkfree.ospf.model.map.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;

import org.hkfree.ospf.gui.mappanel.MapGraphComponent;
import org.hkfree.ospf.setting.MapGraphComponentMode;

import edu.uci.ics.jung.visualization.RenderContext;

/**
 * Třída představující hranu MapaModelu reprezentovanou 2 vrcholy a cenami
 * z jednotlivých vrcholů
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LinkEdge extends AEdge implements Serializable {

    private static final long serialVersionUID = -6326761512343098201L;
    private MapGraphComponent graphComponent = null;
    private RouterVertex vertex1;
    private RouterVertex vertex2;
    private int cost1 = 0;
    private int cost2 = 0;
    private int cost1IPv6 = 0;
    private int cost2IPv6 = 0;
    private String linkEdgeIDv4 = null;
    private String linkEdgeIDv6 = null;
    private int faultCount = 0;
    private float faultIntensity = 0.0f;
    private boolean edgeOfShortestPath = false;
    private boolean actuallyLive = false;
    private boolean actuallyDead = false;
    private boolean extraAddedEdge = false;
    private boolean edgeOfFirstPath = false;
    private boolean edgeOfSecondPath = false;


    /**
     * Konstruktor
     */
    public LinkEdge() {}


    @Override
    public String getLabel() {
	if (!graphComponent.isShowIPv6() && !isIPv4() && isIPv6()) {
	    return null;
	}
	String result = "";
	if (!isEnabled()) {
	    return result;
	}
	if (!linkEdgeIDv4.isEmpty()) {
	    result += Integer.toString(getCost1v4());
	    if (!vertex2.isMultilink()) {
		result += " - " + Integer.toString(getCost2v4());
	    }
	    if (graphComponent.isShowIPv6()) {
		result = "(" + result + ")v4";
	    }
	}
	if (!linkEdgeIDv4.isEmpty() && !linkEdgeIDv6.isEmpty() && graphComponent.isShowIPv6()) {
	    result += " ";
	}
	if (!linkEdgeIDv6.isEmpty() && graphComponent.isShowIPv6()) {
	    result += "(" + Integer.toString(getCost1v6());
	    if (!vertex2.isMultilink()) {
		result += " - " + Integer.toString(getCost2v6());
	    }
	    result += ")v6";
	}
	return result;
    }


    @Override
    public String getDescription() {
	if (!graphComponent.isShowIPv6() && !isIPv4() && isIPv6()) {
	    return null;
	}
	String result = "<html><body>";
	if (!linkEdgeIDv4.isEmpty()) {
	    // popisek pro IPv4
	    result += "<b>IPv4</b> ";
	    result += "<br>" + rb.getString("cdtm.col1") + " <b>" + linkEdgeIDv4 + "</b>";
	    result += "<br>";
	    result += "<br>" + rb.getString("le.0") + ":";
	    result += "<br>" + vertex1.getLabel() + " (" + vertex1.getInfo() + "): " + Integer.toString(cost1) + "";
	    if (!vertex2.isMultilink()) {
		result += "<br>" + vertex2.getLabel() + " (" + vertex2.getInfo() + "): " + Integer.toString(cost2) + "";
	    }
	}
	if (!linkEdgeIDv4.isEmpty() && !linkEdgeIDv6.isEmpty()) {
	    result += "<br><br>";
	}
	if (!linkEdgeIDv6.isEmpty()) {
	    // popisek pro IPv6
	    result += "<b>IPv6</b> ";
	    result += "<br>" + rb.getString("cdtm.col1") + " <b>" + linkEdgeIDv6 + "</b>";
	    result += "<br>" + rb.getString("le.0") + ":";
	    result += "<br>";
	    result += "<br>" + vertex1.getLabel() + " (" + vertex1.getInfo() + "): " + Integer.toString(cost1IPv6) + "";
	    if (!vertex2.isMultilink()) {
		result += "<br>" + vertex2.getLabel() + " (" + vertex2.getInfo() + "): " + Integer.toString(cost2IPv6) + "";
	    }
	}
	result += "</body></html>";
	return result;
    }


    // Používat pouze pro MapPanel (ne v netstatepanel)
    @Override
    public Paint getLineColor() {
	if (!graphComponent.isShowIPv6() && !isIPv4() && isIPv6()) {
	    return null;
	}
	if (isHover()) {
	    return Color.ORANGE;
	}
	if (!isEnabled()) {
	    return new Color(0xcccccc);
	}
	if (isIPv6() && graphComponent.isShowIPv6()) {
	    return Color.GREEN;
	}
	if (graphComponent.getMapGraphComponentMode() == MapGraphComponentMode.ASYMETRIC_LINK && !isSymetric()) {
	    return new Color(0, 150, 255);// modra
	}
	if (isEdgeOfShortestPath() || (isEdgeOfFirstPath() && isEdgeOfSecondPath())) {
	    return Color.GREEN;
	}
	if (isEdgeOfFirstPath()) {
	    return new Color(0, 187, 227);// neco mezi modrou a zelenou
	}
	if (isEdgeOfSecondPath()) {
	    return new Color(255, 80, 80);// cervena
	}
	return new Color(0x666666);
    }


    // používat pouze pro MapPanel (ne v nestatepanel)
    @Override
    public Stroke getStroker() {
	if (!graphComponent.isShowIPv6() && !isIPv4() && isIPv6()) {
	    return null;
	}
	if (isHover()) {
	    return new BasicStroke(3);
	}
	if (!isEnabled()) {
	    return RenderContext.DASHED;
	}
	if (isEdgeOfShortestPath() || isEdgeOfFirstPath() || isEdgeOfSecondPath()) {
	    return new BasicStroke(3);
	}
	return new BasicStroke(1);
    }


    /**
     * Vrací popisek s počtem výpadků spoje
     * @return description
     */
    public String getLinkFaultDescription() {
	if (!graphComponent.isShowIPv6() && !isIPv4() && isIPv6()) {
	    return null;
	}
	return "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + linkEdgeIDv4 + "</b><br><br>"
		+ rb.getString("lftm.col1") + ":<br> <b>" + faultCount + "</b></body></html>";
    }


    public void setGraphComponent(MapGraphComponent graphComponent) {
	this.graphComponent = graphComponent;
    }


    public MapGraphComponent getGraphComponent() {
	return graphComponent;
    }


    public int getCost1v4() {
	return cost1;
    }


    public int getCost2v4() {
	return cost2;
    }


    public int getCost1v6() {
	return cost1IPv6;
    }


    public int getCost2v6() {
	return cost2IPv6;
    }


    public void setCost1v6(int cost1iPv6) {
	cost1IPv6 = cost1iPv6;
    }


    public void setCost2v6(int cost2iPv6) {
	cost2IPv6 = cost2iPv6;
    }


    public void setCost1v4(int cost) {
	cost1 = cost;
    }


    public void setCost2v4(int cost) {
	cost2 = cost;
    }


    /**
     * Metoda, která vrátí instanci prvního vrcholu
     * @return rv
     */
    public RouterVertex getVertex1() {
	return vertex1;
    }


    /**
     * Metoda, která vrátí instanci druhého vrcholu
     * @return rv
     */
    public RouterVertex getVertex2() {
	return vertex2;
    }


    /**
     * Metoda, která nastaví instanci prvního vrcholu
     * @param routerVertex1
     */
    public void setVertex1(RouterVertex vertex1) {
	this.vertex1 = vertex1;
    }


    /**
     * Metoda, která nastaví instanci druhého vrcholu
     * @param routerVertex2
     */
    public void setVertex2(RouterVertex vertex2) {
	this.vertex2 = vertex2;
    }


    /**
     * vrátí id spoje pro IPv4
     * @return linkId
     */
    public String getLinkIDv4() {
	return linkEdgeIDv4;
    }


    /**
     * nastaví id spoje pro IPv4
     * @param linkEdgeID
     */
    public void setLinkIDv4(String linkEdgeID) {
	this.linkEdgeIDv4 = linkEdgeID;
    }


    /**
     * vrátí id spoje pro IPv6
     * @return linkId
     */
    public String getLinkIDv6() {
	return linkEdgeIDv6;
    }


    /**
     * nastaví id spoje pro IPv6
     * @param linkEdgeID
     */
    public void setLinkIDv6(String linkEdgeID) {
	this.linkEdgeIDv6 = linkEdgeID;
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
     * Metoda, která vrátí příznak, zda je hrana součástí multispoje
     * @return boolean
     */
    public boolean isEdgeOfMultilink() {
	if (vertex2 != null)
	    return vertex2.isMultilink();
	else
	    return false;
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
     * Vrací příznak zda se jedná o symetrický spoj
     * @return
     */
    private boolean isSymetric() {
	if (isEdgeOfMultilink()) {
	    for (LinkEdge le : graphComponent.getMapModel().getLinkEdges()) {
		if (le.getVertex2() == vertex2) {
		    if (le.getCost1v4() != cost1) {
			return false;
		    }
		}
	    }
	    return true;
	}
	return cost1 == cost2;
    }


    /**
     * Vraci zda je spoj obsazen v IPv4
     * @return
     */
    public boolean isIPv4() {
	return (cost1 != -1 || cost2 != -1);
    }


    /**
     * Vraci zda je spoj obsazen v IPv6
     * @return
     */
    public boolean isIPv6() {
	return (cost1IPv6 != -1 || cost2IPv6 != -1);
    }
}
