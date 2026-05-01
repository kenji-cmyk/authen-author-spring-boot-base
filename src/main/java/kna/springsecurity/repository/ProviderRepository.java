package kna.springsecurity.repository;

import kna.springsecurity.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByNameIgnoreCase(String name);
}
