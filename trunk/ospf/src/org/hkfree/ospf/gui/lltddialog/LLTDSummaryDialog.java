package org.hkfree.ospf.gui.lltddialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.hkfree.ospf.gui.ospfwin.OspfWinManager;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.tools.Factory;

public class LLTDSummaryDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private OspfWinManager manager = null;
    private JButton btnLoadData = new JButton();
    private JButton btnAddToModels = new JButton();


    public LLTDSummaryDialog(Frame frame, OspfWinManager ospfWinManager) {
	super(frame);
	manager = ospfWinManager;
	createGUI();
	this.setTitle(rb.getString("lltdsd.title"));
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "nastaveni.png")));
	this.pack();
	this.setModal(false);
	this.setLocationRelativeTo(null);
	this.setResizable(true);
    }


    /**
     * Vytvoření GUI
     */
    private void createGUI() {
	btnLoadData.setText(rb.getString("lltdsd.0"));
	btnLoadData.addActionListener(this);
	btnAddToModels.setText(rb.getString("lltdsd.1"));
	btnAddToModels.addActionListener(this);
	Container c = this.getContentPane();
	GroupLayout layout = new GroupLayout(c);
	c.setLayout(layout);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup()
	        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	                .addGroup(layout.createSequentialGroup()
	                        .addComponent(btnLoadData)
	                        .addComponent(btnAddToModels))));
	layout.setVerticalGroup(layout.createSequentialGroup()
	        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(btnLoadData)
	                .addComponent(btnAddToModels)));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == btnLoadData)
	    manager.loadLLTDData();
	else if (e.getSource() == btnAddToModels) {
	    manager.addLLTDtoOspfModels();
	}
    }
}
