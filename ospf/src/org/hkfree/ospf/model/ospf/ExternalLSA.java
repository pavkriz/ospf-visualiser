package org.hkfree.ospf.model.ospf;

/**
 * Prepravka pro externi LSA, který je propagován routerem
 * @author Jan Schovánek
 */
public class ExternalLSA {

    private String network = null;
    private int mask = -1;
    private int metricType = -1;
    private int cost = -1;


    public String getNetwork() {
	return network;
    }


    public void setNetwork(String network) {
	this.network = network;
    }


    public int getMask() {
	return mask;
    }


    public void setMask(int mask) {
	this.mask = mask;
    }


    public int getMetricType() {
	return metricType;
    }


    public void setMetricType(int metricType) {
	this.metricType = metricType;
    }


    public int getCost() {
	return cost;
    }


    public void setCost(int cost) {
	this.cost = cost;
    }
}
