package org.hkfree.ospf.gui.ospfwin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
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
    private OspfWinActionListener actionListener = null;
    private OspfWinManager manager = null;
    private JTabbedPane tabs = new JTabbedPane();
    private JToolBar toolBar;
    private StatusBar statusBar;


    /**
     * Konstruktor
     */
    public OspfWin() {
	manager = new OspfWinManager(this);
	Factory.setRb(manager.getRb());
	rb = Factory.getRb();
	actionListener = new OspfWinActionListener(manager);
	createGUI();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void createGUI() {
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setSize(750, 600);
	this.setLocationRelativeTo(null);
	this.setTitle(rb.getString("ow.title"));
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "ico.gif")));
	Container c = this.getContentPane();
	c.setLayout(new BorderLayout());
	JPanel stred = new JPanel();
	stred.setLayout(new BorderLayout());
	// menu
	OspfWinMenu mainMenu = new OspfWinMenu(actionListener);
	this.setJMenuBar(mainMenu);
	// toolbar
	toolBar = new OspfWinToolBar(actionListener);
	c.add(toolBar, BorderLayout.NORTH);
	stred.add(tabs);
	c.add(stred, BorderLayout.CENTER);
	// statusbar
	statusBar = new StatusBar();
	c.add(statusBar, BorderLayout.SOUTH);
	// pridani klavesovych zkratek
	addKeyShorts();
	try {
	    //nastavení jednotného fontu pro celou aplikaci
	    FontUIResource f = new javax.swing.plaf.FontUIResource(new Font("Arial",Font.PLAIN, 12));
	    Enumeration<Object> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements())
	    {
	        Object key = keys.nextElement();
	        Object value = UIManager.get(key);
	        if (value instanceof FontUIResource)
	        {
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
	map.processModelsAfterStart(true, null, 0);
	this.tabs.add(modelName, map);
    }


    /**
     * Vytvoří záložku modelu a naplní příslušné komponenty daty modelu
     * @param modelName
     * @param model
     */
    public void addAndFillModelTabbedPane(String modelName, MapModel model) {
	MapPanel map = new MapPanel(model);
	map.processModelsAfterStart(true, null, 0);
	this.tabs.add(modelName, map);
    }


    /**
     * Zavře aktuálně vybranou záložku s modelem
     */
    public void closeActiveModelTabbedPane() {
	this.tabs.remove(tabs.indexOfComponent(tabs.getSelectedComponent()));
	this.tabs.repaint();
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
	for (int i = 0; i < tabs.getComponentCount(); i++) {
	    mans.add(((MapPanel) tabs.getComponentAt(i)).getMapDesignWinManager());
	}
	return mans;
    }


    /**
     * Vrací manažera aktivního tabu
     * @return
     */
    public MapManager getActualMDManager() {
	return ((MapPanel) (tabs.getComponentAt(tabs.getSelectedIndex()))).getMapDesignWinManager();
    }
}
