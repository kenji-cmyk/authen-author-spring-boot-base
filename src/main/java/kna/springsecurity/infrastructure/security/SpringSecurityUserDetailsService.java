package kna.springsecurity.infrastructure.security;

import kna.springsecurity.application.port.in.FindUserCredentialUseCase;
import kna.springsecurity.domain.model.UserCredential;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityUserDetailsService implements UserDetailsService {

    private final FindUserCredentialUseCase findUserCredentialUseCase;

    public SpringSecurityUserDetailsService(FindUserCredentialUseCase findUserCredentialUseCase) {
        this.findUserCredentialUseCase = findUserCredentialUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential userCredential = findUserCredentialUseCase.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(userCredential.username())
                .password(userCredential.encodedPassword())
                .roles(userCredential.roles().toArray(new String[0]))
                .build();
    }
}
