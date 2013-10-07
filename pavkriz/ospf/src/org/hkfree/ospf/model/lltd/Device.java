package org.hkfree.ospf.model.lltd;

import java.io.Serializable;

/**
 * Prepravka reprezentující zařízení v LLTD protokolu
 * @author Jan Schovánek
 */
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;
    private String source;
    private String machineName;
    private String ipv4;
    private String ipv6;


    public Device() {}


    public String getSource() {
	return source;
    }


    public void setSource(String source) {
	this.source = source;
    }


    public String getMachineName() {
	return machineName;
    }


    public void setMachineName(String machineName) {
	this.machineName = machineName;
    }


    public String getIpv4() {
	return ipv4;
    }


    public void setIpv4(String ipv4) {
	this.ipv4 = ipv4;
    }


    public String getIpv6() {
	return ipv6;
    }


    public void setIpv6(String ipv6) {
	this.ipv6 = ipv6;
    }


    @Override
    public String toString() {
	return "Device [source=" + source + ", machineName=" + machineName + " ]";
    }
}
