package com.example.userservice.service.serviceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.exception.RoleNotFoundException;
import com.example.userservice.exception.UserCreationException;
import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.model.UserPrinciple;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import com.example.userservice.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for createUser method

    @Test
    public void testCreateUser_Success() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("testUser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");

        Role defaultRole = new Role();
        defaultRole.setName("USER");

        User newUser = new User();
        newUser.setUserId(1L);
        newUser.setUserName("testUser");
        newUser.setEmail("test@example.com");
        newUser.setRole(defaultRole);

        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setUserName("testUser");
        userDto.setEmail("test@example.com");
        userDto.setRoleName("USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(defaultRole));
        when(modelMapper.map(userRequest, User.class)).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(modelMapper.map(newUser, UserDto.class)).thenReturn(userDto);

        // Act
        ApiResponse response = userService.createUser(userRequest);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getHttpStatus());
        assertEquals("User created successfully.", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");

        User existingUser = new User();
        existingUser.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // Act
        ApiResponse response = userService.createUser(userRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getHttpStatus());
        assertEquals("User with email test@example.com already exists.", response.getMessage());
    }

    @Test
    public void testCreateUser_RoleNotFound() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> userService.createUser(userRequest));
        assertEquals("Default role 'USER' not found in the database.", exception.getMessage());
    }

    @Test
    public void testCreateUser_Exception() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        UserCreationException exception = assertThrows(UserCreationException.class, () -> userService.createUser(userRequest));
        assertEquals("Failed to create user: Database Error", exception.getMessage());
    }

    // Test cases for getUserById method

    @Test
    public void testGetUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        Role role = new Role();
        role.setName("USER");
        user.setRole(role);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setUserName("testUser");
        userDto.setRoleName("USER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        // Act
        ApiResponse response = userService.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("User retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    // Test cases for createRole method

    @Test
    public void testCreateRole_Success() {
        // Arrange
        Role role = new Role();
        role.setName("ADMIN");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(role)).thenReturn(role);

        // Act
        ApiResponse response = userService.createRole(role);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getHttpStatus());
        assertEquals("Role created successfully.", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateRole_RoleAlreadyExists() {
        // Arrange
        Role role = new Role();
        role.setName("ADMIN");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        // Act
        ApiResponse response = userService.createRole(role);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getHttpStatus());
        assertEquals("Role with name ADMIN already exists.", response.getMessage());
    }

    @Test
    public void testCreateRole_Exception() {
        // Arrange
        Role role = new Role();
        role.setName("ADMIN");

        when(roleRepository.findByName("ADMIN")).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createRole(role));
        assertEquals("Error occurred while creating role: Database Error", exception.getMessage());
    }

    // Test cases for login method

    @Test
    public void testLogin_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        Authentication authentication = mock(Authentication.class);
        UserPrinciple userPrinciple = new UserPrinciple(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrinciple);
        when(jwtUtil.generateToken(user, any(HttpSession.class))).thenReturn("token");

        // Act
        ApiResponse response = userService.login(email, password, mock(HttpSession.class));

        // Assert
        assertEquals(HttpStatus.resolve(200), response.getHttpStatus());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testLogin_InvalidEmail() {
        // Arrange
        String email = "test@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = userService.login(email, password, mock(HttpSession.class));

        // Assert
        assertEquals(HttpStatus.resolve(401), response.getHttpStatus());
        assertEquals("Invalid email", response.getMessage());
    }

    @Test
    public void testLogin_InvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid password"));

        // Act
        ApiResponse response = userService.login(email, password, mock(HttpSession.class));

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getHttpStatus());
        assertEquals("Invalid password", response.getMessage());
    }

    @Test
    public void testLogin_Exception() {
        // Arrange
        String email = "test@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = userService.login(email, password, mock(HttpSession.class));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertEquals("Something went wrong. Please try again.", response.getMessage());
    }

    // Test cases for updateUser method

    @Test
    public void testUpdateUser_Success() {
        // Arrange
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("updatedUser");
        userRequest.setEmail("updated@example.com");

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setUserName("testUser");
        existingUser.setEmail("test@example.com");

        User updatedUser = new User();
        updatedUser.setUserId(userId);
        updatedUser.setUserName("updatedUser");
        updatedUser.setEmail("updated@example.com");

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setUserName("updatedUser");
        userDto.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(userDto);

        // Act
        ApiResponse response = userService.updateUser(userId, userRequest);

        // Assert
        assertEquals(HttpStatus.resolve(200), response.getHttpStatus());
        assertEquals("User updated successfully.", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        // Arrange
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = userService.updateUser(userId, userRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("User not found with id: " + userId, response.getMessage());
    }

    @Test
    public void testUpdateUser_Exception() {
        // Arrange
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();

        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userId, userRequest));
        assertEquals("Error occurred while updating user: Database Error", exception.getMessage());
    }

    // Test cases for deleteUser method

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setIsActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ApiResponse response = userService.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.resolve(200), response.getHttpStatus());
        assertEquals("User successfully deactivated", response.getMessage());
        assertFalse(user.getIsActive());
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = userService.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("User with ID " + userId + " not found", response.getMessage());
    }

    // Test cases for validateToken method

//    @Test
//    public void testValidateToken_Success() {
//        // Arrange
//        String token = "validToken";
//
//        when(jwtUtil.validateToken(token)).thenReturn(true);
//        when(jwtUtil.extractUserName(token)).thenReturn("testUser");
//
//        // Act
//        ApiResponse response = userService.validateToken(token);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("Token is valid.", response.getMessage());
//    }

//    @Test
//    public void testValidateToken_InvalidToken() {
//        // Arrange
//        String token = "invalidToken";
//
//        when(jwtUtil.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));
//
//        // Act
//        ApiResponse response = userService.validateToken(token);
//
//        // Assert
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
//        assertEquals("Invalid token", response.getMessage());
//    }

    // Test cases for assignRoleToUser method

    @Test
    public void testAssignRoleToUser_Success() {
        // Arrange
        String email = "test@example.com";
        String roleName = "ADMIN";

        User user = new User();
        user.setEmail(email);

        Role role = new Role();
        role.setName("ADMIN");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        // Act
        ApiResponse response = userService.assignRoleToUser(email, roleName);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Role assigned successfully.", response.getMessage());
        assertEquals(roleName, user.getRole().getName());
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() {
        // Arrange
        String email = "test@example.com";
        String roleName = "ADMIN";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.assignRoleToUser(email, roleName));
        assertEquals("User not found with email: " + email, exception.getMessage());
    }

    @Test
    public void testAssignRoleToUser_RoleNotFound() {
        // Arrange
        String email = "test@example.com";
        String roleName = "ADMIN";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.assignRoleToUser(email, roleName));
        assertEquals("Role not found with name: " + roleName, exception.getMessage());
    }

    // Test cases for getAllUsers method

    @Test
    public void testGetAllUsers_Success() {
        // Arrange
        User user1 = new User();
        user1.setUserId(1L);
        User user2 = new User();
        user2.setUserId(2L);

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(user1, UserDto.class)).thenReturn(new UserDto());
        when(modelMapper.map(user2, UserDto.class)).thenReturn(new UserDto());

        // Act
        ApiResponse response = userService.getAllUsers();

        // Assert
        assertEquals(HttpStatus.resolve(200), response.getHttpStatus());
        assertEquals("Users retrieved successfully.", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetAllUsers_NoUsersFound() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        ApiResponse response = userService.getAllUsers();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("No users found.", response.getMessage());
    }

    @Test
    public void testGetAllUsers_Exception() {
        // Arrange
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = userService.getAllUsers();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getHttpStatus());
        assertEquals("An error occurred while fetching users.", response.getMessage());
    }
}