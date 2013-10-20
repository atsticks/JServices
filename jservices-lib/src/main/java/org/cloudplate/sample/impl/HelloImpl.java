package org.cloudplate.sample.impl;

import java.rmi.RemoteException;
import java.util.UUID;

import org.cloudplate.sample.Hello;

//@ProvidedService(types = { Hello.class }, protocol = "rmi", name = "/Hello", type = "service")
public class HelloImpl implements Hello {

	public HelloImpl() throws RemoteException {
		super();
	}

	private UUID uuid = UUID.randomUUID();

	public String getUUID() {
		return uuid.toString();
	}

}
