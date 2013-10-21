package org.jservice.runtime;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.cloudplate.sample.Hello;
import org.cloudplate.sample.impl.HelloImpl;
import org.jservice.JService;
import org.jservice.registry.Service;
import org.jservice.runtime.servers.RMIServer;

public class TestApp {

	public static void main(String[] args) {
		RMIServer rmiServer = new RMIServer(6000, 1000);
		JService.getCatalog().registerService(
				new Service.Builder().withHost("127.0.0.1")
						.withPort(rmiServer.getPort()).withLocation("hello")
						.withProtocol("rmi")
						.withInterfaces(Hello.class.getName())
						.build());
		try {
			rmiServer.getRegistry().rebind("hello",
					new HelloImpl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Set<String> currentServices = new HashSet<String>();
		while (true) {
			Hello hello = JService.getCatalog().getService(Hello.class);
			try {
				System.out.println(hello.getUUID());
				System.out.println("Current Services: "
						+ JService.getCatalog().getServices(Hello.class));
				Thread.sleep(1000L);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("Interrupted...");
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
