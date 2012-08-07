package org.hkfree.ospf.gui.netstateswin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;

import org.hkfree.ospf.gui.costdifferencesdialog.CostDifferencesDialog;
import org.hkfree.ospf.gui.linkfaultdialog.LinkFaultDialog;
import org.hkfree.ospf.model.Constants;
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
    private JLabel statusInfo1 = new JLabel();
    private JLabel statusInfo2 = new JLabel();
    private JPanel status2 = null;
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
	// status panel - dole
	JPanel statusPanel = new JPanel();
	statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.LINE_AXIS));
	statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	statusPanel.setAlignmentY(CENTER_ALIGNMENT);
	statusPanel.setPreferredSize(new Dimension(this.getWidth(), 30));
	JPanel statusH = new JPanel();
	JLabel statusHeading = new JLabel(rb.getString("nsw.0") + ":");
	statusHeading.setFont(new Font("Arial", 2, 11));
	statusH.add(statusHeading);
	JPanel status1 = new JPanel();
	status1.add(statusInfo1);
	statusInfo1.setText(rb.getString("nsw.1"));
	status2 = new JPanel();
	status2.add(statusInfo2);
	statusInfo2.setText(rb.getString("nsw.2"));
	status2.add(new JLabel(new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "help.png"))));
	status2.addMouseListener(new MouseAdapter() { // přeimplementování metod, aby se u
	    					      // tohoto panelu zobrazoval tooltip nepřetržitě
	    int initialD = -1, dismissD = -1;


	    public void mouseEntered(MouseEvent arg0) {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		initialD = ttm.getInitialDelay();
		dismissD = ttm.getDismissDelay();
		ttm.setInitialDelay(1);
		ttm.setDismissDelay(Integer.MAX_VALUE);
	    }


	    public void mouseExited(MouseEvent arg0) {
		if (initialD < 0 || dismissD < 0)
		    return;
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(initialD);
		ttm.setDismissDelay(dismissD);
	    }
	});
	statusPanel.add(statusH);
	statusPanel.add(status1);
	statusPanel.add(status2);
	
	//postranni panel
	JPanel sidePanel = new JPanel();
	sidePanel.setPreferredSize(new Dimension(230,50));
	sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
	
	//tlacitka pro posun vpred/vzad
	JPanel l1 = new JPanel(new GridLayout(2,2));
	l1.setBorder(BorderFactory.createTitledBorder(rb.getString("nsw.3")));
	btnFirstState = new JButton(netStatesWinActListener.getActionFirstState());
	btnPreviousState = new JButton(netStatesWinActListener.getActionPreviousState());
	btnNextState = new JButton(netStatesWinActListener.getActionNextState());
	btnLastState = new JButton(netStatesWinActListener.getActionLastState());
	l1.add(btnPreviousState);
	l1.add(btnNextState);
	l1.add(btnFirstState);
	l1.add(btnLastState);
	sidePanel.add(l1);
	sidePanel.add(Box.createVerticalStrut(10));
	
	//informace
	JPanel l2 = new JPanel();
	l2.setBorder(BorderFactory.createTitledBorder(rb.getString("nsw.4")));
	lblModelName = new JLabel("");
	lblModelName.setFont(new Font("Arial", Font.BOLD, 14));
	l2.add(lblModelName);
	sidePanel.add(l2);
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
	c.add(statusPanel, BorderLayout.SOUTH);
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
     * Nastaví informace o pracovním módu1
     * @param infoText
     */
    public void setStatusInfo1(String infoText) {
	statusInfo1.setText(infoText);
    }


    /**
     * Nastaví informace o pracovním módu2
     * @param infoText
     */
    public void setStatusInfo2(String infoText) {
	statusInfo2.setText(infoText);
    }


    /**
     * Nastaví text status infa 2
     * @param text
     */
    public void setStatusInfo2ToolTip(String text) {
	statusInfo2.setToolTipText(text);
    }


    /**
     * Vrací manager okna
     * @return NetStatesWinManager
     */
    public NetStatesWinManager getManager() {
	return netStatesWinManager;
    }
}
