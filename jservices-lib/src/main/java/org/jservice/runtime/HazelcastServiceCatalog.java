package org.jservice.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jservice.locator.ServiceResolutionException;
import org.jservice.locator.ServiceResolver;
import org.jservice.registry.Service;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastServiceCatalog extends AbstractServiceCatalog {

	private HazelcastInstance data;

	public HazelcastServiceCatalog() {
		Config cfg = new Config();
		data = Hazelcast.newHazelcastInstance(cfg);
	}

	@Override
	public Collection<Service> getServices() {
		return data.getSet("<allInterfaces>");
	}

	@Override
	public Collection<Service> getServices(Class interfaceType) {
		return data.getSet(interfaceType.getName());
	}

	@Override
	public void registerService(Service service) {
		Set<Service> allServices = data.getSet("<allInterfaces>");
		allServices.add(service);
		for (String type : service.getInterfaces()) {
			data.getSet(type).add(service);
		}
	}

}
