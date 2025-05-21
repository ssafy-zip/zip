package com.ssafy.BaeAndChoi.user.presentation;

import com.ssafy.BaeAndChoi.config.util.JwtUtil;
import com.ssafy.BaeAndChoi.user.application.UserService;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.LoginDTO;
import com.ssafy.BaeAndChoi.user.dto.LoginResponseDTO;
import com.ssafy.BaeAndChoi.user.dto.UserInputDTO;
import com.ssafy.BaeAndChoi.user.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        // 1) 인증 시도
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUserId(),
                        loginDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 2) JWT 생성
        Role role = userService.getUser(auth.getName()).getRole();
        String token = jwtUtil.generateToken(
                auth.getName(),
                Map.of("role", role.name())
        );

        return ResponseEntity.ok(new LoginResponseDTO(token, role, loginDTO.getUserId()));
    }

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
