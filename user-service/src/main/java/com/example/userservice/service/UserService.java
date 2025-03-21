package com.example.userservice.service;

import com.example.userservice.model.Role;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import jakarta.servlet.http.HttpSession;

public interface UserService {
 ApiResponse createUser (UserRequest user);
 ApiResponse getUserById(Long userId);
 ApiResponse createRole(Role role);
 ApiResponse login(String email, String password, HttpSession httpSession);
 ApiResponse updateUser(Long userId,UserRequest user);
 ApiResponse deleteUser(Long userId);
 ApiResponse validateToken( String token);
}
