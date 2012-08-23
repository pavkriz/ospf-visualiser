/**
 * FastReverseDNS batch reverse DNS lookup
 * Copyright (C) 2002 Jesse Wilson <jesse@swank.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.hkfree.ospf.tools.rdns;

import java.net.InetAddress;
import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Performs reverse DNS queries using JNDI and a DNS server instead
 * of Java API calls. This provides reverse lookup behaviour similar
 * to nslookup.exe on Windows 2000 systems.
 */
public class ReverseDNS {

    /** The DNS server to perform reverse queries on */
    public String dnsServer;


    /**
     * Creates a new ReverseDNS 'server' which uses the specified host DNS server
     * to perform queries
     */
    public ReverseDNS(String dnsServer) {
	this.dnsServer = dnsServer;
    }


    /**
     * Uses JNDI and DNS service provider for DNS reverse lookup, borrowing
     * from code made available in Dns2000 at http://home.istar.ca/~neutron/dns2000/
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String doReverseLookup(String ip) {
	String hostname = "";
	try { // get a proper inet address from the specified ip
	    InetAddress adr = InetAddress.getByName(ip);
	    byte[] addr = adr.getAddress();
	    // build reverse IP address
	    String addrString = "";
	    for (int i = addr.length - 1; i >= 0; i--) {
		addrString += (addr[i] & 0xFF) + ".";
	    }
	    // set up jndi stuff in a hashtable
	    Hashtable env = new Hashtable();
	    env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
	    env.put("java.naming.provider.url", "dns://" + dnsServer + "/.");
	    DirContext ictx = new InitialDirContext(env);
	    Attributes attrs1 = ictx.getAttributes(addrString + "in-addr.arpa", new String[] { "PTR" });
	    Attribute attrib = attrs1.get("PTR");
	    NamingEnumeration attrvals = attrib.getAll();
	    // return null if this host has no names
	    if (!attrvals.hasMore())
		return null;
	    // return the list of host names separated by spaces
	    while (attrvals.hasMore()) {
		hostname = hostname + (String) attrvals.next() + " ";
	    }
	} catch (Exception e) {
	    return null;
	}
	return hostname;
    }
}
