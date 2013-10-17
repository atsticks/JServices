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
package org.jservice;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.jservice.catalog.ServiceCatalog;
import org.jservice.registry.Service;
import org.jservice.registry.ServiceRegistry;
import org.jservice.runtime.Container;

public class CloadPlateServiceBroker {

	private static final Logger LOGGER = Logger
			.getLogger(CloadPlateServiceBroker.class.getName());

	public static void main(String[] args) {
		Container.start();
		LOGGER.info("RMI based CloudPlateServiceBroker started.");
		ServiceCatalog loc = Container.getInstance(ServiceCatalog.class);
		ServiceRegistry reg = Container.getInstance(ServiceRegistry.class);
		while (true) {
			try {
				Collection<Service> services = loc.getServices();
				if (services.size() == 0) {
					System.out.println(new Date() + ": no services.");
				}
				else {
					StringBuilder servicesString = new StringBuilder();
					servicesString.append(new Date() + " - Services:\n\n");
					for (Service i : services) {
						servicesString.append(i);
						servicesString.append('\n');
					}
					System.out.println(servicesString);
				}
				System.out
						.println("-------------------------------------------------------------");
				Thread.sleep(10000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
