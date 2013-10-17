package org.jservice.runtime;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import org.jboss.weld.environment.se.Weld;

public class Container {

	private static boolean hookRegistered = false;
	private static Weld weld;
	private static org.jboss.weld.environment.se.WeldContainer weldContainer;

	
	public static <T> T getInstance(Class<T> instanceType, Annotation... qualifiers) {
		 return weldContainer.instance().select(instanceType, qualifiers).get();
	}
	
	public static <T> Iterator<T> getInstances(Class<T> instanceType, Annotation... qualifiers) {
		 return weldContainer.instance().select(instanceType, qualifiers).iterator();
	}
	
	public static Set<?> getInstances(String name) {
		 return weldContainer.getBeanManager().getBeans(name);
	}

	public static void fireEvent(Object evt, Annotation... qualifiers){
		 weldContainer.getBeanManager().fireEvent(evt, qualifiers);
	}
	
	public static synchronized void start(){
		if(weld==null){
			System.out.println("*** Starting Container ...");
			weld = new Weld();
			weldContainer = weld.initialize();
			weldContainer.instance().select(Container.class).get();
			System.out.println("*** Container started.");
			if(!hookRegistered){
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						Container.stop();
					}
				});
				hookRegistered = true;
			}
		}
	}
	
	public static synchronized void stop(){
		if(weld!=null){
			System.out.println("*** Stopping Container ...");
			weld.shutdown();
			weld = null;
			System.out.println("*** Container stopped.");
		}
	}
	
	public static void main(String[] args) {
		start();
	}

	@SuppressWarnings("unchecked")
	public static <T>  T getNamedInstance(Class<T> type, String id) {
		Set<?> found = weldContainer.getBeanManager().getBeans(id);
		if(found.isEmpty()){
			return null;
		}
		for (Object object : found) {
			if(type.isAssignableFrom(object.getClass())){
				return (T)object;
			}
		}
		return null;
	}

}