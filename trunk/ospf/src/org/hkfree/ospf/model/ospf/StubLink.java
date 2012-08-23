package org.hkfree.ospf.model.ospf;

/**
 * Spoj ke koncovému uživateli
 * @author Jan Schovánek
 */
public class StubLink {

    private String linkID;
    private String mask;
    private int cost;


    public String getLinkID() {
	return linkID;
    }


    public void setLinkID(String linkID) {
	this.linkID = linkID;
    }


    public String getMask() {
	return mask;
    }


    public void setMask(String mask) {
	this.mask = mask;
    }


    public int getCost() {
	return cost;
    }


    public void setCost(int cost) {
	this.cost = cost;
    }
}
