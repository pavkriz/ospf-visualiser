package org.hkfree.ospf.gui.ospfwin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.hkfree.ospf.gui.netstateswin.NetStatesWin;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.tools.Factory;

/**
 * Status bar pro aplikaci
 * @author Jan Schovánek
 */
public class StatusBar extends JPanel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private final static String space = "   |  ";
    private JLabel statusInfo1 = new JLabel();
    private JLabel statusInfo2 = new JLabel();
    private JLabel statusHeading = null;
    private JButton bZoomIn = null;
    private JButton bZoomOut = null;
    private Icon icoHelp = new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "help.png"));
    private Icon icoZoomOut = new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "zoomOut.png"));
    private Icon icoZoomIn = new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "zoomIn.png"));


    public StatusBar(final JFrame owner) {
	super();
	this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
	this.setAlignmentY(JPanel.CENTER_ALIGNMENT);
	this.setPreferredSize(new Dimension(100, 22));
	statusHeading = new JLabel();
	statusHeading.setText(rb.getString("mdw.noModel"));
	statusInfo2.addMouseListener(new MouseAdapter() {

	    // přeimplementování metod, aby se u tohoto
	    // panelu zobrazoval tooltip nepřetržitě
	    int initialD = -1, dismissD = -1;


	    public void mouseEntered(MouseEvent arg0) {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		initialD = ttm.getInitialDelay();
		dismissD = ttm.getDismissDelay();
		ttm.setInitialDelay(500);
		ttm.setDismissDelay(Integer.MAX_VALUE);
	    }


	    public void mouseExited(MouseEvent arg0) {
		if (initialD < 0 || dismissD < 0)
		    return;
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(initialD);
		ttm.setDismissDelay(dismissD);
	    }
	});
	JPanel panelZoom = new JPanel();
	panelZoom.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
	panelZoom.setAlignmentY(JPanel.TOP_ALIGNMENT);
	bZoomIn = new JButton(new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (owner instanceof OspfWin) {
		    ((OspfWin) owner).getActualMDManager().getGraphComponent().zoomPlus();
		} else if (owner instanceof NetStatesWin) {
		    ((NetStatesWin) owner).getManager().getGraphComponent().zoomPlus();
		}
	    }
	});
	bZoomIn.setIcon(icoZoomIn);
	bZoomIn.setMargin(new Insets(1, 1, 1, 1));
	// bZoomIn.setBorderPainted(false);
	// bZoomIn.setBorder(null);
	// bZoomIn.setFocusable(false);
	// bZoomIn.setContentAreaFilled(false);
	bZoomOut = new JButton(new AbstractAction() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (owner instanceof OspfWin) {
		    ((OspfWin) owner).getActualMDManager().getGraphComponent().zoomMinus();
		} else if (owner instanceof NetStatesWin) {
		    ((NetStatesWin) owner).getManager().getGraphComponent().zoomMinus();
		}
	    }
	});
	bZoomOut.setIcon(icoZoomOut);
	bZoomOut.setMargin(new Insets(1, 1, 1, 1));
	panelZoom.add(bZoomOut);
	panelZoom.add(bZoomIn);
	this.add(panelZoom);
	this.add(statusHeading);
	this.add(statusInfo1);
	this.add(statusInfo2);
	clear();
    }


    /**
     * Nastaví text do pole do statusu
     * @param index index pole ve statusu (indexováno od nuly)
     * @param text zobrazený text
     */
    public void setStatus(int index, String text) {
	statusHeading.setText(space + rb.getString("mdw.2") + ": ");
	bZoomIn.setVisible(true);
	bZoomOut.setVisible(true);
	switch (index) {
	    case 0:
		statusInfo1.setText(text + space);
		break;
	    case 1:
		statusInfo2.setText(text);
		break;
	    default:
		break;
	}
    }


    /**
     * Nastaví tooltip
     * @param index index pole ve statusu
     * @param text zobrazený text
     */
    public void setToolTip(int index, String text) {
	switch (index) {
	    case 0:
		statusInfo1.setToolTipText(text);
		break;
	    case 1:
		statusInfo2.setToolTipText(text);
		statusInfo2.setIcon(icoHelp);
		break;
	    default:
		break;
	}
    }


    /**
     * Vyčistí statusbar
     */
    public void clear() {
	statusInfo1.setText("");
	statusInfo1.setToolTipText("");
	statusInfo2.setText("");
	statusInfo2.setToolTipText("");
	statusInfo2.setIcon(null);
	bZoomIn.setVisible(false);
	bZoomOut.setVisible(false);
	statusHeading.setText(rb.getString("mdw.noModel"));
    }
}
