package com.ssafy.BaeAndChoi.board.dto;

import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequestDTO {
    private String title;
    private String content;
    private PostType category;
}