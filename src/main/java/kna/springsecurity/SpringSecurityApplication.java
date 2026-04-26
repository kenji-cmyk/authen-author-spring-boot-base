package kna.springsecurity;

import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


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
                        .provider("LOCAL")
                        .providerId("LOCAL")
                        .build());
            }
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles("ADMIN")
                        .provider("LOCAL")
                        .providerId("LOCAL")
                        .build());
            }
            
            System.out.println("----- DANH SACH USER TRONG DB -----");
            userRepository.findAll().forEach(u -> 
                System.out.println("User: " + u.getUsername() + " | Role: " + u.getRoles() + " | Provider: " + u.getProvider())
            );
            System.out.println("-----------------------------------");
        };
    }
}
