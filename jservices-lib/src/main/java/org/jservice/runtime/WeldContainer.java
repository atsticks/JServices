package org.jservice.runtime;

import javax.enterprise.event.Observes;

import org.jboss.weld.environment.se.events.ContainerInitialized;


public class WeldContainer{

	public void startup(@Observes ContainerInitialized evt){
		System.err.println("*** Container started. ***");
	}

}