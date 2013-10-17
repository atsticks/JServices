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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Singleton;

import org.cloudplate.servicelocator.Updating;
import org.cloudplate.serviceregistry.impl.ServiceRegistryImpl;
import org.jservice.registry.Service;
import org.slf4j.LoggerFactory;

@Singleton
@Updating
public class UpdatingServiceRegistryImpl extends ServiceRegistryImpl {

	private static final org.slf4j.Logger LOGGER = LoggerFactory
			.getLogger(UpdatingServiceRegistryImpl.class);

	private static final Timer registrationTimer = new Timer(
			"Service Registration Thread", true);

	private static final long DELAY = 2000L;

	private static final long PERIOD = 30000L;

	private Set<Service> localServices = Collections
			.synchronizedSet(new HashSet<Service>());

	public UpdatingServiceRegistryImpl() {
		Runtime.getRuntime().addShutdownHook(
				new Thread("Service Deregistration") {
					@Override
					public void run() {
						try {
							unregisterServices(System.getProperty("host.name"));
						} catch (Exception e) {
							LOGGER.error(e);
						}
					}
				});
		registrationTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateLocalServiceRegistrations();
			}
		}, DELAY, PERIOD);
	}

	protected void updateLocalServiceRegistrations() {
		synchronized (localServices) {
			for (Service service : localServices) {
				try {
					super.unregisterService(service);
					super.registerService(new Service(service.toString(), 60));
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}
	}

	public void registerService(Service service) throws Exception {
		super.registerService(service);
		synchronized (localServices) {
			if (localServices.contains(service)) {
				localServices.remove(service);
			}
			localServices.add(service);
		}
	}

	public void unregisterService(Service service) throws Exception {
		super.unregisterService(service);
		synchronized (localServices) {
			localServices.remove(service);
		}
	}

	public void unregisterServices(String host) throws Exception {
		super.unregisterServices(host);
		synchronized (localServices) {
			for (Iterator iterator = localServices.iterator(); iterator
					.hasNext();) {
				Service def = (Service) iterator.next();
				if (def.getHost().equals(host)) {
					iterator.remove();
				}
			}
		}
	}

	public void unregisterServices(Class type) throws Exception {
		super.unregisterServices(type);
		synchronized (localServices) {
			for (Iterator iterator = localServices.iterator(); iterator
					.hasNext();) {
				Service def = (Service) iterator.next();
				String typeName = def.getServiceType().getConcreteTypeName();
				if (typeName != null && typeName.equals(type.getName()))
					iterator.remove();
			}
		}
	}

}
