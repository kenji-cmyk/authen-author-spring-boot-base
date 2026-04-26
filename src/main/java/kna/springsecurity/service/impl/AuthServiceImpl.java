package kna.springsecurity.service.impl;

import kna.springsecurity.dto.AuthDTO.LoginRequest;
import kna.springsecurity.dto.AuthDTO.LoginResponse;
import kna.springsecurity.dto.AuthDTO.RegisterRequest;
import kna.springsecurity.dto.AuthDTO.RegisterResponse;
import kna.springsecurity.dto.AuthDTO.RefreshTokenRequest;
import kna.springsecurity.dto.AuthDTO.RefreshTokenResponse;
import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
import kna.springsecurity.service.AuthService;
import kna.springsecurity.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import kna.springsecurity.security.CustomUserDetails;
import kna.springsecurity.repository.RoleRepository;
import kna.springsecurity.repository.ProviderRepository;
import kna.springsecurity.entity.Role;
import kna.springsecurity.entity.Provider;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, 
                           RoleRepository roleRepository,
                           ProviderRepository providerRepository,
                           PasswordEncoder passwordEncoder, 
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
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
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")))
                .provider(user.getProvider().getName())
                .email(user.getEmail() == null ? "" : user.getEmail())
                .message("Login success")
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {


        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        Provider localProvider = providerRepository.findByName("LOCAL")
                .orElseThrow(() -> new RuntimeException("Default provider not found"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole)) 
                .provider(localProvider)
                .providerId("LOCAL")
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .username(user.getUsername())
                .roles("ROLE_USER")
                .provider(user.getProvider().getName())
                .email(user.getEmail() == null ? "" : user.getEmail())
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
