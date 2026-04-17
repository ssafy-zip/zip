package com.ssafy.BaeAndChoi.news.application;

import com.ssafy.BaeAndChoi.chatbot.config.Config;
import com.ssafy.BaeAndChoi.news.domain.NewsSummary;
import com.ssafy.BaeAndChoi.news.dto.NewsResponseDTO;
import com.ssafy.BaeAndChoi.news.repository.NewsSummaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSummaryService {
    private final NewsSummaryRepository newsSummaryRepository;
    private final NewsService newsService;
    @Qualifier("newsSummationChatClient")
    private final ChatClient newsSummationChatClient;

    @Transactional
    public String addNewsSummation(){
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
        ChatClient.ChatClientRequestSpec prompt = newsSummationChatClient.prompt()
                .system(Config.NEWS_SUMMATION_PROMPT)
                .user(userInput);

        // 4) AI 응답 수신 및 문자열 합치기
        Flux<String> aiFlux = prompt.stream().content();
        String summary = aiFlux.collectList()
                .block()
                .stream()
                .collect(Collectors.joining());
        log.info(summary);

        NewsSummary newsSummary = NewsSummary.builder()
                .date(LocalDate.now())
                .summary(summary)
                .build();
        newsSummaryRepository.save(newsSummary);
        return "success summary";
    }

    /**
     * 주어진 날짜(date)에 저장된 가장 최근 NewsSummary의 summary를 반환합니다.
     * 없으면 기본 메시지를 리턴.
     */
    public String getTodayNewsSummary(LocalDate date) {
        return newsSummaryRepository
                .findFirstByDate(date)
                .map(NewsSummary::getSummary)
                .orElse("오늘자 요약이 없습니다.");        // 값 없을 때 리턴할 기본문구
    }
}
