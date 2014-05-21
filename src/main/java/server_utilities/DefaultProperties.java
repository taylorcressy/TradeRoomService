/**
 * This class is responsible for loading defaults that are to be used for application run time.
 * The default properties file should be 'traderoom.properties' located in the Classpath resources.
 */
package server_utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;

public class DefaultProperties implements InitializingBean{

	private static final String DEFAULT_PROPERTIES_FILE = "traderoom.properties";
	
	private String fileName;
	private Properties properties;
	
	public DefaultProperties() {
		this.fileName = DEFAULT_PROPERTIES_FILE;
	}
	
	public DefaultProperties(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Properties getProperties() {
		return this.properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public String getStringProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public Integer getIntProperty(String key) {
		return Integer.parseInt(this.properties.getProperty(key));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.loadProperties();
	}
	
	/**
	 * Load the properties from the specified File
	 */
	private void loadProperties() {
		this.properties = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream(new ClassPathResource(this.fileName).getFile());
			
			this.properties.load(input);			
		}
		catch(IOException ioe) {
			//TODO handle
		}
		finally {
			if(input != null) {
				try {
					input.close();
				}
				catch(IOException ioe) {
					//TODO handle
				}
			}
		}
	}
}
