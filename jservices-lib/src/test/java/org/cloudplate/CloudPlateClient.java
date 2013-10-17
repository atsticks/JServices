package org.cloudplate;

import org.cloudplate.catalog.ServiceCatalog;
import org.cloudplate.runtime.Container;
import org.cloudplate.server.RMIRegistrator;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

public class CloudPlateClient {

	public static void main(String[] args) {
		try {
			Container.start();
			org.cloudplate.server.RMIRegistrator reg = Container.getInstance(RMIRegistrator.class);
			reg.registerServices();
			ServiceCatalog catalog = Container.getInstance(ServiceCatalog.class);
			while (true) {
				org.cloudplate.locator.ServiceLocator locator = Container
						.getInstance(org.cloudplate.locator.ServiceLocator.class);
				try {
					printServices(catalog);
					org.cloudplate.sample.Hello hello = locator.getService(org.cloudplate.sample.Hello.class, "rmi");
					if (hello != null) {
						System.out.println("Hello called on: " + hello.getHostName());
					} else {
						System.out.println("Failed to resolve Hello.");
					}
					Thread.sleep(10000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			throw new IllegalStateException("Startup failed.", e1);
		}
	}

	private static void printServices(ServiceCatalog catalog) throws ServiceLocationException {
		ServiceURL[] services;

		services = catalog.getServices();

		StringBuilder servicesString = new StringBuilder();
		servicesString.append("Services:\n\n");
		for (ServiceURL i : services) {
			servicesString.append(i);
			servicesString.append('\n');
		}
		System.out.println(servicesString);
	}

}
