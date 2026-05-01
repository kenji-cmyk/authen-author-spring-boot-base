package kna.springsecurity.service;

import kna.springsecurity.dto.AuthDTO;
import kna.springsecurity.dto.AuthDTO.LoginRequest;
import kna.springsecurity.dto.AuthDTO.LoginResponse;
import kna.springsecurity.dto.AuthDTO.RegisterRequest;
import kna.springsecurity.dto.AuthDTO.RegisterResponse;
import kna.springsecurity.dto.AuthDTO.RefreshTokenRequest;
import kna.springsecurity.dto.AuthDTO.RefreshTokenResponse;
import kna.springsecurity.dto.AuthDTO.Enable2FARequest;
import kna.springsecurity.dto.AuthDTO.Enable2FAResponse;
import kna.springsecurity.dto.AuthDTO.Disable2FARequest;
import kna.springsecurity.dto.AuthDTO.Disable2FAResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    LoginResponse verify2FA(AuthDTO.Verify2FARequest request);
    Enable2FAResponse enable2FA(String authenticatedUsername, Enable2FARequest request);
    Disable2FAResponse disable2FA(String authenticatedUsername, Disable2FARequest request);
}