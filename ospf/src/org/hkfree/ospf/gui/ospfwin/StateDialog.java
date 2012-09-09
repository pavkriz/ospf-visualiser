package org.hkfree.ospf.gui.ospfwin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.hkfree.ospf.tools.Factory;

/**
 * Reprezentuje okno s údaji o právě probíhající operaci
 * @author Jan Schovánek
 */
public class StateDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private static final int POCET_ZNAKU_ZAROVNANI = 35;
    private Dimension size = new Dimension(430, 200);
    private JScrollPane sp = null;
    private ColorPane cp = null;
    private Long time = null;


    /**
     * Konstruktor
     * @param closeable přiznak zda se po zavolání metody close uzavře okno
     * @param owner nadrizene okno
     */
    public StateDialog(OspfWin owner) {
	super(owner);
	createGUI();
	this.setTitle(rb.getString("stated.title"));
	this.setLocationRelativeTo(null);
	this.pack();
    }


    /**
     * Vytvoří a umístí komponenty dialogu
     */
    private void createGUI() {
	cp = new ColorPane();
	cp.setFont(new Font("Monospaced", Font.PLAIN, 12));
	sp = new JScrollPane(cp);
	sp.setPreferredSize(size);
	sp.setMaximumSize(size);
	sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	sp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

	    public void adjustmentValueChanged(AdjustmentEvent e) {
		e.getAdjustable().setValue(e.getAdjustable().getMaximum());
	    }
	});
	this.add(sp, BorderLayout.CENTER);
    }


    /**
     * Přidá text do výpisu
     * @param text
     */
    public void addText(String text) {
	cp.append(Color.BLACK, String.format(" %-" + POCET_ZNAKU_ZAROVNANI + "s", text));
	time = new Date().getTime();
    }


    /**
     * Přidá info o času jak dlouho operace trvala.<br>
     * Může být zavolána pouze pokud byla před ní zavolána metoda addText.
     */
    public void operationSucceeded() {
	cp.append(Color.GREEN, "ok");
	cp.append(Color.DARK_GRAY, String.format("  [ %5sms ]\n", (new Date().getTime() - time)));
	cp.update(cp.getGraphics());
	this.validate();
    }


    /**
     * 
     */
    public void operationFailed() {
	cp.append(Color.RED, "er");
	cp.append(Color.DARK_GRAY, String.format("  [ %5sms ]\n", (new Date().getTime() - time)));
	this.update(this.getGraphics());
    }


    /**
     * Počáteční inicializace
     */
    public void init() {
	cp.setText("");
	time = null;
	this.setVisible(true);
    }


    /**
     * Smaže výpis a uzavře okno
     */
    public void close() {
	this.setVisible(false);
    }

    /**
     * Komponenta pro textový výpis, umožňuje změnu barvy fontu oproto JTextArea.
     */
    class ColorPane extends JTextPane {

	private static final long serialVersionUID = 1L;


	public void append(Color c, String s) {
	    StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
	    int len = getDocument().getLength(); // same value as getText().length();
	    setCaretPosition(len); // place caret at the end (with no selection)
	    setCharacterAttributes(aset, false);
	    replaceSelection(s); // there is no selection, so inserts at caret
	}
    }
}
