package org.cloudplate.locator;

import javax.naming.ServiceUnavailableException;

import org.cloudplate.registry.Service;

public interface ServiceResolver {

	public <T> T resolveService(Service service, Class<T> target)
			throws ServiceResolutionException;

	public <T> T resolveService(Class<T> type, String protocol)
			throws ServiceResolutionException;

}
