package org.hkfree.ospf.model.ospf;

/**
 * Třída představující účastněný router ve spoji a parametry této účasti
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfLinkData {

    private Router router = null;
    private String interfaceIp = "";
    private int costIPv4 = -1;
    private int costIPv6 = -1;


    public OspfLinkData() {}


    public Router getRouter() {
	return router;
    }


    public void setRouter(Router router) {
	this.router = router;
    }


    public String getInterfaceIp() {
	return interfaceIp;
    }


    public void setInterfaceIp(String interfaceIp) {
	this.interfaceIp = interfaceIp;
    }


    public int getCostIPv4() {
	return costIPv4;
    }


    public void setCostIPv4(int costIPv4) {
	this.costIPv4 = costIPv4;
    }


    public int getCostIPv6() {
	return costIPv6;
    }


    public void setCostIPv6(int costIPv6) {
	this.costIPv6 = costIPv6;
    }
}
