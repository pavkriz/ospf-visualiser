package org.hkfree.ospf.gui.mappanel;

import javax.swing.JPanel;

import org.hkfree.ospf.gui.ospfwin.OspfWin;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.map.impl.LinkEdge;
import org.hkfree.ospf.model.map.impl.RouterVertex;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;

/**
 * Třída představující okno návrhu sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class MapPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private MapManager manager = null;
    private MapGraphComponent graphComponent = null;
    private PropertiesPanel prop = null;
    private GraphZoomScrollPane graphPanel = null;
    private OspfWin owner = null;


    /**
     * Konstruktor
     */
    public MapPanel(OspfWin ospfWin) {
	this.owner = ospfWin;
	graphComponent = new MapGraphComponent(this, owner.getManager().getSettings().edgeShaper);
	manager = new MapManager(this);
	manager.setGraphComponent(graphComponent);
    }


    /**
     * Konstruktor
     */
    public MapPanel(OspfWin ospfWin, OspfModel ospfModel) {
	this(ospfWin);
	manager.setOspfModel(ospfModel);
	createComponents();
    }


    /**
     * Konstruktor
     */
    public MapPanel(OspfWin ospfWin, MapModel mapModel) {
	this(ospfWin);
	manager.setMapModel(mapModel);
	createComponents();
    }


    public OspfWin getOwner() {
	return owner;
    }


    /**
     * Vytvoří komponenty GUI
     */
    private void createComponents() {
	prop = new PropertiesPanel(manager.getOspfModel());
	graphPanel = new GraphZoomScrollPane(graphComponent.getVisualizationComponent());
    }


    /**
     * Aktualizuje properties okno s informacemi o routeru/spoji
     * na který bylo kliknuto
     * @param o objekt routeru nebo spoje
     */
    public void setPropertiesPanel(Object o) {
	if (o instanceof RouterVertex) {
	    prop.actualizeValues((RouterVertex) o);
	}
	if (o instanceof LinkEdge) {
	    prop.actualizeValues((LinkEdge) o);
	}
    }


    /**
     * Volá manažera okna ke zpracování předaných modelů
     * @param processWholeModel
     * @param center
     * @param depth
     * @param layout
     */
    public void processModelsAfterStart(boolean processWholeModel, Router center, int depth) {
	manager.setLoadSettings(processWholeModel, center, depth);
	manager.loadMapModel();
	manager.getGraphComponent().setShowIPv6(false);
	this.repaint();
    }


    /**
     * Vrací instanci managera okna
     * @return manager
     */
    public MapManager getMapDesignWinManager() {
	return manager;
    }


    /**
     * Vrací komponentu grafu
     * @return
     */
    public MapGraphComponent getGraphComponent() {
	return graphComponent;
    }


    public JPanel getGraphPanel() {
	return graphPanel;
    }


    public JPanel getPropertiesPanel() {
	return prop;
    }
}
