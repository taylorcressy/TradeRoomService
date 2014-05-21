package database_entities;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String>, UserRepositoryExt {
	
	public User findOneByEmail(String email);
	public User findOneByUsername(String username);		
}
