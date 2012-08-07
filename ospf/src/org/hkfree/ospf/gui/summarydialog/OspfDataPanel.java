package org.hkfree.ospf.gui.summarydialog;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.hkfree.ospf.model.ospf.OspfModel;

/**
 * Třída představující panel s textovým výpisem informací o modelu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfDataPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextArea ospfDataText = new JTextArea();
    private OspfModel ospfModel = null;


    /**
     * Konstruktor
     */
    public OspfDataPanel() {
	makeComponents();
    }


    /**
     * Konstruktor
     * @param model
     */
    public OspfDataPanel(OspfModel model) {
	this.ospfModel = model;
	makeComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
	Font font = new Font("Monospaced", Font.PLAIN, 12);
	ospfDataText.setFont(font);
	ospfDataText.setEditable(false);
	this.setLayout(new BorderLayout());
	this.add(new JScrollPane(ospfDataText));
    }


    /**
     * Nastaví OspfModel
     * @param model
     */
    public void setOspfModel(OspfModel model) {
	this.ospfModel = model;
    }


    /**
     * Vyplní text o modelu
     */
    public void fillOspfDataText() {
	this.ospfDataText.setText(this.ospfModel.modelToString());
    }
}
