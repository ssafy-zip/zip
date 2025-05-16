package com.ssafy.BaeAndChoi.news.scheduler;

import com.ssafy.BaeAndChoi.news.application.NewsCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NewsCrawlScheduler {

    private final NewsCrawlingService crawlingService;
    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 매일 오전 10시에 (서울 시간) 오늘 날짜 뉴스 크롤링
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void crawlYesterdayNews() {
        String yesterday = LocalDate.now().format(YMD);
        crawlingService.crawlNewsByDate(yesterday);
    }
}