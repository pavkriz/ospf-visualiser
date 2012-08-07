package org.hkfree.ospf.gui.summarydialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující dialog s informacemi o načteném modelu
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfModelSummaryDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private OspfWinModelTabbedPane ospfModelTabbedPane = null;
    private JPanel statusPanel = new JPanel();


    /**
     * Konstruktor
     * @param tabbedPane
     * @param model
     */
    public OspfModelSummaryDialog(JFrame owner, OspfWinModelTabbedPane tabbedPane, OspfModel model) {
	int routerCount = model.getRouters().size();
	int linkCount = model.getOspfLinks().size();
	this.ospfModelTabbedPane = tabbedPane;
	this.setLayout(new BorderLayout());
	JLabel lInfo = new JLabel(String.format("%1$s: %2$d              %3$s: %4$d", rb.getString("owmp.1"), routerCount,
		rb.getString("owmp.2"), linkCount));
	lInfo.setFont(new Font("Arial", 0, 12));
	statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	statusPanel.setPreferredSize(new Dimension(this.getWidth(), 30));
	statusPanel.setAlignmentY(CENTER_ALIGNMENT);
	statusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
	statusPanel.add(lInfo);
	this.add(statusPanel, BorderLayout.PAGE_END);
	this.add(tabbedPane, BorderLayout.CENTER);
	this.setTitle(model.getModelName());
	this.setLocationRelativeTo(owner);
	this.setIconImage(this.getToolkit().getImage(getClass().getResource(Constants.URL_IMG_GUI + "ico.gif")));
	this.setSize(new Dimension(700, 500));
    }


    /**
     * Vrací OspfModelTabbedPane
     * @return OspfModelTabbedPane
     */
    public OspfWinModelTabbedPane getOspfModelTabbedPane() {
	return ospfModelTabbedPane;
    }
}
