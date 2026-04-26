package kna.springsecurity.infrastructure.security;

import kna.springsecurity.domain.model.UserCredential;
import kna.springsecurity.domain.port.out.UserCredentialProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InMemoryUserCredentialAdapter implements UserCredentialProvider {

    private final PasswordEncoder passwordEncoder;

    public InMemoryUserCredentialAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserCredential> findByUsername(String username) {
        if (!"user".equals(username)) {
            return Optional.empty();
        }

        UserCredential userCredential = new UserCredential(
                "user",
                passwordEncoder.encode("password"),
                List.of("USER")
        );

        return Optional.of(userCredential);
    }
}
