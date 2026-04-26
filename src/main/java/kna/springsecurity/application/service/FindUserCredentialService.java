package kna.springsecurity.application.service;

import kna.springsecurity.application.port.in.FindUserCredentialUseCase;
import kna.springsecurity.domain.model.UserCredential;
import kna.springsecurity.domain.port.out.UserCredentialProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserCredentialService implements FindUserCredentialUseCase {

    private final UserCredentialProvider userCredentialProvider;

    public FindUserCredentialService(UserCredentialProvider userCredentialProvider) {
        this.userCredentialProvider = userCredentialProvider;
    }

    @Override
    public Optional<UserCredential> findByUsername(String username) {
        return userCredentialProvider.findByUsername(username);
    }
}
