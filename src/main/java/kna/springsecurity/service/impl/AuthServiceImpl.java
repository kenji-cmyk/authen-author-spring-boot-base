package kna.springsecurity.service.impl;

import kna.springsecurity.dto.AuthDTO.LoginRequest;
import kna.springsecurity.dto.AuthDTO.LoginResponse;
import kna.springsecurity.dto.AuthDTO.RegisterRequest;
import kna.springsecurity.dto.AuthDTO.RegisterResponse;
import kna.springsecurity.dto.AuthDTO.RefreshTokenRequest;
import kna.springsecurity.dto.AuthDTO.RefreshTokenResponse;
import kna.springsecurity.entity.User;
import kna.springsecurity.enums.RoleName;
import kna.springsecurity.repository.UserRepository;
import kna.springsecurity.service.AuthService;
import kna.springsecurity.security.jwt.JwtService;
import kna.springsecurity.security.CustomUserDetails;
import kna.springsecurity.repository.ProviderRepository;
import kna.springsecurity.entity.Provider;
import kna.springsecurity.dto.UserDTO.UserResponse;
import kna.springsecurity.exception.custom.BadRequestException;
import kna.springsecurity.exception.custom.ConflictException;
import kna.springsecurity.exception.custom.UnauthorizedException;
import kna.springsecurity.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, 
                           ProviderRepository providerRepository,
                           PasswordEncoder passwordEncoder, 
                           JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new UnauthorizedException("Invalid username or password");
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
            throw new ConflictException("Username already exists");
        }

        Provider localProvider = providerRepository.findByNameIgnoreCase("LOCAL")
                .orElseThrow(() -> new IllegalStateException("Default provider not found"));
    Set<RoleName> roles = request.getRoles() == null || request.getRoles().isEmpty()
        ? Set.of(RoleName.USER)
        : request.getRoles();

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
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
            throw new BadRequestException("Refresh token is required");
        }

        String username = jwtService.extractUsername(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                                               .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if(!jwtService.validateToken(request.getRefreshToken(), new CustomUserDetails(user))) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        return RefreshTokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

}
