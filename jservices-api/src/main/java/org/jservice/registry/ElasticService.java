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
package org.jservice.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to annotate a service endpoint or EJB, that it should be published
 * as elastic service.
 * 
 * @author Anatole Tresch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ElasticService {
	/**
	 * The protocols to be used. By default jservice tries to evaluate, which
	 * protocols are used by a given service implementation.
	 * 
	 * @return the protocols to be used.
	 */
	String[] protocols() default {};

	/**
	 * The name of the service, by default this equals to the simple class name.
	 * 
	 * @return the customized service name.
	 */
	String value() default "";

	/**
	 * Constraints the published types, by default all implemented interfaces
	 * and the service class hierarchy is exposed.
	 * 
	 * @return the contraint array of types to be exposed.
	 */
	Class[] types() default {};

	/**
	 * Return the context of a service, each entry has the form
	 * {@code key=value}.
	 * 
	 * @return key, value pairs defining the context.
	 */
	String[] context() default {};
}
