package com.ssafy.BaeAndChoi.news.application;

import com.ssafy.BaeAndChoi.news.dto.NewsResponseDTO;
import com.ssafy.BaeAndChoi.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    /**
     * 가장 최신 크롤링된 뉴스부터 지정 개수만큼 DTO 로 반환
     */
    public List<NewsResponseDTO> findRecent(int count) {
        var page = newsRepository.findAllByOrderByCrawledAtDesc(
                PageRequest.of(0, count)
        );
        return page.getContent().stream()
                .map(NewsResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
