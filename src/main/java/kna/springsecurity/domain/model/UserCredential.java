package kna.springsecurity.domain.model;

import java.util.List;

public record UserCredential(String username, String encodedPassword, List<String> roles) {
}
