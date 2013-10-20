package org.jservice.runtime.servers;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

	private Registry registry;
	private int port;

	public RMIServer() {
		for (int i = 6000; i < 7000; i++) {
			try {
				registry = LocateRegistry.createRegistry(i);
				this.port = i;
				return;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Registry getRegistry() {
		return registry;
	}

	public int getPort() {
		return port;
	}
}
