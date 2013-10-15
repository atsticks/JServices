package org.cloudplate.locator.spi;

import org.cloudplate.locator.ServiceResolutionException;
import org.cloudplate.registry.Service;

public interface ServiceStub {

	public Service getService();

	public <T> T getService(Class<T> serviceType)
			throws ServiceResolutionException;

}
