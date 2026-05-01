package kna.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ProviderDTO {


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProviderResponse {
        private Long providerId;
        private String providerName;
    }
}
