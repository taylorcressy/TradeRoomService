package database_entities.repositories;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.text.Term;
import org.springframework.data.mongodb.core.query.text.TextCriteria;
import org.springframework.data.mongodb.core.query.text.TextQuery;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import database_entities.TradeItem;

public class TradeItemRepositoryImpl implements TradeItemRepositoryExt {

	private static final Logger log = LoggerFactory.getLogger("databse-logger");
	
	@Autowired
	private GridFS gridOperations;

	@Autowired
	private MongoTemplate mongoOperations;
	
	@Override
	public List<TradeItem> findItemsWithTags(Collection<String> tags) {
		Criteria criteria = Criteria.where("tags").all(tags);
		return mongoOperations.find( new Query(criteria), TradeItem.class);
	}
	
	
	/**
	 * Save a trade item to GridFS
	 * 
	 * @param content
	 * @param filename
	 * @param contentType
	 * @return ObjectId
	 */
	@Override
	public String saveTradeItemImage(byte[] content, String fileName, String contentType) {
		GridFSInputFile gfsFile = gridOperations.createFile(content);

		gfsFile.setFilename(fileName);
		gfsFile.setContentType(contentType);
		gfsFile.save();

		return gfsFile.getId().toString();
	}
	
	/**
	 * Retrieve all trade item images of item
	 * 
	 * @param imageId
	 * @return byte[][]
	 */
	@Override
	public byte[] getTradeItemImage(String imageId) {
		//First get the gridFS file handle
		GridFSDBFile gridFile = gridOperations.find(new ObjectId(imageId));
		if(gridFile == null || gridFile.getInputStream() == null) {
			return null;
		}
		
		try {			
			return IOUtils.toByteArray(gridFile.getInputStream());
		}
		catch(IOException ioe) {
			log.error("Fatal IO Exception: " + ioe.getLocalizedMessage());
			return null;
		}
	}
	
	/**
	 * Delete an image associated with an image Id
	 * 
	 * @param imageId
	 * @return boolean
	 */
	@Override
	public boolean deleteTradeItemImage(String imageId) {
		gridOperations.remove(new ObjectId(imageId));
		Integer deleted = gridOperations.getDB().getLastError().getInt("n");
		
		if(deleted == 1)
			return true;
		else return false;
	}
	
	/**
	 * Text Indexed Search
	 */
	public List<TradeItem> findItemsWithTextIndexedSearchAndOwnerIdNot(String search, String ownerId) {
		TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingPhrase(search);
		Criteria notCriteria = Criteria.where("ownerId").ne(ownerId);
		//Remember to make a variable (The PageRequest)
		Query query = new Query(notCriteria.andOperator(textCriteria));
		//Query query = new TextQuery(criteria).sortByScore().addCriteria(criteria).with(new PageRequest(0, 10)).addCriteria(notCriteria);
		return mongoOperations.find(query, TradeItem.class);
	}
}
