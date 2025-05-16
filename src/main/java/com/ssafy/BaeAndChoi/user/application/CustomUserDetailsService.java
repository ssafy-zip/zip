package com.ssafy.BaeAndChoi.user.application;

import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUserId())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }
}
