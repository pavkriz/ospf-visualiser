package org.hkfree.ospf.gui.sourcedialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.hkfree.ospf.tools.Factory;

/**
 * ActionListener dialogu pro výběr logů k načtení
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LogSourceDialogActionListener implements ActionListener {

    private ResourceBundle rb = Factory.getRb();
    private JDialog owner = null;
    private Action actionStorno = null;
    private Action actionOk = null;
    private Action actionLoadLogsTree = null;


    /**
     * Konstruktor
     * @param owner
     */
    public LogSourceDialogActionListener(JDialog owner) {
	this.owner = owner;
	createActions();
    }


    /**
     * Vytvoří akce
     */
    private void createActions() {
	actionOk = new AbstractAction(rb.getString("slsdal.0")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((LogSourceDialog) owner).applySettings();
		((LogSourceDialog) owner).setVisible(false);
	    }
	};
	actionStorno = new AbstractAction(rb.getString("storno")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((LogSourceDialog) owner).setVisible(false);
	    }
	};
	actionLoadLogsTree = new AbstractAction(rb.getString("slsdal.1")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((LogSourceDialog) owner).loadLogSourceTree();
	    }
	};
    }


    /**
	 * 
	 */
    public void actionPerformed(ActionEvent arg0) {}


    /**
     * Vrací akci storna
     * @return action
     */
    public Action getActionStorno() {
	return actionStorno;
    }


    /**
     * Vrací akci potvrzení
     * @return action
     */
    public Action getActionOk() {
	return actionOk;
    }


    /**
     * Vrací akci načtení stromu dostupných logů
     * @return action
     */
    public Action getActionLoadLogsTree() {
	return actionLoadLogsTree;
    }
}
