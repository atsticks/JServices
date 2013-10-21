package org.jservice.runtime.servers;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMIServer {

	private Registry registry;
	private int port;
	private static final Logger LOG = LoggerFactory.getLogger(RMIServer.class);

	public RMIServer(int startPort, int range) {
		for (int i = startPort; i < (startPort + range); i++) {
			try {
				registry = LocateRegistry.createRegistry(i);
				this.port = i;
				LOG.info("RMI Server succesfully started at port: " + this.port);
				return;
			} catch (RemoteException e) {
				LOG.debug("RMI Server could not be started on port: "
						+ this.port + ", trying next port...");
			}
		}
		throw new IllegalStateException("Failed to start local RMI server.");
	}

	public Registry getRegistry() {
		return registry;
	}

	public int getPort() {
		return port;
	}
}
