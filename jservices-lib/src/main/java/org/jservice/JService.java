package org.jservice;

import org.jservice.catalog.ServiceCatalog;
import org.jservice.runtime.HazelcastServiceCatalog;

public class JService {

	private static final ServiceCatalog serviceCatalog = new HazelcastServiceCatalog();

	private JService() {
	}
	
	public static ServiceCatalog getCatalog() {
		return serviceCatalog;
	}

}
