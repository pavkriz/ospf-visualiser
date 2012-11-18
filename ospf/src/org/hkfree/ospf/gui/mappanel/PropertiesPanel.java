package org.hkfree.ospf.gui.mappanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.hkfree.ospf.model.map.LinkEdge;
import org.hkfree.ospf.model.map.RouterVertex;
import org.hkfree.ospf.model.ospf.ExternalLSA;
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
	if (model != null) {
	    createComponents();
	}
    }


    /**
     * Vytvoření komponent
     */
    private void createComponents() {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode(model.getModelName());
	for (Router r : model.getRouters()) {
	    root.add(new DefaultMutableTreeNode(r.getId() + " - " + r.getName()));
	}
	tree = new JTree(root);
	tree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addMouseListener(new MouseAdapter() {

	    public void mouseClicked(MouseEvent me) {
		String text = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject().toString();
		if (me.getClickCount() == 1) {
		    // klik - zobrazení informací v properties okně
		    actualizeValues(model.getRouterByIp(text.substring(0, text.indexOf("-") - 1)));
		} else if (me.getClickCount() == 2) {
		    // double klik - vyhledání routeru
		    // String text = ((DefaultMutableTreeNode)
		    // tree.getLastSelectedPathComponent()).getUserObject().toString();
		    // model.getRouterByIp(text.substring(0, text.indexOf("-") - 1))
		    // TODO dodelat vyhledavani routeru z property okna, az po uprave na novy vzhled viz email pro PK
		}
	    }
	});
	JScrollPane scrollPane = new JScrollPane(tree);
	pInfo = new JPanel();
	JSplitPane splitInfo = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, pInfo);
	splitInfo.setDividerLocation(180);
	this.setLayout(new BorderLayout());
	this.add(splitInfo, BorderLayout.CENTER);
    }


    /**
     * Vyhledá router dle routervertex a zavolá metodu pro aktualizaci informací v property okně.
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
	actualizeValues(model.getRouterByIp(rv.getDescription()));
    }


    /**
     * Provede aktualizaci property okna dle routeru
     * @param r router
     */
    private void actualizeValues(Router r) {
	pInfo.removeAll();
	if (r != null) {
	    pInfo.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.NORTHWEST;
	    c.fill = GridBagConstraints.BOTH;
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
	    pInfo.add(new JLabel(r.getId()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getName()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.3") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getSuffix()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.4") + ": " + String.valueOf(model.getCountOfLinksContainingRouter(r))), c);
	    // tabulka se Stuby
	    c.gridy = c.gridy + 1;
	    c.gridwidth = 2;
	    c.insets = new Insets(20, 0, 0, 0);
	    c.weighty = 1;
	    pInfo.add(getStubTable(r), c);
	    // tabulka s External LSA
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 0, 0, 0);
	    pInfo.add(getExternalLSATable(r), c);
	}
	pInfo.updateUI();
    }


    /**
     * Vrací tabulku s External LSA
     * @param r router ze kterého se načtou data
     * @return
     */
    private Component getExternalLSATable(Router r) {
	String[] columnNames = { rb.getString("pw.17"), rb.getString("pw.11.short"), rb.getString("pw.16.short") };
	Object data[][] = new Object[r.getExternalLsa().size()][3];
	int i = 0;
	for (ExternalLSA e : r.getExternalLsa()) {
	    data[i][0] = e.getNetwork();
	    data[i][1] = e.getMetricType();
	    data[i][2] = e.getMask();
	    i++;
	}
	JTable table = new JTable(data, columnNames);
	table.getColumnModel().getColumn(1).setMaxWidth(40);
	table.getColumnModel().getColumn(2).setMaxWidth(25);
	table.setFillsViewportHeight(true);
	table.setAutoCreateRowSorter(true);
	JScrollPane scrollPane = new JScrollPane(table);
	return scrollPane;
    }


    /**
     * Vrací tabulku se Stub sítěmi
     * @param r router ze ktere se načtou data
     * @return
     */
    private Component getStubTable(Router r) {
	String[] columnNames = { rb.getString("pw.14"), rb.getString("pw.11.short"), rb.getString("pw.16.short") };
	Object data[][] = new Object[r.getStubs().size()][3];
	int i = 0;
	for (StubLink s : r.getStubs()) {
	    data[i][0] = s.getLinkID();
	    data[i][1] = s.getCost();
	    data[i][2] = s.getMask();
	    i++;
	}
	JTable table = new JTable(data, columnNames);
	table.getColumnModel().getColumn(1).setMaxWidth(40);
	table.getColumnModel().getColumn(2).setMaxWidth(25);
	table.setFillsViewportHeight(true);
	table.setAutoCreateRowSorter(true);
	JScrollPane scrollPane = new JScrollPane(table);
	return scrollPane;
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
	pInfo.add(new JLabel(rb.getString("pw.0") + ":"), c);
	c.gridy = c.gridy + 1;
	c.insets = new Insets(0, 30, 0, 0);
	pInfo.add(new JLabel(le.getLinkIDv4()), c);
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
	    pInfo.add(new JLabel(rb.getString("pw.0") + ": " + r.getId()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r.getName()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.11") + ": " + String.valueOf(le.getCost1v4())), c);
	}
	// router 2
	if (r2 != null) {
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(20, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.15") + " 2"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.0") + ": " + r2.getId()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.2") + ":"), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(0, 30, 0, 0);
	    pInfo.add(new JLabel(r2.getName()), c);
	    c.gridy = c.gridy + 1;
	    c.insets = new Insets(2, 6, 0, 0);
	    pInfo.add(new JLabel(rb.getString("pw.11") + ": " + String.valueOf(le.getCost2v4())), c);
	}
	c.gridx = 0;
	c.gridy = c.gridy + 1;
	c.weighty = 1;
	pInfo.add(new JLabel(""), c);
	pInfo.updateUI();
    }
}
