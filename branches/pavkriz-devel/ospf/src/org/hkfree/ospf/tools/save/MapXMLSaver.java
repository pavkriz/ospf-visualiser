package org.hkfree.ospf.tools.save;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Třída sloužící pro ukládání rozpracované práce návrhu sítě do XML
 * @author Jakub Menzel
 */
public class MapXMLSaver {

    private MapModel mapModel = null;
    private Map<RouterVertex, Point2D> vertexPositions = null;
    private Map<RouterVertex, String> vertexIdentificators = new HashMap<RouterVertex, String>();
    private File outputFile = null;


    /**
     * Konstruktor
     */
    public MapXMLSaver() {}


    /**
     * Nastaví mapModel
     * @param mapModel
     */
    public void setMapModel(MapModel mapModel) {
	this.mapModel = mapModel;
    }


    /**
     * Nastaví pozice routerů, které byly zobrazeny v grafu
     * @param positions
     */
    public void setVertexPositions(Map<RouterVertex, Point2D> positions) {
	this.vertexPositions = positions;
    }


    /**
     * Nastaví výstupní soubor
     * @param outputFile
     */
    public void setOutputFile(File outputFile) {
	this.outputFile = outputFile;
    }


    /**
     * Vytvoří identifikátory routerů
     */
    public void createRVertexIdentificators() {
	int i = 0;
	for (RouterVertex rv : mapModel.getRouterVertices()) {
	    vertexIdentificators.put(rv, "rv" + i);
	    i++;
	}
    }


    /**
     * Vrací integer reprezentaci bool hodnoty
     * @param value
     * @return int
     */
    public int getValueOfBool(boolean value) {
	if (value)
	    return 1;
	else
	    return 0;
    }


    /**
     * Vytvoří dokument z modelu a uloží ho do zvoleného souboru
     */
    public void createDocumentFromModel() {
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	try {
	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    Document doc = builder.newDocument();
	    Element ELMmapModel = doc.createElement("map-model");
	    doc.appendChild(ELMmapModel);
	    Element ELMrouterVertexes = doc.createElement("router-vertices");
	    ELMmapModel.appendChild(ELMrouterVertexes);
	    for (RouterVertex rv : mapModel.getRouterVertices()) {
		Element ELMrouterVertex = doc.createElement("router-vertex");
		ELMrouterVertexes.appendChild(ELMrouterVertex);
		Element ELMid = doc.createElement("id");
		ELMid.appendChild(doc.createTextNode(vertexIdentificators.get(rv)));
		ELMrouterVertex.appendChild(ELMid);
		Element ELMrName = doc.createElement("r-name");
		ELMrName.appendChild(doc.createTextNode(rv.getName()));
		ELMrouterVertex.appendChild(ELMrName);
		Element ELMrDescription = doc.createElement("r-description");
		ELMrDescription.appendChild(doc.createTextNode(rv.getInfo()));
		ELMrouterVertex.appendChild(ELMrDescription);
		Element ELMrIsMultilink = doc.createElement("r-is-multilink");
		ELMrIsMultilink.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(rv.isMultilink()))));
		ELMrouterVertex.appendChild(ELMrIsMultilink);
		Element ELMrIsEnabled = doc.createElement("r-is-enabled");
		ELMrIsEnabled.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(rv.isEnabled()))));
		ELMrouterVertex.appendChild(ELMrIsEnabled);
		Element ELMrIsLocked = doc.createElement("r-is-locked");
		ELMrIsLocked.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(rv.isLocked()))));
		ELMrouterVertex.appendChild(ELMrIsLocked);
		Element ELMrIsPermDispl = doc.createElement("r-is-permanently-displayed");
		ELMrIsPermDispl
			.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(rv.isPermanentlyDisplayed()))));
		ELMrouterVertex.appendChild(ELMrIsPermDispl);
		Element ELMrGpsLat = doc.createElement("r-gps-lat");
		ELMrGpsLat.appendChild(doc.createTextNode(Double.toString(rv.getGpsLatitude())));
		ELMrouterVertex.appendChild(ELMrGpsLat);
		Element ELMrGpsLng = doc.createElement("r-gps-lng");
		ELMrGpsLng.appendChild(doc.createTextNode(Double.toString(rv.getGpsLongtitude())));
		ELMrouterVertex.appendChild(ELMrGpsLng);
		Element ELMrIsExtraAdded = doc.createElement("r-is-extra-added");
		ELMrIsExtraAdded.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(rv.isExtraAddedVertex()))));
		ELMrouterVertex.appendChild(ELMrIsExtraAdded);
		Element ELMrPosition = doc.createElement("router-position");
		ELMrouterVertex.appendChild(ELMrPosition);
		double posX = 0;
		double posY = 0;
		if (vertexPositions.keySet().contains(rv)) {
		    posX = vertexPositions.get(rv).getX();
		    posY = vertexPositions.get(rv).getY();
		}
		Element ELMposX = doc.createElement("position-x");
		ELMposX.appendChild(doc.createTextNode(Double.toString(posX)));
		ELMrPosition.appendChild(ELMposX);
		Element ELMposY = doc.createElement("position-y");
		ELMposY.appendChild(doc.createTextNode(Double.toString(posY)));
		ELMrPosition.appendChild(ELMposY);
	    }
	    Element ELMlinkEdges = doc.createElement("link-edges");
	    ELMmapModel.appendChild(ELMlinkEdges);
	    for (LinkEdge le : mapModel.getLinkEdges()) {
		Element ELMlinkEdge = doc.createElement("link-edge");
		ELMlinkEdges.appendChild(ELMlinkEdge);
		Element ELMlId = doc.createElement("l-id");
		ELMlId.appendChild(doc.createTextNode(le.getLinkIDv4()));
		ELMlinkEdge.appendChild(ELMlId);
		Element ELMlRvId1 = doc.createElement("l-r1-id");
		ELMlRvId1.appendChild(doc.createTextNode(vertexIdentificators.get(le.getVertex1())));
		ELMlinkEdge.appendChild(ELMlRvId1);
		Element ELMlRvId2 = doc.createElement("l-r2-id");
		ELMlRvId2.appendChild(doc.createTextNode(vertexIdentificators.get(le.getVertex2())));
		ELMlinkEdge.appendChild(ELMlRvId2);
		Element ELMlRvcost1 = doc.createElement("l-r1-cost");
		ELMlRvcost1.appendChild(doc.createTextNode(Integer.toString(le.getCost1v4())));
		ELMlinkEdge.appendChild(ELMlRvcost1);
		Element ELMlRvcost2 = doc.createElement("l-r2-cost");
		ELMlRvcost2.appendChild(doc.createTextNode(Integer.toString(le.getCost2v4())));
		ELMlinkEdge.appendChild(ELMlRvcost2);
		Element ELMlIsEnabled = doc.createElement("l-is-enabled");
		ELMlIsEnabled.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(le.isEnabled()))));
		ELMlinkEdge.appendChild(ELMlIsEnabled);
		Element ELMlIsExtraAdded = doc.createElement("l-is-extra-added");
		ELMlIsExtraAdded.appendChild(doc.createTextNode(Integer.toString(getValueOfBool(le.isExtraAddedEdge()))));
		ELMlinkEdge.appendChild(ELMlIsExtraAdded);
	    }
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Transformer transformer = factory.newTransformer();
	    Result result = new StreamResult(outputFile);
	    Source source = new DOMSource(doc);
	    transformer.transform(source, result);
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	}
    }
}
