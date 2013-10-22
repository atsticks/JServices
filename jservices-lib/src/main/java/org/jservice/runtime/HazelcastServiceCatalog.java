package org.jservice.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.jservice.catalog.Service;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ILock;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

public class HazelcastServiceCatalog extends AbstractServiceCatalog {
	private String catalogId;
	private HazelcastInstance data;
	private List<Service> allServices = Collections
			.synchronizedList(new ArrayList<Service>());
	private List<Service> locallyRegisteredServices = Collections
			.synchronizedList(new ArrayList<Service>());
	private Map<String, List<Service>> typedServices = new ConcurrentHashMap<String, List<Service>>();
	private Timer timer = new Timer("Service Updater", true);
	private static final String DEFAULT_CATALOG_ID = "default";

	public HazelcastServiceCatalog() {
		this(DEFAULT_CATALOG_ID);
	}

	public HazelcastServiceCatalog(String catalogId) {
		Objects.requireNonNull(catalogId);
		this.catalogId = catalogId;
		log.info("Initializing Hazelcast service catalog...");
		Config cfg = new Config();
		data = Hazelcast.newHazelcastInstance(cfg);
		synch();
		ISet<Service> services = data.getSet("<allInterfaces>");
		services.addItemListener(new ServiceChangeListener(), true);
		// Removing expired remote services...
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ILock lock = data.getLock("Expiry-Updater");
				lock.lock();
				try {
					IAtomicLong lastRun = data.getAtomicLong("Last-Expiry-Run");
					if ((lastRun.get() + 20000L) > System.currentTimeMillis()) {
						return;
					}
					List<Service> expiredServices = new ArrayList<Service>();
					synchronized (allServices) {
						for (Service s : allServices) {
							if (s.isExpired()) {
								expiredServices.add(s);
							}
						}
					}
					ISet<Service> services = data.getSet("<allInterfaces>");
					for (Service s : expiredServices) {
						services.remove(s);
					}
				}
				finally {
					lock.unlock();
				}
			}
		}, 20000L, 20000L);
		// Registering local services...
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Set<Service> distributedServices = data
						.getSet("<allInterfaces>");
				for (Service service : locallyRegisteredServices) {
					if (service.IsExpiredWithin(100L)) {
						service.updateExpiry();
						distributedServices.add(service);
					}
				}
			}
		}, 0L, 10000L);
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
			log.info("interrupt ignored.");
		}
	}

	public void synch() {
		log.info("Synching service catalog...");
		ISet<Service> services = data.getSet("<allInterfaces>");
		synchronized (allServices) {
			allServices.clear();
			allServices.addAll(services);
			this.typedServices.clear();
			for (Service service : this.allServices) {
				for (String type : service.getInterfaces()) {
					List<Service> typedList = typedServices.get(type);
					if (typedList == null) {
						typedList = new ArrayList<>();
						this.typedServices.put(type, typedList);
					}
					synchronized (typedList) {
						typedList.add(service);
					}
				}
			}
		}
	}

	@Override
	protected void removeService(Service service) {
		removeLocally(service);
		ISet<Service> services = data.getSet("<allInterfaces>");
		services.remove(service);
	}

	@Override
	public void removeLocally(Service service) {
		log.error("Disabling removed service: " + service + "...");
		synchronized (this.allServices) {
			this.allServices.remove(service);
		}
		for (String type : service.getInterfaces()) {
			List<Service> typedList = typedServices.get(type);
			if (typedList != null) {
				typedList.remove(service);
			}
		}
	}

	@Override
	public Collection<Service> getServices() {
		return allServices;
	}

	@Override
	public Collection<Service> getServices(Class interfaceType) {
		Collection<Service> services = typedServices.get(interfaceType
				.getName());
		if (services == null) {
			return Collections.emptySet();
		}
		return services;
	}
	
	@Override
	public Collection<Service> getServices(Map<String,String> context) {
		List<Service> services = new ArrayList<>();
		for (Service service : this.allServices) {
			for (String type : service.getInterfaces()) {
				List<Service> typedList = typedServices.get(type);
				if (typedList == null) {
					typedList = new ArrayList<>();
					this.typedServices.put(type, typedList);
				}
				synchronized (typedList) {
					typedList.add(service);
				}
			}
		}
		return services;
	}

	@Override
	public void registerService(Service service) {
		log.info("Registering new local service: " + service + "...");
		locallyRegisteredServices.add(service);
		addLocally(service);
	}

	private void handeServiceAddedEvent(Service service) {
		addLocally(service);
	}

	@Override
	public String getCatalogId() {
		return catalogId;
	}

	@Override
	public boolean isAvailable() {
		return data != null && data.getLifecycleService().isRunning();
	}

	@Override
	public void unregisterService(Service service) {
		removeService(service);
	}

	@Override
	public void unregisterServices(Map<String, String> context) {
		Collection<Service> services = getServices(context);
		for (Service s : services) {
			removeService(s);
		}
	}

	@Override
	public void unregisterServices(Class type) {
		Collection<Service> services = getServices(type);
		for (Service s : services) {
			removeService(s);
		}
	}

	public void addLocally(Service service) {
		log.debug("Adding service locally: " + service + "...");
		synchronized (this.allServices) {
			if (!this.allServices.contains(service)) {
				this.allServices.add(service);
			}
		}
		for (String type : service.getInterfaces()) {
			List<Service> concreteServices = this.typedServices.get(type);
			if (concreteServices == null) {
				synchronized (allServices) {
					concreteServices = this.typedServices.get(type);
					if (concreteServices == null) {
						concreteServices = Collections
								.synchronizedList(new ArrayList<Service>());
						this.typedServices.put(type, concreteServices);
					}
				}
			}
			synchronized (concreteServices) {
				if (!concreteServices.contains(service)) {
					concreteServices.add(service);
				}
			}
		}
	}

	private void handeServiceRemovedEvent(Service service) {
		removeLocally(service);
	}

	private final class ServiceChangeListener implements ItemListener<Service> {

		@Override
		public void itemAdded(ItemEvent<Service> evt) {
			handeServiceAddedEvent(evt.getItem());
		}

		@Override
		public void itemRemoved(ItemEvent<Service> evt) {
			handeServiceRemovedEvent(evt.getItem());
		}

	}

}
