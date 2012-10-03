package org.hkfree.ospf.gui.netstateswin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;

import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro výběr modelů ke zpracování
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NSModelChooseDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JButton btnOk = new JButton();
    private JButton btnStorno = new JButton();
    private JButton btnAll = new JButton();
    private JButton btnNone = new JButton();
    private boolean selectionConfirmed = false;
    private CheckBoxList ospfModelsList = new CheckBoxList();


    /**
     * Konstruktor
     * @param ospfModels
     */
    public NSModelChooseDialog(List<OspfModel> ospfModels) {
	loadModelsListItems(ospfModels);
	JScrollPane scrollPane = new JScrollPane(ospfModelsList);
	btnOk.setText(rb.getString("ok"));
	btnOk.addActionListener(this);
	btnStorno.setText(rb.getString("storno"));
	btnStorno.addActionListener(this);
	btnAll.setText(rb.getString("nsmcd.all"));
	btnAll.addActionListener(this);
	btnNone.setText(rb.getString("nsmcd.none"));
	btnNone.addActionListener(this);
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
		.addGroup(layout.createSequentialGroup()
			.addComponent(btnAll, 100, 100, 150)
			.addComponent(btnNone, 100, 100, 150))
		.addComponent(scrollPane, 300, 300, 300)
		.addGroup(layout.createSequentialGroup()
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(btnOk, 100, 100, 100)
			.addComponent(btnStorno, 100, 100, 100)));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(btnAll)
			.addComponent(btnNone))
		.addComponent(scrollPane, 300, 300, 300)
		.addGroup(layout.createParallelGroup()
			.addComponent(btnOk)
			.addComponent(btnStorno)));
	this.setTitle(rb.getString("nsmcd.title"));
	this.pack();
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	this.setModal(true);
	this.setVisible(true);
    }


    /**
     * Přidá do výběrového seznamu všechny modely
     * @param ospfModels
     */
    public void loadModelsListItems(List<OspfModel> ospfModels) {
	for (OspfModel model : ospfModels) {
	    ((DefaultListModel) ospfModelsList.getModel()).addElement(new ModelCheckBoxItem(model.getModelName(), false,
		    model));
	}
    }


    /**
     * Vrací příznak, zda je dialog potvrzený
     * @return boolean
     */
    public boolean selectionConfirmed() {
	return selectionConfirmed;
    }


    /**
     * Obsluha událostí
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btnStorno) {
	    this.setVisible(false);
	} else if (e.getSource() == btnOk) {
	    selectionConfirmed = true;
	    this.setVisible(false);
	} else if (e.getSource() == btnAll) {
	    ospfModelsList.setAllChecked();
	} else if (e.getSource() == btnNone) {
	    ospfModelsList.setNoneChecked();
	}
    }


    /**
     * Vrací seznam vybraných modelů
     * @return OspfModel
     */
    public List<OspfModel> getSelectedOspfModels() {
	List<OspfModel> selectedModels = new ArrayList<OspfModel>();
	for (int i = 0; i < ospfModelsList.getModel().getSize(); i++) {
	    if (((ModelCheckBoxItem) ospfModelsList.getModel().getElementAt(i)).isSelected()) {
		selectedModels.add(((ModelCheckBoxItem) ospfModelsList.getModel().getElementAt(i)).getOspfModel());
	    }
	}
	return selectedModels;
    }
}
