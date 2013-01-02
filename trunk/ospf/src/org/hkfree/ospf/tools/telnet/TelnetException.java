package org.hkfree.ospf.tools.telnet;

/**
 * Výjimka pro použití v implementaci telnetu
 * @author Jan Schovánek
 */
public class TelnetException extends Exception {

    private static final long serialVersionUID = 1L;


    public TelnetException() {}


    public TelnetException(String message)
    {
	super(message);
    }
}
