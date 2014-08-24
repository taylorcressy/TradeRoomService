package database_entities.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import database_entities.TradeRequest;

public interface TradeRequestRepository extends PagingAndSortingRepository<TradeRequest, String>, TradeRequestRepositoryExt{
	
}
