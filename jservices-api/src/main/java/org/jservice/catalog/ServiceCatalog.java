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
package org.jservice.catalog;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jservice.registry.Service;

/**
 * Models a distributed service catalog, describing all published services.
 * 
 * @author Anatole Tresch
 */
public interface ServiceCatalog {

//	/**
//	 * Collect all available keys from the contexts of the currently registered
//	 * {@link Service} instances.
//	 * 
//	 * @return the available context keys, never {@code null}.
//	 */
//	public Set<String> getContextKeys();

	/**
	 * Access all published service from this catalog.
	 * 
	 * @return the services found.
	 */
	public Collection<Service> getServices();

	
	/**
	 * 
	 * Access all published services of a certain type, provided by the given
	 * protocol.
	 * 
	 * @param interfaceType
	 *            the required type, not {@code null}.
	 * @param protocol
	 *            the target protocols
	 * @return the services found.
	 */
	public Collection<Service> getServices(Class interfaceType);
	
	
	public void registerService(Service service);
	
	
	public <T> T getService(Class<T> interfaceType);

	/**
	 * 
	 * Access all published services of a certain type, provided by the given
	 * protocol.
	 * 
	 * @param interfaceType
	 *            the required type, not {@code null}.
	 * @param protocol
	 *            the target protocols
	 * @return the services found.
	 */
	public Collection<Service> getServices(Class interfaceType,
			String... protocols);

	/**
	 * Access all published services of a certain type, provided by the given
	 * protocol.
	 * 
	 * @param interfaceType
	 *            the required type, not {@code null}.
	 * @param pathExpression
	 *            regular expression to evaluate the services to be selected,
	 *            compared to the location path.
	 * @param protocol
	 *            the target protocols
	 * @return the services found.
	 */
	public Collection<Service> getServices(Class interfaceType,
			String pathExpression, String... protocols);

	/**
	 * Evaluate all services that match the given name expression.
	 * 
	 * @param nameExpression
	 *            the expression to be compared with the names of the currently
	 *            registered services.
	 * @return the services found.
	 */
	public Collection<Service> findServices(String nameExpression);

	/**
	 * Evaluate all services that match the given context.
	 * 
	 * @param context
	 *            the context to be compared with the names of the currently
	 *            registered services, hereby all keys must be present in a
	 *            {@link Service}'s context registered and the values must match
	 *            the regular expression.
	 * @return the services found.
	 */
	public Collection<Service> findServices(
			Map<String, String> contextExpression);

	/**
	 * Evaluate all services that match the given context.
	 * 
	 * @param type
	 *            the type of the service (fully qualified type name).
	 * @param context
	 *            the context to be compared with the names of the currently
	 *            registered services, hereby all keys must be present in a
	 *            {@link Service}'s context registered and the values must match
	 *            the regular expression.
	 * @return the services found.
	 */
	public Collection<Service> findServices(String type,
			Map<String, String> contextExpression);
//
//	/**
//	 * Evaluate all {@link Service} instances, with the given protocol.
//	 * 
//	 * @param protocol
//	 *            The target protocol
//	 * @return the services found, never {@code null}.
//	 */
//	public Collection<Service> findServicesByProtocol(String protocol);

	/**
	 * Access the available protocols for a given type.
	 * 
	 * @param type
	 *            The service type (fully qualified type name).
	 * @return the supported protocols of the {@link Service} instances of the
	 *         given type.
	 */
	public Set<String> getProtocols(String type);

	/**
	 * Access the available protocols for a given type.
	 * 
	 * @param type
	 *            The service type (fully qualified type name).
	 * @param name
	 *            The service name expression.
	 * @return the supported protocols of the {@link Service} instances of the
	 *         given type.
	 */
	public Set<String> getProtocols(String type, String name);

}
