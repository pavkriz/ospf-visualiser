package org.hkfree.ospf.gui.ospfwin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Reprezentuje okno s údaji o právě probíhající operaci
 * @author Jan Schovánek
 */
public class LogDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final int POCET_ZNAKU_ZAROVNANI = 35;
	private JTextArea ta = null;
	private Long time = null;
	private boolean closeable = false;


	/**
	 * Konstruktor
	 * @param closeable přiznak zda se po zavolání metody close uzavře okno
	 * @param owner nadrizene okno
	 */
	public LogDialog(boolean closeable, OspfWin owner) {
		this.closeable = closeable;
		this.setLocationRelativeTo(owner);
		this.setSize(new Dimension(450, 200));
		ta = new JTextArea();
		ta.setEditable(false);
		ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.setVisible(false);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(ta), BorderLayout.CENTER);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				ta.setText("");
				time = null;
			}
		});
	}


	/**
	 * Přida text do výpisu
	 * @param text
	 */
	public void addText(String text) {
		ta.append(" " + String.format("%-" + POCET_ZNAKU_ZAROVNANI + "s", text));
		time = new Date().getTime();
		this.setVisible(true);
		this.update(this.getGraphics());
	}


	/**
	 * Přidá info o času jak dlouho operace trvala.<br>
	 * Může být zavolána pouze pokud byla před ní zavolána metoda addText.
	 */
	public void operationFinished() {
		ta.append("ok\t[ " + (new Date().getTime() - time) + "ms ]\n");
		this.update(this.getGraphics());
	}


	/**
	 * Smaže výpis a uzavře okno
	 */
	public void close() {
		if (this.closeable) {
			this.setVisible(false);
		}
	}
}
