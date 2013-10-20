package org.jservice;

import org.jservice.catalog.ServiceCatalog;
import org.jservice.locator.ServiceResolver;
import org.jservice.runtime.DefaultServiceResolver;
import org.jservice.runtime.HazelcastServiceCatalog;

public class JService {

	private static final ServiceCatalog serviceCatalog = new HazelcastServiceCatalog();
	private static final ServiceResolver serviceResolver = new DefaultServiceResolver();

	private JService() {
	}
	
	public static ServiceCatalog getCatalog() {
		return serviceCatalog;
	}

	public static ServiceResolver getResolver() {
		return serviceResolver;
	}
}
