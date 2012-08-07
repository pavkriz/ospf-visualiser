package org.hkfree.ospf.gui.sourcedialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.hkfree.ospf.tools.Factory;

/**
 * ActionListener dialogu pro výběr vstupních dat
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class ModelSourceDialogActionListener implements ActionListener {

    private ResourceBundle rb = Factory.getRb();
    private JDialog owner = null;
    private Action actionStorno = null;
    private Action actionOk = null;
    private Action actionLoadSourcesRemote = null;
    private Action actionLoadSourcesLocal = null;
    private Action actionChooseLocalFolder = null;
    private Action actionChooseLocalFolderSingleModel = null;


    /**
     * Konstruktor
     * @param owner
     */
    public ModelSourceDialogActionListener(JDialog owner) {
	this.owner = owner;
	createActions();
    }


    /**
     * Vytvoří akce
     */
    private void createActions() {
	actionOk = new AbstractAction(rb.getString("ssdal.0")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		try {
		    ((ModelSourceDialog) owner).applySettings();
		    ((ModelSourceDialog) owner).setVisible(false);
		} catch (Exception e1) {
		    // osetrenim vyjimky je to, ze zustane zobrazen dialog pro vyber zdroje
		}
		
	    }
	};
	actionOk.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("ssdal.0.title"));
	actionStorno = new AbstractAction(rb.getString("storno")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		owner.setVisible(false);
	    }
	};
	actionLoadSourcesRemote = new AbstractAction(rb.getString("ssdal.1")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((ModelSourceDialog) owner).loadRemoteServerSourceTree();
	    }
	};
	actionLoadSourcesRemote.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("ssdal.1.title"));
	actionLoadSourcesLocal = new AbstractAction(rb.getString("ssdal.1")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((ModelSourceDialog) owner).loadLocalSourceTree();
	    }
	};
	actionLoadSourcesLocal.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("ssdal.1.title"));
	actionChooseLocalFolder = new AbstractAction(rb.getString("ssdal.2")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((ModelSourceDialog) owner).chooseLocalFolder();
	    }
	};
	actionChooseLocalFolder.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("ssdal.2.title"));
	actionChooseLocalFolderSingleModel = new AbstractAction(rb.getString("ssdal.2")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((ModelSourceDialog) owner).chooseLocalFolderForSingleModel();
	    }
	};
	actionChooseLocalFolderSingleModel.putValue(AbstractAction.SHORT_DESCRIPTION, rb.getString("ssdal.2.title"));
    }


    public void actionPerformed(ActionEvent arg0) {}


    /**
     * Vrací akci storna
     * @return action
     */
    public Action getActionStorno() {
	return actionStorno;
    }


    /**
     * Vrací akci načtení stromu dostupných dat ze serveru
     * @return action
     */
    public Action getActionLoadSourcesRemote() {
	return actionLoadSourcesRemote;
    }


    /**
     * Vrací akci načtení stromu dostupných dat na lokálním disku
     * @return action
     */
    public Action getActionLoadSourcesLocal() {
	return actionLoadSourcesLocal;
    }


    /**
     * Vrací akci otevření výběru místní složky
     * @return action
     */
    public Action getActionChooseLocalFolder() {
	return actionChooseLocalFolder;
    }


    /**
     * Vrací akci otevření výběru složky se single modelem
     * @return action
     */
    public Action getActionChooseLocalFolderSingleModel() {
	return actionChooseLocalFolderSingleModel;
    }


    /**
     * Vrací akci po stisku OK při načítání pomocí telnetu
     * @return
     */
    public Action getActionOk() {
	return actionOk;
    }
}
