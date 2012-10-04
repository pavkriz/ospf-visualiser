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

import java.util.Map;

/**
 * performs DNS lookups avoiding idle blocking by using multiple threads
 * pro potreby OSPF visualiseru upravil Jan Schovánek
 */
public class FastReverseDNS implements Runnable {

    // the reverse DNS service to use
    private ReverseDNS dns;
    // the IP addresses to traverse
    private IPEnumeration IPList;
    private Map<String, String> names;
    // the default number of threads to use in normal use
    public static final int DEFAULT_NUM_THREADS = 60;


    /**
     * Creates a new instance that will query IP addresses from the specified
     * list on the specified ReverseDNS server.
     * @param names
     */
    public FastReverseDNS(ReverseDNS dns, IPEnumeration IPList, Map<String, String> names) {
	this.dns = dns;
	this.IPList = IPList;
	this.names = names;
    }


    /**
     * While IPs are left in the range, query the reverse server and output the
     * hostname
     */
    public void run() {
	boolean moreIPs = true;
	while (moreIPs) {
	    String currentIP;
	    // attempt to get a new unique IP from the list
	    synchronized (IPList) {
		if (IPList.hasMoreElements()) {
		    currentIP = (String) IPList.nextElement();
		} else {
		    moreIPs = false;
		    break;
		}
	    }
	    // get the hostname for this IP
	    String hostname = dns.doReverseLookup(currentIP);
	    // don't display hostnames that are null
	    if (hostname != null) { // display this hostname to system.out
		synchronized (names) {
		    names.put(currentIP, hostname);
		}
	    }
	}
    }
}