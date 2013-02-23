package org.hkfree.ospf.model.lltd;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * LLTD model, obsahuje datum vytvoření, veřejnou IP adresu sítě, ve které byl model vytvořen,
 * seznam zarizeni a spoju mezi nimi.
 * @author Jan Schovánek
 */
public class LLTDModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String publicIP = null;
    private Date date = null;
    private List<Device> devices = null;
    private List<Relation> relations = null;


    public LLTDModel() {}


    public String getPublicIP() {
	return publicIP;
    }


    public void setPublicIP(String publicIP) {
	this.publicIP = publicIP;
    }


    public Date getDate() {
	return date;
    }


    public void setDate(Date date) {
	this.date = date;
    }


    public List<Device> getDevices() {
	return devices;
    }


    public void setDevices(List<Device> devices) {
	this.devices = devices;
    }


    public List<Relation> getRelations() {
	return relations;
    }


    public void setRelations(List<Relation> relations) {
	this.relations = relations;
    }


    @Override
    public String toString() {
	return "LLTD [publicIP=" + publicIP + ", date=" + date + ", devices=" + devices + ", relations=" + relations + "]";
    }
}
