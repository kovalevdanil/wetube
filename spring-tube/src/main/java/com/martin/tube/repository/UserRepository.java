package com.martin.tube.repository;

import com.martin.tube.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    
    @Query(value = "select * from users where id in " +
            "(select subscriber_id from subscribers where user_id = :userId) offset :offset limit :limit",nativeQuery = true)
    List<User> findSubscribers(Long userId, Integer offset, Integer limit);
}
