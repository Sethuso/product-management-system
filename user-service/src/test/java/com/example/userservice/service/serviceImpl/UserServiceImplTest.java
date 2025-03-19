package com.example.userservice.service.serviceImpl;

import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.model.UserPrinciple;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import com.example.userservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        userRequest = new UserRequest();
        userRequest.setUserName("John");
        userRequest.setEmail("john@example.com");
        userRequest.setPassword("password");

        role = new Role();
        role.setName("USER");

        user = new User();
        user.setUserId(1L);
        user.setUserName("John");
        user.setEmail("john@example.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        user.setRole(role);
        user.setCreatedBy("John");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testCreateUser_Success() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(modelMapper.map(any(UserRequest.class), eq(User.class))).thenReturn(user);
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        ApiResponse response = userService.createUser(userRequest);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));

        ApiResponse response = userService.createUser(userRequest);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT, response.getHttpStatus());
    }

    @Test
    public void testCreateUser_RoleNotFound() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        ApiResponse response = userService.createUser(userRequest);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    }

    @Test
    public void testCreateRole_Success() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        ApiResponse response = userService.createRole(role);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
    }

    @Test
    public void testCreateRole_RoleAlreadyExists() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));

        ApiResponse response = userService.createRole(role);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT, response.getHttpStatus());
    }

    @Test
    public void testGetUserById_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ApiResponse response = userService.getUserById(1L);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    public void testVerify_SuccessfulLogin() {
        MockHttpSession session = new MockHttpSession();
        Authentication authentication = mock(Authentication.class);
        UserPrinciple userPrinciple = mock(UserPrinciple.class);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrinciple);
        when(jwtUtil.generateToken(any(User.class), eq(session))).thenReturn("jwt-token");

        ApiResponse response = userService.verify("john@example.com", "password", session);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

    @Test
    public void testVerify_InvalidPassword() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid password"));

        MockHttpSession session = new MockHttpSession();
        ApiResponse response = userService.verify("john@example.com", "wrongpassword", session);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
    }

    @Test
    public void testVerify_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        MockHttpSession session = new MockHttpSession();
        ApiResponse response = userService.verify("nonexistent@example.com", "password", session);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    }

    @Test
    public void testVerify_AuthenticationFailure() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("User not found"));

        MockHttpSession session = new MockHttpSession();
        ApiResponse response = userService.verify("john@example.com", "password", session);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
    }

    @Test
    public void testUpdateUser_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        ApiResponse response = userService.updateUser(1L, userRequest);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = userService.updateUser(1L, userRequest);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    }

    @Test
    public void testDeleteUser_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        ApiResponse response = userService.deleteUser(1L);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = userService.deleteUser(1L);

        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    }
}