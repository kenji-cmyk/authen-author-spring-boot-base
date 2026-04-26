package kna.springsecurity.service.impl;

import kna.springsecurity.pkg.PageResponse;
import kna.springsecurity.dto.UserDTO;
import kna.springsecurity.entity.User;
import kna.springsecurity.repository.UserRepository;
import kna.springsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public PageResponse<UserDTO.UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<UserDTO.UserResponse> items = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserDTO.UserResponse>builder()
                .items(items)
                .total(userPage.getTotalElements())
                .page(userPage.getNumber())
                .limit(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .build();
    }

    @Override
    public UserDTO.UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    public UserDTO.UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    private UserDTO.UserResponse mapToUserResponse(User user) {
        String roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(","));

        return UserDTO.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .provider(user.getProvider() != null ? user.getProvider().getName() : "LOCAL")
                .build();
    }
}
