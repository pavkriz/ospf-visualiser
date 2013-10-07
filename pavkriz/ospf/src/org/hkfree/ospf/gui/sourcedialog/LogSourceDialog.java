package org.hkfree.ospf.gui.sourcedialog;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog výběru seznamu dostupných logů pro načtení
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class LogSourceDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private AppSettings settings = null;
    private boolean logSourceDialogConfirmed = false;
    private JTree logSourceTree = null;
    private DefaultMutableTreeNode logRootNode = new DefaultMutableTreeNode();
    private LogSourceDialogActionListener actionListener = null;
    private JTextField addressField = new JTextField();
    private JPanel panelLoad = new JPanel();
    private JScrollPane logScrollPane = null;
    private JTextField rowFolderIdentification = new JTextField(); // "folder"
    private JTextField rowZIPFileIdentification = new JTextField(); // "compressed"


    /**
     * Konstruktor
     */
    public LogSourceDialog() {
	actionListener = new LogSourceDialogActionListener(this);
	createComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    private void createComponents() {
	logSourceTree = new JTree(logRootNode);
	// remote sources tab
	JLabel labelAddress = new JLabel(rb.getString("slsd.1") + ":");
	JButton loadLogSrcTreeBtn = new JButton(actionListener.getActionLoadLogsTree());
	JLabel labelFolderIdnt = new JLabel(rb.getString("slsd.2") + ":");
	JLabel labelFileIdnt = new JLabel(rb.getString("slsd.3") + ":");
	addressField.setMaximumSize(new Dimension(1025, 25));
	rowFolderIdentification.setMaximumSize(new Dimension(130, 25));
	rowZIPFileIdentification.setMaximumSize(new Dimension(130, 25));
	JButton remoteOk = new JButton(actionListener.getActionOk());
	JButton remoteStorno = new JButton(actionListener.getActionStorno());
	logScrollPane = new JScrollPane(logSourceTree);
	panelLoad.setLayout(new BorderLayout());
	panelLoad.add(logScrollPane);
	GroupLayout layout = new GroupLayout(this.getContentPane());
	this.getContentPane().setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createParallelGroup()
		.addComponent(labelAddress)
		.addComponent(addressField)
		.addGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(labelFolderIdnt)
				.addComponent(labelFileIdnt))
			.addGroup(layout.createParallelGroup()
				.addComponent(rowFolderIdentification)
				.addComponent(rowZIPFileIdentification)))
		.addComponent(panelLoad)
		.addGroup(layout.createSequentialGroup()
			.addComponent(loadLogSrcTreeBtn, 150, 150, 150)
			.addGap(50)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE)
			.addComponent(remoteOk, 100, 120, 120)
			.addComponent(remoteStorno, 100, 100, 100)));
	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(labelAddress)
		.addComponent(addressField)
		.addGroup(layout.createParallelGroup()
			.addComponent(labelFolderIdnt)
			.addComponent(rowFolderIdentification))
		.addGroup(layout.createParallelGroup()
			.addComponent(labelFileIdnt)
			.addComponent(rowZIPFileIdentification))
		.addComponent(panelLoad, 300, 300, 300)
		.addGroup(layout.createParallelGroup()
			.addComponent(loadLogSrcTreeBtn)
			.addComponent(remoteOk)
			.addComponent(remoteStorno))
		.addGap(10));
	this.setResizable(false);
	this.setTitle(rb.getString("slsd.title"));
	this.pack();
	this.setLocationRelativeTo(null);
    }


    /**
     * Aplikuje defaultní nastavení
     */
    public void setDataLoadSettings(AppSettings settings) {
	this.settings = settings;
	this.addressField.setText(settings.logsRemoteZipSourcePath);
	this.rowFolderIdentification.setText(settings.logsRemoteFolderIdentification);
	this.rowZIPFileIdentification.setText(settings.logsRemoteZipFileIdentification);
    }


    /**
     * Načte rozbalovací strom se seznamem dostupných logů
     */
    public void loadLogSourceTree() {
	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	RemoteSourceTreeLoader loader = new RemoteSourceTreeLoader();
	loader.setFileNameStartFilter("ospfd");
	loader.setAddress(this.getLogSourcesAddress());
	loader.setParsingIdentifications(rowFolderIdentification.getText(), rowZIPFileIdentification.getText());
	logRootNode = loader.loadTree(true);
	logSourceTree = new JTree(logRootNode) {

	    private static final long serialVersionUID = 1L;


	    @Override
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
	logSourceTree.setCellRenderer(renderer);
	NodeEditor editor = new NodeEditor();
	logSourceTree.setCellEditor(editor);
	logSourceTree.setEditable(true);
	panelLoad.remove(logScrollPane);
	logScrollPane = new JScrollPane(logSourceTree);
	panelLoad.add(logScrollPane);
	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	this.repaint();
	this.validate();
    }


    /**
     * Vrací adresu složky logů na serveru
     * @return addess
     */
    public String getLogSourcesAddress() {
	String address = addressField.getText();
	if (address.substring(address.length() - 1).equals("/"))
	    address = address.substring(0, address.length() - 1);
	return address;
    }


    /**
     * Aplikuje nastavení pro načtení logů ze serveru
     */
    public void applySettings() {
	settings.clearFilePaths();
	addPathsToSettings(logRootNode, "");
	settings.setDataSourceType(Constants.REMOTE_SERVER);
	settings.setDataType(Constants.ZIP);
	settings.logsRemoteZipSourcePath = addressField.getText();
	settings.logsRemoteFolderIdentification = rowFolderIdentification.getText();
	settings.logsRemoteZipFileIdentification = rowZIPFileIdentification.getText();
	logSourceDialogConfirmed = true;
    }


    /**
     * Vrací příznak zda byl dialog potvrzen
     * @return boolean
     */
    public boolean logSourceDialogConfirmed() {
	return logSourceDialogConfirmed;
    }


    /**
     * Nastavuje potvrzení dialogu
     * @param logSourceDialogConfirmed
     */
    public void setLogSourceDialogConfirmed(boolean logSourceDialogConfirmed) {
	this.logSourceDialogConfirmed = logSourceDialogConfirmed;
    }


    /**
     * Přidá úplně cesty k souborům pro načtení
     * @param node
     * @param addressPath
     */
    public void addPathsToSettings(DefaultMutableTreeNode node, String addressPath) {
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
}
