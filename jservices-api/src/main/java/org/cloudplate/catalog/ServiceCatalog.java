package org.cloudplate.catalog;

import java.util.Collection;
import java.util.Set;

import org.cloudplate.registry.Service;

public interface ServiceCatalog {

	public Set<String> getAttributeKeys();

	public Collection<Service> getServices();

	public Collection<Service> getServices(Class interfaceType);

	public Collection<Service> getServices(Class interfaceType,
			String protocol);

	public Collection<Service> getServices(Class interfaceType,
			String pathExpression, String protocol);

	public Collection<Service> findServices(String namePath);

	public Collection<Service> findServices(String namePath,
			String protocol);

	public Collection<Service> findServicesByProtocol(String protocol);

	public Set<String> getProtocols(String type, String name);

}
