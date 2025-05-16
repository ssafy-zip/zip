package com.ssafy.BaeAndChoi.news.repository;

import com.ssafy.BaeAndChoi.news.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface NewsRepository extends JpaRepository<News, Integer> {
    boolean existsByLink(String link);

    Page<News> findAllByOrderByCrawledAtDesc(Pageable pageable);
}
