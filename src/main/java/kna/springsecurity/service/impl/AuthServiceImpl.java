package kna.springsecurity.service.impl;

import kna.springsecurity.cache.TempTokenService;
import kna.springsecurity.cache.TempTokenService.TempTokenData;
import kna.springsecurity.cache.TempTokenService.TempTokenPurpose;
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
import kna.springsecurity.entity.User;
import kna.springsecurity.enums.RoleName;
import kna.springsecurity.mfa.TwoFactorAuthenService;
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
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final TwoFactorAuthenService twoFactorAuthenService;
    private final TempTokenService tempTokenService;

    public AuthServiceImpl(UserRepository userRepository, 
                           ProviderRepository providerRepository,
                           PasswordEncoder passwordEncoder, 
                           JwtService jwtService,
                           UserMapper userMapper,
                           TwoFactorAuthenService twoFactorAuthenService,
                           TempTokenService tempTokenService) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.twoFactorAuthenService = twoFactorAuthenService;
        this.tempTokenService = tempTokenService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));


        
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new UnauthorizedException("Invalid username or password");
        }

        UserResponse userInfo = userMapper.mapToUserResponse(user);

        if (user.isMfaEnabled()){
            if (!StringUtils.hasText(user.getSecretKey())) {
            throw new UnauthorizedException("2FA is enabled but not configured correctly");
            }

            boolean mfaVerified = Boolean.TRUE.equals(user.getMfaVerified());
            TempTokenPurpose tempTokenPurpose = mfaVerified
                ? TempTokenPurpose.LOGIN_2FA
                : TempTokenPurpose.REGISTER_2FA_SETUP;
            String message = mfaVerified
                ? "Login success, please verify 2FA"
                : "Please verify 2FA setup to complete account activation";

            return LoginResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .userInfo(userInfo)
                    .tempToken(tempTokenService.generateAndStore(user.getId(), tempTokenPurpose))
                    .message(message)
                    .build();
        }

        return LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .userInfo(userInfo)
                .tempToken(tempTokenService.generateAndStore(user.getId(), TempTokenPurpose.REGISTER_2FA_SETUP))
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
        ? Set.of(RoleName.USER) : request.getRoles();

        boolean mfaEnabled = Boolean.TRUE.equals(request.getMfaEnabled());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .provider(localProvider)
            .mfaEnabled(mfaEnabled)
            .mfaVerified(!mfaEnabled)
                .build();

        String qrUri = null;
        String tempToken = null;
        if(mfaEnabled){
            user.setSecretKey(twoFactorAuthenService.generateNewSecret());
            qrUri = twoFactorAuthenService.generateQRCodeImgUri(user);
        }

        userRepository.save(user);

        if (mfaEnabled) {
            tempToken = tempTokenService.generateAndStore(user.getId(), TempTokenPurpose.REGISTER_2FA_SETUP);
        }

        UserResponse userInfo = userMapper.mapToUserResponse(user);

        return RegisterResponse.builder()
                .userInfo(userInfo)
                .secretImageUri(qrUri)
            .tempToken(tempToken)
            .message(mfaEnabled
                ? "Register success, please verify 2FA setup"
                : "Register success")
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

    @Override
    public LoginResponse verify2FA (Verify2FARequest request){

        TempTokenData tokenData = tempTokenService.getTokenData(request.getTempToken());

        if(tokenData == null || tokenData.userId() == null) {
            throw new BadRequestException("Temp token invalid or expired");
        }

        User user = userRepository.findById(tokenData.userId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.isMfaEnabled() || !StringUtils.hasText(user.getSecretKey())) {
            throw new BadRequestException("2FA is not enabled for this account");
        }

        if(twoFactorAuthenService.isOTPNotValid(user.getSecretKey(), request.getOtp())){
            throw new UnauthorizedException("OTP is not valid");
        }

        tempTokenService.delete(request.getTempToken());

        String message;
        if (tokenData.purpose() == TempTokenPurpose.REGISTER_2FA_SETUP) {
            user.setMfaVerified(true);
            userRepository.save(user);
            message = "2FA setup verified. Login success";
        } else {
            if (!Boolean.TRUE.equals(user.getMfaVerified())) {
                throw new UnauthorizedException("Please complete 2FA setup verification before login");
            }
            message = "Login success";
        }

        UserResponse userInfo = userMapper.mapToUserResponse(user);

        return LoginResponse.builder()
                .message(message)
                .refreshToken(jwtService.generateRefreshToken(user))
                .accessToken(jwtService.generateAccessToken(user))
                .userInfo(userInfo)
                .build();
    }

    @Override
    public Enable2FAResponse enable2FA(String authenticatedUsername, Enable2FARequest request) {
        if (!StringUtils.hasText(authenticatedUsername)) {
            throw new UnauthorizedException("Unauthorized");
        }

        TempTokenData tokenData = tempTokenService.getTokenData(request.getTempToken());

        if (tokenData == null || tokenData.userId() == null) {
            throw new BadRequestException("Temp token invalid or expired");
        }

        User user = userRepository.findById(tokenData.userId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!authenticatedUsername.equals(user.getUsername())) {
            throw new UnauthorizedException("Temp token does not belong to authenticated user");
        }

        if (user.isMfaEnabled() && Boolean.TRUE.equals(user.getMfaVerified())) {
            throw new ConflictException("2FA is already enabled");
        }

        if (!StringUtils.hasText(user.getSecretKey())) {
            user.setSecretKey(twoFactorAuthenService.generateNewSecret());
        }

        user.setMfaEnabled(true);
        user.setMfaVerified(false);
        userRepository.save(user);

        String qrUri = twoFactorAuthenService.generateQRCodeImgUri(user);

        return Enable2FAResponse.builder()
                .secretImageUri(qrUri)
                .tempToken(request.getTempToken())
                .message("2FA setup initiated. Verify OTP via /api/auth/verify-2fa")
                .build();
    }

    @Override
    public Disable2FAResponse disable2FA(String authenticatedUsername, Disable2FARequest request) {
        if (!StringUtils.hasText(authenticatedUsername)) {
            throw new UnauthorizedException("Unauthorized");
        }

        TempTokenData tokenData = tempTokenService.getTokenData(request.getTempToken());

        if (tokenData == null || tokenData.userId() == null) {
            throw new BadRequestException("Temp token invalid or expired");
        }

        User user = userRepository.findById(tokenData.userId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!authenticatedUsername.equals(user.getUsername())) {
            throw new UnauthorizedException("Temp token does not belong to authenticated user");
        }

        if (!user.isMfaEnabled()) {
            throw new BadRequestException("2FA is not enabled");
        }

        if (!StringUtils.hasText(user.getSecretKey())) {
            throw new BadRequestException("2FA secret is missing");
        }

        if (twoFactorAuthenService.isOTPNotValid(user.getSecretKey(), request.getOtp())) {
            throw new UnauthorizedException("OTP is not valid");
        }

        tempTokenService.delete(request.getTempToken());

        user.setMfaEnabled(false);
        user.setMfaVerified(true);
        user.setSecretKey(null);
        userRepository.save(user);

        return Disable2FAResponse.builder()
                .message("2FA disabled successfully")
                .userInfo(userMapper.mapToUserResponse(user))
                .build();
    }

}
