package org.hkfree.ospf.gui.mappanel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

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
import org.hkfree.ospf.model.ospf.StubLink;
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
	if (r != null) {
	    pInfo.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.NORTHWEST;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.ipadx = 2;
	    c.ipady = 2;
	    c.weightx = 1;
	    c.gridwidth = 2;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.0") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getRouterID()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getRouterName()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.3") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getNameSuffix()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.4") + ": " + String.valueOf(model.getRouterLinksCount(r))), c);
	    c.gridy = c.gridy + 1;
	    c.gridwidth = 1;
	    c.insets = new Insets(20, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.14")), c);
	    c.gridx = 1;
	    pInfo.add(new JLabel(rb.getString("pw.11")), c);
	    c.gridwidth = 1;
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    for (StubLink sl : r.getStubs()) {
		c.gridy = c.gridy + 1;
		c.gridx = 0;
		pInfo.add(new JLabel(sl.getLinkID()), c);
		c.gridx = 1;
		pInfo.add(new JLabel(String.valueOf(sl.getCost())), c);
	    }
	    c.gridx = 0;
	    c.gridy = c.gridy + 1;
	    c.weighty = 1;
	    pInfo.add(new JLabel(""), c);
	}
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
	c.ipadx = 2;
	c.ipady = 2;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 0;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.10") + ":"), c);
	c.gridy = c.gridy + 1;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(le.getLinkID()), c);
	c.gridy = c.gridy + 1;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.12") + ": " + String.valueOf(le.getFaultCount())), c);
	c.gridy = c.gridy + 1;
	c.insets = new Insets(2, 6, 0, 0);
	pInfo.add(new JLabel(rb.getString("pw.13") + ": " + String.valueOf(le.getFaultIntensity())), c);
	// router 1
	if (r != null) {
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(20, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.15") + " 1"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.0") + ": " + r.getRouterID()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getRouterName()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.11") + ": " + String.valueOf(le.getCost1())), c);
	}
	// router 2
	if (r2 != null) {
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(20, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.15") + " 2"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.0") + ": " + r2.getRouterID()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r2.getRouterName()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.11") + ": " + String.valueOf(le.getCost2())), c);
	}
	c.gridx = 0;
	c.gridy = c.gridy + 1;
	c.weighty = 1;
	pInfo.add(new JLabel(""), c);
	pInfo.updateUI();
    }
}
