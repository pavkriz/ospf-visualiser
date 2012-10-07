package org.hkfree.ospf.model.ospfcomponent;

import org.hkfree.ospf.model.ospf.Link;

/**
 * Třída představující záznam o tom, na kolik částí se výpadkem spoje síť rozpadne
 * @author Jakub Menzel
 */
public class LinkImportance {

    private Link ospfLink = null;
    private int afterDisableNetPartsCount = 1;


    /**
     * Konstruktor
     * @param ospfLink
     * @param partsCount
     */
    public LinkImportance(Link ospfLink, int partsCount) {
	this.ospfLink = ospfLink;
	this.afterDisableNetPartsCount = partsCount;
    }


    /**
     * Vrací ospfLink
     * @return ospfLink
     */
    public Link getOspfLink() {
	return ospfLink;
    }


    /**
     * Nastavuje ospfLink
     * @param ospfLink
     */
    public void setOspfLink(Link ospfLink) {
	this.ospfLink = ospfLink;
    }


    /**
     * Vrací počet částí sítě, na které se síť rozpadně po výpadku spoje
     * @return afterDisableNetPartsCount
     */
    public int getAfterDisableNetPartsCount() {
	return afterDisableNetPartsCount;
    }


    /**
     * Nastavuje počet částí sítě, na které se síť rozpadně po výpadku spoje
     * @param afterDisableNetPartsCount
     */
    public void setAfterDisableNetPartsCount(int afterDisableNetPartsCount) {
	this.afterDisableNetPartsCount = afterDisableNetPartsCount;
    }
}
