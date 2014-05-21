package database_entities;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeItemRepository extends PagingAndSortingRepository<TradeItem, String>, TradeItemRepositoryExt{	
	public List<TradeItem> findAllByNameContains(String word);
}
