package com.chapter8.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**编写jersey的配置文件，主要是指定扫描的包packages("com.chapter8.jersey");
 * @author dell
 *
 */
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		register(RequestContextFilter.class);
		// 配置restful package.
		packages("com.chapter8.jersey");
	}
}