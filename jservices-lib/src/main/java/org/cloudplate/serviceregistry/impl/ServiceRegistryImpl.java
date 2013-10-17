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
package org.cloudplate.serviceregistry.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.cloudplate.catalog.ServiceRegistry;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

@Singleton
public class ServiceRegistryImpl implements ServiceRegistry {

	@Inject
	private Advertiser avertiser;

	@Inject
	private Locator locator;

	public void registerService(ServiceURL service) throws Exception {
		avertiser.register(service, null);
	}

	public void unregisterService(ServiceURL service) throws Exception {
		avertiser.deregister(service);
	}

	public void unregisterServices(String host) throws Exception {
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:"), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String hostName = def.getHost();
			if (hostName != null && hostName.equals(host)) {
				avertiser.deregister(def);
			}
		}
	}

	public void unregisterServices(Class type) throws Exception {
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:"), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String typeName = def.getServiceType().getConcreteTypeName();
			if (typeName != null && typeName.equals(type.getName())) {
				avertiser.deregister(def);
			}
		}
	}

	public boolean ping() {
		return true;
	}
}
