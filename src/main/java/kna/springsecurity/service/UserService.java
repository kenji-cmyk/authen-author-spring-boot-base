package kna.springsecurity.service;

import kna.springsecurity.pkg.PageResponse;
import kna.springsecurity.dto.UserDTO.UserResponse;

public interface UserService {
    PageResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
}
