package com.ssafy.BaeAndChoi.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequestDTO {
    private String title;
    private String content;
    private Integer userId;
}
