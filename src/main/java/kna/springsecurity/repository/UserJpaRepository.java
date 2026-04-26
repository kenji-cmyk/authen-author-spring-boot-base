package kna.springsecurity.repository;

import kna.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByUsername(String username);
}
