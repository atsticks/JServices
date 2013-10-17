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

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;

import org.jservice.locator.ServiceResolver;
import org.jservice.registry.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeblogicServiceResolver implements ServiceResolver {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 446951875759183376L;
	private final static Map<String, InitialContext> CONTEXTS = new ConcurrentHashMap<String, InitialContext>();
	private static final Logger LOGGER = LoggerFactory.getLogger(WeblogicServiceResolver.class);

	public WeblogicServiceResolver() {
	}

	@SuppressWarnings("unchecked")
	public <T> T resolve(Service serviceURI, Class<T> targetInterface) throws ServiceUnavailableException {
		String host = serviceURI.getHost();
		int port = serviceURI.getPort();
		System.out.print("Locating WLS JNDI on " + host + ":" + port + "  ... ");
		String providerURL = host + ':' + port;
		InitialContext context = CONTEXTS.get(providerURL);
		try {
			if (context == null) {
				Hashtable<Object, Object> env = new Hashtable<Object, Object>();
				env.put("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
				env.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
				env.put("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
				// optional. Defaults to localhost. Only needed if web server is
				// running
				// on a different host than the appserver
				// env.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
				// optional. Defaults to 3700. Only needed if target orb port is
				// not 3700.
				// env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
				env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
				env.put(Context.PROVIDER_URL, providerURL);
				try {
					context = new InitialContext(env);
				} catch (NamingException e) {
					LOGGER.error(e);
				}
				CONTEXTS.put(providerURL, context);
			}
			LOGGER.debug("Looking up name " + serviceURI.getURLPath() + " ... ");
			return (T) context.lookup(serviceURI.getURLPath());
		} catch (Exception e) {
			LOGGER.info("Failed to locate service: " + serviceURI, e);
			throw new ServiceUnavailableException("Failed to locate service: " + serviceURI);
		}
	}

}
