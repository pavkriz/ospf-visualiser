package org.hkfree.ospf.model.ospf;

/**
 * Třída představující účastněný router ve spoji a parametry této účasti
 * @author Jakub Menzel
 */
public class OspfLinkData {

    private Router router = null;
    private String interfaceIp = "";
    private int cost = -1;


    /**
     * Konstruktor
     */
    public OspfLinkData() {}


    /**
     * Nastavuje router
     * @param router
     */
    public OspfLinkData(Router router) {
	this.router = router;
    }


    /**
     * Konstruktor
     * @param router
     * @param interfaceIp
     * @param cost
     */
    public OspfLinkData(Router router, String interfaceIp, int cost) {
	this.router = router;
	this.interfaceIp = interfaceIp;
	this.cost = cost;
    }


    /**
     * Vrací router
     * @return router
     */
    public Router getRouter() {
	return router;
    }


    /**
     * Vrací ip adresu rozhraní
     * @return string
     */
    public String getInterfaceIp() {
	return interfaceIp;
    }


    /**
     * Vrací cenu
     * @return int
     */
    public int getCost() {
	return cost;
    }


    /**
     * Nastavuje router
     * @param router
     */
    public void setRouter(Router router) {
	this.router = router;
    }


    /**
     * Nastavuje adresu rozhraní
     * @param interfaceIp
     */
    public void setInterfaceIp(String interfaceIp) {
	this.interfaceIp = interfaceIp;
    }


    /**
     * Nastavuje cenu
     * @param cost
     */
    public void setCost(int cost) {
	this.cost = cost;
    }
}
