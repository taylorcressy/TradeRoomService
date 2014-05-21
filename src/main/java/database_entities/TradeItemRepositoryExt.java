package database_entities;

import java.util.Collection;
import java.util.List;

public interface TradeItemRepositoryExt {
	
	public String saveTradeItemImage(byte[] content, String filename, String contentType);
	public byte[] getTradeItemImage(String imageId);
	public boolean deleteTradeItemImage(String imageId);
	
	public List<TradeItem> findItemsWithTags(Collection<String> tags);
}
