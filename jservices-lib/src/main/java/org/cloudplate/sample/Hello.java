package org.cloudplate.sample;

import java.rmi.RemoteException;



public interface Hello extends java.rmi.Remote {

	public String getUUID()throws RemoteException;
	
}
