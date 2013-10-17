/*
 * Copyright (c) 2013, Anatole Tresch.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: Anatole Tresch - initial implementation.
 */
package org.cloudplate.servicelocator.impl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.naming.ServiceUnavailableException;

import org.jservice.locator.ServiceResolver;
import org.jservice.registry.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RMIServiceResolver implements ServiceResolver {

	private final static Map<String, Registry> REGISTRIES = new ConcurrentHashMap<String, Registry>();

	private static final Logger LOGGER = LoggerFactory.getLogger(RMIServiceResolver.class);

	public <T> T resolve(Service serviceURI, Class<T> targetInterface) throws ServiceUnavailableException {
		String host = serviceURI.getHost();
		int port = serviceURI.getPort();
		LOGGER.debug("Locating registry on " + host + ":" + port + "  ... ");
		String key = host + ':' + port;
		Registry naming = REGISTRIES.get(key);
		try {
			if (naming == null) {
				naming = LocateRegistry.getRegistry(host, port);
				REGISTRIES.put(key, naming);
			}
			String[] pathSplitted = serviceURI.getURLPath().split("#");
			boolean compatible = false;
			if(pathSplitted.length>1){
				String[] interfaces = extractInterfaces(pathSplitted[1]);
				compatible = false;
				for(String interfaceName:interfaces){
					if(interfaceName.equals(targetInterface.getName())){
						compatible = true;
						break;
					}
				}
			}
			if(!compatible){
				throw new ServiceUnavailableException("Target interface not declared by service: " + targetInterface);
			}
			LOGGER.debug("Looking up name " + pathSplitted[0] + " ... ");
			return (T) naming.lookup(pathSplitted[0]);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Failed to locate service: " + serviceURI, e);
			throw new ServiceUnavailableException("Failed to locate service: " + serviceURI);
		}
	}
	
	private String[] extractInterfaces(String fragment) {
		if (fragment != null) {
			return fragment.split(",");
		}
		return new String[0];
	}

}
