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

        if(userRepository.findByUserId((input.getUserId())) != null){
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
        return userRepository.findByUserId(userId);
    }

    @Transactional
    public String deleteUserBy(String userId) {
        if(userRepository.findByUserId(userId) == null){
            throw new BadRequestException("User not found");
        }

        userRepository.deleteByUserId(userId);
        return "success delete user";
    }

}
