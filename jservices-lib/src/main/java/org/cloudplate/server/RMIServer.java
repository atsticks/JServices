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
package org.cloudplate.server;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Random;

import javax.inject.Singleton;

@Singleton
public class RMIServer {

	private static final int MAX_PORT = 32000;

	private static final int MAX_TRIES = 10;

	private String address = "127.0.0.1";
	private int port;
	private Registry server;
	private Random random = new Random();

	public RMIServer() {
		for (int i = 0; i < MAX_TRIES; i++) {
			this.port = random.nextInt(MAX_PORT);
			try {
				this.server = java.rmi.registry.LocateRegistry
						.createRegistry(port);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				// continue
			}
		}
	}

	public void publishService(String url, Class<Remote> clazz) throws AccessException,
			RemoteException, InstantiationException, IllegalAccessException,
			AlreadyBoundException {
		this.server.bind(url, (Remote) clazz.newInstance());
	}

	public void publishService(String name, Remote instance)
			throws AccessException, RemoteException, InstantiationException,
			IllegalAccessException, AlreadyBoundException {
		this.server.bind(name, instance);
	}

	public String getHost() {
		return address;
	}

	public int getPort() {
		return port;
	}

}
