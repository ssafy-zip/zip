package com.ssafy.BaeAndChoi.user.presentation;

import com.ssafy.BaeAndChoi.config.util.JwtUtil;
import com.ssafy.BaeAndChoi.user.application.UserService;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.*;
import com.ssafy.BaeAndChoi.user.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping("getUserInfo")
    public ResponseEntity<UserDetailResponseDTO> findById(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        User user = userService.getUser(userId);
        UserDetailResponseDTO dto = UserDetailResponseDTO.builder()
                .id(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("deleteUserId")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        return ResponseEntity.ok(userService.deleteUserBy(userId));
    }

    @PostMapping("updateUser")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequestDTO request,
     @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ResponseEntity.ok(userService.updateUser(request, userId));
    }

    @PostMapping("updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody String newPassword,@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        log.info("비밀번호 변경 진행");
        return ResponseEntity.ok(userService.updatePassword(userId,newPassword));
    }
}
