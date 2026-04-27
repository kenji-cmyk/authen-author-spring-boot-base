package kna.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import kna.springsecurity.dto.ProviderDTO.ProviderResponse;

import java.util.Set;

public class UserDTO {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private Set<String> roles;
        private ProviderResponse provider;
    }
}