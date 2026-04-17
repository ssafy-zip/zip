package com.ssafy.BaeAndChoi.user.presentation;

import com.ssafy.BaeAndChoi.board.application.BoardService;
import com.ssafy.BaeAndChoi.board.application.CommentService;
import com.ssafy.BaeAndChoi.config.util.JwtUtil;
import com.ssafy.BaeAndChoi.email.application.EmailService;
import com.ssafy.BaeAndChoi.email.domain.FindIdRequest;
import com.ssafy.BaeAndChoi.exception.BadRequestException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final BoardService boardService;
    private final CommentService commentService;
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
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequestDTO dto,@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        log.info("비밀번호 변경 진행");
        return ResponseEntity.ok(userService.updatePassword(userId,dto.getNewPassword()));
    }

    /**
     * 이메일로 아이디를 전송하는 엔드포인트
     */
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody FindIdRequest dto) {
        String userId = userService.findUserIdByEmail(dto.getEmail());
        if (userId == null) {
            throw new BadRequestException("등록된 아이디가 없습니다.");
        }

        // HTML 이메일 보내기
        emailService.sendFindIdHtmlEmail(dto.getEmail(), userId);

        return ResponseEntity.ok(Map.of("message", "회원가입시 사용하신 이메일로 아이디를 발송했습니다."));
    }

    @PostMapping("/canExchangePassword")
    public ResponseEntity<String> canExchangePassword(@RequestBody ExchangablePasswordRequestDTO dto) {
        boolean exchangable = userService.checkExchangable(dto.getUserId(),dto.getEmail());

        return ResponseEntity.ok(exchangable ? "true" : "false");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String userId, @RequestBody ResetPasswordRequestDTO dto){
        return ResponseEntity.ok(userService.resetPassword(userId,dto.getPassword()));
    }

    /**
     * 내가 작성한 게시글 목록 반환
     */
    @GetMapping("/myPage/myArticleListsByCategory")
    public ResponseEntity<List<CategoryArticlesDto>> myArticleListsByCategory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        var dtoList = boardService.findMyArticlesByCategory(userId);
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 내가 작성한 댓글 목록 반환
     */
    @GetMapping("/myPage/myCommentsListsByCategory")
    public ResponseEntity<List<CategoryCommentsDto>> myCommentsListsByCategory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        List<CategoryCommentsDto> result = commentService.findMyCommentsByCategory(userId);
        return ResponseEntity.ok(result);
    }
}
