package com.example.userservice.controller;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("com/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("user")
    public ApiResponse createUser(@Valid @RequestBody UserRequest user){
        return userService.createUser(user);
    }

    @PostMapping("role")
    public ApiResponse createRole(@RequestBody Role role){
        return userService.createRole(role);
    }

    @GetMapping("user")
    public ApiResponse getByUserId(@RequestParam Long userId){
        return userService.getUserById(userId);
    }
    @PostMapping("/login")
    public ApiResponse login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        return userService.verify(email, password, session);
    }

    @PutMapping("user")
    public ApiResponse updateUser(@RequestParam Long id , @RequestParam UserRequest userRequest){
        return userService.updateUser(id, userRequest);
    }

    @DeleteMapping("user")
    public ApiResponse deleteUser(@RequestParam Long id){
        return userService.deleteUser(id);
    }

}
