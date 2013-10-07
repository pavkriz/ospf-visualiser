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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A range of IP addresses that can be enumerated in sequence
 * upravil Jan Schovánek
 */
@SuppressWarnings("rawtypes")
public class IPEnumeration implements Enumeration {

    private int index = -1;
    private List<String> ips = new ArrayList<String>();


    public IPEnumeration(List<String> ips) {
	this.ips = ips;
	if (!ips.isEmpty()) {
	    index++;
	}
    }


    /**
     * returns true if the current address is less than the last address
     */
    public boolean hasMoreElements() {
	return ((ips.size() - 1) > index);
    }


    /**
     * returns the current address as a String or null and increments the address counter
     */
    public Object nextElement() {
	return ips.get(index++);
    }
}