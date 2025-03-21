package com.example.userservice.controller;

import com.example.userservice.model.Role;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserController userController;

    private UserRequest userRequest;
    private Role role;
    private ApiResponse apiResponse;

    @BeforeEach
    public void setUp() {
        userRequest = new UserRequest();
        userRequest.setUserName("John");
        userRequest.setEmail("john@example.com");
        userRequest.setPassword("password");

        role = new Role();
        role.setName("USER");

        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Success");
        apiResponse.setHttpStatus(200);
    }

    @Test
    public void testCreateUser_Success() {
        // Mock the service response
        when(userService.createUser(any(UserRequest.class))).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = userController.createUser(userRequest);

        // Verify the results
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify that the service method was called
        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    public void testCreateRole_Success() {
        // Mock the service response
        when(userService.createRole(any(Role.class))).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = userController.createRole(role);

        // Verify the results
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify that the service method was called
        verify(userService, times(1)).createRole(any(Role.class));
    }

    @Test
    public void testGetUserById_Success() {
        // Mock the service response
        when(userService.getUserById(anyLong())).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = userController.getByUserId(1L);

        // Verify the results
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify that the service method was called
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    public void testLogin_Success() {
        // Mock the service response
        when(userService.login(anyString(), anyString(), any(HttpSession.class))).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = userController.login("john@example.com", "password", session);

        // Verify the results
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify that the service method was called
        verify(userService, times(1)).login(anyString(), anyString(), any(HttpSession.class));
    }

    @Test
    public void testUpdateUser_Success() {
        // Mock the service response
        when(userService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = userController.updateUser(1L, userRequest);

        // Verify the results
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify that the service method was called
        verify(userService, times(1)).updateUser(anyLong(), any(UserRequest.class));
    }

    @Test
    public void testDeleteUser_Success() {
        // Mock the service response
        when(userService.deleteUser(anyLong())).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = userController.deleteUser(1L);

        // Verify the results
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify that the service method was called
        verify(userService, times(1)).deleteUser(anyLong());
    }
}