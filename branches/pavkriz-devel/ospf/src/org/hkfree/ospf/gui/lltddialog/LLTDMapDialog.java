package org.hkfree.ospf.gui.lltddialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.MODE;
import org.hkfree.ospf.model.lltd.Device;
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.lltd.Relation;
import org.hkfree.ospf.tools.Factory;

/**
 * Dialog zobrazující mapu LLTD modelu
 * @author Jan Schovánek
 */
public class LLTDMapDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<Device> devices = null;
    private List<Relation> relations = null;
    private LLTDGraphComponent graph = null;
    private Icon icoZoomOut = new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "zoomOut.png"));
    private Icon icoZoomIn = new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "zoomIn.png"));
    private JPanel pInfo = null;


    public LLTDMapDialog(LLTDModel lltd) {
	devices = lltd.getDevices();
	relations = lltd.getRelations();
	createGUI();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	this.setTitle(rb.getString("lltdd.title") + "   " + lltd.getPublicIP() + "   " + df.format(lltd.getDate()));
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "ico.gif")));
	this.setMinimumSize(new Dimension(600, 400));
	this.pack();
    }


    /**
     * Vytvoření GUI
     */
    private void createGUI() {
	// komponenta grafu
	graph = new LLTDGraphComponent(devices, relations, this);
	// panel s ovladanim
	String[] lables = { rb.getString("lltdd.0"), rb.getString("lltdd.1") };
	final JComboBox modes = new JComboBox(lables);
	modes.setSelectedIndex(0);
	modes.addActionListener(new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (modes.getSelectedIndex() == 0) {
		    graph.setMode(MODE.TRANSFORMING);
		} else if (modes.getSelectedIndex() == 1) {
		    graph.setMode(MODE.PICKING);
		}
	    }
	});
	JPanel panelControl = new JPanel();
	panelControl.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
	panelControl.setAlignmentY(JPanel.TOP_ALIGNMENT);
	JButton bZoomIn = new JButton(new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public void actionPerformed(ActionEvent e) {
		graph.zoomPlus();
	    }
	});
	bZoomIn.setIcon(icoZoomIn);
	JButton bZoomOut = new JButton(new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public void actionPerformed(ActionEvent e) {
		graph.zoomMinus();
	    }
	});
	bZoomOut.setIcon(icoZoomOut);
	// ovladaci prvky
	panelControl.add(modes);
	panelControl.add(bZoomOut);
	panelControl.add(bZoomIn);
	// panel s info o zarizeni nebo vztahu-spoji
	pInfo = new JPanel();
	// panel nahore
	JPanel pTop = new JPanel();
	pTop.add(panelControl, BorderLayout.EAST);
	pTop.add(pInfo, BorderLayout.CENTER);
	// pridani prvku do okna
	this.add(pTop, BorderLayout.NORTH);
	this.add(graph.getVisualizationComponent(), BorderLayout.CENTER);
    }


    /**
     * Aktualizace panelu zobrazujiciho informace o zarizeni nebo vztahu (spoji)
     * @param o Device, Relation
     */
    public void actualizePropertyPanel(Object o) {
	pInfo.removeAll();
	pInfo.setLayout(new GridBagLayout());
	if (o instanceof Device) {
	    Device d = (Device) o;
	    GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.NORTHWEST;
	    c.fill = GridBagConstraints.BOTH;
	    c.ipadx = 2;
	    c.ipady = 2;
	    c.weightx = 1;
	    c.gridwidth = 1;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.insets = new Insets(0, 4, 0, 0);
	    pInfo.add(new JLabel(rb.getString("lltdd.3") + ":"), c);
	    c.gridx = c.gridx + 1;
	    pInfo.add(new JLabel(d.getMachineName()), c);
	    c.gridx = c.gridx - 1;
	    c.gridy = c.gridy + 1;
	    pInfo.add(new JLabel(rb.getString("lltdd.2") + ":"), c);
	    c.gridx = c.gridx + 1;
	    pInfo.add(new JLabel(d.getSource()), c);
	    c.gridx = c.gridx - 1;
	    c.gridy = c.gridy + 1;
	    pInfo.add(new JLabel(rb.getString("lltdd.4") + ":"), c);
	    c.gridx = c.gridx + 1;
	    pInfo.add(new JLabel(d.getIpv4()), c);
	    c.gridx = c.gridx - 1;
	    c.gridy = c.gridy + 1;
	    pInfo.add(new JLabel(rb.getString("lltdd.5") + ":"), c);
	    c.gridx = c.gridx + 1;
	    pInfo.add(new JLabel(d.getIpv6()), c);
	} else if (o instanceof Relation) {
	    Relation r = (Relation) o;
	    GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.NORTHWEST;
	    c.fill = GridBagConstraints.BOTH;
	    c.ipadx = 2;
	    c.ipady = 2;
	    c.weightx = 1;
	    c.gridwidth = 1;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.insets = new Insets(0, 4, 0, 0);
	    pInfo.add(new JLabel(rb.getString("lltdd.6") + ":"), c);
	    c.gridx = c.gridx + 1;
	    pInfo.add(new JLabel(r.getFrom().getMachineName()), c);
	    c.gridx = c.gridx - 1;
	    c.gridy = c.gridy + 1;
	    pInfo.add(new JLabel(rb.getString("lltdd.7") + ":"), c);
	    c.gridx = c.gridx + 1;
	    pInfo.add(new JLabel(r.getTo().getMachineName()), c);
	    c.gridx = c.gridx - 1;
	    c.gridy = c.gridy + 1;
	    pInfo.add(new JLabel(rb.getString("lltdd.8") + ":"), c);
	    c.gridx = c.gridx + 1;
	    // nastaveni prenosoveho media, LLTD aplikace oznacuje wifi jako 00, ethernetove spojeni jako 02
	    String medium = "";
	    if (r.getMedium().equals("00"))
		medium = "Wi-Fi";
	    else if (r.getMedium().equals("02"))
		medium = "ETH";
	    pInfo.add(new JLabel(medium), c);
	}
	pInfo.updateUI();
    }
}
