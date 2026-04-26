package kna.springsecurity.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
import kna.springsecurity.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");
        String providerId = oauthUser.getName(); 

        String loginName = (String) attributes.get("login");
        String username = (email != null) ? email : (loginName != null ? loginName : providerId);

        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    System.out.println("Đăng ký User mới từ " + provider + ": " + username);
                    User newUser = User.builder()
                            .username(username)
                            .roles("USER")
                            .provider(provider)
                            .providerId(providerId)
                            .build();
                    return userRepository.save(newUser);
                });


        String token = jwtService.generateAccessToken(user);
        String targetUrl = "/home?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
