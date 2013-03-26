package org.hkfree.ospf.model.map.impl;

import java.io.Serializable;

public class DeviceVertex extends AVertex implements Serializable {

    private static final long serialVersionUID = 1L;
    private String mac;
    private String ipv4;
    private String ipv6;
    private String machineName;


    @Override
    public String getLabel() {
	if (!isVisible()) {
	    return null;
	}
	return machineName;
    }


    @Override
    public String getDescription() {
	if (!isVisible()) {
	    return null;
	}
	String result = "<html><body>";
	result += "<b>" + rb.getString("dv.0") + "</b>";
	result += "<br>" + rb.getString("dv.1") + ": <b>" + getMachineName() + "</b>";
	result += "<br>MAC: " + getMac();
	result += "<br>IPv4: " + getIpv4();
	result += "<br>IPv6: " + getIpv6();
	result += "</body></html>";
	return result;
    }


    public String getMac() {
	return mac;
    }


    public void setMac(String mac) {
	this.mac = mac;
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


    public String getMachineName() {
	return machineName;
    }


    public void setMachineName(String machineName) {
	this.machineName = machineName;
    }
}
