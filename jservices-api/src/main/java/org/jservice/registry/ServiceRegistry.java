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
package org.jservice.registry;

import java.util.Map;

/**
 * The ServiceRegistry is the main administration interface for registering
 * additional services into a catalog. It will rarely used directly, since
 * service registrations is performed transparently by evaluating the
 * {@link ElasticService} annotation.
 * 
 * @author Anatole Tresch
 */
public interface ServiceRegistry {
	/**
	 * Access the id of the underlying catalog.
	 * 
	 * @return the catalog id, never {@code null}.
	 */
	public String getCatalogId();

	/**
	 * Registers a new service to the catalog.
	 * <p>
	 * Depending on the catalog implementation there might by some latency until
	 * the service is visible within the cloud/cluster.
	 * 
	 * @param service
	 *            the service descriptor.
	 * @param context
	 *            additional context, that allows to filter the services
	 *            required.
	 */
	public void registerService(Service service, Map<String, String> context);

	/**
	 * Unregisters a service from the catalog.
	 * <p>
	 * Depending on the catalog implementation there might by some latency until
	 * the service is effectively removed from the cloud/cluster.
	 * 
	 * @param service
	 *            the service descriptor.
	 * @param context
	 *            additional context, that allows to filter the service to be
	 *            removed.
	 */
	public void unregisterService(Service service, Map<String, String> context);

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
	 * @param service
	 *            the service descriptor.
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
	 * Determines if the current instance is successfully a member of the
	 * service catalog with the given id.
	 * 
	 * @return true, if this instance has successfully joined a catalog, and
	 *         therefore is able to register services.
	 */
	public boolean isAvailable();

}
