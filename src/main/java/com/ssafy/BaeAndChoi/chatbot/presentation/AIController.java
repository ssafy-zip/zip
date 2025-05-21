package com.ssafy.BaeAndChoi.chatbot.presentation;

import com.ssafy.BaeAndChoi.chatbot.config.Config;
import com.ssafy.BaeAndChoi.chatbot.domain.ChatMessage;
import com.ssafy.BaeAndChoi.chatbot.repository.ChatMessageRepository;
import com.ssafy.BaeAndChoi.news.application.NewsService;
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
    private final NewsService newsService;

    @Qualifier("defaultChatClient")
    private final ChatClient defaultChatClient;
    @Qualifier("newsSummationChatClient")
    private final ChatClient newsSummationChatClient;

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

    // 최근 뉴스 20개를 보고, 오늘의 뉴스 요약해주는 기능
    @GetMapping("/newsSummation")
    public ResponseEntity<Map<String, String>> getNewsSummation() {
        // 1) 최근 20개 뉴스 조회
        List<NewsResponseDTO> newsList = newsService.findRecent(20);

        // 2) 뉴스 목록을 포함한 사용자 입력 생성
        StringBuilder sb = new StringBuilder();
        for (NewsResponseDTO n : newsList) {
            sb.append("제목: ").append(n.getTitle()).append("\n");
            sb.append("기사 링크 : ").append(n.getLink()).append("\n");
        }
        String userInput = sb.toString();

        // 3) 뉴스 요약 전용 ChatClient 사용
        ChatClientRequestSpec prompt = newsSummationChatClient.prompt()
                .system(Config.NEWS_SUMMATION_PROMPT)
                .user(userInput);

        // 4) AI 응답 수신 및 문자열 합치기
        Flux<String> aiFlux = prompt.stream().content();
        String summary = aiFlux.collectList()
                .block()
                .stream()
                .collect(Collectors.joining());

        // 5) 요약 반환
        return ResponseEntity.ok(Map.of("summary", summary));
    }
}
