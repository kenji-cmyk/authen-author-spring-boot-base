package kna.springsecurity.repository.impl;

import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserJpaRepository;
import kna.springsecurity.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }
}
