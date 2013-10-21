package org.jservice.runtime;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.cloudplate.sample.Hello;
import org.cloudplate.sample.impl.HelloImpl;
import org.jservice.JService;
import org.jservice.registry.Service;
import org.jservice.runtime.servers.RMIServer;

public class TestApp {

	public static void main(String[] args) {
		RMIServer rmiServer = new RMIServer();
		JService.getCatalog().registerService(
				new Service.Builder().withHost("127.0.0.1")
						.withPort(rmiServer.getPort()).withLocation("/hello")
						.withProtocol("rmi")
						.withInterfaces(Hello.class.getName())
						.build());
		try {
			Hello stub = (Hello) UnicastRemoteObject.exportObject(
					new HelloImpl());
			rmiServer.getRegistry().bind("/hello",
					stub);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) {
			Hello hello = JService.getCatalog().getService(Hello.class);
			try {
				System.out.println(hello.getUUID());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
