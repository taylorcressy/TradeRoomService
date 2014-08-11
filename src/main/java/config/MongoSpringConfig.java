package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;

import database_entities.User;

@Configuration
@EnableMongoRepositories(basePackages = { "database_entities", "database_entities.utilities" })
public class MongoSpringConfig {

	private static String SRV_DB = "TradeRoom";
	private MongoTemplate temp;
	
	@Bean
	public GridFS gridFsTemplate() throws Exception {
		if(temp == null)
			mongoTemplate();
		
		return new GridFS(temp.getDb());
	}
	
	// The operations template
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		temp = new MongoTemplate(new TradeRoomMongoDbFactory(new MongoClient(), SRV_DB));
		temp.indexOps(User.class).ensureIndex(new GeospatialIndex("position"));
		return temp;
	}

	@Bean
	public String getMappingBasePackage() {
		return "database_entities";
	}
}
