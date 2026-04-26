package kna.springsecurity.config;

import kna.springsecurity.entity.Provider;
import kna.springsecurity.entity.Role;
import kna.springsecurity.repository.ProviderRepository;
import kna.springsecurity.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Roles
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                    Role.builder().name("ROLE_USER").build(),
                    Role.builder().name("ROLE_ADMIN").build()
            ));
            System.out.println("Initialized default roles: ROLE_USER, ROLE_ADMIN");
        }

        // Initialize Providers
        if (providerRepository.count() == 0) {
            providerRepository.saveAll(List.of(
                    Provider.builder().name("LOCAL").build(),
                    Provider.builder().name("GOOGLE").build(),
                    Provider.builder().name("GITHUB").build()
            ));
            System.out.println("Initialized default providers: LOCAL, GOOGLE, GITHUB");
        }

        // Initialize Users
        if (userRepository.count() == 0) {
            Role userRole = roleRepository.findByName("ROLE_USER").orElse(null);
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
            Provider localProvider = providerRepository.findByName("LOCAL").orElse(null);

            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("User123@"))
                    .roles(Set.of(userRole))
                    .provider(localProvider)
                    .providerId("LOCAL")
                    .build());

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin123@"))
                    .roles(Set.of(adminRole))
                    .provider(localProvider)
                    .providerId("LOCAL")
                    .build());
            
            System.out.println("Initialized default users: user, admin");
        }
    }
}
