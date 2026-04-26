package kna.springsecurity.infrastructure.security;

import kna.springsecurity.application.port.in.FindUserUseCase;
import kna.springsecurity.domain.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityUserDetailsService implements UserDetailsService {

    private final FindUserUseCase findUserUseCase;

    public SpringSecurityUserDetailsService(FindUserUseCase findUserUseCase) {
        this.findUserUseCase = findUserUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserUseCase.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CustomUserDetails(user);
    }
}
