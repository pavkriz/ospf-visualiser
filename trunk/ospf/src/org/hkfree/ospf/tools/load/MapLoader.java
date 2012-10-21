package org.hkfree.ospf.tools.load;

import java.util.ResourceBundle;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.ospf.Link;
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
     * Převede celý zadaný OspfModel na MapaModel
     */
    public static MapModel convertWholeModel(OspfModel ospfModel) {
	ResourceBundle rb = Factory.getRb();
	MapModel mapModel = new MapModel();
	int multilinkCount = 0;
	int cost1 = 0;
	int cost2 = 0;
	int cost1IPv6 = 0;
	int cost2IPv6 = 0;
	String descr1 = "";
	String descr2 = ""; // pouzito take pro popisek multilinku
	String id1 = "";
	String id2 = "";
	for (Link link : ospfModel.getLinks()) {
	    if (link.getRoutersCount() == 2) {
		// id routeru
		id1 = link.getOspfLinkData().get(0).getRouter().getId();
		id2 = link.getOspfLinkData().get(1).getRouter().getId();
		// popisky
		if (link.getOspfLinkData().get(0).getRouter().getName().equals("")) {
		    descr1 = link.getOspfLinkData().get(0).getRouter().getId();
		} else {
		    descr1 = link.getOspfLinkData().get(0).getRouter().getName();
		}
		if (link.getOspfLinkData().get(1).getRouter().getName().equals("")) {
		    descr2 = link.getOspfLinkData().get(1).getRouter().getId();
		} else {
		    descr2 = link.getOspfLinkData().get(1).getRouter().getName();
		}
		// ceny
		cost1 = link.getOspfLinkData().get(0).getCostIPv4();
		cost2 = link.getOspfLinkData().get(1).getCostIPv4();
		cost1IPv6 = link.getOspfLinkData().get(0).getCostIPv6();
		cost2IPv6 = link.getOspfLinkData().get(1).getCostIPv6();
		// gps souradnice
		GPSPoint gp1 = link.getOspfLinkData().get(0).getRouter().getGpsPosition();
		GPSPoint gp2 = link.getOspfLinkData().get(1).getRouter().getGpsPosition();
		// pridani spoje v mapModelu
		// System.out.println(cost1IPv6 + " " + cost2IPv6);
		mapModel.addLinkEdge(id1, id2, descr1, descr2, cost1, cost2, cost1IPv6, cost2IPv6, gp1, gp2,
			link.getLinkIDv4(), link.getLinkIDv6(),
			link.getOspfLinkData());
	    } else {
		multilinkCount++;
		descr2 = "<html><body>" + rb.getString("cdtm.col1") + ": <b>" + link.getLinkIDv4() + "</b></body></html>";
		for (OspfLinkData old : link.getOspfLinkData()) {
		    id1 = old.getRouter().getId();
		    GPSPoint gp = old.getRouter().getGpsPosition();
		    if (!old.getRouter().getName().equals("")) {
			descr1 = old.getRouter().getName();
		    } else {
			descr1 = old.getRouter().getId();
		    }
		    mapModel.addLinkEdge(id1, Constants.MULTILINK + Integer.toString(multilinkCount), descr1, descr2,
			    old.getCostIPv4(), -1, old.getCostIPv6(), -1, gp, null, link.getLinkIDv4(), link.getLinkIDv6(),
			    link.getOspfLinkData());
		}
	    }
	}
	return mapModel;
    }
}
