package kna.springsecurity.domain.port.out;

import kna.springsecurity.domain.model.User;

import java.util.Optional;

public interface UserProvider {
    Optional<User> findByUsername(String username);
}
