package database_entities.repositories;

import java.util.Collection;
import java.util.List;

import database_entities.TradeItem;

public interface TradeItemRepositoryExt {
	
	public String saveTradeItemImage(byte[] content, String filename, String contentType);
	public byte[] getTradeItemImage(String imageId);
	public boolean deleteTradeItemImage(String imageId);
	
	public List<TradeItem> findItemsWithTags(Collection<String> tags);
	
	public List<TradeItem> findItemsWithTextIndexedSearchAndOwnerIdNot(String search, String ownerId);
}
