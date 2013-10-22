package org.jservice.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jservice.JService;
import org.jservice.catalog.Service;
import org.jservice.catalog.ServiceCatalog;
import org.jservice.catalog.ServiceResolutionException;
import org.jservice.catalog.spi.ServiceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServiceCatalog implements ServiceCatalog {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private Map<Class, Object> proxies = new ConcurrentHashMap<Class, Object>();

	private final Random random = new Random();

	private Map<String, ServiceResolverSpi> resolverSpis = new ConcurrentHashMap<String, ServiceResolverSpi>();

	protected abstract void removeService(Service service);

	@Override
	public abstract Collection<Service> getServices();

	@Override
	public abstract Collection<Service> getServices(Class interfaceType);

	@Override
	public abstract void registerService(Service service);

	public AbstractServiceCatalog() {
		// TODO Use loading mechanism, eg ServiceLoader
		resolverSpis.put("rmi", new RmiResolverSpi());
	}

	@Override
	public Collection<Service> getServices(Class type,
			String... protocols) {
		List<Service> result = new ArrayList<>();
		for (Service s : getServices()) {
			if (s.isImplementing(type.getName())) {
				for (String p : protocols) {
					if (s.getProtocol().equals(p)) {
						result.add(s);
						break;
					}
				}
			}
		}
		return result;
	}

	@Override
	public Collection<Service> getServices(Class type,
			String pathExpression, String... protocols) {
		List<Service> result = new ArrayList<>();
		for (Service s : getServices()) {
			if (s.isImplementing(type.getName())
					&& s.getLocation().matches(pathExpression)) {
				for (String p : protocols) {
					if (s.getProtocol().equals(p)) {
						result.add(s);
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Evaluate all services that match the given name expression.
	 * 
	 * @param nameExpression
	 *            the expression to be compared with the names of the currently
	 *            registered services.
	 * @return the services found.
	 */
	public Collection<Service> findServices(String nameExpression) {
		List<Service> result = new ArrayList<>();
		for (Service s : getServices()) {
			if (s.isImplementationMatching(nameExpression)) {
				result.add(s);
			}
		}
		return result;
	}

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
			Map<String, String> contextExpressions) {
		List<Service> result = new ArrayList<>();
		for (Service s : getServices()) {
			if (s.isMatchingContext(contextExpressions)) {
				result.add(s);
			}
		}
		return result;
	}

	@Override
	public Collection<Service> findServices(String type,
			Map<String, String> contextExpression) {
		List<Service> result = new ArrayList<>();
		for (Service s : getServices()) {
			if (s.isImplementing(type)
					&& s.isMatchingContext(contextExpression)) {
				result.add(s);
			}
		}
		return result;
	}

	@Override
	public Set<String> getProtocols(String type) {
		Set<String> result = new HashSet<>();
		for (Service s : getServices()) {
			if (s.isImplementing(type)) {
				result.add(s.getProtocol());
			}
		}
		return result;
	}

	@Override
	public Set<String> getProtocols(String type, String name) {
		Set<String> result = new HashSet<>();
		for (Service s : getServices()) {
			if (s.isImplementing(type) && s.getLocation().matches(name)) {
				result.add(s.getProtocol());
			}
		}
		return result;
	}

	private Service selectOne(Collection<Service> services) {
		List<Service> serviceList = new ArrayList<>(services);
		try {
			if (serviceList.isEmpty()) {
				log.warn("No service available.");
				return null;
			}
			return serviceList.get(random.nextInt(serviceList.size()));
		} catch (Exception e) {
			log.error("Failed to select service.", e);
			return null;
		}
	}

	@Override
	public <T> T getService(Class<T> interfaceType) {
		T proxy = (T) this.proxies.get(interfaceType);
		if (proxy == null) {
			proxy = createProxy(interfaceType);
			this.proxies.put(interfaceType, proxy);
		}
		return proxy;
	}

	private <T> T createProxy(Class<T> type) {
		log.debug("Creating service proxy for: " + type.getName() + "...");
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { type, Remote.class },
				new DefaultInvocationHandler(type));
	}

	private final class DefaultInvocationHandler implements
			InvocationHandler {

		private Class interfaceType;

		public DefaultInvocationHandler(Class interfaceType) {
			this.interfaceType = interfaceType;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			final int MAX = 10;
			for (int i = 0; i < MAX; i++) {
				log.debug("Invoking method " + method + " on "
						+ interfaceType.getName() + "(loop: " + (i + 1)
						+ " of " + MAX + ")...");
				Collection<Service> services = getServices(interfaceType);
				Service service = selectOne(services);
				if (service != null) {
					try {
						return method.invoke(
								resolveService(
										service,
										interfaceType),
								args);
					} catch (ServiceResolutionException e) {
						log.debug("Resolution of service " + service
								+ " failed, removing dervice...");
						removeService(service);
					}
				}
				try {
					Thread.sleep(20L);
				} catch (InterruptedException iex) {
					log.warn("Wait interupted, ignoring...");
				}
			}
			throw new IllegalStateException("Service not available: "
					+ interfaceType.getName());
		}
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
