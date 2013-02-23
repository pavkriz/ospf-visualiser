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
import javax.swing.JLabel;

import org.hkfree.ospf.gui.ospfwin.OspfWinManager;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.EDGE_SHAPER;
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
    private JComboBox cbEdgeShaper = null;
    private JCheckBox chbCloseLogDialog = null;
    private JButton btnSave = new JButton();
    private JButton btnStorno = new JButton();


    // private ButtonGroup gLayout = new ButtonGroup();
    /**
     * Konstruktor
     * @param okno předek
     * @param ospfWinManager manažer ospfWin
     * @param appSettings současné nastavení aplikace
     * @param rb popisky v nastaveném jazyce
     */
    public SettingsDialog(Frame frame, OspfWinManager ospfWinManager, AppSettings appSettings) {
	super(frame);
	this.settings = appSettings;
	this.manager = ospfWinManager;
	createGUI();
	this.setTitle(rb.getString("sd.title"));
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "nastaveni.png")));
	this.pack();
	this.setModal(true);
	this.setLocationRelativeTo(null);
	this.setResizable(false);
    }


    /**
     * Vytvoření GUI
     */
    private void createGUI() {
	// naplneni obrazky s jazyky
	ImageIcon[] images = new ImageIcon[LANGUAGE.values().length];
	for (int i = 0; i < LANGUAGE.values().length; i++) {
	    images[i] = createImageIcon(Constants.URL_IMG_GUI + "lng_" + LANGUAGE.values()[i] + ".png");
	}
	// jazyk
	cbLangs = new JComboBox(images);
	cbLangs.setSelectedIndex(settings.language.ordinal());
	cbLangs.addActionListener(this);
	// edge shaper
	String[] shapes = new String[EDGE_SHAPER.values().length];
	for (int i = 0; i < EDGE_SHAPER.values().length; i++) {
	    shapes[i] = EDGE_SHAPER.values()[i].toString();
	}
	cbEdgeShaper = new JComboBox(shapes);
	cbEdgeShaper.setSelectedIndex(settings.edgeShaper.ordinal());
	cbEdgeShaper.addActionListener(this);
	// zavreni logovaciho dialogu po odkonceni operace
	chbCloseLogDialog = new JCheckBox(rb.getString("sd.closeLogDialog"));
	chbCloseLogDialog.setSelected(settings.closeLogDialog);
	// labely
	JLabel l1 = new JLabel(rb.getString("sd.1"));
	JLabel l2 = new JLabel(rb.getString("sd.2"));
	// JPanel pLayout = new JPanel();
	// JRadioButton rbSpring = new JRadioButton();
	// JRadioButton rbFr = new JRadioButton();
	// rbSpring.setActionCommand(LAYOUT.SPRING.toString());
	// rbFr.setActionCommand(LAYOUT.FR.toString());
	// rbFr.setText(rb.getString("sd.1"));
	// rbSpring.setText(rb.getString("sd.2"));
	// rbSpring.setSelected(settings.layout == LAYOUT.SPRING);
	// rbFr.setSelected(settings.layout == LAYOUT.FR);
	// gLayout.add(rbSpring);
	// gLayout.add(rbFr);
	// pLayout.setBorder(BorderFactory.createTitledBorder(rb.getString("sd.layout")));
	// pLayout.setLayout(new FlowLayout(FlowLayout.CENTER));
	// pLayout.add(rbFr);
	// pLayout.add(rbSpring);
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
	                .addGroup(layout.createSequentialGroup()
	                        .addComponent(l1)
	                        .addComponent(cbLangs))
	                .addGroup(layout.createSequentialGroup()
	                        .addComponent(l2)
	                        .addComponent(cbEdgeShaper))
	                .addComponent(chbCloseLogDialog)
	                .addGroup(layout.createSequentialGroup()
	                        .addComponent(btnSave, 100, 100, 100)
	                        .addComponent(btnStorno, 100, 100, 100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
	        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(l1)
	                .addComponent(cbLangs))
	        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(l2)
	                .addComponent(cbEdgeShaper))
	        .addComponent(chbCloseLogDialog)
	        .addGap(20)
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
	    settings.edgeShaper = (EDGE_SHAPER.values()[cbEdgeShaper.getSelectedIndex()]);
	    settings.closeLogDialog = chbCloseLogDialog.isSelected();
	    manager.saveSettings();
	    manager.actualizeBySettings();
	    this.setVisible(false);
	}
    }
}
