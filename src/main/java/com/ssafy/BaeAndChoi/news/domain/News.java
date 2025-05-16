package com.ssafy.BaeAndChoi.news.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true, length = 500)
    private String link;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String source;

    @Column(length = 500)
    private String imageUrl;   // ← 추가

    @Column(nullable = false)
    private LocalDateTime crawledAt;

    @PrePersist
    public void prePersist() {
        this.crawledAt = LocalDateTime.now();
    }
}
