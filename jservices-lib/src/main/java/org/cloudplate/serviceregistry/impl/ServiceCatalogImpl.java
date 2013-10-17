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
package org.cloudplate.serviceregistry.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.cloudplate.catalog.ServiceCatalog;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

/**
 * topic: newService,removedService,newInstance,removedInstance store: servicetype:serviceID ->
 * ServiceURLs instances: instances
 * 
 * @author Anatole
 * 
 */
@Singleton
public class ServiceCatalogImpl implements ServiceCatalog {

	private static final Logger LOGGER = Logger.getLogger(ServiceCatalogImpl.class);

	@Inject
	private Locator locator;

	private String[] scopes = new String[0];

	@Inject
	public ServiceCatalogImpl() {
	}

	public String[] getScopes() {
		return scopes;
	}

	public ServiceURL[] getServices() throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			result.add(def);
			System.out.println("Registered: " + def);
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	public ServiceURL[] getServicesByType(Class interfaceType) throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String[] interfaces = extractInterfaces(def.getURLPath());
			for (String interfaceName : interfaces) {
				if (interfaceName.equals(interfaceType.getName())) {
					result.add(def);
				}
			}
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	public ServiceURL[] getServicesByPath(String pathExp) throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String path = def.getURLPath();
			if (path != null && path.matches(pathExp)) {
				result.add(def);
			}
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	public ServiceURL[] getServicesByPath(String pathExp, String protocol) throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String path = def.getURLPath();
			if (path != null && path.matches(pathExp)) {
				if (def.getProtocol().equals(protocol)) {
					result.add(def);
				}
			}
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	public ServiceURL[] getServicesByType(Class interfaceType, String pathExp, String protocol) throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String path = def.getURLPath();
			if (path != null && path.matches(pathExp)) {
				if (def.getProtocol().equals(protocol)) {
					String[] interfaces = extractInterfaces(def.getURLPath());
					for (String interfaceName : interfaces) {
						if (interfaceName.equals(interfaceType.getName())) {
							result.add(def);
						}
					}
				}
			}
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	private String[] extractInterfaces(String urlPath) {
		int index = urlPath.indexOf('#');
		if (index < 0) {
			return new String[0];
		}
		String fragment = urlPath.substring(index + 1);
		if (fragment != null) {
			return fragment.split(",");
		}
		return new String[0];
	}

	public ServiceURL[] getServicesByProtocol(String protocol) throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String path = def.getURLPath();
			if (def.getProtocol().equals(protocol)) {
				result.add(def);
			}
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	public ServiceURL[] getServicesByType(Class interfaceType, String protocol) throws ServiceLocationException {
		Set<ServiceURL> result = new HashSet<ServiceURL>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			String path = def.getURLPath();
			if (def.getProtocol().equals(protocol)) {
				String typeName = def.getServiceType().getConcreteTypeName();
				String[] interfaces = extractInterfaces(def.getURLPath());
				for (String interfaceName : interfaces) {
					if (interfaceName.equals(interfaceType.getName())) {
						result.add(def);
					}
				}
			}
		}
		return result.toArray(new ServiceURL[result.size()]);
	}

	public String[] getProtocols(String type, String name) throws ServiceLocationException {
		Set<String> protos = new HashSet<String>();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:" + getTargetTier()), null, null);
		while (sle.hasMoreElements()) {
			ServiceURL def = (ServiceURL) sle.nextElement();
			protos.add(def.getProtocol());
		}
		return protos.toArray(new String[protos.size()]);
	}

	private String getTargetTier() {
		return "bt";
	}

	public boolean isConnected() {
		return true;
	}

	public static void main(String[] args) {
		ServiceURL url;
		try {
			// service:myservice.myorg://hostname.domain.com:6672
			url = new ServiceURL(
					"service:Hello:Hello_1_0.MyEar.DOM0.csintra.net://rmi://localhost:1234/Hello?a=b&b=c#com.csg.cs.core.Hello,java.io.Serializable",
					10);
			System.out.println("OK: " + url);
			System.out.println("  Host        : " + url.getHost());
			System.out.println("  Length      : " + url.getLength());
			System.out.println("  Lifetime    : " + url.getLifetime());
			System.out.println("  Port        : " + url.getPort());
			System.out.println("  Protocol    : " + url.getProtocol());
			System.out.println("  Path        : " + url.getURLPath());
			System.out.println("  Service Type: " + url.getServiceType());
			System.out.println("      Abstract Type: " + url.getServiceType().getAbstractTypeName());
			System.out.println("      Concrete Type: " + url.getServiceType().getConcreteTypeName());
			System.out.println("      Naming Authority: " + url.getServiceType().getNamingAuthority());
			System.out.println("      Principal Type Name: " + url.getServiceType().getPrincipleTypeName());
		} catch (ServiceLocationException e) {
			e.printStackTrace();
		}
	}

}
