package kna.springsecurity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import kna.springsecurity.dto.UserDTO.UserResponse;
import kna.springsecurity.enums.RoleName;

import java.util.Set;

public class AuthDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 1, max = 15, message = "Username must be between 1 and 15 characters")
        @Pattern(regexp = "^[a-zA-Z].*", message = "Username must start with a letter")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private String message;
        private String tempToken;
        private UserResponse userInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 1, max = 15, message = "Username must be between 1 and 15 characters")
        @Pattern(regexp = "^[a-zA-Z].*", message = "Username must start with a letter")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).*$",
                message = "Password must contain at least one uppercase letter, one number and one special character (@#$%^&+=!)")
        private String password;
        private Set<RoleName> roles;
        private Boolean mfaEnabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RegisterResponse {
        private UserResponse userInfo;
        private String secretImageUri;
        private String tempToken;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Verify2FARequest {
        @NotBlank(message = "Temp token is required")
        private String tempToken;

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 digits")
        private String otp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enable2FARequest {
        @NotBlank(message = "Temp token is required")
        private String tempToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Enable2FAResponse {
        private String secretImageUri;
        private String tempToken;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Disable2FARequest {
        @NotBlank(message = "Temp token is required")
        private String tempToken;

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 digits")
        private String otp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Disable2FAResponse {
        private UserResponse userInfo;
        private String message;
    }

}
