package com.kjt.springsecurity.service;

import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy user: " + username);
        }

        Set<GrantedAuthority> authorities = new java.util.HashSet<>();

        // Add roles
        if (user.getUserRoles() != null) {
            user.getUserRoles().forEach(userRole -> {
                if (userRole.getRole() != null) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                            "ROLE_" + userRole.getRole().getName()));

                    // Add permissions from role
                    if (userRole.getRole().getRolePermissions() != null) {
                        userRole.getRole().getRolePermissions().forEach(rolePermission -> {
                            if (rolePermission.getPermission() != null) {
                                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                        rolePermission.getPermission().getName()));
                            }
                        });
                    }
                }
            });
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
