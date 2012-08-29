package org.hkfree.ospf.gui.about;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující Dialogové okno "O Aplikaci"
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class AboutApplicationDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static String homePage = "code.google.com/p/ospf-visualiser/";
	private ResourceBundle rb = Factory.getRb();
	private JButton btnOk = new JButton();


	/**
	 * Konstruktor třídy - volá metodu pro vytvoření layoutu okna - nastaví oknu
	 * potřebné parametry
	 */
	public AboutApplicationDialog(Frame okno) {
		super(okno);
		createGUI(okno);
		this.setTitle(rb.getString("aboutApp.title"));
		this.pack();
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}


	/**
	 * Metoda, která použije komponenty a vytvoří celý layout hlavního okna
	 */
	private void createGUI(Frame okno) {
		JLabel ikona = new JLabel(new ImageIcon(this.getClass().getResource(Constants.URL_IMG_GUI + "icon.png")));
		JLabel nazev = new JLabel(rb.getString("aboutApp.0"));
		nazev.setFont(new Font("Arial", 0, 13));
		JLabel version = new JLabel("v.3.0.2");
		version.setFont(new Font("Arial", 3, 12));
		JTextArea infoText = new JTextArea();
		infoText.setEditable(false);
		infoText.setFont(new Font("Arial", 0, 11));
		infoText.setText("Jan Schovánek\n" + "FIM UHK " + "(C) 2012\n\n" + "Jakub Menzel \n" + "FIM UHK " + "(C) 2011\n\n\nweb:\n"+homePage);
		btnOk.setText(rb.getString("ok"));
		btnOk.addActionListener(this);
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(ikona)
				.addGap(20)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(nazev).addComponent(version)
								.addComponent(infoText).addComponent(btnOk, Alignment.TRAILING, 100, 100, 100)));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(ikona)
								.addGroup(layout.createSequentialGroup().addComponent(nazev).addComponent(version)))
				.addGap(20).addComponent(infoText).addGap(20).addComponent(btnOk));
	}


	/**
	 * Doimplementovaná metoda volaná actionlisenerem, když nastane stisknutí
	 * tlačítka
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOk)
			this.setVisible(false);
	}
}
