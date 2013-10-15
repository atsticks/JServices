package org.cloudplate.locator.spi;

import org.cloudplate.locator.ServiceResolutionException;
import org.cloudplate.registry.Service;

public interface ServiceResolverSpi {

	public <T> T resolve(Service service, Class<T> targetInterface)
			throws ServiceResolutionException;

}
