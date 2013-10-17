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
package org.jservice.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jservice.catalog.ServiceCatalog;
import org.jservice.locator.ServiceResolver;

/**
 * This class models an abstract and serializable service descriptor, which are
 * distributed within a {@link ServiceCatalog}.
 * 
 * @author Anatole Tresch
 */
public final class Service {
	/** The service protocol, e.g. rmi, rest, soap, corba, http etc. */
	private String protocol;
	/** The host or cluster ip address. */
	private String host;
	/**
	 * The location identifier, to be interpreted by the {@link ServiceResolver}
	 * to locate the service on the given host.
	 */
	private String location;
	/** The exposed interfaces. */
	private Set<String> interfaces;
	/** The context, used for subselecting services. */
	private Map<String, String> context = new HashMap<String, String>();;

	/**
	 * Constructor, use the {@link Builder} for creating new {@link Service}
	 * instances.
	 * 
	 * @param protocol
	 *            The protocl
	 * @param host
	 *            The host
	 * @param location
	 *            The location identifier
	 * @param context
	 *            The context
	 * @param interfaces
	 *            The exposed interfaces, not {@code null} or empty.
	 */
	private Service(String protocol, String host, String location,
			Map<String, String> context, Class... interfaces) {
		this.protocol = protocol;
		this.host = host;
		this.location = location;
		for (Class cl : interfaces) {
			this.interfaces.add(cl.getName());
		}
	}

	/**
	 * Access the service protocol, e.g. rmi, rest, soap, corba, http etc.
	 * 
	 * @return the protocol
	 */
	public final String getProtocol() {
		return protocol;
	}

	/**
	 * Access the host or cluster ip address.
	 * 
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * Access the location identifier, to be interpreted by the
	 * {@link ServiceResolver} to locate the service on the given host.
	 * 
	 * @return the location
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * Access the exposed interfaces.
	 * 
	 * @return the interfaces
	 */
	public final Set<String> getInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	/**
	 * Access the service context.
	 * 
	 * @return the context
	 */
	public final Map<String, String> getContext() {
		return Collections.unmodifiableMap(context);
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
				+ ((context == null) ? 0 : context.hashCode());
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
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
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
		return "Service [protocol=" + protocol + ", host=" + host
				+ ", location=" + location + ", interfaces=" + interfaces
				+ ", context=" + context + "]";
	}

	/**
	 * Builder to create new instances of {@link Service} using a fluent
	 * programming style.
	 * 
	 * @author Anatole Tresch
	 */
	public static final class Builder {
		/** The protocol, @see {@link Service#protocol}. */
		private String protocol;
		/** The host, @see {@link Service#host}. */
		private String host;
		/** The location, @see {@link Service#location}. */
		private String location;
		/** The exposed interfaces, @see {@link Service#interfaces}. */
		private Set<String> interfaces = new HashSet<String>();
		/** The service context, @see {@link Service#context}. */
		private Map<String, String> context = new HashMap<String, String>();

		/**
		 * Constructor.
		 * 
		 * @param protocol
		 *            the protocol to set
		 */
		public final Builder withProtocol(String protocol) {
			this.protocol = Objects.requireNonNull(protocol);
			;
			return this;
		}

		/**
		 * Sets the service host.
		 * 
		 * @param host
		 *            the host to set
		 */
		public final Builder withHost(String host) {
			this.host = Objects.requireNonNull(host);
			;
			return this;
		}

		/**
		 * Sets the service location identifier.
		 * 
		 * @param location
		 *            the location to set
		 */
		public final Builder withLocation(String location) {
			this.location = Objects.requireNonNull(location);
			return this;
		}

		/**
		 * Sets the service exposed interfaces.
		 * 
		 * @param interfaces
		 *            the interfaces to set
		 */
		public final Builder withInterfaces(Set<String> interfaces) {
			this.interfaces = Objects.requireNonNull(interfaces);
			return this;
		}

		/**
		 * @param attribtues
		 *            the attribtues to set
		 */
		public final Builder withContext(Map<String, String> context) {
			this.context = Objects.requireNonNull(context);
			return this;
		}

		/**
		 * Create a new {@link Service} instances.
		 * 
		 * @return the new {@link Service}.
		 */
		public Service build() {
			return new Service(protocol, host, location, context,
					interfaces.toArray(new Class[interfaces.size()]));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Service.Builder [protocol=" + protocol + ", host=" + host
					+ ", location=" + location + ", interfaces=" + interfaces
					+ ", context=" + context + "]";
		}

	}

}
