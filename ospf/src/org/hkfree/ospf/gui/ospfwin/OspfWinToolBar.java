package org.hkfree.ospf.gui.ospfwin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.hkfree.ospf.tools.Factory;

/**
 * Třída představující toolbar okna návrhu sítě
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfWinToolBar extends JToolBar {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private ActionListener actionListener = null;
    private JTextField tfFind = new JTextField();


    /**
     * Konstruktor
     * @param actionListener
     * @param mapDesignWinManager
     */
    public OspfWinToolBar(ActionListener actionListener) {
	this.actionListener = actionListener;
	makeComponents();
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
	// z puvodniho
	this.setName(rb.getString("owtb.0"));
	this.setOrientation(JToolBar.HORIZONTAL);
	this.setRollover(true);
	this.add(((OspfWinActionListener) actionListener).getActionOpenXMLModel());
	this.add(((OspfWinActionListener) actionListener).getActionLoadData());
	this.add(((OspfWinActionListener) actionListener).getActionLoadLog());
	this.addSeparator();
	this.add(((OspfWinActionListener) actionListener).getActionShowNetStates());
	this.add(((OspfWinActionListener) actionListener).getActionCenterRouter());
	this.addSeparator();
	this.add(((OspfWinActionListener) actionListener).getActionCloseActualModel());
	this.add(((OspfWinActionListener) actionListener).getActionZoom());
	this.add(((OspfWinActionListener) actionListener).getActionTransformingMode());
	this.add(((OspfWinActionListener) actionListener).getActionPickingMode());
	this.addSeparator();
	this.add(((OspfWinActionListener) actionListener).getActionShowNeighboursMode());
	this.add(((OspfWinActionListener) actionListener).getActionCostChangingMode());
	this.add(((OspfWinActionListener) actionListener).getActionShortestPath());
	this.add(((OspfWinActionListener) actionListener).getActionTwoRoutersShortesPathMode());
	this.add(((OspfWinActionListener) actionListener).getActionAsymetricLinksMode());
	this.add(((OspfWinActionListener) actionListener).getActionLockMode());
	this.add(((OspfWinActionListener) actionListener).getActionGPS());
	this.add(((OspfWinActionListener) actionListener).getActionLockAll());
	this.add(((OspfWinActionListener) actionListener).getActionGPSAll());
//	this.add(((OspfWinActionListener) actionListener).getActionStartLayouting());
//	this.add(((OspfWinActionListener) actionListener).getActionStopLayouting());
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStartFR());
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStartSpring());
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStopSpring());
	this.addSeparator();
	this.add(((OspfWinActionListener) actionListener).getActionAddVertexes());
	this.add(((OspfWinActionListener) actionListener).getActionAddEdges());
	this.addSeparator();
	this.add(getSearchPanel());
    }


    /**
     * Vrací panel s prvky pro vyhledávání
     * @return
     */
    private JPanel getSearchPanel() {
	tfFind.setText(rb.getString("mdwtb.2"));
	tfFind.setForeground(Color.GRAY);
	tfFind.setToolTipText(rb.getString("mdwtb.1"));
	tfFind.getDocument().addDocumentListener(new DocumentListener() {

	    public void removeUpdate(DocumentEvent e) {
		String s = tfFind.getText();
		if (s.length() != String.valueOf(rb.getString("mdwtb.2")).length() || !s.contains(rb.getString("mdwtb.2"))) {
		    tfFind.setForeground(Color.BLACK);
		} else {
		    tfFind.setForeground(Color.GRAY);
		}
	    }


	    public void insertUpdate(DocumentEvent e) {
		String s = tfFind.getText();
		if (!s.contains(rb.getString("mdwtb.2")) || s.length() != String.valueOf(rb.getString("mdwtb.2")).length()) {
		    tfFind.setForeground(Color.BLACK);
		} else {
		    tfFind.setForeground(Color.GRAY);
		}
	    }


	    public void changedUpdate(DocumentEvent arg0) {}
	});
	tfFind.setAction(((OspfWinActionListener) actionListener).getActionSearchRouter());
	tfFind.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		if (tfFind.getText().isEmpty()) {
		    tfFind.setText(rb.getString("mdwtb.2"));
		    tfFind.setForeground(Color.GRAY);
		}
	    }
	});
	JButton btnFind = new JButton(((OspfWinActionListener) actionListener).getActionSearchRouter());
	btnFind.setBorder(BorderFactory.createEmptyBorder());
	JPanel findPanel = new JPanel();
	findPanel.setPreferredSize(new Dimension(180, 25));
	findPanel.setMaximumSize(new Dimension(190, 25));
	findPanel.setLayout(new BorderLayout());
	btnFind.setBorder(BorderFactory.createEtchedBorder());
	tfFind.setBorder(BorderFactory.createEmptyBorder());
	findPanel.add(tfFind, BorderLayout.CENTER);
	findPanel.add(btnFind, BorderLayout.EAST);
	findPanel.setBorder(BorderFactory.createEtchedBorder());
	return findPanel;
    }


    public JTextField getTfFind() {
	return tfFind;
    }
}
