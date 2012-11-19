package org.hkfree.ospf.gui.ospfwin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.hkfree.ospf.gui.mappanel.MapManager;
import org.hkfree.ospf.gui.mappanel.MapPanel;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.map.MapModel;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující hlavní okno aplikace
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWin extends JFrame {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = null; // zde je null, v teto tride se teprve nastavuje pres konstruktor manageru
    private OspfWinManager manager = null;
    private OspfWinActionListener actionListener = null;
    private OspfWinMenu mainMenu;
    private JToolBar toolBar;
    private StatusBar statusBar;
    private StateDialog stateDialog;
    private JComboBox<String> cbModels = null;
    private Map<String, MapPanel> models = null;
    private JSplitPane spPanel = null;
    private Dimension propSize = null;


    /**
     * Konstruktor
     */
    public OspfWin() {
	manager = new OspfWinManager(this);
	Factory.setRb(manager.getRb());
	rb = Factory.getRb();
	actionListener = new OspfWinActionListener(manager);
	models = new HashMap<String, MapPanel>();
	propSize = new Dimension(190, 10);
	createGUI();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void createGUI() {
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setSize(900, 600);
	this.setLocationRelativeTo(null);
	this.setTitle(rb.getString("ow.title"));
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "ico.gif")));
	Container c = this.getContentPane();
	c.setLayout(new BorderLayout());
	JPanel stred = new JPanel();
	stred.setLayout(new BorderLayout());
	// menu
	mainMenu = new OspfWinMenu(actionListener);
	this.setJMenuBar(mainMenu);
	// toolbar
	toolBar = new OspfWinToolBar(actionListener);
	c.add(toolBar, BorderLayout.NORTH);
	cbModels = new JComboBox<String>();
	cbModels.setVisible(false);
	cbModels.setFont(new Font("Arial", Font.PLAIN, 10));
	cbModels.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		actualizeContent();
	    }
	});
	JPanel pSide = new JPanel(new BorderLayout());
	pSide.add(cbModels, BorderLayout.NORTH);
	spPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pSide, null);
	spPanel.setPreferredSize(propSize);
	spPanel.setOneTouchExpandable(true);
	c.add(spPanel, BorderLayout.CENTER);
	// statusbar
	statusBar = new StatusBar(this);
	c.add(statusBar, BorderLayout.SOUTH);
	// info o provadene akci
	stateDialog = new StateDialog(this);
	// pridani klavesovych zkratek
	addKeyShorts();
	try {
	    // nastavení jednotného fontu pro celou aplikaci
	    FontUIResource f = new javax.swing.plaf.FontUIResource(new Font("Arial", Font.PLAIN, 12));
	    Enumeration<Object> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
		Object key = keys.nextElement();
		Object value = UIManager.get(key);
		if (value instanceof FontUIResource) {
		    UIManager.put(key, f);
		}
	    }
	    // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    System.out.print(e.getMessage());
	}
	SwingUtilities.updateComponentTreeUI(OspfWin.this);
    }


    /**
     * Aktualzace komponent hlavniho okna
     */
    private void actualizeContent() {
	manager.checkActionsEnable();
	MapPanel mp = ((MapPanel) models.get(cbModels.getSelectedItem()));
	spPanel.setRightComponent(null);
	if (((JPanel) spPanel.getLeftComponent()).getComponentCount() > 1) {
	    ((JPanel) spPanel.getLeftComponent()).remove(1);
	}
	if (mp != null) {
	    spPanel.setRightComponent(mp.getGraphPanel());
	    ((JPanel) spPanel.getLeftComponent()).add(mp.getPropertiesPanel(), 1);
	    ((JPanel) spPanel.getLeftComponent()).setPreferredSize(propSize);
	} else {
	    cbModels.setVisible(false);
	}
    }


    /**
     * Přidá klávesové zkratky do aplikace
     */
    private void addKeyShorts() {
	// focus pro vyhledavani routeru
	this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F"), "find");
	this.getRootPane().getActionMap().put("find", new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		((OspfWinToolBar) toolBar).getTfFind().requestFocus();
		((OspfWinToolBar) toolBar).getTfFind().selectAll();
	    }
	});
    }


    /**
     * Vytvoří záložku modelu a naplní příslušné komponenty daty modelu
     * @param modelName
     * @param model
     */
    public void addAndFillModelTabbedPane(String modelName, OspfModel model) {
	MapPanel map = new MapPanel(model);
	addMapPanel(modelName, map);
    }


    /**
     * Vytvoří záložku modelu a naplní příslušné komponenty daty modelu
     * @param modelName
     * @param model
     */
    public void addAndFillModelTabbedPane(String modelName, MapModel model) {
	MapPanel map = new MapPanel(model);
	addMapPanel(modelName, map);
    }


    /**
     * Přidá panel s grafem do roletky dle jeho názvu.
     * Pokud se již název v roletce vyskytuje, upraví jej
     * @param name název modelu
     * @param mapPanel panel s grafem
     */
    private void addMapPanel(String name, MapPanel mapPanel) {
	mapPanel.processModelsAfterStart(true, null, 0);
	// pokud je jiz vlozen model se stejnym nazvem, upravi se nazev
	int i = 1;
	while (true) {
	    if (!models.containsKey(name)) {
		break;
	    }
	    if (name.endsWith("_" + i)) {
		name = name.substring(0, name.lastIndexOf("_" + i));
	    }
	    i++;
	    name = name + "_" + i;
	}
	// prida se model
	models.put(name, mapPanel);
	cbModels.insertItemAt(name, cbModels.getItemCount());
	if (cbModels.getItemCount() == 1) {
	    cbModels.setSelectedIndex(0);
	    cbModels.setVisible(true);
	}
    }


    /**
     * Zavře aktuálně vybraný model
     */
    public void closeActiveModelTabbedPane() {
	String name = cbModels.getSelectedItem().toString();
	cbModels.removeItemAt(cbModels.getSelectedIndex());
	if (cbModels.getSelectedObjects().length > 0) {
	    cbModels.setSelectedIndex(0);
	}
	models.remove(name);
	actualizeContent();
    }


    /**
     * Zobrazí informační dialog
     * @param messageTitle
     * @param textMessage
     */
    public void showInfoMessage(String messageTitle, String textMessage) {
	JOptionPane.showMessageDialog(this, "\n" + textMessage + "\n\n", messageTitle, JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Zobrazí varovný dialog
     * @param messageTitle
     * @param textMessage
     */
    public void showAlertMessage(String messageTitle, String textMessage) {
	JOptionPane.showMessageDialog(this, "\n" + textMessage + "\n\n", messageTitle, JOptionPane.WARNING_MESSAGE);
    }


    /**
     * Zobrazí dialog s chybou
     * @param messageTitle
     * @param textMessage
     */
    public void showErrorMessage(String messageTitle, String textMessage) {
	JOptionPane.showMessageDialog(this, "\n" + textMessage + "\n\n", messageTitle, JOptionPane.ERROR_MESSAGE);
    }


    /**
     * Vrací actionlistener okna
     * @return actionlistener
     */
    public OspfWinActionListener getOspfWinActListener() {
	return actionListener;
    }


    /**
     * Vrací statusbar
     * @return
     */
    public StatusBar getStatusBar() {
	return statusBar;
    }


    /**
     * Vrací dialog s info o probíhající akci
     * @return
     */
    public StateDialog getStateDialog() {
	return stateDialog;
    }


    /**
     * Vrací řetězec k vyhledání
     * @return
     */
    protected String getSearhString() {
	return ((OspfWinToolBar) toolBar).getTfFind().getText();
    }


    /**
     * Vrací kolekci manažerů všech tabů
     * @return
     */
    public Set<MapManager> getAllMDManager() {
	Set<MapManager> mans = new HashSet<MapManager>();
	for (MapPanel mp : models.values()) {
	    mans.add(mp.getMapDesignWinManager());
	}
	return mans;
    }


    /**
     * Vrací manažera aktivní mapy, pokud není žádná mapa, vrací null
     * @return
     */
    public MapManager getActualMDManager() {
	if (cbModels.getSelectedItem() == null) {
	    return null;
	}
	return ((MapPanel) models.get(cbModels.getSelectedItem().toString())).getMapDesignWinManager();
    }


    /**
     * aktualizuje stav toggle tlacitka v toolbaru pro zobrazeniIPv6
     * a check tlacitka v menu
     */
    public void actualizeIPv6mode() {
	boolean b = manager.getActualMDManager().getGraphComponent().isShowIPv6();
	((OspfWinToolBar) toolBar).getIPv6ToggleButton().setSelected(b);
	((OspfWinMenu) mainMenu).getIPv6CheckBoxItem().setSelected(b);
    }
}
