package org.hkfree.ospf.model.ospfcomponent;

import org.hkfree.ospf.model.ospf.OspfLink;

/**
 * Třída představující záznam o tom, na kolik částí se výpadkem spoje síť rozpadne
 * @author Jakub Menzel
 */
public class LinkImportance {

    private OspfLink ospfLink = null;
    private int afterDisableNetPartsCount = 1;


    /**
     * Konstruktor
     * @param ospfLink
     * @param partsCount
     */
    public LinkImportance(OspfLink ospfLink, int partsCount) {
	this.ospfLink = ospfLink;
	this.afterDisableNetPartsCount = partsCount;
    }


    /**
     * Vrací ospfLink
     * @return ospfLink
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
