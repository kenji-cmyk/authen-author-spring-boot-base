package kna.springsecurity;

import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
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
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .roles("USER")
                        .build());
            }
        };
    }
}
