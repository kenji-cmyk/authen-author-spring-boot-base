package kna.springsecurity;

import kna.springsecurity.infrastructure.security.JpaUserRepository;
import kna.springsecurity.infrastructure.security.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class SpringSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(new UserEntity("user", passwordEncoder.encode("password"), List.of("USER")));
            }
        };
    }
}
