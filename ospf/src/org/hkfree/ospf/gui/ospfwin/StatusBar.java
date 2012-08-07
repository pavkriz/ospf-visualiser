package org.hkfree.ospf.gui.ospfwin;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.tools.Factory;

public class StatusBar extends JPanel {

    private static final long serialVersionUID = 1L;
    private ResourceBundle rb = Factory.getRb();
    private JLabel statusInfo1 = new JLabel();
    private JLabel statusInfo2 = new JLabel();
    private JPanel status1 = null;	//pro tooltip
    private JPanel status2 = null;	//pro tooltip
    private JLabel lIcon = null;
    private JLabel statusHeading = null;
    

    public StatusBar() {
	super();
	this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	this.setBorder(new BevelBorder(BevelBorder.LOWERED));
	this.setAlignmentY(CENTER_ALIGNMENT);
	this.setPreferredSize(new Dimension(100, 30));
	
	lIcon = new JLabel(new ImageIcon(getClass().getResource(Constants.URL_IMG_GUI + "help.png")));
	JPanel statusH = new JPanel();
	statusHeading = new JLabel();
	statusHeading.setText(rb.getString("mdw.noModel"));
	statusH.add(statusHeading);
	status1 = new JPanel();
	status1.add(statusInfo1);
	status2 = new JPanel();
	status2.addMouseListener(new MouseAdapter() {

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
	status2.add(statusInfo2);
	status2.add(lIcon);
	lIcon.setVisible(false);
	this.add(statusH);
	this.add(status1);
	this.add(status2);
    }
    
    
    /**
     * Nastaví text do pole do statusu
     * @param index index pole ve statusu (indexováno od nuly)
     * @param text zobrazený text
     */
    public void setStatus(int index, String text) {
	statusHeading.setText(rb.getString("mdw.2") + ":");
	switch (index) {
	    case 0:
		statusInfo1.setText(text);
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
		status1.setToolTipText(text);
		break;
	    case 1:
		status2.setToolTipText(text);
		if (!status2.isVisible()) {
		    lIcon.setVisible(true);
		}
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
	statusInfo2.setText("");
	status1.setToolTipText("");
	status2.setToolTipText("");
	statusHeading.setText(rb.getString("mdw.noModel"));
	setVisibleIcon(false);
    }
    
    /**
     * Nastaví viditelnost ikony
     */
    public void setVisibleIcon(boolean b) {
	lIcon.setVisible(b);
    }
}
