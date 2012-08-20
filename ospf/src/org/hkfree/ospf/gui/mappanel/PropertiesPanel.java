package org.hkfree.ospf.gui.mappanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.tools.Factory;

/**
 * Properties panel pro vložení do komponenty s grafem.
 * Zobrazuje informace o router/spoji na který se kliklo.
 * @author Jan Schovánek
 */
public class PropertiesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private OspfModel model;
    private JPanel pInfo;
    private JTree tree;


    /**
     * Konstruktor
     * @param ospfModel
     */
    public PropertiesPanel(OspfModel ospfModel) {
	this.model = ospfModel;
	createComponents();
    }


    /**
     * Vytvoření komponent
     */
    private void createComponents() {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode(model.getModelName());
	for (Router r : model.getRouters()) {
	    root.add(new DefaultMutableTreeNode(r.getRouterID() + " - " + r.getRouterName()));
	}
	tree = new JTree(root);
	tree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	JScrollPane scrollPane = new JScrollPane(tree);
	pInfo = new JPanel();
	JSplitPane splitInfo = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, pInfo);
	splitInfo.setDividerLocation(180);
	this.setLayout(new BorderLayout());
	this.add(splitInfo, BorderLayout.CENTER);
    }


    /**
     * Aktualizace hodnot týkajících se routeru
     * @param rv router
     */
    public void actualizeValues(RouterVertex rv) {
	DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
	for (int i = 0; i < tree.getModel().getChildCount(root); i++) {
	    if (((DefaultMutableTreeNode) tree.getModel().getChild(root, i)).toString().startsWith(rv.getDescription())) {
		tree.setSelectionRow(i + 1);
		tree.scrollRowToVisible(i + 1);
	    }
	}
	pInfo.removeAll();
	Router r = model.getRouterByIp(rv.getDescription());
	pInfo.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.fill = GridBagConstraints.HORIZONTAL;
	c.ipadx = 2;
	c.ipady = 2;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 0;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.0") + ":"), c);
	c.gridy = 1;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(r.getRouterID()), c);
	c.gridy = 2;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	c.gridy = 3;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(r.getRouterName()), c);
	c.gridy = 4;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.3") + ":"), c);
	c.gridy = 5;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(r.getNameSuffix()), c);
	c.gridy = 6;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.4") + ":"), c);
	c.gridy = 7;
	c.weighty = 1;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(String.valueOf(model.getRouterLinksCount(r))), c);
	pInfo.updateUI();
    }


    /**
     * Aktualizace hodnot týkajících spoje
     * @param le spoj
     */
    public void actualizeValues(LinkEdge le) {
	pInfo.removeAll();
	Router r = model.getRouterByIp(le.getRVertex1().getDescription());
	Router r2 = model.getRouterByIp(le.getRVertex2().getDescription());
	pInfo.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.fill = GridBagConstraints.HORIZONTAL;
	int y=0;
	c.ipadx = 2;
	c.ipady = 2;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = y++;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.10") + ":"), c);
	c.gridy = y++;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(le.getLinkID()), c);
	c.gridy = y++;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.11") + " 1:"), c);
	c.gridy = y++;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(String.valueOf(le.getCost1())), c);
	c.gridy = y++;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.11") + " 2:"), c);
	c.gridy = y++;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(String.valueOf(le.getCost2())), c);
	c.gridy = y++;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.12") + ":"), c);
	c.gridy = y++;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(String.valueOf(le.getFaultCount())), c);
	c.gridy = y++;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.13") + ":"), c);
	c.gridy = y++;
	c.weighty = 1;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(String.valueOf(le.getFaultIntensity())), c);
	pInfo.updateUI();
    }
}
