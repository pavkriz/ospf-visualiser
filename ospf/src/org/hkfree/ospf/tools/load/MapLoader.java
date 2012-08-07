package org.hkfree.ospf.tools.load;

import java.util.ResourceBundle;

import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.ospf.OspfLink;
import org.hkfree.ospf.model.ospf.OspfLinkData;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.geo.GPSPoint;

/**
 * Třída, která slouží k převádění OspfModelu na MapaModel
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapLoader {

    /**
     * Metoda, která převede celý zadaný OspfModel na MapaModel
     */
    public static MapModel convertWholeModel(OspfModel ospfModel) {
	ResourceBundle rb = Factory.getRb();
	MapModel mapModel = new MapModel();
	int multilinkCount = 0, cost1 = 0, cost2 = 0;
	String descr1 = "", descr2 = "", ip = "", ip1 = "", ip2 = "";
	for (OspfLink link : ospfModel.getOspfLinks()) {
	    if (link.getRoutersCount() == 2) {
		/**
		 * for (Router r : link.getRoutersOfLink().keySet()){
		 * if (ip1.equals("")){
		 * ip1 = r.getRouterIP();
		 * if (!r.getRouterName().equals(""))
		 * descr1 = r.getRouterName();
		 * else
		 * descr1 = r.getRouterIP();
		 * cost1 = link.getRoutersOfLink().get(r);
		 * }else{
		 * ip2 = r.getRouterIP();
		 * if (!r.getRouterName().equals(""))
		 * descr2 = r.getRouterName();
		 * else
		 * descr2 = r.getRouterIP();
		 * cost2 = link.getRoutersOfLink().get(r);
		 * }
		 * }
		 */
		ip1 = link.getOspfLinkData().get(0).getRouter().getRouterID();
		ip2 = link.getOspfLinkData().get(1).getRouter().getRouterID();
		if (link.getOspfLinkData().get(0).getRouter().getRouterName().equals("")) {
		    descr1 = link.getOspfLinkData().get(0).getRouter().getRouterID();
		} else {
		    descr1 = link.getOspfLinkData().get(0).getRouter().getRouterName();
		}
		if (link.getOspfLinkData().get(1).getRouter().getRouterName().equals("")) {
		    descr2 = link.getOspfLinkData().get(1).getRouter().getRouterID();
		} else {
		    descr2 = link.getOspfLinkData().get(1).getRouter().getRouterName();
		}
		cost1 = link.getOspfLinkData().get(0).getCost();
		cost2 = link.getOspfLinkData().get(1).getCost();
		GPSPoint gp1 = link.getOspfLinkData().get(0).getRouter().getGpsPosition();
		GPSPoint gp2 = link.getOspfLinkData().get(1).getRouter().getGpsPosition();
		mapModel.addLinkEdge(ip1, ip2, descr1, descr2, cost1, cost2, gp1, gp2, link.getLinkID());
		ip1 = "";
		ip2 = "";
	    } else {
		multilinkCount++;
		String multilinkDescr = "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + link.getLinkID()
			+ "</b></body></html>";
		/**
		 * String multilinkDescr = "<html><body>ID spoje: <b>" + link.getLinkName() + "</b><br>
		 * <br>
		 * Ceny do spoje z routerů:<br>
		 * ";
		 * for (Router r : link.getRoutersOfLink().keySet()){
		 * if (!r.getRouterName().equals(""))
		 * multilinkDescr += "<b>" + r.getRouterName() +"</b> (" + r.getRouterIP() + ") : <b>" +
		 * link.getRoutersOfLink().get(r) + "</b><br>
		 * ";
		 * else
		 * multilinkDescr += "<b>" + r.getRouterIP() +"</b> (" + r.getRouterIP() + ") : <b>" +
		 * link.getRoutersOfLink().get(r) + "</b><br>
		 * ";
		 * }
		 * multilinkDescr += "</body></html>";
		 */
		/**
		 * for (Router r : link.getRoutersOfLink().keySet()){
		 * ip = r.getRouterIP();
		 * if (!r.getRouterName().equals(""))
		 * descr1 = r.getRouterName();
		 * else
		 * descr1 = r.getRouterIP();
		 * output.addLinkEdge(ip, "Multispoj"+Integer.toString(multilinkCount),
		 * descr1,multilinkDescr,link.getRoutersOfLink().get(r),0,link.getLinkName());
		 * }
		 */
		for (OspfLinkData old : link.getOspfLinkData()) {
		    ip = old.getRouter().getRouterID();
		    GPSPoint gp = old.getRouter().getGpsPosition();
		    if (!old.getRouter().getRouterName().equals(""))
			descr1 = old.getRouter().getRouterName();
		    else
			descr1 = old.getRouter().getRouterID();
		    mapModel.addLinkEdge(ip, rb.getString("mm.0") + Integer.toString(multilinkCount), descr1,
			    multilinkDescr, old.getCost(), 0, gp, null, link.getLinkID());
		}
	    }
	}
	return mapModel;
    }
}
