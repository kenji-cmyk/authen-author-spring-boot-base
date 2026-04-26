package kna.springsecurity.controller;

import kna.springsecurity.dto.UserDTO.LoginRequest;
import kna.springsecurity.dto.UserDTO.LoginResponse;
import kna.springsecurity.dto.UserDTO.RegisterRequest;
import kna.springsecurity.dto.UserDTO.RegisterResponse;
import kna.springsecurity.dto.UserDTO.RefreshTokenRequest;
import kna.springsecurity.dto.UserDTO.RefreshTokenResponse;
import kna.springsecurity.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request); 

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        
        RefreshTokenResponse response = authService.refreshToken(request);
        
        return ResponseEntity.ok(response);
    }
}
