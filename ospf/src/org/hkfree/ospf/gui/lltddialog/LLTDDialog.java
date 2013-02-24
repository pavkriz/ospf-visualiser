package org.hkfree.ospf.gui.lltddialog;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JDialog;

import org.hkfree.ospf.model.lltd.Device;
import org.hkfree.ospf.model.lltd.LLTDModel;
import org.hkfree.ospf.model.lltd.Relation;
import org.hkfree.ospf.tools.Factory;

/**
 * Dialog zobrazující mapu LLTD modelu
 * @author Jan Schovánek
 */
public class LLTDDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private List<Device> devices = null;
    private List<Relation> relations = null;
    LLTDGraphComponent graph = null;


    public LLTDDialog(LLTDModel lltd) {
	devices = lltd.getDevices();
	relations = lltd.getRelations();
	createGUI();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	this.setTitle(rb.getString("lltdd.title") + "   " + lltd.getPublicIP() + "   " + df.format(lltd.getDate()));
	this.pack();
    }


    /**
     * Vytvoření GUI
     */
    private void createGUI() {
	graph = new LLTDGraphComponent(devices, relations);
	this.add(graph.getVisualizationComponent(), BorderLayout.CENTER);
    }
}
