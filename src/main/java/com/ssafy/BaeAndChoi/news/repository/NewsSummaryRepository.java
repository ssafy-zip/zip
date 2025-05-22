package com.ssafy.BaeAndChoi.news.repository;

import com.ssafy.BaeAndChoi.news.domain.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long> {
    /**
     * 주어진 날짜(date)에 저장된 NewsSummary 중,
     * 가장 큰 id(=가장 나중에 저장된) 한 건을 Optional로 반환
     */
    Optional<NewsSummary> findFirstByDate(LocalDate date);
}