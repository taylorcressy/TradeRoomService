package database_entities.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoExceptionTranslator;

import com.mongodb.MongoException;

public class MongoExceptionTranslatorImpl extends MongoExceptionTranslator {
	
	@Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        if (ex instanceof MongoException.DuplicateKey) {
            return new DetailedDuplicateKeyException(ex.getMessage());
        }
        return super.translateExceptionIfPossible(ex);
    }
}
