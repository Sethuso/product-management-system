package com.example.userservice.config;

import com.example.userservice.model.Role;
import com.example.userservice.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Config {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }

    @Bean
    @Transactional
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            List<String> roleNames = Arrays.asList("USER", "ADMIN");

            roleNames.forEach(roleName -> {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                    System.out.println("Role '" + roleName + "' created successfully.");
                }
            });
        };
    }
}
