package org.hkfree.ospf.gui.ospfwin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

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
    private JTextField tfFind = null;
    private JToggleButton togIPv6 = null;


    /**
     * Konstruktor
     * @param actionListener
     * @param mapDesignWinManager
     */
    public OspfWinToolBar(ActionListener actionListener) {
	this.actionListener = actionListener;
	init();
	makeComponents();
    }


    private void init() {
	tfFind = new JTextField();
	togIPv6 = new JToggleButton();
	togIPv6.setAction(((OspfWinActionListener) actionListener).getActionIPv6Toggle());
	togIPv6.setText("");
	togIPv6.setSelected(false);
    }


    /**
     * Vytvoří komponenty GUI
     */
    public void makeComponents() {
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
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStartFR());
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStartJS());
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStartSpring());
	this.add(((OspfWinActionListener) actionListener).getActionLayoutStopSpring());
	this.addSeparator();
	this.add(((OspfWinActionListener) actionListener).getActionAddVertexes());
	this.add(((OspfWinActionListener) actionListener).getActionAddEdges());
	this.addSeparator();
	this.add(getIPv6ToggleButton());
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
	tfFind.addFocusListener(new FocusListener() {

	    @Override
	    public void focusLost(FocusEvent e) {
		if (tfFind.getText().isEmpty()) {
		    tfFind.setText(rb.getString("mdwtb.2"));
		    tfFind.setForeground(Color.GRAY);
		}
	    }


	    @Override
	    public void focusGained(FocusEvent e) {
		if (tfFind.getText().equals(rb.getString("mdwtb.2"))) {
		    tfFind.setText("");
		    tfFind.setForeground(Color.BLACK);
		}
	    }
	});
	tfFind.setAction(((OspfWinActionListener) actionListener).getActionSearchRouter());
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


    public JToggleButton getIPv6ToggleButton() {
	return togIPv6;
    }


    public JTextField getTfFind() {
	return tfFind;
    }
}
