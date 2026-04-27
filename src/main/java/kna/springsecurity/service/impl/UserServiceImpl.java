package kna.springsecurity.service.impl;

import kna.springsecurity.mapper.UserMapper;
import kna.springsecurity.pkg.PageResponse;
import kna.springsecurity.dto.UserDTO.UserResponse;
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
    private final UserMapper userMapper;

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<UserResponse> items = userPage.getContent().stream()
                .map(userMapper::mapToUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .items(items)
                .total(userPage.getTotalElements())
                .page(userPage.getNumber())
                .limit(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .build();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return userMapper.mapToUserResponse(user);
    }

}
