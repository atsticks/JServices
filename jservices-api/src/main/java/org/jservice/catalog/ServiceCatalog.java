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

/**
 * Models a distributed service catalog, describing all published services.
 * 
 * @author Anatole Tresch
 */
public interface ServiceCatalog {

	/**
	 * Access the id of the catalog.
	 * 
	 * @return the catalog id, never {@code null}.
	 */
	public String getCatalogId();

	/**
	 * Determines if the current instance is successfully a member of the
	 * service catalog with the given id.
	 * 
	 * @return true, if this instance has successfully joined a catalog, and
	 *         therefore is able to register services.
	 */
	public boolean isAvailable();

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
	 * @return the services found.
	 */
	public Collection<Service> getServices(Class interfaceType);

	/**
	 * Register a service into the catalog.
	 * 
	 * @param service
	 *            The new service, not {@code null}.
	 */
	public void registerService(Service service);

	/**
	 * Unregisters a service from the catalog.
	 * <p>
	 * Depending on the catalog implementation there might by some latency until
	 * the service is effectively removed from the cloud/cluster.
	 * 
	 * @param service
	 *            the service descriptor.
	 */
	public void unregisterService(Service service);

	/**
	 * Unregisters all service that match the given context. Hereby all
	 * attributes within the context must match, as a regular expression, e.g.
	 * 
	 * <pre>
	 * name=.*
	 * vendor=org.jservice.*
	 * </pre>
	 * 
	 * ...will remove all services from jservice (which may be a bad idea).
	 * <p>
	 * Depending on the catalog implementation there might by some latency until
	 * the service is effectively removed from the cloud/cluster.
	 * 
	 * @param context
	 *            additional context, that allows to filter the service to be
	 *            removed.
	 */
	public void unregisterServices(Map<String, String> context);

	/**
	 * Removes all service of a given type, regardless the context.
	 * 
	 * @param type
	 *            The service interface to be completely removed from the
	 *            catalog, not {@code null}.
	 */
	public void unregisterServices(Class type);

	/**
	 * Resolve a service proxy for the given target type.
	 * 
	 * @param interfaceType
	 *            the target interface type, not {@code null},
	 * @return the proxy instance, never {@code null}.
	 */
	public <T> T getService(Class<T> interfaceType);

    /**
     * Resolves the given service.
     * @param service the service descriptor.
     * @param type The service type
     * @param <T> The service type class
     * @return the resolved instance, ready for use.
     * @throws ServiceResolutionException if no such service is available.
     */
    public <T> T resolveService(Service service, Class<T> type) throws ServiceResolutionException;

	/**
	 * 
	 * Access all published services of a certain type, provided by the given
	 * protocol.
	 * 
	 * @param interfaceType
	 *            the required type, not {@code null}.
	 * @param protocols
	 *            the possible target protocols
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
	 * @param protocols
	 *            the possible target protocols
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
			Map<String, String> context);

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
			Map<String, String> context);

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

	/**
	 * Remove/disable a service from the local service catalog. This will not
	 * enable remove the service globally, also after synchronization the
	 * service will be reestablished.
	 * 
	 * @param service
	 *            the service to be locally removed.
	 */
	public void removeLocally(Service service);

	public Collection<Service> getServices(Map<String, String> context);
}
