package kna.springsecurity.infrastructure.security;

import kna.springsecurity.domain.model.User;
import kna.springsecurity.domain.port.out.UserProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserAdapter implements UserProvider {

    private final JpaUserRepository userRepository;

    public JpaUserAdapter(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(entity -> new User(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getRoles()
                ));
    }
}
