/*
 * Copyright (c) 2013, Anatole Tresch.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: Anatole Tresch - initial implementation.
 */
package org.cloudplate.servicelocator.impl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

public final class Instance implements Serializable {

	private static final long serialVersionUID = -3642836370046566204L;
	private String host;
	private String address;
	private Properties properties = System.getProperties();
	private String[] endPoints;
	private long started = System.currentTimeMillis();
	private long created = System.currentTimeMillis();
	private long ttl;

	public Instance(String host, String address, int ttl) {
		if (host == null) {
			throw new IllegalArgumentException("host is required.");
		}
		if (address == null) {
			throw new IllegalArgumentException("address is required.");
		}
		if (ttl <= 0) {
			throw new IllegalArgumentException("ttl must be > 0.");
		}
		this.host = host;
		this.ttl = ttl * 1000L;
		this.address = address;
	}

	public static Instance create(int ttl) throws UnknownHostException {
		InetAddress adr = InetAddress.getLocalHost();
		return new Instance(adr.getHostName(), adr.getHostAddress(), ttl);
	}

	public static Instance create() throws UnknownHostException {
		InetAddress adr = InetAddress.getLocalHost();
		return new Instance(adr.getHostName(), adr.getHostAddress(), 60);
	}

	/**
	 * @return the endPoints
	 */
	public final String[] getEndPoints() {
		return endPoints;
	}

	/**
	 * @param endPoints
	 *            the endPoints to set
	 */
	public final void setEndPoints(String[] endPoints) {
		this.endPoints = endPoints;
	}

	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @return the address
	 */
	public final String getAddress() {
		return address;
	}

	/**
	 * @return the properties
	 */
	public final Properties getProperties() {
		return properties;
	}

	/**
	 * @return the started
	 */
	public final long getStarted() {
		return started;
	}

	/**
	 * @return the ttl
	 */
	public final long getTtl() {
		return ttl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Instance [host=" + host + ", address=" + address + ", started="
				+ started + ", properties=" + properties + ", endPoints="
				+ Arrays.toString(endPoints) + ", created=" + created
				+ ", ttl=" + ttl + "]";
	}

	public void refresh() {
		this.created = System.currentTimeMillis();
	}
	
	public boolean isValid() {
		return (System.currentTimeMillis() - created) <= ttl;
	}

	
}
