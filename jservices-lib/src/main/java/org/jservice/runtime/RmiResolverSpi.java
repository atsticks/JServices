package org.jservice.runtime;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.jservice.catalog.Service;
import org.jservice.catalog.ServiceResolutionException;
import org.jservice.catalog.spi.ServiceResolverSpi;

public class RmiResolverSpi implements ServiceResolverSpi {

	@Override
	public String getProtocol() {
		return "rmi";
	}

	@Override
	public <T> T resolve(Service service, Class<T> targetInterface)
			throws ServiceResolutionException {
		try {
			return (T) Naming.lookup("//" + service.getHost() + ":"
					+ service.getPort() + "/" + service.getLocation());
		} catch (MalformedURLException | RemoteException
				| NotBoundException e) {
			throw new ServiceResolutionException(service, e);
		}
	}

}
