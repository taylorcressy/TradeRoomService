/**
 * Class holding the singleton references to all MongoDB Collections
 * 
 * This class contains purely static methods and should never be instantiated 
 * directly.
 * 
 * Moreover, this class can be used to pool meta information regarding the Application
 * Context and the MongoDB instance currently connected (if connected)
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @date 10 April 2014
 */
package database_entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.WriteConcern;

public class RepositoryFactory {

	private static final Logger log = LoggerFactory.getLogger("database-logger");

	//The application Context
	private static GenericXmlApplicationContext context;
	private static MongoTemplate operations;
	private static WriteConcern writeConcern = WriteConcern.ACKNOWLEDGED;
	
	/**
	 * Retrieve the Singleton reference to the MongoTemplate for committing operations
	 * to MongoDB
	 * @return	MongoTemplate
	 */
	public static MongoTemplate getMongoOperationsInstance() {
		if(RepositoryFactory.operations == null) {
			log.debug("Creating Application Context Reference and Generating Static MongoTemplate");
			RepositoryFactory.context = new GenericXmlApplicationContext("SpringConfig.xml");
			RepositoryFactory.operations = (MongoTemplate) RepositoryFactory.context.getBean("mongoTemplate");
			RepositoryFactory.operations.setWriteConcern(RepositoryFactory.writeConcern);
		}
		return RepositoryFactory.operations;
	}
	
	/**
	 * Close the connections to MongoDB 
	 * 
	 * NOTE: SOMETHING NEEDS TO CALL THIS ON SERVER SHUTDOWN!
	 */
	public static void shutdownMongoOperations() {
		if(RepositoryFactory.operations != null) {
			RepositoryFactory.context.close();
		}
	}
}
