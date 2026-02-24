package com.company.ppm.security;

import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.repository.UserRepository;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName().name())
                        .map(this::authority)
                        .collect(Collectors.toSet()))
                .build();
    }

    private GrantedAuthority authority(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
}
