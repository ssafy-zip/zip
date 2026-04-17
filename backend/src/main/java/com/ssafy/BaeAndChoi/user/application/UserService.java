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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        userRepository.save(user);
        return "success update password";
    }

    public String findUserIdByEmail(@NotBlank String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        return user.getUserId();
    }

    public boolean checkExchangable(@NotBlank String userId, @Email @NotBlank String email) {
        User user = userRepository.findUserByUserIdAndEmail(userId,email)
                .orElseThrow(() -> new BadRequestException("회원가입 시 사용한 정보를 정확히 입력해주세요"));

        return true;
    }

    @Transactional
    public String resetPassword(String userId, String password){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("회원가입 시 사용한 정보를 정확히 입력해주세요"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return "success";
    }
}
