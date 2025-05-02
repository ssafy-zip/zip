package com.ssafy.BaeAndChoi.user.application;

import com.ssafy.BaeAndChoi.exception.BadRequestException;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.UserInputDTO;
import com.ssafy.BaeAndChoi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String createUser(UserInputDTO input){
        if(userRepository.findByUserId(input.getUserId()).isPresent()){
            throw new BadRequestException("User already exists");
        }


        User user = User.builder()
                .userId(input.getUserId())
                .name(input.getName())
                .password(input.getPassword())
                .phone(input.getPhone())
                .email(input.getEmail())
                .role(input.getRole())
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
    public String deleteUserBy(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        userRepository.deleteByUserId(userId);
        return "success delete user";
    }

    @Transactional
    public String updateUser(UserInputDTO input){
        User user = userRepository.findByUserId(input.getUserId())
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        user.update(input);
        return "success update user";
    }
}
