package database_entities.utilities;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusMessagesAndCodesRepository extends PagingAndSortingRepository<StatusMessagesAndCodes, Integer>{

}
