package com.ssafy.BaeAndChoi.chatbot.presentation;

import com.ssafy.BaeAndChoi.chatbot.config.Config;
import com.ssafy.BaeAndChoi.chatbot.domain.ChatMessage;
import com.ssafy.BaeAndChoi.chatbot.repository.ChatMessageRepository;
import com.ssafy.BaeAndChoi.news.application.NewsService;
import com.ssafy.BaeAndChoi.news.application.NewsSummaryService;
import com.ssafy.BaeAndChoi.news.dto.NewsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AIController {

    private final ChatMessageRepository chatMessageRepository;

    @Qualifier("defaultChatClient")
    private final ChatClient defaultChatClient;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String userId
    ) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
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

        // 부동산 기본 ChatClient 사용
        ChatClientRequestSpec prompt = defaultChatClient.prompt()
                .system(Config.DEFAULT_PROMPT)
                .user(userInput);

        Flux<String> aiResponseFlux = prompt.stream().content();
        String content = aiResponseFlux.collectList()
                .block()
                .stream()
                .collect(Collectors.joining());

        if (userId != null && !userId.isBlank()) {
            ChatMessage userMsg = new ChatMessage(userId, "user", userInput);
            chatMessageRepository.save(userMsg);
            ChatMessage assistantMsg = new ChatMessage(userId, "assistant", content);
            chatMessageRepository.save(assistantMsg);

            return ResponseEntity.ok(Map.of(
                    "role", "assistant",
                    "content", content,
                    "userTimestamp", String.valueOf(userMsg.getTimestamp()),
                    "assistantTimestamp", String.valueOf(assistantMsg.getTimestamp())
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "role", "assistant",
                    "content", content
            ));
        }
    }
}
