package kna.springsecurity.domain.port.out;

import kna.springsecurity.domain.model.UserCredential;

import java.util.Optional;

public interface UserCredentialProvider {

    Optional<UserCredential> findByUsername(String username);
}
