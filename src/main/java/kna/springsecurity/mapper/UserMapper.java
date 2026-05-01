package kna.springsecurity.mapper;


import kna.springsecurity.dto.ProviderDTO.ProviderResponse;
import kna.springsecurity.dto.UserDTO.UserResponse;
import kna.springsecurity.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final ProviderMapper providerMapper;

    public UserMapper(ProviderMapper providerMapper) {
        this.providerMapper = providerMapper;
    }

    public UserResponse mapToUserResponse (User user){
        if (user.getProvider() == null) {
            throw new IllegalStateException("Provider is missing for user id: " + user.getId());
        }

        ProviderResponse providerInfo = providerMapper.mapToProviderResponse(user.getProvider());
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername() == null ? "" : user.getUsername())
                .email(user.getEmail() == null ? "" : user.getEmail())
                .roles(
                        user.getRoles().stream()
                        .map(role -> role.toDatabaseRoleName())
                                .collect(Collectors.toSet())
                )
                .provider(providerInfo)
                .mfaEnabled(user.isMfaEnabled())
                .mfaVerified(Boolean.TRUE.equals(user.getMfaVerified()))
                .build();
    }
}
