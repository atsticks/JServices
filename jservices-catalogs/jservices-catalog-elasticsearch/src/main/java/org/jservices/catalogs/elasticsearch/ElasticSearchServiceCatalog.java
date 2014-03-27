package org.jservices.catalogs.elasticsearch;

import org.jservice.catalog.AbstractServiceCatalog;
import org.jservice.catalog.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ElasticSearchServiceCatalog extends AbstractServiceCatalog{
	private String catalogId;

	private List<Service> allServices = Collections
			.synchronizedList(new ArrayList<Service>());
	private List<Service> locallyRegisteredServices = Collections
			.synchronizedList(new ArrayList<Service>());
	private Map<String, List<Service>> typedServices = new ConcurrentHashMap<String, List<Service>>();
	private Timer timer = new Timer("Service Updater", true);
	private static final String DEFAULT_CATALOG_ID = "default";

	public ElasticSearchServiceCatalog() {
		this(DEFAULT_CATALOG_ID);
	}

	public ElasticSearchServiceCatalog(String catalogId) {
		Objects.requireNonNull(catalogId);
		this.catalogId = catalogId;
		log.info("Initializing ElasticSearch service catalog...");

		// Registering local services...
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				Set<Service> distributedServices = data
//						.getSet("<allInterfaces>");
//				for (Service service : locallyRegisteredServices) {
//					if (service.IsExpiredWithin(100L)) {
//						service.setExpiry(100000L);
//						distributedServices.add(service);
//					}
//				}
//			}
//		}, 0L, 10000L);
//		try {
//			Thread.sleep(100L);
//		} catch (InterruptedException e) {
//			log.info("interrupt ignored.");
//		}
	}

	public void synch() {
//		log.info("Synching service catalog...");
//		ISet<Service> services = data.getSet("<allInterfaces>");
//		synchronized (allServices) {
//			allServices.clear();
//			allServices.addAll(services);
//			this.typedServices.clear();
//			for (Service service : this.allServices) {
//				for (String type : service.getInterfaces()) {
//					List<Service> typedList = typedServices.get(type);
//					if (typedList == null) {
//						typedList = new ArrayList<>();
//						this.typedServices.put(type, typedList);
//					}
//					synchronized (typedList) {
//						typedList.add(service);
//					}
//				}
//			}
//		}
	}

	@Override
	protected void removeService(Service service) {
//		removeLocally(service);
//		ISet<Service> services = data.getSet("<allInterfaces>");
//		services.remove(service);
	}

	@Override
	public void removeLocally(Service service) {
//		log.error("Disabling removed service: " + service + "...");
//		synchronized (this.allServices) {
//			this.allServices.remove(service);
//		}
//		for (String type : service.getInterfaces()) {
//			List<Service> typedList = typedServices.get(type);
//			if (typedList != null) {
//				typedList.remove(service);
//			}
//		}
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
		return true;
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



}
