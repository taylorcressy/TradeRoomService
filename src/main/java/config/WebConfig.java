package config;

import http_controllers.filters.ExecutionTimeInterceptor;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class WebConfig {
	
	/**
	 * Handler Mapping
	 */
	public InterceptorRegistry interceptorRegistry() {
		InterceptorRegistry registry = new InterceptorRegistry();
		registry.addInterceptor(new ExecutionTimeInterceptor());
		return registry;
	}
	
	@Bean
	@Autowired
	public CommonsMultipartResolver multipartResolver(ServletContext context) {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver(context);
		resolver.setMaxInMemorySize(5000);
		return resolver;
	}
}
