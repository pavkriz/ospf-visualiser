package org.hkfree.ospf.gui.netstateswin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.hkfree.ospf.gui.costdifferencesdialog.CostDifferencesDialog;
import org.hkfree.ospf.gui.linkfaultdialog.LinkFaultDialog;
import org.hkfree.ospf.gui.ospfwin.StatusBar;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.netchange.CostDifference;
import org.hkfree.ospf.model.netchange.NetChangeModel;
import org.hkfree.ospf.model.ospffault.IntervalFault;
import org.hkfree.ospf.tools.Factory;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;

/**
 * Třída představující okno zobrazení stavu sítě v čase
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class NetStatesWin extends JFrame {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private NetStatesWinActionListener netStatesWinActListener = null;
    private NetStatesWinManager netStatesWinManager = null;
    private NSWGraphComponent graphComponent = null;
    private StatusBar statusBar = null;
    private JButton btnFirstState = null;
    private JButton btnPreviousState = null;
    private JButton btnNextState = null;
    private JButton btnLastState = null;
    private JTextArea txtLiveLinks = new JTextArea();
    private JTextArea txtDeadLinks = new JTextArea();
    private JTextArea txtLiveRouters = new JTextArea();
    private JTextArea txtDeadRouters = new JTextArea();
    private JLabel lblModelName = new JLabel("");


    /**
     * Konstruktor
     */
    public NetStatesWin() {
	graphComponent = new NSWGraphComponent();
	graphComponent.setOwner(this);
	netStatesWinManager = new NetStatesWinManager(this);
	netStatesWinManager.setGraphComponent(graphComponent);
	netStatesWinActListener = new NetStatesWinActionListener();
	netStatesWinActListener.setWinManager(netStatesWinManager);
	createComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void createComponents() {
	this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	Container c = this.getContentPane();
	c.setLayout(new BorderLayout());
	// menu
	NetStatesWinMenu mainMenu = new NetStatesWinMenu(netStatesWinActListener);
	this.setJMenuBar(mainMenu);
	// toolbar
	NetStatesWinToolBar toolBar = new NetStatesWinToolBar(netStatesWinActListener);
	c.add(toolBar, BorderLayout.NORTH);
	// statusbar
	statusBar = new StatusBar();
	getManager().setStatusText(0, rb.getString("mode." + MODE.TRANSFORMING));
	getManager().setStatusText(1, rb.getString("mode." + MODE.NONE));
	getManager().setStatusTextToolTip(1, rb.getString("mode." + MODE.NONE + ".toolTip"));
	// postranni panel
	JPanel sidePanel = new JPanel();
	sidePanel.setPreferredSize(new Dimension(230, 50));
	sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
	// tlacitka pro posun vpred/vzad
	JPanel l1 = new JPanel(new GridBagLayout());
	l1.setMaximumSize(new Dimension(220, 50));
	btnFirstState = new JButton(netStatesWinActListener.getActionFirstState());
	btnPreviousState = new JButton(netStatesWinActListener.getActionPreviousState());
	btnNextState = new JButton(netStatesWinActListener.getActionNextState());
	btnLastState = new JButton(netStatesWinActListener.getActionLastState());
	lblModelName.setFont(new Font("Arial", Font.BOLD, 15));
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
	gbc.weightx = 0.5;
	gbc.gridwidth = 2;
	l1.add(lblModelName, gbc);
	gbc.gridwidth = 1;
	gbc.gridx = 0;
	gbc.gridy = 1;
	l1.add(btnPreviousState, gbc);
	gbc.gridx = 1;
	gbc.gridy = 1;
	l1.add(btnNextState, gbc);
	gbc.gridx = 0;
	gbc.gridy = 2;
	l1.add(btnFirstState, gbc);
	gbc.gridx = 1;
	gbc.gridy = 2;
	l1.add(btnLastState, gbc);
	sidePanel.add(l1);
	sidePanel.add(Box.createVerticalStrut(10));
	JPanel l3 = new JPanel();
	l3.setLayout(new BorderLayout());
	l3.setBorder(BorderFactory.createTitledBorder(rb.getString("nsw.5")));
	l3.add(new JScrollPane(txtLiveLinks), BorderLayout.CENTER);
	txtLiveLinks.setFont(new Font("Arial", 0, 12));
	txtLiveLinks.setForeground(new Color(0, 153, 0));
	sidePanel.add(l3);
	JPanel l4 = new JPanel();
	l4.setLayout(new BorderLayout());
	l4.setBorder(BorderFactory.createTitledBorder(rb.getString("nsw.6")));
	l4.add(new JScrollPane(txtDeadLinks), BorderLayout.CENTER);
	txtDeadLinks.setFont(new Font("Arial", 0, 12));
	txtDeadLinks.setForeground(Color.RED);
	sidePanel.add(l4);
	JPanel l5 = new JPanel();
	l5.setLayout(new BorderLayout());
	l5.setBorder(BorderFactory.createTitledBorder(rb.getString("nsw.7")));
	l5.add(new JScrollPane(txtLiveRouters), BorderLayout.CENTER);
	txtLiveRouters.setFont(new Font("Arial", 0, 12));
	txtLiveRouters.setForeground(new Color(0, 153, 0));
	sidePanel.add(l5);
	JPanel l6 = new JPanel();
	l6.setLayout(new BorderLayout());
	l6.setBorder(BorderFactory.createTitledBorder(rb.getString("nsw.8")));
	l6.add(new JScrollPane(txtDeadRouters), BorderLayout.CENTER);
	txtDeadRouters.setFont(new Font("Arial", 0, 12));
	txtDeadRouters.setForeground(Color.RED);
	sidePanel.add(l6);
	c.add(sidePanel, BorderLayout.EAST);
	c.add(statusBar, BorderLayout.SOUTH);
	c.add(new GraphZoomScrollPane(graphComponent.getVisualizationComponent()), BorderLayout.CENTER);
	this.setSize(800, 600);
	this.setMinimumSize(new Dimension(800, 600));
	this.setLocationRelativeTo(null);
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "ico.gif")));
	this.setTitle(rb.getString("nsw.title"));
    }


    /**
     * Zavře okno
     */
    public void closeWin() {
	this.setVisible(false);
    }


    /**
     * Vrací actionlistener okna
     * @return ActionListener
     */
    public NetStatesWinActionListener getNSWActionListener() {
	return netStatesWinActListener;
    }


    /**
     * Nastavuje NetChangeModel
     * @param netChangeModel
     */
    public void setNetChangeModel(NetChangeModel netChangeModel) {
	netStatesWinManager.setNetChangeModel(netChangeModel);
    }


    /**
     * Zpracuje nastavené modely
     */
    public void processModelsAfterStart() {
	netStatesWinManager.loadNetStateModel();
    }


    /**
     * Nastaví informace o aktuálních výpadcích
     * @param modelName
     * @param liveLinks
     * @param deadLinks
     * @param liveRouters
     * @param deadRouters
     */
    public void setChangeInfo(String modelName, String liveLinks, String deadLinks, String liveRouters, String deadRouters) {
	netStatesWinManager.getOwner().repaint();
	lblModelName.setText(modelName);
	txtLiveLinks.setText(liveLinks);
	txtDeadLinks.setText(deadLinks);
	txtLiveRouters.setText(liveRouters);
	txtDeadRouters.setText(deadRouters);
    }


    /**
     * Zobrazí dialog se změnami cen spojů
     * @param differences
     */
    public void showCostDifferencesDialog(List<CostDifference> differences) {
	CostDifferencesDialog cdd = new CostDifferencesDialog(differences);
	cdd.setVisible(true);
    }


    /**
     * Zobrazí dialog s výpadky spoje
     * @param intervalFaults
     */
    public void showLinkFaultsDialog(List<IntervalFault> intervalFaults, String linkID) {
	LinkFaultDialog lfd = new LinkFaultDialog(intervalFaults, linkID);
	lfd.setVisible(true);
    }


    /**
     * Vrací manager okna
     * @return NetStatesWinManager
     */
    public NetStatesWinManager getManager() {
	return netStatesWinManager;
    }


    /**
     * Vrací status bar
     * @return
     */
    protected StatusBar getStatusBar() {
	return statusBar;
    }
}
