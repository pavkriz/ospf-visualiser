package org.hkfree.ospf.model.map;

/**
 * Třída představující hranu grafu stromu nejkratších cest
 * @author Jakub Menzel
 */
public class EdgeOfSPT {

    private int cost = 0;
    private String linkID = "";


    /**
     * Konstruktor
     * @param cost
     * @param linkID
     */
    public EdgeOfSPT(int cost, String linkID) {
	this.cost = cost;
	this.linkID = linkID;
    }


    /**
     * Vrací cenu
     * @return int
     */
    public int getCost() {
	return cost;
    }


    /**
     * Nastavuje cenu
     * @param cost
     */
    public void setCost(int cost) {
	this.cost = cost;
    }


    /**
     * Vrací linkId
     * @return linkId
     */
    public String getLinkID() {
	return linkID;
    }


    /**
     * nastavuje linkId
     * @param linkID
     */
    public void setLinkID(String linkID) {
	this.linkID = linkID;
    }
}
