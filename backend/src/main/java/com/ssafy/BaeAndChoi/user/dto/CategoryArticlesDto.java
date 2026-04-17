package com.ssafy.BaeAndChoi.user.dto;

import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryArticlesDto {
    private PostType category;
    private List<MyArticleDto> articles;
}