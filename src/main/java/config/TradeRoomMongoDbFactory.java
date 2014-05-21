package config;

import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;

import database_entities.exceptions.MongoExceptionTranslatorImpl;

public class TradeRoomMongoDbFactory extends SimpleMongoDbFactory {

	private final PersistenceExceptionTranslator translator = new MongoExceptionTranslatorImpl();
	
	public TradeRoomMongoDbFactory(Mongo mongo, String databaseName) {
		super(mongo, databaseName);
	}
	
	@Override
	public PersistenceExceptionTranslator getExceptionTranslator() {
		return this.translator;
	}
}
