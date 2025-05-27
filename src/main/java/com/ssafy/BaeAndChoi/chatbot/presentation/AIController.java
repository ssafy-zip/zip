package com.ssafy.BaeAndChoi.chatbot.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.BaeAndChoi.chatbot.config.Config;
import com.ssafy.BaeAndChoi.chatbot.domain.ChatMessage;
import com.ssafy.BaeAndChoi.chatbot.repository.ChatMessageRepository;
import com.ssafy.BaeAndChoi.house.application.ApartmentService;
import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.ApartmentDeal;
import com.ssafy.BaeAndChoi.house.domain.SearchOption;
import com.ssafy.BaeAndChoi.news.application.NewsService;
import com.ssafy.BaeAndChoi.news.application.NewsSummaryService;
import com.ssafy.BaeAndChoi.news.dto.NewsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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
    private final ApartmentService apartmentService;
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

    @PostMapping("/recommend-apartment-lite")
    public ResponseEntity<Map<String, Object>> recommendApartmentLite(@RequestParam String code) {
        List<Apartment> apartments = apartmentService.searchApartments(new SearchOption(null, code));
        String userPrompt = buildApartmentPrompt(apartments);

        ChatClientRequestSpec prompt = defaultChatClient.prompt()
                .system(Config.RECOMMENDATION_SUMMATION_PROMPT)
                .user(userPrompt);

        String content = prompt.stream().content()
                .collectList().block()
                .stream().collect(Collectors.joining());

        List<Object> parsed;
        try {
            parsed = parseJsonResponse(content);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "AI 응답 파싱 실패", "raw", content));
        }

        return ResponseEntity.ok(Map.of(
                "role", "assistant",
                "content", parsed
        ));
    }

    private List<Object> parseJsonResponse(String rawContent) {
        // 1. Markdown 코드블록 제거
        String cleaned = rawContent
                .replaceAll("^```json\\s*", "")   // 맨 앞의 ```json 또는 ``` 제거
                .replaceAll("\\s*```$", "");      // 맨 뒤의 ``` 제거

        // 2. JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(cleaned, new TypeReference<List<Object>>() {});
        } catch (JsonProcessingException e) {
            // 로그로 원시 응답과 함께 전달
            log.error("JSON 파싱 실패", e);
            throw new RuntimeException("AI 응답을 JSON으로 변환하지 못했습니다.");
        }
    }




    private String buildApartmentPrompt(List<Apartment> apartments) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < apartments.size(); i++) {
            Apartment apt = apartments.get(i);
            ApartmentDeal deal = (apt.getDeals() != null && !apt.getDeals().isEmpty()) ? apt.getDeals().get(0) : null;

            sb.append("  {\n");
            sb.append("    \"apartment\": {\n");
            sb.append(String.format("      \"aptSeq\": \"%s\",\n", apt.getAptSeq()));
            sb.append(String.format("      \"name\": \"%s\",\n", apt.getAptNm()));
            sb.append(String.format("      \"address\": \"%s %s-%s\",\n", apt.getUmdNm(), apt.getBonbun(), apt.getBubun()));
            sb.append(String.format("      \"buildYear\": %d,\n", apt.getBuildYear()));
            sb.append(String.format("      \"roadAddress\": \"%s %s-%s\"\n", apt.getRoadNm(), apt.getRoadNmBonbun(), apt.getRoadNmBubun()));
            sb.append("    },\n");

            if (deal != null) {
                sb.append("    \"deal\": {\n");
                sb.append(String.format("      \"date\": \"%s\",\n", deal.getDealYmd()));
                sb.append(String.format("      \"price\": \"%s만원\",\n", deal.getDealAmount()));
                sb.append(String.format("      \"area\": %.1f,\n", deal.getExcluUseAr()));
                sb.append(String.format("      \"floor\": %d\n", deal.getFloor()));
                sb.append("    }\n");
            } else {
                sb.append("    \"deal\": null\n");
            }

            sb.append(i == apartments.size() - 1 ? "  }\n" : "  },\n");
        }

        sb.append("]");
        return sb.toString();
    }


}
