package org.cloudplate.registry;

import java.util.Map;


public interface ServiceRegistry {

	public void registerService(Service service, Map<String,String> context);

	public void unregisterService(Service service, Map<String,String> context);
	
	public void unregisterServices(Map<String,String> context);
	
	public void unregisterServices(Class type);

	public boolean ping();

}
 