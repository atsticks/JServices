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

import javax.naming.ServiceUnavailableException;

import org.cloudplate.locator.spi.ServiceResolver;
import org.cloudplate.locator.spi.ServiceStub;

import ch.ethz.iks.slp.ServiceURL;

public final class StubImpl implements ServiceStub, Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1470717032967377999L;

	private ServiceURL service;
	private ServiceResolver resolver;

	public StubImpl(ServiceURL service, ServiceResolver resolver) {
		this.service = service;
		this.resolver = resolver;
	}

	public ServiceURL getServiceURL() {
		return this.service;
	}

	public ServiceResolver getServiceResolver() {
		return this.resolver;
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
		result = prime * result + ((service == null) ? 0 : service.hashCode());
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
		StubImpl other = (StubImpl) obj;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
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
		return "ServiceRecord [service=" + service + "]";
	}

	public <T> T getService(Class<T> serviceType) throws ServiceUnavailableException {
		return this.resolver.resolve(getServiceURL(), serviceType);
	}

}
