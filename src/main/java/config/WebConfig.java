package config;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class WebConfig {

	@Bean
	@Autowired
	public CommonsMultipartResolver multipartResolver(ServletContext context) {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver(context);
		resolver.setMaxInMemorySize(5000);
		return resolver;
	}
}
