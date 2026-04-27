package kna.springsecurity.mapper;


import kna.springsecurity.dto.UserDTO.UserResponse;
import kna.springsecurity.entity.Role;
import kna.springsecurity.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse mapToUserResponse (User user){
        if (user.getProvider() == null) {
            throw new IllegalStateException("Provider is missing for user id: " + user.getId());
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .provider(user.getProvider().getName())
                .build();
    }
}
