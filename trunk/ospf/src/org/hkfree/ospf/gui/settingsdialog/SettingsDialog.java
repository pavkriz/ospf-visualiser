package org.hkfree.ospf.gui.settingsdialog;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.hkfree.ospf.gui.ospfwin.OspfWinManager;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.LANGUAGE;
import org.hkfree.ospf.model.Constants.LAYOUT;
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
    private JComboBox<ImageIcon> cbLangs = null;
    private JCheckBox chbCloseLogDialog = null;
    private JButton btnSave = new JButton();
    private JButton btnStorno = new JButton();
    private ButtonGroup gLayout = new ButtonGroup();
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
	// naplneni obrazky s jazyky
	images = new ImageIcon[LANGUAGE.values().length];
	for (int i = 0; i < LANGUAGE.values().length; i++) {
	    images[i] = createImageIcon(Constants.URL_IMG_GUI + "lng_" + LANGUAGE.values()[i] + ".png");
	}
	cbLangs = new JComboBox<ImageIcon>(images);
	cbLangs.setSelectedIndex(settings.language.ordinal());
	cbLangs.addActionListener(this);
	chbCloseLogDialog = new JCheckBox(rb.getString("sd.closeLogDialog"));
	chbCloseLogDialog.setSelected(settings.closeLogDialog);
	JPanel pLayout = new JPanel();
	JRadioButton rbSpring = new JRadioButton();
	JRadioButton rbFr = new JRadioButton();
	rbSpring.setActionCommand(LAYOUT.SPRING.toString());
	rbFr.setActionCommand(LAYOUT.FR.toString());
	rbFr.setText(rb.getString("sd.1"));
	rbSpring.setText(rb.getString("sd.2"));
	rbSpring.setSelected(settings.layout == LAYOUT.SPRING);
	rbFr.setSelected(settings.layout == LAYOUT.FR);
	gLayout.add(rbSpring);
	gLayout.add(rbFr);
	pLayout.setBorder(BorderFactory.createTitledBorder(rb.getString("sd.layout")));
	pLayout.setLayout(new FlowLayout(FlowLayout.CENTER));
	pLayout.add(rbFr);
	pLayout.add(rbSpring);
	btnSave.setText(rb.getString("save"));
	btnSave.addActionListener(this);
	btnStorno.setText(rb.getString("storno"));
	btnStorno.addActionListener(this);
	Container c = this.getContentPane();
	GroupLayout layout = new GroupLayout(c);
	c.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(cbLangs)
			.addComponent(chbCloseLogDialog)
			.addComponent(pLayout)
			.addGroup(layout.createSequentialGroup()
				.addComponent(btnSave, 100, 100, 100)
				.addComponent(btnStorno, 100, 100, 100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(cbLangs)
		.addGap(20)
		.addComponent(chbCloseLogDialog)
		.addGap(20)
		.addComponent(pLayout)
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
	    settings.language = (LANGUAGE.values()[cbLangs.getSelectedIndex()]);
	    settings.closeLogDialog = chbCloseLogDialog.isSelected();
	    settings.layout = LAYOUT.valueOf(gLayout.getSelection().getActionCommand());
	    manager.saveSettings();
	    this.setVisible(false);
	}
    }
}
