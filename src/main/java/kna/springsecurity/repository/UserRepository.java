package kna.springsecurity.repository;

import kna.springsecurity.entity.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}
