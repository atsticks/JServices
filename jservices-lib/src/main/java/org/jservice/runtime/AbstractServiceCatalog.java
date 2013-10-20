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

import org.cloudplate.sample.Hello;
import org.jservice.JService;
import org.jservice.catalog.ServiceCatalog;
import org.jservice.locator.ServiceResolutionException;
import org.jservice.registry.Service;

public abstract class AbstractServiceCatalog implements ServiceCatalog {

	private Map<Class, Object> proxies = new ConcurrentHashMap<Class, Object>();

	private final Random random = new Random();

	@Override
	public abstract Collection<Service> getServices();

	@Override
	public abstract Collection<Service> getServices(Class interfaceType);

	@Override
	public abstract void registerService(Service service);

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
		Service service = null;
		while (service == null) {
			List<Service> serviceList = new ArrayList<>(services);
			try {
				return serviceList.get(random.nextInt(serviceList.size()));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return null;
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
			Collection<Service> services = getServices(interfaceType);
			Service service = selectOne(services);
			while (service != null) { // TODO count max number of service
										// resolutions...
				try {
					// return method.invoke(
					return ((Hello) JService.getResolver().resolveService(
							service,
							interfaceType)).getUUID();
					// args);
				} catch (ServiceResolutionException e) {
					e.printStackTrace();
					System.out
							.println("Service resolution exception occurred, continuing...");
				}
			}
			if (service == null) {
				throw new IllegalStateException("Service not available: "
						+ interfaceType.getName());
			}
			return null;
		}

	}
}
