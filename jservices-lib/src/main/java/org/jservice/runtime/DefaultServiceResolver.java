package org.jservice.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jservice.locator.ServiceResolutionException;
import org.jservice.locator.ServiceResolver;
import org.jservice.locator.spi.ServiceResolverSpi;
import org.jservice.registry.Service;

public class DefaultServiceResolver implements ServiceResolver {

	private Map<String, ServiceResolverSpi> resolverSpis = new ConcurrentHashMap<String, ServiceResolverSpi>();

	public DefaultServiceResolver() {
		// TODO Use loading mechanism, eg ServiceLoader
		resolverSpis.put("rmi", new RmiResolverSpi());
	}

	@Override
	public <T> T resolveService(Service service, Class<T> target)
			throws ServiceResolutionException {
		ServiceResolverSpi spi = resolverSpis.get(service.getProtocol());
		if (spi != null) {
			return spi.resolve(service, target);
		}
		throw new ServiceResolutionException(service);
	}

}
