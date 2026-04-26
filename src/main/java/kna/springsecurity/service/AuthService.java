package kna.springsecurity.service;

import kna.springsecurity.dto.UserDTO.LoginRequest;
import kna.springsecurity.dto.UserDTO.LoginResponse;
import kna.springsecurity.dto.UserDTO.RegisterRequest;
import kna.springsecurity.dto.UserDTO.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
}