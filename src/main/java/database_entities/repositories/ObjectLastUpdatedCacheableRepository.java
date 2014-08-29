package database_entities.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import database_entities.LastUpdated;

@Repository
public interface ObjectLastUpdatedCacheableRepository extends CrudRepository<LastUpdated, String>  {

	@Override
	@CachePut("LastUpdatedCache")
	public <S extends LastUpdated> S save(S lastUpdated);
	
	@Override
	@CacheEvict("LastUpdatedCache")
	public void delete(String objectId);
	
	@Cacheable("LastUpdatedCache")
	public LastUpdated findOne(String objectId);
}
