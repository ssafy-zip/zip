package com.ssafy.BaeAndChoi.user.dto;

import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 내가 작성한 댓글 하나의 정보
 */
@Getter
@Builder
public class MyCommentDto {
    private Integer commentId;       // 댓글 ID
    private Integer boardId;         // 댓글이 달린 게시글 ID
    private String boardTitle;       // 게시글 제목
    private String content;          // 댓글 내용
    private LocalDateTime createdAt; // 댓글 작성일시
    private LocalDateTime updatedAt; // 댓글 수정일시

    public static MyCommentDto fromEntity(com.ssafy.BaeAndChoi.board.domain.Comment c) {
        return MyCommentDto.builder()
            .commentId(c.getId())
            .boardId(c.getBoard().getId())
            .boardTitle(c.getBoard().getTitle())
            .content(c.getContent())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }
}