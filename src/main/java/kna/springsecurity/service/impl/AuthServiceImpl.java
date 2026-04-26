package kna.springsecurity.service.impl;

import kna.springsecurity.dto.UserDTO.LoginRequest;
import kna.springsecurity.dto.UserDTO.LoginResponse;
import kna.springsecurity.dto.UserDTO.RegisterRequest;
import kna.springsecurity.dto.UserDTO.RegisterResponse;
import kna.springsecurity.dto.UserDTO.RefreshTokenRequest;
import kna.springsecurity.dto.UserDTO.RefreshTokenResponse;
import kna.springsecurity.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import kna.springsecurity.repository.UserRepository;
import kna.springsecurity.service.AuthService;
import kna.springsecurity.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import kna.springsecurity.security.CustomUserDetails;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password"); 
        }

        return LoginResponse.builder()
                .username(user.getUsername())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .roles(user.getRoles())
                .message("Login success")
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {


        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles("USER") 
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .username(user.getUsername())
                .roles("USER")
                .message("Register success")
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        
        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new RuntimeException("Refresh token is required");
        }

        String username = jwtService.extractUsername(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                                               .orElseThrow(() -> new RuntimeException("User not found"));

        if(!jwtService.validateToken(request.getRefreshToken(), new CustomUserDetails(user))) {
            throw new RuntimeException("Invalid refresh token");
        }

        return RefreshTokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }
}
