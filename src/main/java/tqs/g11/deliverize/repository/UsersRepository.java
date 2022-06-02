package tqs.g11.deliverize.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.g11.deliverize.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    Optional<User> getUserById(Long id);

    @Query("select user from User user where (:role is null or user.role = :role)")
    List<User> findByRole(@Param("role") String role);
}
