package org.web.shop_service.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "role")
    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsernameWithRole(@Param("username") String username);
    boolean existsByUsername(String username);
}
