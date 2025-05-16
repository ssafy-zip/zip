package com.ssafy.BaeAndChoi.news.application;

import com.ssafy.BaeAndChoi.news.domain.News;
import com.ssafy.BaeAndChoi.news.repository.NewsRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCrawlingService {
    private static final String BASE_URL = "https://land.naver.com/news/headline.naver";
    private static final long PAGE_LOAD_WAIT_MS = 2_000;

    private final NewsRepository newsRepository;

    /**
     * 지정된 날짜(YYYYMMDD)의 헤드라인 뉴스를 크롤링해서 저장
     */
    @Transactional
    public void crawlNewsByDate(String date) {
        WebDriver driver = createHeadlessDriver();
        try {
            Document doc = fetchDocument(driver, date);
            parseAndSave(doc);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("크롤링 중 인터럽트 발생", e);
        } finally {
            driver.quit();
        }
    }

    private WebDriver createHeadlessDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments(
                "--headless",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080"
        );
        return new ChromeDriver(opts);
    }

    private Document fetchDocument(WebDriver driver, String date) throws InterruptedException {
        String url = BASE_URL + "?bss_ymd=" + date;
        driver.get(url);
        Thread.sleep(PAGE_LOAD_WAIT_MS);
        String html = driver.getPageSource();
        return Jsoup.parse(html);
    }

    @Transactional
    public void parseAndSave(Document doc) {
        List<News> newsList = new ArrayList<>();
        Elements items = doc.select("ul#land_news_list li.news_item");
        for (Element item : items) {
            Element linkEl  = item.selectFirst("a");
            Element imgEl   = item.selectFirst("div.thumb_img.ss img");
            Element titleEl = item.selectFirst("div.text > p.title");
            Element descEl  = item.selectFirst("div.text > p.description");
            Element srcEl   = item.selectFirst("div.text > p.description.source");

            if (linkEl == null || titleEl == null) continue;

            String link        = linkEl.absUrl("href");
            String imageUrl    = imgEl  != null ? imgEl.attr("src").trim() : "";
            String title       = titleEl.text().trim();
            String description = descEl  != null ? descEl.text().trim() : "";
            String source      = srcEl   != null ? srcEl.text().trim() : "";

            if (newsRepository.existsByLink(link)) continue;

            News news = News.builder()
                    .title(title)
                    .link(link)
                    .description(description)
                    .source(source)
                    .imageUrl(imageUrl)          // ← 넣어 줌
                    .build();
            newsList.add(news);
        }
        newsRepository.saveAll(newsList);
    }

}
