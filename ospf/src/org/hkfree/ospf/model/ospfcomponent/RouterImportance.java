package org.hkfree.ospf.model.ospfcomponent;

import org.hkfree.ospf.model.ospf.Router;

/**
 * Třída představující záznam o tom, na kolik částí se výpadkem routeru síť rozpadne
 * @author Jakub Menzel
 */
public class RouterImportance {

    private Router router = null;
    private int afterDisableNetPartsCount = 1;


    /**
     * Konstruktor
     * @param router
     * @param partsCount
     */
    public RouterImportance(Router router, int partsCount) {
	this.router = router;
	this.afterDisableNetPartsCount = partsCount;
    }


    /**
     * Vrací router
     * @return router
     */
    public Router getRouter() {
	return router;
    }


    /**
     * Nastaví router
     * @param router
     */
    public void setRouter(Router router) {
	this.router = router;
    }


    /**
     * Vrací počet částí sítě, na které se síť rozpadně po výpadku routeru
     * @return afterDisableNetPartsCount
     */
    public int getAfterDisableNetPartsCount() {
	return afterDisableNetPartsCount;
    }


    /**
     * Nastaví počet částí sítě, na které se síť rozpadně po výpadku routeru
     * @param afterDisableNetPartsCount
     */
    public void setAfterDisableNetPartsCount(int afterDisableNetPartsCount) {
	this.afterDisableNetPartsCount = afterDisableNetPartsCount;
    }
}
