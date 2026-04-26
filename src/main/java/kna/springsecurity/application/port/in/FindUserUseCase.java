package kna.springsecurity.application.port.in;

import kna.springsecurity.domain.model.User;

import java.util.Optional;

public interface FindUserUseCase {
    Optional<User> findByUsername(String username);
}
