package com.ssafy.BaeAndChoi.user.application;

import com.ssafy.BaeAndChoi.exception.BadRequestException;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.LoginDTO;
import com.ssafy.BaeAndChoi.user.dto.UserDetailResponseDTO;
import com.ssafy.BaeAndChoi.user.dto.UserInputDTO;
import com.ssafy.BaeAndChoi.user.dto.UserUpdateRequestDTO;
import com.ssafy.BaeAndChoi.user.enums.Role;
import com.ssafy.BaeAndChoi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String createUser(UserInputDTO input){
        if(userRepository.findByUserId(input.getUserId()).isPresent()){
            throw new BadRequestException("User already exists");
        }

        User user = User.builder()
                .userId(input.getUserId())
                .name(input.getName())
                .password(passwordEncoder.encode(input.getPassword()))
                .phone(input.getPhone())
                .email(input.getEmail())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return "success create user";
    }

    public User getUser(String userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User does not exist"));
        return user;
    }

    @Transactional
    public String updateUser(UserUpdateRequestDTO input, String userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        user.setName(input.getName());
        user.setPhone(input.getPhone());
        user.setEmail(input.getEmail());
        userRepository.save(user);

        return "success update user";
    }

    @Transactional
    public String deleteUserBy(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        userRepository.deleteByUserId(userId);
        return "success delete user";
    }

    @Transactional
    public String updatePassword(String userId, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return "success update password";
    }
}
