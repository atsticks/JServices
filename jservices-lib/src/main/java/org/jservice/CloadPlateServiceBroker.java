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
