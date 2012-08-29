package org.hkfree.ospf.gui.settingsdialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import org.hkfree.ospf.gui.ospfwin.OspfWinManager;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.LANGUAGE;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog pro nastavení aplikace
 * @author Jan Schovánek
 */
public class SettingsDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private OspfWinManager manager = null;
    private AppSettings settings = null;
    private JComboBox cbLangs = null;
    private JCheckBox chbCloseLogDialog = null;
    private JButton btnSave = new JButton();
    private JButton btnStorno = new JButton();
    private ImageIcon[] images;


    /**
     * Konstruktor
     * @param okno předek
     * @param ospfWinManager manažer ospfWin
     * @param appSettings současné nastavení aplikace
     * @param rb popisky v nastaveném jazyce
     */
    public SettingsDialog(Frame okno, OspfWinManager ospfWinManager, AppSettings appSettings) {
	super(okno);
	this.settings = appSettings;
	this.manager = ospfWinManager;
	createGUI(okno);
	this.setTitle(rb.getString("sd.title"));
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "nastaveni.png")));
	this.pack();
	this.setModal(true);
	this.setLocationRelativeTo(null);
	this.setResizable(false);
    }


    /**
     * Vytvoření GUI
     * @param okno předek
     * @param rb popisky v nastaveném jazyce
     */
    private void createGUI(Frame okno) {
	Container c = this.getContentPane();
	GroupLayout layout = new GroupLayout(c);
	c.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	// naplneni obrazky s jazyky
	images = new ImageIcon[LANGUAGE.values().length];
	for (int i = 0; i < LANGUAGE.values().length; i++) {
	    images[i] = createImageIcon(Constants.URL_IMG_GUI + "lng_" + LANGUAGE.values()[i] + ".png");
	}
	cbLangs = new JComboBox(images);
	cbLangs.setSelectedIndex(settings.lng.ordinal());
	cbLangs.addActionListener(this);
	chbCloseLogDialog = new JCheckBox(rb.getString("sd.closeLogDialog"));
	chbCloseLogDialog.setSelected(settings.closeLogDialog);
	btnSave.setText(rb.getString("save"));
	btnSave.addActionListener(this);
	btnStorno.setText(rb.getString("storno"));
	btnStorno.addActionListener(this);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(cbLangs)
			.addComponent(chbCloseLogDialog)
			.addGroup(layout.createSequentialGroup()
				.addComponent(btnSave, 100, 100, 100)
				.addComponent(btnStorno, 100, 100, 100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(cbLangs)
		.addComponent(chbCloseLogDialog)
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(btnStorno)
			.addComponent(btnSave)));
    }


    /**
     * Načtení ikony jazyka
     * @param path
     * @return
     */
    protected static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = SettingsDialog.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	}
    }


    /**
     * Obsluha událostí
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btnStorno)
	    this.setVisible(false);
	else if (e.getSource() == btnSave) {
	    settings.lng = (LANGUAGE.values()[cbLangs.getSelectedIndex()]);
	    settings.closeLogDialog = chbCloseLogDialog.isSelected();
	    manager.saveSettings();
	    this.setVisible(false);
	}
    }
}
