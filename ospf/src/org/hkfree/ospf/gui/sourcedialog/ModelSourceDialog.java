package org.hkfree.ospf.gui.sourcedialog;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.hkfree.ospf.gui.ospfwin.OspfWin;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.tools.DateUtil;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog výběru vstupních dat pro načtení
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class ModelSourceDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JFrame owner = null;
    private JTree remoteSourceTree = null;
    private JTree localSourceTree = null;
    private JTextField tfRemoteAddressField = new JTextField();
    private JTextField tfLocalAddressField = new JTextField();
    private JTextField tfSingleLocalAddressField = new JTextField();
    private JTextField tfRowZIPFileIdentification = new JTextField();
    private JTextField tfZIPRemoteAddressFieldPath = new JTextField();
    private JTextField tfTime = new JTextField();
    private DefaultListModel<String> model = null;
    private JFileChooser localFolderChooser = new JFileChooser();
    private JTabbedPane sourceTypeTabs = null;
    private DefaultMutableTreeNode remoteRootNode = null;
    private DefaultMutableTreeNode localRootNode = null;
    private ModelSourceDialogActionListener actListener = null;
    private JPanel remoteSourcesPanel = null;
    private JScrollPane remoteScrollPane = null;
    private JPanel remoteScrollPanel = null;
    private JPanel localSourcesPanel = null;
    private JScrollPane localScrollPane = null;
    private JPanel localScrollPanel = null;
    private JPanel localSingleSourcePanel = null;
    private JPanel telnetSourcePanel = null;
    private JPanel remoteDateToDatePanel = null;
    private JTextField tfTelnetUrl = new JTextField();
    private JTextField tfTelnetPortIPv4 = new JTextField();
    private JTextField tfTelnetPortIPv6 = new JTextField();
    private JTextField tfTelnetPassword = new JTextField();
    private JTextField tfTelnetRDNSServer = new JTextField();
    private JTextField tfTelnetTimeout = new JTextField();
    private ButtonGroup groupWhereAdd = new ButtonGroup();
    private boolean loadDialogConfirmed = false;
    private int countDays = 0;
    private AppSettings settings = null;


    /**
     * Konstruktor
     */
    public ModelSourceDialog(JFrame owner, AppSettings settings) {
	this.owner = owner;
	setDataLoadSettings(settings);
	actListener = new ModelSourceDialogActionListener(this);
	createComponents();
	sourceTypeTabs.setSelectedIndex(settings.loadDataTypIndex);
    }


    /**
     * Aplikuje nastavení
     */
    private void setDataLoadSettings(AppSettings settings) {
	this.settings = settings;
	this.tfRemoteAddressField.setText(settings.modelZipRemotePath);
	this.tfLocalAddressField.setText(settings.modelZipLocalPath);
	this.tfSingleLocalAddressField.setText(settings.modelSingleLocalPath);
	this.tfRowZIPFileIdentification.setText(settings.remoteZipFileIdentification);
	this.tfTelnetUrl.setText(settings.telnetUrl);
	this.tfTelnetPortIPv4.setText(String.valueOf(settings.telnetPortIPv4));
	this.tfTelnetPortIPv6.setText(String.valueOf(settings.telnetPortIPv6));
	this.tfTelnetPassword.setText(settings.telnetPassword);
	this.tfTelnetRDNSServer.setText(settings.rdnsServer);
	this.tfTelnetTimeout.setText(String.valueOf(settings.telnetTimeout));
	this.tfZIPRemoteAddressFieldPath.setText(settings.modelZipRemotePathBetween);
	this.tfTime.setText(settings.modelTimeBetween);
	this.countDays = settings.countDaysBack;
    }


    /**
     * Nastaví potvrzení dialogu
     * @param value
     */
    public void setLoadDialogConfirmed(boolean value) {
	this.loadDialogConfirmed = value;
    }


    /**
     * Vrací příznak zda byl dialog potvrzen
     */
    public boolean loadDialogConfirmed() {
	return this.loadDialogConfirmed;
    }


    /**
     * Vytvoří komponenty GUI
     */
    private void createComponents() {
	remoteSourceTree = new JTree(remoteRootNode);
	localSourceTree = new JTree(localRootNode);
	sourceTypeTabs = new JTabbedPane();
	sourceTypeTabs.setTabPlacement(SwingConstants.LEFT);
	// inicializace panelu s nastavenim pro nacteni dat
	initRemoteDateToDatePanel();
	initRemoteSourcesPanel();
	initLocalSourcesPanel();
	initLocalSingleSourcePanel();
	initTelnetSourcePanel();
	sourceTypeTabs.add(rb.getString("ssd.17"), remoteDateToDatePanel);
	sourceTypeTabs.add(rb.getString("ssd.4"), remoteSourcesPanel);
	sourceTypeTabs.add(rb.getString("ssd.6"), localSourcesPanel);
	sourceTypeTabs.add(rb.getString("ssd.9"), localSingleSourcePanel);
	sourceTypeTabs.add(rb.getString("ssd.13"), telnetSourcePanel);
	JButton btnOk = new JButton(actListener.getActionOk());
	JButton btnStorno = new JButton(actListener.getActionStorno());
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(sourceTypeTabs)
			.addGroup(layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
					GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnOk, 100, 100, 100)
				.addComponent(btnStorno, 100, 100, 100))));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(sourceTypeTabs)
		.addGroup(layout.createParallelGroup()
			.addComponent(btnOk)
			.addComponent(btnStorno)));
	this.setTitle(rb.getString("ssd.title"));
	this.pack();
	this.setResizable(false);
	this.setLocationRelativeTo(null);
    }


    /**
     * Inicializace panelu pro vyber dat dle casoveho rozmezi
     */
    private void initRemoteDateToDatePanel() {
	remoteDateToDatePanel = new JPanel();
	JLabel lbl2 = new JLabel(rb.getString("ssd.2") + ":");
	JLabel lPath = new JLabel(rb.getString("ssd.21") + ":");
	JLabel lDFrom = new JLabel(rb.getString("ssd.18") + ":");
	JLabel lDTo = new JLabel(rb.getString("ssd.19") + ":");
	JLabel lTime = new JLabel(rb.getString("ssd.20") + ":");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Calendar cal = Calendar.getInstance();
	cal.setTime(new Date());
	cal.add(Calendar.DATE, -countDays);
	final JTextField tfDateFrom = new JTextField(sdf.format(cal.getTime()));
	final JTextField tfDateTo = new JTextField(sdf.format(new Date()));
	model = new DefaultListModel<String>();
	final JList<String> list = new JList<String>(model);
	JScrollPane scroll = new JScrollPane(list);
	JButton btnAdd = new JButton();
	btnAdd.setAction(new AbstractAction(rb.getString("ssd.26")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		try {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd--hh-mm");
		    Calendar calFrom = Calendar.getInstance();
		    Calendar calTo = Calendar.getInstance();
		    calFrom.setTime(sdf.parse(tfDateFrom.getText() + "--" + tfTime.getText()));
		    calTo.setTime(sdf.parse(tfDateTo.getText() + "--" + tfTime.getText()));
		    countDays = -1;
		    while (calFrom.before(calTo) || DateUtil.isItSameDay(calFrom.getTime(), calTo.getTime())) {
			String s = String.format("%1$4d-%2$2d-%3$2d--%4$2d-%5$2d", calFrom.get(Calendar.YEAR),
				calFrom.get(Calendar.MONTH) + 1, calFrom.get(Calendar.DATE),
				calFrom.get(Calendar.HOUR_OF_DAY), calFrom.get(Calendar.MINUTE));
			model.add(model.size(), s.replace(' ', '0'));
			calFrom.add(Calendar.DATE, 1);
			countDays++;
		    }
		    if (!model.isEmpty()) {
			list.setSelectedIndex(0);
		    }
		} catch (ParseException ex) {
		    ((OspfWin) owner).showErrorMessage(rb.getString("error"), ex.getLocalizedMessage());
		}
	    }
	});
	JButton btnRemove = new JButton();
	btnRemove.setAction(new AbstractAction(rb.getString("ssd.27")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		if (list.getSelectedIndex() >= 0) {
		    model.remove(list.getSelectedIndex());
		    if (!model.isEmpty()) {
			list.setSelectedIndex(0);
		    }
		}
	    }
	});
	JButton btnRemoveAll = new JButton();
	btnRemoveAll.setAction(new AbstractAction(rb.getString("ssd.28")) {

	    private static final long serialVersionUID = 1L;


	    public void actionPerformed(ActionEvent e) {
		model.removeAllElements();
	    }
	});
	JRadioButton rbMapPanel = new JRadioButton(rb.getString("ssd.24"));
	rbMapPanel.setActionCommand(AppSettings.MAP_PANEL);
	rbMapPanel.setSelected(settings.fromDateToDateLoadTo.equals(AppSettings.MAP_PANEL));
	JRadioButton rbNetStateWindow = new JRadioButton(rb.getString("ssd.25"));
	rbNetStateWindow.setActionCommand(AppSettings.NET_STATE_WINDOW);
	rbNetStateWindow.setSelected(settings.fromDateToDateLoadTo.equals(AppSettings.NET_STATE_WINDOW));
	GroupLayout l = new GroupLayout(remoteDateToDatePanel);
	remoteDateToDatePanel.setLayout(l);
	groupWhereAdd.add(rbMapPanel);
	groupWhereAdd.add(rbNetStateWindow);
	l.setAutoCreateContainerGaps(true);
	l.setAutoCreateGaps(true);
	l.setHorizontalGroup(l.createSequentialGroup()
		.addGroup(l.createParallelGroup()
			.addComponent(lPath)
			.addComponent(tfZIPRemoteAddressFieldPath, 320, 320, 320)
			.addComponent(lDFrom)
			.addComponent(tfDateFrom, 100, 100, 100)
			.addComponent(lDTo)
			.addComponent(tfDateTo, 100, 100, 100)
			.addComponent(lTime)
			.addComponent(tfTime, 100, 100, 100)
			.addComponent(rbMapPanel)
			.addComponent(rbNetStateWindow)
			.addGroup(l.createSequentialGroup()
				.addComponent(btnAdd, 100, 100, 100)
				.addComponent(btnRemove, 100, 100, 100))
			.addComponent(btnRemoveAll, 100, 100, 100))
		.addGroup(l.createParallelGroup()
			.addComponent(lbl2)
			.addComponent(scroll)));
	l.setVerticalGroup(l.createParallelGroup()
		.addGroup(l.createSequentialGroup()
			.addComponent(lbl2)
			.addComponent(scroll))
		.addGroup(l.createSequentialGroup()
			.addComponent(lPath)
			.addComponent(tfZIPRemoteAddressFieldPath, 25, 25, 25)
			.addComponent(lDFrom)
			.addComponent(tfDateFrom, 25, 25, 25)
			.addComponent(lDTo)
			.addComponent(tfDateTo, 25, 25, 25)
			.addComponent(lTime)
			.addComponent(tfTime, 25, 25, 25)
			.addComponent(rbMapPanel)
			.addComponent(rbNetStateWindow)
			.addGap(30)
			.addGroup(l.createParallelGroup()
				.addComponent(btnAdd)
				.addComponent(btnRemove))
			.addComponent(btnRemoveAll)));
    }


    /**
     * Inicializace panelu pro vyber dat telnetem
     */
    private void initTelnetSourcePanel() {
	telnetSourcePanel = new JPanel();
	JLabel lTelnetUrl = new JLabel(rb.getString("ssd.10") + ":");
	JLabel lTelnetPortIPv4 = new JLabel(rb.getString("ssd.11") + " IPv4:");
	JLabel lTelnetPortIPv6 = new JLabel(rb.getString("ssd.11") + " IPv6:");
	JLabel lTelnetPassword = new JLabel(rb.getString("ssd.12") + ":");
	JLabel lTelnetTimeout = new JLabel(rb.getString("ssd.16") + ":");
	JLabel lTelnetRDNS = new JLabel(rb.getString("ssd.15") + ":");
	tfTelnetUrl.setMaximumSize(new Dimension(350, 25));
	tfTelnetPortIPv4.setMaximumSize(new Dimension(100, 25));
	tfTelnetPortIPv6.setMaximumSize(new Dimension(100, 25));
	tfTelnetPassword.setMaximumSize(new Dimension(350, 25));
	tfTelnetTimeout.setMaximumSize(new Dimension(100, 25));
	tfTelnetRDNSServer.setMaximumSize(new Dimension(350, 25));
	GroupLayout l3 = new GroupLayout(telnetSourcePanel);
	telnetSourcePanel.setLayout(l3);
	l3.setAutoCreateContainerGaps(true);
	l3.setAutoCreateGaps(true);
	l3.setHorizontalGroup(l3.createSequentialGroup()
		.addGroup(l3.createParallelGroup(Alignment.TRAILING)
			.addComponent(lTelnetUrl)
			.addComponent(lTelnetPortIPv4)
			.addComponent(lTelnetPortIPv6)
			.addComponent(lTelnetPassword)
			.addComponent(lTelnetTimeout)
			.addComponent(lTelnetRDNS))
		.addGroup(l3.createParallelGroup()
			.addComponent(tfTelnetUrl)
			.addComponent(tfTelnetPortIPv4)
			.addComponent(tfTelnetPortIPv6)
			.addComponent(tfTelnetPassword)
			.addComponent(tfTelnetTimeout)
			.addComponent(tfTelnetRDNSServer)));
	l3.setVerticalGroup(l3.createSequentialGroup()
		.addGroup(l3.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lTelnetUrl)
			.addComponent(tfTelnetUrl))
		.addGroup(l3.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lTelnetPortIPv4)
			.addComponent(tfTelnetPortIPv4))
		.addGroup(l3.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lTelnetPortIPv6)
			.addComponent(tfTelnetPortIPv6))
		.addGroup(l3.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lTelnetPassword)
			.addComponent(tfTelnetPassword))
		.addGroup(l3.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lTelnetRDNS)
			.addComponent(tfTelnetRDNSServer))
		.addGroup(l3.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lTelnetTimeout)
			.addComponent(tfTelnetTimeout)));
    }


    /**
     * Inicializace panelu pro vyber dat z lokalni slozky
     */
    private void initLocalSingleSourcePanel() {
	localSingleSourcePanel = new JPanel();
	JLabel lbl7 = new JLabel(rb.getString("ssd.8") + ":");
	JButton selectSnglLclFolderBtn = new JButton(actListener.getActionChooseLocalFolderSingleModel());
	tfSingleLocalAddressField.setMaximumSize(new Dimension(500, 25));
	GroupLayout l2 = new GroupLayout(localSingleSourcePanel);
	localSingleSourcePanel.setLayout(l2);
	l2.setAutoCreateContainerGaps(true);
	l2.setAutoCreateGaps(true);
	l2.setHorizontalGroup(l2.createSequentialGroup()
		.addGroup(l2.createParallelGroup()
			.addComponent(lbl7)
			.addComponent(tfSingleLocalAddressField)
			.addComponent(selectSnglLclFolderBtn, Alignment.TRAILING)));
	l2.setVerticalGroup(l2.createSequentialGroup()
		.addComponent(lbl7)
		.addComponent(tfSingleLocalAddressField)
		.addComponent(selectSnglLclFolderBtn));
    }


    /**
     * Inicializace panelu pro vyber dat z lokalnich zdroju
     */
    private void initLocalSourcesPanel() {
	localSourcesPanel = new JPanel();
	localFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	tfLocalAddressField.setMaximumSize(new Dimension(320, 25));
	JLabel lbl5 = new JLabel(rb.getString("ssd.1") + ":");
	JButton selectLclFolderBtn = new JButton(actListener.getActionChooseLocalFolder());
	JButton loadLclSrcTreeBtn = new JButton(actListener.getActionLoadSourcesLocal());
	localScrollPane = new JScrollPane(localSourceTree);
	localScrollPanel = new JPanel();
	localScrollPanel.setLayout(new BorderLayout());
	localScrollPanel.add(localScrollPane, BorderLayout.CENTER);
	GroupLayout l1 = new GroupLayout(localSourcesPanel);
	localSourcesPanel.setLayout(l1);
	l1.setAutoCreateContainerGaps(true);
	l1.setAutoCreateGaps(true);
	l1.setHorizontalGroup(l1.createSequentialGroup()
		.addGroup(l1.createParallelGroup()
			.addComponent(lbl5)
			.addComponent(tfLocalAddressField)
			.addComponent(selectLclFolderBtn, Alignment.TRAILING)
			.addComponent(loadLclSrcTreeBtn))
		.addComponent(localScrollPanel));
	l1.setVerticalGroup(l1.createSequentialGroup()
		.addGroup(l1.createParallelGroup()
			.addGroup(l1.createSequentialGroup()
				.addComponent(lbl5)
				.addComponent(tfLocalAddressField)
				.addComponent(selectLclFolderBtn)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
					Short.MAX_VALUE)
				.addComponent(loadLclSrcTreeBtn))
			.addComponent(localScrollPanel)));
    }


    /**
     * Inicializace panelu pro vyber dat ze vzdalenych zdroju
     */
    private void initRemoteSourcesPanel() {
	remoteSourcesPanel = new JPanel();
	JLabel lbl1 = new JLabel(rb.getString("ssd.1") + ":");
	JButton loadRmtSrcTreeBtn = new JButton(actListener.getActionLoadSourcesRemote());
	JLabel lbl3 = new JLabel(rb.getString("ssd.3") + ":");
	tfRemoteAddressField.setMaximumSize(new Dimension(350, 25));
	tfRowZIPFileIdentification.setMaximumSize(new Dimension(320, 25));
	remoteScrollPane = new JScrollPane(remoteSourceTree);
	remoteScrollPanel = new JPanel();
	remoteScrollPanel.setLayout(new BorderLayout());
	remoteScrollPanel.add(remoteScrollPane);
	GroupLayout l = new GroupLayout(remoteSourcesPanel);
	remoteSourcesPanel.setLayout(l);
	l.setAutoCreateContainerGaps(true);
	l.setAutoCreateGaps(true);
	l.setHorizontalGroup(l.createSequentialGroup()
		.addGroup(l.createParallelGroup()
			.addComponent(lbl1)
			.addComponent(tfRemoteAddressField)
			.addComponent(lbl3)
			.addComponent(tfRowZIPFileIdentification)
			.addComponent(loadRmtSrcTreeBtn))
		.addComponent(remoteScrollPanel));
	l.setVerticalGroup(l.createSequentialGroup()
		.addGroup(l.createParallelGroup()
			.addGroup(l.createSequentialGroup()
				.addComponent(lbl1)
				.addComponent(tfRemoteAddressField)
				.addComponent(lbl3)
				.addComponent(tfRowZIPFileIdentification)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
					Short.MAX_VALUE)
				.addComponent(loadRmtSrcTreeBtn))
			.addComponent(remoteScrollPanel)));
    }


    /**
     * Načte rozbalovací strom dostupných dat na serveru
     */
    public void loadRemoteServerSourceTree() {
	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	RemoteSourceTreeLoader loader = new RemoteSourceTreeLoader();
	loader.setAddress(tfRemoteAddressField.getText());
	loader.setFileIdentification(tfRowZIPFileIdentification.getText());
	remoteRootNode = loader.loadTree(false);
	remoteSourceTree = new JTree(remoteRootNode) {

	    private static final long serialVersionUID = 1L;


	    public boolean isPathEditable(TreePath path) {
		Object comp = path.getLastPathComponent();
		if (comp instanceof DefaultMutableTreeNode) {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
		    Object userObject = node.getUserObject();
		    if (userObject instanceof FileNameCheckBoxNode) {
			return true;
		    }
		}
		return false;
	    }
	};
	NodeRenderer renderer = new NodeRenderer();
	remoteSourceTree.setCellRenderer(renderer);
	NodeEditor editor = new NodeEditor();
	remoteSourceTree.setCellEditor(editor);
	remoteSourceTree.setEditable(true);
	remoteScrollPanel.remove(remoteScrollPane);
	remoteScrollPane = new JScrollPane(remoteSourceTree);
	remoteScrollPanel.add(remoteScrollPane);
	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	this.repaint();
    }


    /**
     * Načte rozbalovací strom dostupných dat v lokálním umístění
     */
    public void loadLocalSourceTree() {
	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	LocalSourceTreeLoader loader = new LocalSourceTreeLoader();
	loader.setPath(this.getLocalSourcesAddress());
	localRootNode = loader.loadTree();
	localSourceTree = new JTree(localRootNode) {

	    private static final long serialVersionUID = 1L;


	    public boolean isPathEditable(TreePath path) {
		Object comp = path.getLastPathComponent();
		if (comp instanceof DefaultMutableTreeNode) {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
		    Object userObject = node.getUserObject();
		    if (userObject instanceof FileNameCheckBoxNode) {
			return true;
		    }
		}
		return false;
	    }
	};
	NodeRenderer renderer = new NodeRenderer();
	localSourceTree.setCellRenderer(renderer);
	NodeEditor editor = new NodeEditor();
	localSourceTree.setCellEditor(editor);
	localSourceTree.setEditable(true);
	localScrollPanel.remove(localScrollPane);
	localScrollPane = new JScrollPane(localSourceTree);
	localSourcesPanel.add(localScrollPane);
	localScrollPanel.add(localScrollPane);
	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	this.repaint();
    }


    /**
     * Otevře dialog pro výběr složky
     */
    public void chooseLocalFolder() {
	if (localFolderChooser.showDialog(this, rb.getString("ssd.14")) == JFileChooser.APPROVE_OPTION) {
	    tfLocalAddressField.setText(localFolderChooser.getSelectedFile().getAbsolutePath());
	}
    }


    /**
     * Otevře dialog pro výběr složky single modelu
     */
    public void chooseLocalFolderForSingleModel() {
	if (localFolderChooser.showDialog(this, rb.getString("ssd.14")) == JFileChooser.APPROVE_OPTION) {
	    tfSingleLocalAddressField.setText(localFolderChooser.getSelectedFile().getAbsolutePath());
	}
    }


    /**
     * Vrací cestu ke složce dat v lokálním umístění
     */
    private String getLocalSourcesAddress() {
	String address = tfLocalAddressField.getText();
	if (address.substring(address.length() - 1).equals("/"))
	    address = address.substring(0, address.length() - 1);
	return address;
    }


    /**
     * Aplikuje nastavení pro načtení dat ze serveru
     */
    @SuppressWarnings("unchecked")
    private void applyRemoteSettings() {
	settings.clearFilePaths();
	// načtení cest k vybraným zdrojům dat
	Enumeration<DefaultMutableTreeNode> en = remoteRootNode.preorderEnumeration();
	while (en.hasMoreElements()) {
	    DefaultMutableTreeNode node = en.nextElement();
	    Object o = node.getUserObject();
	    if (o instanceof FileNameCheckBoxNode) {
		if (((FileNameCheckBoxNode) o).isSelected()) {
		    TreeNode[] pathNode = node.getPath();
		    String path = "" + pathNode[0] + pathNode[1] + "-" + pathNode[2] + "-" + pathNode[3] + "/" + pathNode[4];
		    settings.addFilePath(path);
		}
	    }
	}
	settings.setDataSourceType(Constants.REMOTE_SERVER);
	settings.setDataType(Constants.ZIP);
	settings.modelZipRemotePath = tfRemoteAddressField.getText();
	settings.remoteZipFileIdentification = tfRowZIPFileIdentification.getText();
	loadDialogConfirmed = true;
    }


    /**
     * Aplikuje nastavení pro načtení dat z lokálního umístění ze ZIPu
     */
    private void applyLocalSettings() {
	settings.clearFilePaths();
	addPathsToSettings(localRootNode, "");
	settings.setDataSourceType(Constants.LOCAL);
	settings.setDataType(Constants.ZIP);
	settings.modelZipLocalPath = tfLocalAddressField.getText();
	loadDialogConfirmed = true;
    }


    /**
     * Aplikuje nastavení pro načtení dat single modelu ze složky se všemi soubory
     */
    private void applyLocalSingleSettings() {
	settings.clearFilePaths();
	settings.addFilePath(tfSingleLocalAddressField.getText());
	settings.setDataSourceType(Constants.LOCAL);
	settings.setDataType(Constants.FOLDER);
	settings.modelSingleLocalPath = tfSingleLocalAddressField.getText();
	loadDialogConfirmed = true;
    }


    /**
     * Přidá k souborům v nastavení úplné cesty
     * @param node
     * @param addressPath
     */
    private void addPathsToSettings(DefaultMutableTreeNode node, String addressPath) {
	if (node != null) {
	    String separator = "/";
	    if (addressPath.equals(""))
		separator = "";
	    Object userObject = node.getUserObject();
	    if (userObject instanceof FileNameCheckBoxNode) {
		if (((FileNameCheckBoxNode) userObject).isSelected()) {
		    settings.addFilePath(addressPath + separator + (((FileNameCheckBoxNode) userObject).getName()));
		    ((FileNameCheckBoxNode) userObject).setSelected(false);
		}
	    }
	    for (int i = 0; i < node.getChildCount(); i++) {
		addPathsToSettings((DefaultMutableTreeNode) node.getChildAt(i), addressPath + separator + node.toString());
	    }
	}
    }


    /**
     * Aplikuje nastavení pro načtení dat přes telnet
     * @throws Exception
     */
    private void applyTelnetSettings() throws Exception {
	settings.clearFilePaths();
	settings.addFilePath("ospf_data_network");
	settings.telnetUrl = tfTelnetUrl.getText();
	try {
	    settings.telnetPortIPv4 = Integer.valueOf(tfTelnetPortIPv4.getText());
	    settings.telnetPortIPv6 = Integer.valueOf(tfTelnetPortIPv6.getText());
	    settings.telnetTimeout = Integer.valueOf(tfTelnetTimeout.getText());
	} catch (Exception e) {
	    throw new Exception("telnet port parse error");
	}
	settings.telnetPassword = tfTelnetPassword.getText();
	settings.rdnsServer = tfTelnetRDNSServer.getText();
	settings.setDataSourceType(Constants.TELNET);
	loadDialogConfirmed = true;
    }


    /**
     * Aplikuje nastavení pro načtení dat z časového rozmezí
     */
    private void applyFromDateToDateSettings() {
	settings.clearFilePaths();
	for (int i = 0; i < model.size(); i++) {
	    String s = (String) model.get(i);
	    settings.addFilePath(tfZIPRemoteAddressFieldPath.getText() + s.substring(0, 10) + "/" + s + ".zip");
	}
	settings.countDaysBack = countDays;
	settings.modelZipRemotePathBetween = tfZIPRemoteAddressFieldPath.getText();
	settings.modelTimeBetween = tfTime.getText();
	settings.fromDateToDateLoadTo = groupWhereAdd.getSelection().getActionCommand();
	settings.setDataSourceType(Constants.REMOTE_SERVER);
	settings.setDataType(Constants.ZIP);
	loadDialogConfirmed = true;
    }


    /**
     * Aplikuje nastaveni dle dialogoveho okna
     * @throws Exception vyjimka pri chybnem nastaveni zdroje
     */
    public void applySettings() throws Exception {
	switch (sourceTypeTabs.getSelectedIndex()) {
	    case Constants.FROM_DATE_TO_DATE:
		applyFromDateToDateSettings();
		break;
	    case Constants.ZIP_SERVER:
		applyRemoteSettings();
		break;
	    case Constants.ZIP_LOCAL:
		applyLocalSettings();
		break;
	    case Constants.LOCAL_SOURCES:
		applyLocalSingleSettings();
		break;
	    case Constants.TELNET:
		applyTelnetSettings();
		break;
	}
	settings.loadDataTypIndex = sourceTypeTabs.getSelectedIndex();
    }
}
