package org.cloudplate.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Service {
	private String protocol;
	private String host;
	private String location;
	private Set<String> interfaces;
	private Map<String, String> attributes = new HashMap<String, String>();;

	private Service(String protocol, String host, String location,
			Map<String, String> attributes, Class... interfaces) {
		this.protocol = protocol;
		this.host = host;
		this.location = location;
		for (Class cl : interfaces) {
			this.interfaces.add(cl.getName());
		}
	}

	/**
	 * @return the protocol
	 */
	public final String getProtocol() {
		return protocol;
	}

	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @return the location
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * @return the interfaces
	 */
	public final Set<String> getInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	/**
	 * @return the attribtues
	 */
	public final Map<String, String> getAttribtues() {
		return Collections.unmodifiableMap(attributes);
	}

	public static final class Builder {

		private String protocol;
		private String host;
		private String location;
		private Set<String> interfaces = new HashSet<String>();;
		private Map<String, String> attributes = new HashMap<String, String>();

		/**
		 * @param protocol
		 *            the protocol to set
		 */
		public final Builder withProtocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		/**
		 * @param host
		 *            the host to set
		 */
		public final Builder withHost(String host) {
			this.host = host;
			return this;
		}

		/**
		 * @param location
		 *            the location to set
		 */
		public final Builder withLocation(String location) {
			this.location = location;
			return this;
		}

		/**
		 * @param interfaces
		 *            the interfaces to set
		 */
		public final Builder withInterfaces(Set<String> interfaces) {
			this.interfaces = interfaces;
			return this;
		}

		/**
		 * @param attribtues
		 *            the attribtues to set
		 */
		public final Builder withAttribtues(Map<String, String> attributes) {
			this.attributes = attributes;
			return this;
		}

		public Service build() {
			return new Service(protocol, host, location, attributes,
					interfaces.toArray(new Class[interfaces.size()]));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((attributes == null) ? 0 : attributes.hashCode());
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result
					+ ((interfaces == null) ? 0 : interfaces.hashCode());
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			result = prime * result
					+ ((protocol == null) ? 0 : protocol.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
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
			Builder other = (Builder) obj;
			if (attributes == null) {
				if (other.attributes != null)
					return false;
			} else if (!attributes.equals(other.attributes))
				return false;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (interfaces == null) {
				if (other.interfaces != null)
					return false;
			} else if (!interfaces.equals(other.interfaces))
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			if (protocol == null) {
				if (other.protocol != null)
					return false;
			} else if (!protocol.equals(other.protocol))
				return false;
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Builder [protocol=" + protocol + ", host=" + host
					+ ", location=" + location + ", interfaces=" + interfaces
					+ ", attributes=" + attributes + "]";
		}

	}

}
