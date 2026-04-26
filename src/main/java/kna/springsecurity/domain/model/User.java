package kna.springsecurity.domain.model;

import java.util.List;

public record User(Long id, String username, String encodedPassword, List<String> roles) {
}
