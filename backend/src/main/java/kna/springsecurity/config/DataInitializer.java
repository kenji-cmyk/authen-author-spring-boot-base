package kna.springsecurity.config;

import kna.springsecurity.entity.Provider;
import kna.springsecurity.enums.RoleName;
import kna.springsecurity.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Component
@Order(2)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
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
            Provider localProvider = providerRepository.findByNameIgnoreCase("LOCAL")
                .orElseThrow(() -> new RuntimeException("LOCAL provider not found"));

            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("User123@"))
                    .roles(Set.of(RoleName.USER))
                    .provider(localProvider)
                    .mfaEnabled(false)
                    .mfaVerified(true)
                    .build());

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin123@"))
                    .roles(Set.of(RoleName.ADMIN))
                    .provider(localProvider)
                    .mfaEnabled(false)
                    .mfaVerified(true)
                    .build());
            
            System.out.println("Initialized default users: user, admin");
        }
    }
}
