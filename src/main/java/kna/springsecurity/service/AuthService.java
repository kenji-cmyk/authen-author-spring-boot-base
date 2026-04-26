package kna.springsecurity.service;

import kna.springsecurity.dto.UserDTO.LoginRequest;
import kna.springsecurity.dto.UserDTO.LoginResponse;
import kna.springsecurity.dto.UserDTO.RegisterRequest;
import kna.springsecurity.dto.UserDTO.RegisterResponse;
import kna.springsecurity.dto.UserDTO.RefreshTokenRequest;
import kna.springsecurity.dto.UserDTO.RefreshTokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request); 
}