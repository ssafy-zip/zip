package com.ssafy.BaeAndChoi.user.dto;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyArticleDto {
    private Integer id;
    private String title;
    private PostType category;
    private LocalDateTime createdAt;
    private Integer views;
    private Integer likes;

    // static 팩토리 메서드로도 사용
    public static MyArticleDto from(Board board) {
        return MyArticleDto.builder()
            .id(board.getId())
            .title(board.getTitle())
            .category(board.getCategory())
            .createdAt(board.getCreatedAt())
            .views(board.getViews())
            .likes(board.getLikes())
            .build();
    }
}