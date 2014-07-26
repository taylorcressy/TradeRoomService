package database_entities;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

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
}
