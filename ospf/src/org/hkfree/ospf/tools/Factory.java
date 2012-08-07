package org.hkfree.ospf.tools;

import java.util.ResourceBundle;

/**
 * Tovární třída vracející instance singletonů
 * @author Jan Schovánek
 */
public class Factory {

    private static ResourceBundle rb = null;


    public static ResourceBundle getRb() {
	return rb;
    }


    public static void setRb(ResourceBundle rb) {
	Factory.rb = rb;
    }
}
