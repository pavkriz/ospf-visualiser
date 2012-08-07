package org.hkfree.ospf.app;

import org.hkfree.ospf.gui.ospfwin.OspfWin;

/**
 * Spouštěcí třída aplikace
 * @author Jakub Menzel
 */
public class StartApp {

    public static void main(String[] args) {
	OspfWin okno = new OspfWin();
	okno.setVisible(true);
    }
}
