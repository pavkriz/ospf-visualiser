package org.hkfree.ospf.model.ospfcomponent;

import org.hkfree.ospf.model.ospf.Router;

/**
 * Třída představující router jako vrchol modelu komponent, kde je počítán počet komponent
 * @author Jakub Menzel
 */
public class OspfGraphVertex {

    private Router router = null;
    private boolean enabled = true;
    private int state = OspfGraphComponentModel.FRESH;
    private int graphComponentCountAfterDisable = 1;


    /**
     * Konstruktor
     * @param r
     */
    public OspfGraphVertex(Router r) {
	this.router = r;
    }


    /**
     * Vrací router
     * @return router
     */
    public Router getRouter() {
	return router;
    }


    /**
     * Nastavuje router
     * @param router
     */
    public void setRouter(Router router) {
	this.router = router;
    }


    /**
     * Vrací příznak, zda je router aktivní (počítan jako součást grafu)
     * @return boolean
     */
    public boolean isEnabled() {
	return enabled;
    }


    /**
     * Nastavuje příznak, zda je router aktivní (počítan jako součást grafu)
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }


    /**
     * Vrací stav vrcholu v rámci procházení grafu
     * @return state
     */
    public int getState() {
	return state;
    }


    /**
     * Nastavuje stav vrcholu v rámci procházení grafu
     * @param state
     */
    public void setState(int state) {
	this.state = state;
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
     * Nastavuje vrchol jako ještě neotevřený
     */
    public void setFresh() {
	this.state = OspfGraphComponentModel.FRESH;
    }


    /**
     * Nastavuje vrchol jako otevřený
     */
    public void setOpened() {
	this.state = OspfGraphComponentModel.OPEN;
    }


    /**
     * Nastavuje vrchol jako uzavřený
     */
    public void setClosed() {
	this.state = OspfGraphComponentModel.CLOSED;
    }


    /**
     * Vrací počet komponent, na který se graf rozpadne při neúčasti tohoto vrcholu
     * @return count
     */
    public int getGraphComponentCountAfterDisable() {
	return graphComponentCountAfterDisable;
    }


    /**
     * Nastavuje počet komponent, na který se graf rozpadne při neúčasti tohoto vrcholu
     * @param graphComponentCountAfterDisable
     */
    public void setGraphComponentCountAfterDisable(int graphComponentCountAfterDisable) {
	this.graphComponentCountAfterDisable = graphComponentCountAfterDisable;
    }
}
