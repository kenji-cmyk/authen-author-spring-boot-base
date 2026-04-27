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
import kna.springsecurity.security.CustomUserDetails;
import kna.springsecurity.repository.RoleRepository;
import kna.springsecurity.repository.ProviderRepository;
import kna.springsecurity.entity.Role;
import kna.springsecurity.entity.Provider;
import kna.springsecurity.dto.UserDTO.UserResponse;
import kna.springsecurity.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, 
                           RoleRepository roleRepository,
                           ProviderRepository providerRepository,
                           PasswordEncoder passwordEncoder, 
                           JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password"); 
        }

        UserResponse userInfo = userMapper.mapToUserResponse(user);

        return LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .userInfo(userInfo)
                .message("Login success")
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {


        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Provider localProvider = providerRepository.findByName("LOCAL")
                .orElseThrow(() -> new RuntimeException("Default provider not found"));
        Set<Role> resolvedRoles = resolveRoles(request.getRoles());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(resolvedRoles)
                .provider(localProvider)
                .build();

        userRepository.save(user);

        UserResponse userInfo = userMapper.mapToUserResponse(user);

        return RegisterResponse.builder()
                .userInfo(userInfo)
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

    private Set<Role> resolveRoles(String rawRoles) {
        if (!StringUtils.hasText(rawRoles)) {
            return Set.of(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found")));
        }

        Set<String> normalizedRoleNames = Arrays.stream(rawRoles.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(String::toUpperCase)
                .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
                .collect(Collectors.toSet());

        if (normalizedRoleNames.isEmpty()) {
            return Set.of(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found")));
        }

        return normalizedRoleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
    }
}
