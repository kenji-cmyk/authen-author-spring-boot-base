package kna.springsecurity.controller;

import kna.springsecurity.dto.AuthDTO.Disable2FARequest;
import kna.springsecurity.dto.AuthDTO.Disable2FAResponse;
import kna.springsecurity.dto.AuthDTO.Enable2FARequest;
import kna.springsecurity.dto.AuthDTO.Enable2FAResponse;
import kna.springsecurity.dto.AuthDTO.Verify2FARequest;
import kna.springsecurity.dto.AuthDTO.LoginRequest;
import kna.springsecurity.dto.AuthDTO.LoginResponse;
import kna.springsecurity.dto.AuthDTO.RegisterRequest;
import kna.springsecurity.dto.AuthDTO.RegisterResponse;
import kna.springsecurity.dto.AuthDTO.RefreshTokenRequest;
import kna.springsecurity.dto.AuthDTO.RefreshTokenResponse;
import kna.springsecurity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request); 

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        
        RefreshTokenResponse response = authService.refreshToken(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<LoginResponse> verify2FA (@Valid @RequestBody Verify2FARequest request){
        LoginResponse response = authService.verify2FA(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/enable-2fa")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Enable2FAResponse> enable2FA(
            Authentication authentication,
            @Valid @RequestBody Enable2FARequest request) {
        return ResponseEntity.ok(authService.enable2FA(authentication.getName(), request));
    }

    @PostMapping("/disable-2fa")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Disable2FAResponse> disable2FA(
            Authentication authentication,
            @Valid @RequestBody Disable2FARequest request) {
        return ResponseEntity.ok(authService.disable2FA(authentication.getName(), request));
    }
}
