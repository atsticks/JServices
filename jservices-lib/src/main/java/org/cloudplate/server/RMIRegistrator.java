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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.Remote;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.cloudplate.servicelocator.Updating;
import org.jservice.registry.ElasticService;
import org.jservice.registry.Service;
import org.jservice.registry.ServiceRegistry;

@Singleton
public class RMIRegistrator {

	@Inject
	private RMIServer server;

	@Inject @Updating
	private ServiceRegistry serviceRegistry;

	private static String[] ignoredPackages = new String[] { "javax", "java", "sun", "com.sun", "javassist", "org.jboss", "org.eclipse",
			"org.apache", "org.homemotion.dao" };
	

	private AnnotationDB initializeAnnotationDB() throws IOException {
		AnnotationDB annotationDB = new AnnotationDB();
		annotationDB.setIgnoredPackages(ignoredPackages);
		URL[] urls = ClasspathUrlFinder.findClassPaths();
		annotationDB.scanArchives(urls);
		return annotationDB;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void registerServices() {
		try {
			AnnotationDB annotationDB = initializeAnnotationDB();
			Map<String, Set<String>> index = annotationDB.getAnnotationIndex();
			Set<String> classes = index.get(ElasticService.class.getName());
			for (String className : classes) {
				try {
					Class typeClass = Class.forName(className);
					ElasticService reg = (ElasticService) typeClass.getAnnotation(ElasticService.class);
					for(String proto: reg.protocols()){
						if (!"rmi".equals(proto)) {
							continue;
						}
					}
					Remote service = (Remote) typeClass.newInstance();
					Class[] types = reg.types();
					server.publishService(reg.value(), service);
					if (types.length == 0) {
						Service serviceReg = new Service(createURI(reg, typeClass), 60);
						serviceRegistry.registerService(serviceReg);
					} else {
						for (Class type : types) {
							Service serviceReg = new Service(createURI(reg, type), 60);
							serviceRegistry.registerService(serviceReg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to load annotations.", e);
		}
	}

	private String createURI(ElasticService reg, Class typeClass) throws URISyntaxException {
		String path = reg.value();
		if (!path.startsWith("/")) {
			path = '/' + path;
		}
		//service:Hello:Hello_1_0.MyEar.DOM0.csintra.net://rmi://localhost:1234/Hello?a=b&b=c#com.csg.cs.core.Hello,java.io.Serializable
		return "service:"+getLayer()+":" + typeClass.getSimpleName() +".standalone://rmi://" + server.getHost() + ":" + server.getPort() + path +'#' + typeClass.getName();
	}
	
	public String getLayer(){
		return "bt";
	}
}
