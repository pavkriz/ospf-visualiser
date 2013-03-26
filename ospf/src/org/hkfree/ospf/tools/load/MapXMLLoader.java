package org.hkfree.ospf.tools.load;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Třída sloužící k načtení MapModelu z XML dokumentu určité struktury
 * @author Jakub Menzel
 */
public class MapXMLLoader {

    private MapModel mapModel = new MapModel();
    private Map<RouterVertex, Point2D> rvertexPositions = new HashMap<RouterVertex, Point2D>();
    private Map<String, RouterVertex> rvertexIdentificators = new HashMap<String, RouterVertex>();
    private File inputFile = null;


    /**
     * Vrací bool hodnotu z 0 nebo 1
     * @param i
     * @return boolean
     */
    public boolean getBoolValue(int i) {
	return i == 1;
    }


    /**
     * Vrací MapModel
     * @return mapModel
     */
    public MapModel getMapModel() {
	return this.mapModel;
    }


    /**
     * Vrací pozice vrcholů
     * @return positions
     */
    public Map<RouterVertex, Point2D> getRVertexPositions() {
	return this.rvertexPositions;
    }


    /**
     * Nastavuje vstupní soubor ke zpracování
     * @param inputFile
     */
    public void setInputFile(File inputFile) {
	this.inputFile = inputFile;
    }


    /**
     * Načte MapModel ze souboru
     */
    public void loadModelFromDocument() {
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	try {
	    builder = domFactory.newDocumentBuilder();
	    Document doc = builder.parse(inputFile);
	    NodeList nodes = doc.getElementsByTagName("map-model");
	    NodeList routerVertexesNL = ((Element) nodes.item(0)).getElementsByTagName("router-vertices");
	    NodeList routerVertexNL = ((Element) routerVertexesNL.item(0)).getElementsByTagName("router-vertex");
	    for (int i = 0; i < routerVertexNL.getLength(); i++) {
		RouterVertex routerVertex = new RouterVertex();
		mapModel.getVertices().add(routerVertex);
		NodeList id = ((Element) routerVertexNL.item(i)).getElementsByTagName("id");
		rvertexIdentificators.put(getTagStringData((Element) id.item(0)), routerVertex);
		NodeList name = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-name");
		routerVertex.setName(getTagStringData((Element) name.item(0)));
		NodeList description = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-description");
		routerVertex.setInfo(getTagStringData((Element) description.item(0)));
		NodeList isMultilink = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-is-multilink");
		routerVertex.setMultilink(getBoolValue(Integer.valueOf(getTagIntData((Element) isMultilink.item(0)))));
		NodeList isEnabled = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-is-enabled");
		routerVertex.setEnabled(getBoolValue(Integer.valueOf(getTagIntData((Element) isEnabled.item(0)))));
		NodeList isLocked = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-is-locked");
		routerVertex.setLocked(getBoolValue(Integer.valueOf(getTagIntData((Element) isLocked.item(0)))));
		NodeList isPermDispl = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-is-permanently-displayed");
		routerVertex.setPermanentlyDisplayed(getBoolValue(Integer.valueOf(getTagIntData((Element) isPermDispl
			.item(0)))));
		NodeList gpsLat = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-gps-lat");
		routerVertex.setGpsLatitude(Double.valueOf(getTagDoubleData((Element) gpsLat.item(0))));
		NodeList gpsLng = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-gps-lng");
		routerVertex.setGpsLongtitude(Double.valueOf(getTagDoubleData((Element) gpsLng.item(0))));
		NodeList isExtraAdded = ((Element) routerVertexNL.item(i)).getElementsByTagName("r-is-extra-added");
		routerVertex
			.setExtraAddedVertex(getBoolValue(Integer.valueOf(getTagIntData((Element) isExtraAdded.item(0)))));
		NodeList routerPosition = ((Element) routerVertexNL.item(i)).getElementsByTagName("router-position");
		NodeList posX = ((Element) routerPosition.item(0)).getElementsByTagName("position-x");
		Double x = Double.valueOf(getTagDoubleData((Element) posX.item(0)));
		NodeList posY = ((Element) routerPosition.item(0)).getElementsByTagName("position-y");
		Double y = Double.valueOf(getTagDoubleData((Element) posY.item(0)));
		if (x != 0 && y != 0) {
		    rvertexPositions.put(routerVertex, new Point2D.Double(x, y));
		}
	    }
	    NodeList linkEdgesNL = ((Element) nodes.item(0)).getElementsByTagName("link-edges");
	    NodeList linkEdgeNL = ((Element) linkEdgesNL.item(0)).getElementsByTagName("link-edge");
	    for (int i = 0; i < linkEdgeNL.getLength(); i++) {
		LinkEdge linkEdge = new LinkEdge();
		mapModel.getEdges().add(linkEdge);
		NodeList id = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-id");
		linkEdge.setLinkIDv4(getTagStringData((Element) id.item(0)));
		NodeList rv1 = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-r1-id");
		linkEdge.setVertex1(rvertexIdentificators.get(getTagStringData((Element) rv1.item(0))));
		NodeList rv2 = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-r2-id");
		linkEdge.setVertex2(rvertexIdentificators.get(getTagStringData((Element) rv2.item(0))));
		NodeList cost1 = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-r1-cost");
		linkEdge.setCost1v4(Integer.valueOf(getTagIntData((Element) cost1.item(0))));
		NodeList cost2 = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-r2-cost");
		linkEdge.setCost2v4(Integer.valueOf(getTagIntData((Element) cost2.item(0))));
		NodeList isEnabled = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-is-enabled");
		linkEdge.setEnabled(getBoolValue(Integer.valueOf(getTagIntData((Element) isEnabled.item(0)))));
		NodeList isExtraAdded = ((Element) linkEdgeNL.item(i)).getElementsByTagName("l-is-extra-added");
		linkEdge.setExtraAddedEdge(getBoolValue(Integer.valueOf(getTagIntData((Element) isExtraAdded.item(0)))));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }


    /**
     * vrací obsah elementu
     * @return int
     */
    public int getTagIntData(Element el) {
	Node elN = el.getFirstChild();
	if (elN instanceof CharacterData) {
	    return Integer.valueOf(((CharacterData) elN).getData());
	} else
	    return 0;
    }


    /**
     * vrací obsah elementu
     * @return double
     */
    public double getTagDoubleData(Element el) {
	Node elN = el.getFirstChild();
	if (elN instanceof CharacterData) {
	    return Double.valueOf(((CharacterData) elN).getData());
	} else
	    return 0;
    }


    /**
     * vrací obsah elementu
     * @return string
     */
    public String getTagStringData(Element el) {
	Node elN = el.getFirstChild();
	if (elN instanceof CharacterData) {
	    return ((CharacterData) elN).getData();
	} else
	    return "";
    }
}
