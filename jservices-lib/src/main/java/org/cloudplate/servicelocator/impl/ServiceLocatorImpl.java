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

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.naming.ServiceUnavailableException;

import org.apache.log4j.Logger;
import org.cloudplate.catalog.ServiceCatalog;
import org.cloudplate.locator.ServiceLocator;
import org.cloudplate.locator.spi.ServiceResolver;
import org.cloudplate.locator.spi.ServiceStub;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * topic: newService,removedService,newInstance,removedInstance store: servicetype:serviceID ->
 * ServiceURLs instances: instances
 * 
 * @author Anatole
 * 
 */
@Singleton
public class ServiceLocatorImpl implements ServiceLocator {

	private static final Logger LOGGER = Logger.getLogger(ServiceLocatorImpl.class);
	private static final int MAX_TRIES = 10;
	private Map<String, ServiceResolver> resolvers = new ConcurrentHashMap<String, ServiceResolver>();

	@Inject
	private ServiceCatalog catalog;

	private Random random = new Random();

	public ServiceLocatorImpl() {
		registerServiceResolver(new RMIServiceResolver(), "rmi");
	}

	private ServiceStub adaptService(ServiceURL serviceURL) {
		ServiceResolver resolver = resolvers.get(serviceURL.getProtocol());
		if (resolver == null) {
			throw new IllegalArgumentException("Unsupported protocol(register a corresponding resolver): " + serviceURL.getProtocol());
		}
		return new StubImpl(serviceURL, resolver);
	}

	public ServiceResolver[] getServiceResolvers() {
		return resolvers.values().toArray(new ServiceResolver[resolvers.size()]);
	}

	public ServiceResolver getServiceResolver(String protocol) {
		return resolvers.get(protocol);
	}

	public void registerServiceResolver(ServiceResolver resolver, String protocol) {
		if (resolver == null || protocol == null) {
			throw new IllegalArgumentException("Resolver and/or protocol are missing.");
		}
		if (resolvers.containsKey(protocol)) {
			throw new IllegalArgumentException("Resolver for protocol '" + protocol + "' already registered.");
		}
		this.resolvers.put(protocol, resolver);
	}

	public ServiceStub getServiceAdapter(ServiceURL serviceURL) {
		ServiceStub
		// provider = cachedAdapters.get(serviceURL);
		// if (provider == null) {
		provider = adaptService(serviceURL);
		// cachedAdapters.put(serviceURL, provider);
		// }
		return provider;
	}

	public <T> T getService(ServiceURL serviceURL, Class<T> target) throws ServiceUnavailableException {
		ServiceStub adapter = adaptService(serviceURL);
		if (adapter != null) {
			return (T) adapter.getService(target);
		}
		throw new ServiceUnavailableException("Failed to adapt service: " + serviceURL);
	}

	public ServiceURL[] getServiceDefinitions() throws ServiceUnavailableException {
		try {
			return this.catalog.getServices();
		} catch (ServiceLocationException e) {
			LOGGER.error("Error accessing service definitions.", e);
			throw new ServiceUnavailableException(e.getMessage());
		}
	}

	public <T> T getService(Class<T> type, String protocol) throws ServiceUnavailableException {
		ServiceURL[] services;
		try {
			services = this.catalog.getServicesByType(type, "rmi");
		} catch (ServiceLocationException e1) {
			LOGGER.error("Error localizing service: " + type.getName(), e1);
			throw new ServiceUnavailableException(e1.getMessage());
		}
		if (services != null && services.length > 0) {
			if (services.length == 1) {
				try {
					return getService(services[0], type);
				} catch (ServiceUnavailableException e) {
					e.printStackTrace();
				}
			} else {
				for (int i = 0; i < MAX_TRIES; i++) {
					int index = random.nextInt(services.length);
					try {
						return getService(services[index], type);
					} catch (ServiceUnavailableException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

}
