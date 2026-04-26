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
import kna.springsecurity.repository.RoleRepository;
import kna.springsecurity.repository.ProviderRepository;
import kna.springsecurity.entity.Role;
import kna.springsecurity.entity.Provider;
import java.util.Set;
import java.util.Collections;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");
        String providerId = oauthUser.getName(); 
        String username = email.split("@")[0];

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Default role not found"));
                    Provider oauthProvider = providerRepository.findByName(provider.toUpperCase())
                            .orElseThrow(() -> new RuntimeException("Provider " + provider + " not found"));

                    User newUser = User.builder()
                            .email(email)
                            .username(username)
                            .roles(Set.of(userRole))
                            .provider(oauthProvider)
                            .providerId(providerId)
                            .build();
                    return userRepository.save(newUser);
                });


        String token = jwtService.generateAccessToken(user);
        String targetUrl = "/home?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
