package com.example.userservice.service.serviceImpl;


import com.example.userservice.model.User;
import com.example.userservice.model.UserPrinciple;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    public void testLoadUserByUsername_Success() {
        // Mock the repository to return a user
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Call the method under test
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Verify the results
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserPrinciple);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());

        // Verify that the repository method was called
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Mock the repository to return an empty optional
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Verify that the method throws an exception
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com")
        );

        // Verify the exception message
        assertEquals("User not found...", exception.getMessage());

        // Verify that the repository method was called
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}
