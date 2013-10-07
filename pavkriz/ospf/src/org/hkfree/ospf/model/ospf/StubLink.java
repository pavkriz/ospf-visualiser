package org.hkfree.ospf.model.ospf;

/**
 * Spoj ke koncovému uživateli
 * @author Jan Schovánek
 */
public class StubLink {

    private String linkID;
    private int mask = -1;
    private int cost = -1;


    public String getLinkID() {
	return linkID;
    }


    public void setLinkID(String linkID) {
	this.linkID = linkID;
    }


    public int getMask() {
	return mask;
    }


    public void setMask(int mask) {
	this.mask = mask;
    }


    public int getCost() {
	return cost;
    }


    public void setCost(int cost) {
	this.cost = cost;
    }


    @Override
    public String toString() {
	return "StubLink [linkID=" + linkID + ", mask=" + mask + ", cost=" + cost + "]";
    }
}
