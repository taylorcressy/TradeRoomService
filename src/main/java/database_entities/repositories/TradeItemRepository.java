package database_entities.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import database_entities.TradeItem;

@Repository
public interface TradeItemRepository extends PagingAndSortingRepository<TradeItem, String>, TradeItemRepositoryExt{
	public List<TradeItem> findAllByNameContains(String word);
	public List<TradeItem> findAllByTagsContains(String tag);
	public List<TradeItem> findAllByTagsContains(String [] tags);
	public List<TradeItem> findAllByDescriptionLike(String like);
	
}
