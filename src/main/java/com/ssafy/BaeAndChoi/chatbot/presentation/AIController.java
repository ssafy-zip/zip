package com.ssafy.BaeAndChoi.chatbot.presentation;

import com.ssafy.BaeAndChoi.chatbot.config.Config;
import com.ssafy.BaeAndChoi.chatbot.domain.ChatMessage;
import com.ssafy.BaeAndChoi.chatbot.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AIController {

    private final ChatClient chatClient;
    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String userId
    ) {

        // 1) 비로그인 시 빈 리스트 반환
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // 2) 로그인 사용자는 userDetails에서 userId 추출
//        String userId = userDetails.getUsername();
        List<ChatMessage> history = chatMessageRepository
                .findByUserIdOrderByTimestampAsc(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> sendMessage(
            @RequestParam String userId,
            @RequestBody Map<String, String> request
    ) {
        String userInput = request.get("userInput");

        // 1) GPT prompt 준비
        ChatClientRequestSpec prompt = chatClient.prompt()
                .system(Config.DEFAULT_PROMPT)
                .user(userInput);

        // 2) AI 응답 수신 (Flux → String)
        Flux<String> aiResponseFlux = prompt.stream().content();
        String content = aiResponseFlux.collectList()
                .block()
                .stream()
                .collect(Collectors.joining());

        // 3) 로그인 여부에 따라 대화 저장
        if (userId != null) {
//            String userId = userDetails.getUsername();

            ChatMessage userMsg = new ChatMessage(userId, "user", userInput);
            chatMessageRepository.save(userMsg);

            ChatMessage assistantMsg = new ChatMessage(userId, "assistant", content);
            chatMessageRepository.save(assistantMsg);

            // 4) 타임스탬프 반환
            return ResponseEntity.ok(Map.of(
                    "role", "assistant",
                    "content", content,
                    "userTimestamp", String.valueOf(userMsg.getTimestamp()),
                    "assistantTimestamp", String.valueOf(assistantMsg.getTimestamp())
            ));
        } else {
            // 비로그인 사용자는 저장 없이 응답만 반환
            return ResponseEntity.ok(Map.of(
                    "role", "assistant",
                    "content", content
            ));
        }
    }

}
