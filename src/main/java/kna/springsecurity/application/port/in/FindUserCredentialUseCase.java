package kna.springsecurity.application.port.in;

import kna.springsecurity.domain.model.UserCredential;

import java.util.Optional;

public interface FindUserCredentialUseCase {

    Optional<UserCredential> findByUsername(String username);
}
