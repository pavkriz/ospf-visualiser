package org.hkfree.ospf.gui.netstateswin;

import java.util.List;

import javax.swing.JFrame;

import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.ospffault.IntervalFault;
import org.hkfree.ospf.model.ospffault.OspfLinkFault;

/**
 * Třída představující manažer okna zobrazení stavu sítě v čase
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetStatesWinManager {

    private JFrame owner = null;
    private NSWGraphComponent graphComponent = null;
    private NetChangeModel netChangeModel = new NetChangeModel();
    private int actualNetStateIndex = 0;


    /**
     * Konstruktor
     * @param owner
     */
    public NetStatesWinManager(JFrame owner) {
	this.owner = owner;
    }


    /**
     * Načte NetStateModel
     */
    public void loadNetStateModel() {
	graphComponent.setNetChangeModel(netChangeModel);
	graphComponent.createGraph();
	graphComponent.applyActualNetStateView(actualNetStateIndex);
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName()
		.toString(), "", "", "", "");
    }


    /**
     * Aplikuje pohled na první stav sítě
     */
    public void setFirstNetStateView() {
	netChangeModel.resetActuallyLivedAndDeads();
	actualNetStateIndex = 0;
	netChangeModel.setActualNetStateIndex(actualNetStateIndex);
	graphComponent.applyActualNetStateView(actualNetStateIndex);
	((NetStatesWin) owner).getNSWActionListener().getActionFirstState().setEnabled(false);
	((NetStatesWin) owner).getNSWActionListener().getActionPreviousState().setEnabled(false);
	((NetStatesWin) owner).getNSWActionListener().getActionNextState().setEnabled(true);
	((NetStatesWin) owner).getNSWActionListener().getActionLastState().setEnabled(true);
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName()
		.toString(), "", "", "", "");
	graphComponent.tryShowShortestPathOnOtherState();
	checkCostDifferences();
    }


    /**
     * Aplikuje pohled na předchozí stav sítě
     */
    public void setPreviousNetStateView() {
	netChangeModel.resetActuallyLivedAndDeads();
	if (actualNetStateIndex > 0) {
	    actualNetStateIndex--;
	}
	netChangeModel.setActualNetStateIndex(actualNetStateIndex);
	if (actualNetStateIndex == 0) {
	    ((NetStatesWin) owner).getNSWActionListener().getActionFirstState().setEnabled(false);
	    ((NetStatesWin) owner).getNSWActionListener().getActionPreviousState().setEnabled(false);
	}
	((NetStatesWin) owner).getNSWActionListener().getActionNextState().setEnabled(true);
	((NetStatesWin) owner).getNSWActionListener().getActionLastState().setEnabled(true);
	graphComponent.applyActualNetStateView(actualNetStateIndex);
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName()
		.toString(), netChangeModel.getActualLivedLinksLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualDeadLinksLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualLivedRoutersLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualDeadRoutersLogAndMarkThem(actualNetStateIndex));
	graphComponent.tryShowShortestPathOnOtherState();
	checkCostDifferences();
    }


    /**
     * Aplikuje pohled na následující stav sítě
     */
    public void setNextNetStateView() {
	netChangeModel.resetActuallyLivedAndDeads();
	if (actualNetStateIndex < netChangeModel.getNetStatesCount() - 1) {
	    actualNetStateIndex++;
	}
	netChangeModel.setActualNetStateIndex(actualNetStateIndex);
	if (actualNetStateIndex == netChangeModel.getNetStatesCount() - 1) {
	    ((NetStatesWin) owner).getNSWActionListener().getActionLastState().setEnabled(false);
	    ((NetStatesWin) owner).getNSWActionListener().getActionNextState().setEnabled(false);
	}
	((NetStatesWin) owner).getNSWActionListener().getActionFirstState().setEnabled(true);
	((NetStatesWin) owner).getNSWActionListener().getActionPreviousState().setEnabled(true);
	graphComponent.applyActualNetStateView(actualNetStateIndex);
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName()
		.toString(), netChangeModel.getActualLivedLinksLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualDeadLinksLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualLivedRoutersLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualDeadRoutersLogAndMarkThem(actualNetStateIndex));
	graphComponent.tryShowShortestPathOnOtherState();
	checkCostDifferences();
    }


    /**
     * Aplikuje pohled na poslední stav sítě
     */
    public void setLastNetStateView() {
	netChangeModel.resetActuallyLivedAndDeads();
	actualNetStateIndex = netChangeModel.getNetStatesCount() - 1;
	netChangeModel.setActualNetStateIndex(actualNetStateIndex);
	graphComponent.applyActualNetStateView(actualNetStateIndex);
	((NetStatesWin) owner).getNSWActionListener().getActionFirstState().setEnabled(true);
	((NetStatesWin) owner).getNSWActionListener().getActionPreviousState().setEnabled(true);
	((NetStatesWin) owner).getNSWActionListener().getActionNextState().setEnabled(false);
	((NetStatesWin) owner).getNSWActionListener().getActionLastState().setEnabled(false);
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName()
		.toString(), netChangeModel.getActualLivedLinksLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualDeadLinksLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualLivedRoutersLogAndMarkThem(actualNetStateIndex),
		netChangeModel.getActualDeadRoutersLogAndMarkThem(actualNetStateIndex));
	graphComponent.tryShowShortestPathOnOtherState();
	checkCostDifferences();
    }


    /**
     * Pokud je nastaven ospf link fault model, je povolena akce zobrazení výpadků spojů
     */
    public void checkLinkFaultModeToEnable() {
	if (netChangeModel.getOspfLinkFaultModel() != null) {
	    ((NetStatesWin) owner).getNSWActionListener().getActionLinkFaultMode().setEnabled(true);
	}
    }


    /**
     * Nastaví komponentu vykreslení grafu
     * @param component
     */
    public void setGraphComponent(NSWGraphComponent component) {
	this.graphComponent = component;
    }


    /**
     * Vrací komponentu vykreslení grafu
     * @return component
     */
    public NSWGraphComponent getGraphComponent() {
	return this.graphComponent;
    }


    /**
     * Vrací NetChangeModel
     * @return NetChangeModel
     */
    public NetChangeModel getNetChangeModel() {
	return netChangeModel;
    }


    /**
     * Nastavuje NetChangeModel
     * @param netChangeModel
     */
    public void setNetChangeModel(NetChangeModel netChangeModel) {
	this.netChangeModel = netChangeModel;
    }


    /**
     * Zavře okno
     */
    public void closeNetStatesWin() {
	((NetStatesWin) owner).closeWin();
    }


    /**
     * Vrací vlastníka
     * @return owner
     */
    public JFrame getOwner() {
	return owner;
    }


    /**
     * Nastavuje vlastníka
     * @param owner
     */
    public void setOwner(JFrame owner) {
	this.owner = owner;
    }


    /**
     * Zkontroluje zda nebyly nalezeny nějaké rozdíly v costech mezi současným a předchozím modelem
     */
    private void checkCostDifferences() {
	netChangeModel.checkExistenceOfCostDifferences();
	if (netChangeModel.existCostDifferences()) {
	    ((NetStatesWin) owner).getNSWActionListener().getActionShowCostDifferences().setEnabled(true);
	} else {
	    ((NetStatesWin) owner).getNSWActionListener().getActionShowCostDifferences().setEnabled(false);
	}
    }


    /**
     * Zobrazí dialog se změnami cen
     */
    public void showCostDifferences() {
	((NetStatesWin) owner).showCostDifferencesDialog(netChangeModel.getCostDifferences());
    }


    /**
     * Zobrazí dialog s výpadky spoje
     * @param linkID
     */
    public void showLinkFaultDialog(String linkID) {
	List<IntervalFault> ifs = null;
	for (OspfLinkFault olf : netChangeModel.getOspfLinkFaultModel().getOspfLinkFaults()) {
	    if (olf.getOspfLink().getLinkIDv4().equals(linkID)) {
		ifs = olf.getIntervalFaults();
		break;
	    }
	}
	if (ifs != null) {
	    ((NetStatesWin) owner).showLinkFaultsDialog(ifs, linkID);
	}
    }


    /**
     * Nastaví text status infa dle indesu (číslováno od 0)
     * @param text
     */
    protected void setStatusText(int index, String text) {
	((NetStatesWin) owner).getStatusBar().setStatus(index, text);
    }


    /**
     * Nastaví text tooltipu status infa dle indexu (číslováno od 0)
     * @param text
     */
    protected void setStatusTextToolTip(int index, String text) {
	((NetStatesWin) owner).getStatusBar().setToolTip(index, text);
    }
    
    
    public void setMode(MODE mode) {
   	switch (mode) {
//   	    case SHOW_NEIGHBORS:
//   		graphComponent.setShowNeighboursMode();
//   		break;
//   	    case COST_CHANGING:
//   		graphComponent.setCostChangingMode();
//   		break;
//   	    case PICKING:
//   		graphComponent.setPickingMode();
//   		break;
//   	    case TRANSFORMING:
//   		graphComponent.setTransformingMode();
//   		break;
   	    case LAYOUT_FR_START:
   	    case LAYOUT_SPRING_START:
   	    case LAYOUT_SPRING_STOP:
   		graphComponent.layouting(mode);
   		break;
   	    case ZOOM:
   		graphComponent.setZoomMode();
   		break;
//   	    case LOCK_VERTEX:
//   		graphComponent.setLockVertexMode();
//   		break;
//   	    case LOCK_ALL:
//   		graphComponent.lockAllRouterVertices();
//   		break;
//   	    case SHORTEST_PATH:
//   		graphComponent.setShowShortestPathMode();
//   		break;
//   	    case GPS:
//   		graphComponent.setGPSPositioningMode();
//   		break;
//   	    case GPS_ALL:
//   		graphComponent.setAllVerticesToGPSPosition();
//   		break;
//   	    case ADD_VERTEXES:
//   		graphComponent.setAddRVertexMode();
//   		break;
//   	    case ADD_EDGES:
//   		graphComponent.setAddLEdgeMode();
//   		break;
//   	    case ASYMETRIC_LINKS:
//   		graphComponent.setAsymetricLinkMode();
//   		break;
//   	    case SHORTEST_PATH_TWO_ROUTERS:
//   		graphComponent.setTwoRoutersShortesPathMode();
//   		break;
//   	    case IPV6:
//   		graphComponent.setShowIPv6(!graphComponent.isShowIPv6());
//   		graphComponent.setShowIPv6(graphComponent.isShowIPv6());
//   		break;
   	    default:
   		break;
   	}
    }
}
