package org.hkfree.ospf.gui.ospfwin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.tools.Factory;

/**
 * Status bar pro aplikaci
 * @author Jan Schovánek
 */
public class StatusBar extends JPanel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JLabel statusInfo1 = new JLabel();
    private JLabel statusInfo2 = new JLabel();
    private JLabel statusHeading = null;
    private Icon ico = new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "help.png"));


    public StatusBar() {
	super();
	this.setLayout(new FlowLayout(FlowLayout.LEADING));
	this.setAlignmentY(JPanel.TOP_ALIGNMENT);
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
	this.add(statusHeading);
	this.add(statusInfo1);
	this.add(statusInfo2);
    }


    /**
     * Nastaví text do pole do statusu
     * @param index index pole ve statusu (indexováno od nuly)
     * @param text zobrazený text
     */
    public void setStatus(int index, String text) {
	statusHeading.setText(rb.getString("mdw.2") + ": ");
	switch (index) {
	    case 0:
		statusInfo1.setText(text + "   |  ");
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
		statusInfo2.setIcon(ico);
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
	statusHeading.setText(rb.getString("mdw.noModel"));
    }

}
