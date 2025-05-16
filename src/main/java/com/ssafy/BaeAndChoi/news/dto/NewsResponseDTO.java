package com.ssafy.BaeAndChoi.news.dto;

import com.ssafy.BaeAndChoi.news.domain.News;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class NewsResponseDTO {
    private final Long id;
    private final String title;
    private final String link;
    private final String thumbnail;
    private final String description;
    private final String source;
    private final String crawledAt;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private NewsResponseDTO(News n) {
        this.id = n.getId();
        this.title = n.getTitle();
        this.link = n.getLink();
        this.thumbnail = n.getImageUrl();
        this.description = n.getDescription();
        this.source = n.getSource();
        this.crawledAt = n.getCrawledAt().format(FMT);
    }

    public static NewsResponseDTO fromEntity(News n) {
        return new NewsResponseDTO(n);
    }
}
