package com.ssafy.BaeAndChoi.news.presentation;

import com.ssafy.BaeAndChoi.news.application.NewsCrawlingService;
import com.ssafy.BaeAndChoi.news.application.NewsService;
import com.ssafy.BaeAndChoi.news.dto.NewsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsCrawlingService naverNewsCrawlingService;
    private final NewsService newsService;

    /**
     * @param date ISO 형식(yyyy-MM-dd)으로 입력
     */
    @GetMapping("/crawl")
    public ResponseEntity<String> crawlNews(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        String ymd = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        log.info("크롤링 시작: {}", ymd);
        naverNewsCrawlingService.crawlNewsByDate(ymd);
        log.info("크롤링 완료: {}", ymd);
        return ResponseEntity.ok("Crawled news for date: " + date);
    }

    /** 가장 최근 뉴스 데이터 20건 반환*/
    @GetMapping
    public ResponseEntity<List<NewsResponseDTO>> recentNews() {
        List<NewsResponseDTO> list = newsService.findRecent(20);
        return ResponseEntity.ok(list);
    }
}
