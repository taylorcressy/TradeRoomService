package database_entities.repositories;

import java.util.List;

import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import database_entities.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String>, UserRepositoryExt {
	
	public User findOneByEmail(String email);
	public User findOneByUsername(String username);
	public User findOneByFacebookId(String facebookId);
	
	public List<User> findAllByIdIn(List<String> ids);
	public List<User> findAllByUsernameLike(String search);	
	public List<User> findAllByUsernameLikeAndIdNot(String search, String id);
	public List<User> findAllByFacebookIdIn(List<String> facebookIds);
	
	/*
	 * Geo-Location Functions
	 */
	List<User> findByPositionNear(Point location, Distance distance);
	List<User> findByPositionWithin(Circle circle);
	List<User> findByPositionWithin(Box box);
	
}
