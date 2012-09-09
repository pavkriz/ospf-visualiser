package org.hkfree.ospf.gui.netstateswin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.tools.Factory;

/**
 * ActionListener okna zobrazení stavu sítě v čase
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetStatesWinActionListener implements ActionListener {

    private ResourceBundle rb = Factory.getRb();
    private NetStatesWinManager netStatesWinManager = null;
    private Action actionCloseWin = null;
    private Action actionFirstState = null;
    private Action actionPreviousState = null;
    private Action actionNextState = null;
    private Action actionLastState = null;
    private Action actionPickingMode = null;
    private Action actionTransformingMode = null;
    private Action actionStartLayouting = null;
    private Action actionLockMode = null;
    private Action actionLockAll = null;
    private Action actionNoneMode = null;
    private Action actionShortestPath = null;
    private Action actionShowCostDifferences = null;
    private Action actionLinkFaultMode = null;


    /**
     * Konstruktor
     */
    public NetStatesWinActionListener() {
	createActions();
    }


    /**
     * Nastavuje manažer okna
     * @param winManager
     */
    public void setWinManager(NetStatesWinManager winManager) {
	this.netStatesWinManager = winManager;
    }


    /**
     * Vytváří akce
     */
    public void createActions() {
	actionCloseWin = new AbstractAction(rb.getString("close"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "exit.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.closeNetStatesWin();
	    }
	};
	actionCloseWin.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("close"));
	actionFirstState = new AbstractAction("", new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "first.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.setFirstNetStateView();
	    }
	};
	actionFirstState.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.0.title"));
	actionFirstState.setEnabled(false);
	actionPreviousState = new AbstractAction("", new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "previous.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.setPreviousNetStateView();
	    }
	};
	actionPreviousState.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.1.title"));
	actionPreviousState.setEnabled(false);
	actionNextState = new AbstractAction("", new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "next.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.setNextNetStateView();
	    }
	};
	actionNextState.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.2.title"));
	actionLastState = new AbstractAction("", new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "last.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.setLastNetStateView();
	    }
	};
	actionLastState.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.3.title"));
	actionPickingMode = new AbstractAction(rb.getString("nswal.4"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "picking.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().setPickingMode();
		netStatesWinManager.setStatusText(0, rb.getString("nswal.4"));
	    }
	};
	actionPickingMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.4.title"));
	actionTransformingMode = new AbstractAction(rb.getString("nswal.5"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "transforming.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().setTransformingMode();
		netStatesWinManager.setStatusText(0, rb.getString("nswal.5"));
	    }
	};
	actionTransformingMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.5.title"));
	actionStartLayouting = new AbstractAction(rb.getString("nswal.6"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "startlayout.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().startLayouting();
	    }
	};
	actionStartLayouting.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.6.title"));
	actionStartLayouting.setEnabled(true);
	actionLockAll = new AbstractAction(rb.getString("nswal.8"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "lock_all.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().lockAllRouterVertexes();
	    }
	};
	actionLockAll.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.8.title"));
	actionLockMode = new AbstractAction(rb.getString("nswal.9"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "lock.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().setLockVertexMode();
		netStatesWinManager.setStatusText(1, rb.getString("nswal.9"));
		netStatesWinManager.setStatusTextToolTip(1, rb.getString("nswal.9.toolTip"));
	    }
	};
	actionLockMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.9.title"));
	actionShortestPath = new AbstractAction(rb.getString("nswal.10"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "shortest_path.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().setShowShortestPathMode();
		netStatesWinManager.setStatusText(1, rb.getString("nswal.10"));
		netStatesWinManager.setStatusTextToolTip(1, rb.getString("nswal.10.toolTip"));
	    }
	};
	actionShortestPath.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.10.title"));
	actionNoneMode = new AbstractAction(rb.getString("nswal.11"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "none.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().setNoneMode();
		netStatesWinManager.setStatusText(1, rb.getString("nswal.11"));
		netStatesWinManager.setStatusTextToolTip(1, rb.getString("nswal.11.toolTip"));
	    }
	};
	actionNoneMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.11.title"));
	actionShowCostDifferences = new AbstractAction(rb.getString("nswal.12"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "cost_difference.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.showCostDifferences();
	    }
	};
	actionShowCostDifferences.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.12.title"));
	actionShowCostDifferences.setEnabled(false);
	actionLinkFaultMode = new AbstractAction(rb.getString("nswal.13"), new ImageIcon(getClass().getResource(
		Constants.URL_IMG_GUI + "link_fault.png"))) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		netStatesWinManager.getGraphComponent().setLinkFaultMode();
		netStatesWinManager.setStatusText(1, rb.getString("nswal.13"));
		netStatesWinManager.setStatusTextToolTip(1, rb.getString("nswal.13.toolTip"));
	    }
	};
	actionLinkFaultMode.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("nswal.13.title"));
	actionLinkFaultMode.setEnabled(false);
    }


    /**
	*	
	*/
    public void actionPerformed(ActionEvent action) {}


    /**
     * Vrací akci zavření okna
     */
    public Action getActionCloseWin() {
	return actionCloseWin;
    }


    /**
     * Vrací akci zobrazení prvního modelu
     * @return action
     */
    public Action getActionFirstState() {
	return actionFirstState;
    }


    /**
     * Vrací akci přechodu na předchozí model
     * @return action
     */
    public Action getActionPreviousState() {
	return actionPreviousState;
    }


    /**
     * Vrací akci přechdou na následující model
     * @return action
     */
    public Action getActionNextState() {
	return actionNextState;
    }


    /**
     * Vrací akci přechodu na poslední model
     * @return action
     */
    public Action getActionLastState() {
	return actionLastState;
    }


    /**
     * Vrací manažera okna
     * @return NetStatesWinManager
     */
    public NetStatesWinManager getNetStatesWinManager() {
	return netStatesWinManager;
    }


    /**
     * Vrací akci změny režimu na ruční úravu vrcholů
     * @return action
     */
    public Action getActionPickingMode() {
	return actionPickingMode;
    }


    /**
     * Vrací akci změny režimu na posun celé mapy
     * @return action
     */
    public Action getActionTransformingMode() {
	return actionTransformingMode;
    }


    /**
     * Vrací akci zapnutí automatického rozvrhování
     * @return action
     */
    public Action getActionStartLayouting() {
	return actionStartLayouting;
    }


    /**
     * Vrací akci změnny režimu na zamykání vrcholů
     * @return action
     */
    public Action getActionLockMode() {
	return actionLockMode;
    }


    /**
     * Vrací akci zamčení všech vrcholů
     * @return action
     */
    public Action getActionLockAll() {
	return actionLockAll;
    }


    /**
     * Vrací akci změny režimu na zobrazení nejkratších cest
     * @return action
     */
    public Action getActionShortestPath() {
	return actionShortestPath;
    }


    /**
     * Vrací akci vypnutí nastaveného režimu
     * @return action
     */
    public Action getActionNoneMode() {
	return actionNoneMode;
    }


    /**
     * Vrací akci zobrazení změn v cenách
     * @return action
     */
    public Action getActionShowCostDifferences() {
	return actionShowCostDifferences;
    }


    /**
     * Vrací akci změny režimu na zobrazení výpadků spojů
     * @return action
     */
    public Action getActionLinkFaultMode() {
	return actionLinkFaultMode;
    }
}
