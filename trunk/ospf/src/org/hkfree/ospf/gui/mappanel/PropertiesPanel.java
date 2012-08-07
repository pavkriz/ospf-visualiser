package org.hkfree.ospf.gui.mappanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
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
	    root.add(new DefaultMutableTreeNode(r.getRouterID()));
	}
	JTree tree = new JTree(root);
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	JScrollPane scrollPane = new JScrollPane(tree);
	pInfo = new JPanel();
	pInfo.setBorder(BorderFactory.createTitledBorder(rb.getString("orp.6")));
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
	pInfo.removeAll();
	Router r = model.getRouterByIp(rv.getDescription());
	Box box = Box.createVerticalBox();
	box.setAlignmentX(Box.LEFT_ALIGNMENT);
	box.add(new JLabel(rb.getString("orp.7") + ":"));
	box.add(new JLabel(r.getRouterID()));
	box.add(new JLabel(rb.getString("orp.8") + ":"));
	box.add(new JLabel(r.getRouterName()));
	box.add(new JLabel(rb.getString("orp.10") + ":"));
	box.add(new JLabel(r.getNameSuffix()));
	box.add(new JLabel(rb.getString("orp.9") + ":"));
	box.add(new JLabel(String.valueOf(model.getRouterLinksCount(r))));
	pInfo.add(box);
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
	Box box = Box.createVerticalBox();
	box.setAlignmentX(Box.LEFT_ALIGNMENT);
	JLabel l = new JLabel(le.getLinkID());
	l.setBackground(Color.yellow);
	box.add(l);
	//box.add(new JLabel(le.getLinkDescription()));
	//box.add(new JLabel(le.getLinkFaultDescription()));
	box.add(new JLabel(String.valueOf(le.getCost1())));
	box.add(new JLabel(String.valueOf(le.getCost2())));
	box.add(new JLabel(String.valueOf(le.getFaultCount())));
	box.add(new JLabel(String.valueOf(le.getFaultIntensity())));
	pInfo.add(box);
	pInfo.updateUI();
    }
}
