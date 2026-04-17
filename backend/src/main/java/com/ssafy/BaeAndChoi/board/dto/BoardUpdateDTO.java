package com.ssafy.BaeAndChoi.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUpdateDTO {
    private String title;
    private String content;
}
