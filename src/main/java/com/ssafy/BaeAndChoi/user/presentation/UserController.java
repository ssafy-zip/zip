package com.ssafy.BaeAndChoi.user.presentation;

import com.ssafy.BaeAndChoi.user.application.UserService;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.UserInputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody UserInputDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("getUserById")
    public ResponseEntity<User> findById(@RequestParam String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("deleteUserId")
    public ResponseEntity<String> deleteUser(@RequestParam String userId) {
        return ResponseEntity.ok(userService.deleteUserBy(userId));
    }

    @PostMapping("updateUser")
    public ResponseEntity<String> updateUser(@RequestBody UserInputDTO request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }
}
