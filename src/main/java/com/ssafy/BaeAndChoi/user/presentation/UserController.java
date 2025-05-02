package com.ssafy.BaeAndChoi.user.presentation;

import com.ssafy.BaeAndChoi.user.application.UserService;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.UserInputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody UserInputDTO request) {
        System.out.println("addUser");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("findId/{id}")
    public ResponseEntity<User> findById(@RequestParam String request) {
        return ResponseEntity.ok(userService.getUser(request));
    }
}
