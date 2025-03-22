package com.example.userservice.controller;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.userservice.model.Role;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for createUser endpoint

    @Test
    public void testCreateUser_Success() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("testUser");

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "User created successfully", UUID.randomUUID().toString(), HttpStatus.CREATED);
        when(userService.createUser(userRequest)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.createUser(userRequest);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getHttpStatus());
        assertEquals("User created successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateUser_Exception() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("testUser");

        when(userService.createUser(userRequest)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.createUser(userRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to create user"));
    }

    // Test cases for createRole endpoint

    @Test
    public void testCreateRole_Success() {
        // Arrange
        Role role = new Role();
        role.setName("ADMIN");

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "Role created successfully", UUID.randomUUID().toString(), HttpStatus.CREATED);
        when(userService.createRole(role)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.createRole(role);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getHttpStatus());
        assertEquals("Role created successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateRole_Exception() {
        // Arrange
        Role role = new Role();
        role.setName("ADMIN");

        when(userService.createRole(role)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.createRole(role);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to create role"));
    }

    // Test cases for getByUserId endpoint

    @Test
    public void testGetByUserId_Success() {
        // Arrange
        Long userId = 1L;

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "User retrieved successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(userService.getUserById(userId)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.getByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("User retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetByUserId_Exception() {
        // Arrange
        Long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.getByUserId(userId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to retrieve user"));
    }

    // Test cases for login endpoint

    @Test
    public void testLogin_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        HttpSession session = mock(HttpSession.class);

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "Login successful", UUID.randomUUID().toString(), HttpStatus.OK);
        when(userService.login(email, password, session)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.login(email, password, session);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testLogin_Exception() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        HttpSession session = mock(HttpSession.class);

        when(userService.login(email, password, session)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.login(email, password, session);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to login"));
    }

    // Test cases for updateUser endpoint

    @Test
    public void testUpdateUser_Success() {
        // Arrange
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("updatedUser");

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "User updated successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(userService.updateUser(userId, userRequest)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.updateUser(userId, userRequest);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("User updated successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateUser_Exception() {
        // Arrange
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("updatedUser");

        when(userService.updateUser(userId, userRequest)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.updateUser(userId, userRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to update user"));
    }

    // Test cases for validateToken endpoint

    @Test
    public void testValidateToken_Success() {
        // Arrange
        String token = "validToken";

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "Token is valid", UUID.randomUUID().toString(), HttpStatus.OK);
        when(userService.validateToken(token)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.validateToken(token);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Token is valid", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testValidateToken_Exception() {
        // Arrange
        String token = "invalidToken";

        when(userService.validateToken(token)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.validateToken(token);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to validate token"));
    }

    // Test cases for deleteUser endpoint

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "User deleted successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(userService.deleteUser(userId)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("User deleted successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testDeleteUser_Exception() {
        // Arrange
        Long userId = 1L;

        when(userService.deleteUser(userId)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to delete user"));
    }

    // Test cases for assignRoleToUser endpoint

    @Test
    public void testAssignRoleToUser_Success() {
        // Arrange
        String email = "test@example.com";
        String roleName = "ADMIN";

        ApiResponse expectedResponse = ApiResponse.success(new Object(), "Role assigned successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(userService.assignRoleToUser(email, roleName)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = userController.assignRoleToUser(email, roleName);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Role assigned successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testAssignRoleToUser_Exception() {
        // Arrange
        String email = "test@example.com";
        String roleName = "ADMIN";

        when(userService.assignRoleToUser(email, roleName)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = userController.assignRoleToUser(email, roleName);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to assign role"));
    }
}