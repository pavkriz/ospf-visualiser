package org.hkfree.ospf.gui.netstateswin;

import java.util.List;

import javax.swing.JFrame;

import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.ospffault.IntervalFault;
import org.hkfree.ospf.model.ospffault.OspfLinkFault;

/**
 * Třída představující manažer okna zobrazení stavu sítě v čase
 * @author Jakub Menzel
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
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName().toString(), "", "", "", "");
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
	((NetStatesWin) owner).setChangeInfo(netChangeModel.getNetStates().get(actualNetStateIndex).getStateName().toString(), "", "", "", "");
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
     * Pokud je nastaven ospf link fault model, je povolena akce zobrzení výpadků spojů
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
    public void checkCostDifferences() {
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
	    if (olf.getOspfLink().getLinkID().equals(linkID)) {
		ifs = olf.getIntervalFaults();
		break;
	    }
	}
	if (ifs != null) {
	    ((NetStatesWin) owner).showLinkFaultsDialog(ifs, linkID);
	}
    }


    /**
     * Nastaví text status infa 1
     * @param text
     */
    public void setStatusInfo1Text(String text) {
	((NetStatesWin) owner).setStatusInfo1(text);
    }


    /**
     * Nastaví text status infa 2
     * @param text
     */
    public void setStatusInfo2Text(String text) {
	((NetStatesWin) owner).setStatusInfo2(text);
    }


    /**
     * Nastaví tooltip text status infa 2
     * @param text
     */
    public void setStatusInfo2ToolTip(String text) {
	((NetStatesWin) owner).setStatusInfo2ToolTip(text);
    }
}
