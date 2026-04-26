package kna.springsecurity.application.service;

import kna.springsecurity.application.port.in.FindUserUseCase;
import kna.springsecurity.domain.model.User;
import kna.springsecurity.domain.port.out.UserProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserService implements FindUserUseCase {

    private final UserProvider userProvider;

    public FindUserService(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userProvider.findByUsername(username);
    }
}
