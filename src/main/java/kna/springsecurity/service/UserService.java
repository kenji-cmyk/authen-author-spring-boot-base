package kna.springsecurity.service;

import kna.springsecurity.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
}
