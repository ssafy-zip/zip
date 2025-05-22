package com.ssafy.BaeAndChoi.user.dto;

import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 카테고리별로 댓글 목록을 묶어 반환
 */
@Getter
@Builder
public class CategoryCommentsDto {
    private PostType category;          // 게시판 카테고리
    private List<MyCommentDto> comments; // 해당 카테고리에 달린 내 댓글들
}