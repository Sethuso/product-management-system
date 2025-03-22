package com.example.userservice.service.serviceImpl;

import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.exception.RoleNotFoundException;
import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.model.UserPrinciple;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ApiResponse;
import com.example.userservice.service.UserService;
import com.example.userservice.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper modelMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional
    public ApiResponse createUser(UserRequest userRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            logger.info("Creating user with username: {} | TraceId: {}", userRequest.getUserName(), traceId);

            // Check if a user with the same email already exists
            Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
            if (existingUser.isPresent()) {
                logger.error("User with email {} already exists | TraceId: {}", userRequest.getEmail(), traceId);
                return ApiResponse.failure("User with email " + userRequest.getEmail() + " already exists.", traceId, HttpStatus.CONFLICT);
            }

            // Fetching default role
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RoleNotFoundException("Default role 'USER' not found in the database."));

            // Mapping UserRequest to User entity using ModelMapper
            User newUser = modelMapper.map(userRequest, User.class);
            newUser.setRole(defaultRole);
            newUser.setCreatedBy(userRequest.getUserName());
            newUser.setPassword(encoder.encode(userRequest.getPassword()));
            newUser.setIsActive(true);

            // Save user to the database
            User savedUser = userRepository.save(newUser);

            // Map User to UserDto
            UserDto userDto = modelMapper.map(savedUser, UserDto.class);
            userDto.setRoleName(savedUser.getRole().getName()); // Set role name explicitly

            logger.info("User created successfully with ID: {} | TraceId: {}", savedUser.getUserId(), traceId);

            return ApiResponse.success(userDto, "User created successfully.", traceId, HttpStatus.CREATED);

        } catch (Exception ex) {
            logger.error("Error occurred while creating user: {} | TraceId: {}", ex.getMessage(), traceId);
            throw new RuntimeException(ex.getMessage()); // Let GlobalExceptionHandler handle this
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getUserById(Long userId) {
        String traceId = UUID.randomUUID().toString();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Map User to UserDto
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRoleName(user.getRole().getName()); // Set role name explicitly

        return ApiResponse.success(userDto, "User retrieved successfully", traceId, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse createRole(Role role) {
        String traceId = UUID.randomUUID().toString();
        try {
            Optional<Role> existingRole = roleRepository.findByName(role.getName());
            if (existingRole.isPresent()) {
                return ApiResponse.failure("Role with name " + role.getName() + " already exists.", traceId, HttpStatus.CONFLICT);
            }

            Role savedRole = roleRepository.save(role);
            return ApiResponse.success(savedRole, "Role created successfully.", traceId, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error occurred while creating role: {} | TraceId: {}", e.getMessage(), traceId);
            throw new RuntimeException("Error occurred while creating role: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse login(String email, String password, HttpSession httpSession) {
        String traceId = UUID.randomUUID().toString();
        logger.warn("Attempting login for user: {}", email);

        try {
            // Check if user exists
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid email"));

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            if (authentication.isAuthenticated()) {
                UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
                String token = jwtUtil.generateToken(userPrinciple.getUser(), httpSession);

                // Return success response
                return ApiResponse.success(token, "Login successful", traceId, HttpStatus.OK);
            } else {
                return ApiResponse.failure("Invalid credentials", traceId, HttpStatus.UNAUTHORIZED);
            }
        } catch (BadCredentialsException e) {
            logger.error("Invalid password for user: {}", email);
            return ApiResponse.failure("Invalid password", traceId, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage());
            return ApiResponse.failure("Something went wrong. Please try again.", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse updateUser(Long userId, UserRequest userRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            logger.info("Updating user with ID: {} | TraceId: {}", userId, traceId);

            // Retrieve existing user
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // Update user details
            if (userRequest.getUserName() != null) {
                existingUser.setUserName(userRequest.getUserName());
            }
            if (userRequest.getEmail() != null) {
                existingUser.setEmail(userRequest.getEmail());
            }
            if (userRequest.getPassword() != null) {
                existingUser.setPassword(encoder.encode(userRequest.getPassword()));
            }

            // Set audit information
            existingUser.setUpdatedBy(userRequest.getUserName());

            // Save updated user
            User updatedUser = userRepository.save(existingUser);

            // Map User to UserDto
            UserDto userDto = modelMapper.map(updatedUser, UserDto.class);
            userDto.setRoleName(updatedUser.getRole().getName()); // Set role name explicitly

            logger.info("User updated successfully with ID: {} | TraceId: {}", updatedUser.getUserId(), traceId);

            return ApiResponse.success(userDto, "User updated successfully.", traceId, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            logger.error("Error updating user: {} | TraceId: {}", ex.getMessage(), traceId);
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while updating user: {} | TraceId: {}", ex.getMessage(), traceId);
            throw new RuntimeException("Error occurred while updating user: " + ex.getMessage());
        }
    }

    @Override
    public ApiResponse deleteUser(Long userId) {
        String traceId = UUID.randomUUID().toString();
        try {
            logger.info("Deactivating user with ID: {} | TraceId: {}", userId, traceId);

            // Find the user by ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // Set the user as inactive
            user.setIsActive(false);

            // Save the updated user
            User deactivatedUser = userRepository.save(user);

            // Map User to UserDto
            UserDto userDto = modelMapper.map(deactivatedUser, UserDto.class);
            userDto.setRoleName(deactivatedUser.getRole().getName()); // Set role name explicitly

            logger.info("User deactivated successfully with ID: {} | TraceId: {}", deactivatedUser.getUserId(), traceId);

            return ApiResponse.success(userDto, "User deactivated successfully.", traceId, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            logger.error("Error deactivating user: {} | TraceId: {}", ex.getMessage(), traceId);
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while deactivating user: {} | TraceId: {}", ex.getMessage(), traceId);
            throw new RuntimeException("Error occurred while deactivating user: " + ex.getMessage());
        }
    }

    @Override
    public ApiResponse validateToken(String token) {
        String traceId = UUID.randomUUID().toString();
        logger.info("Validating token: {} | TraceId: {}", token, traceId);
        try {
            jwtUtil.validateToken(token);  // This method will throw an exception if the token is invalid
            String username = jwtUtil.extractUserName(token);
            logger.info("Token is valid for user: {} | TraceId: {}", username, traceId);
            return ApiResponse.success(null, "Token is valid.", traceId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Token validation failed: {} | TraceId: {}", e.getMessage(), traceId);
            return ApiResponse.failure(e.getMessage(), traceId, HttpStatus.UNAUTHORIZED);
        }
    }
    @Override
    public ApiResponse assignRoleToUser(String email, String roleName) {
        String traceId = UUID.randomUUID().toString();  // Generate a new traceId
        logger.info("[{}] Assigning role '{}' to user with email '{}'.", traceId, roleName, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        user.setRole(role);
        userRepository.save(user);

        logger.info("[{}] Successfully assigned role '{}' to user '{}'.", traceId, roleName, email);
        return ApiResponse.success(null, "Role assigned successfully.", traceId, HttpStatus.OK);
    }



}