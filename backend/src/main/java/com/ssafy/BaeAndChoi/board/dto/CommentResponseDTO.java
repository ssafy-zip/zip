package com.ssafy.BaeAndChoi.board.dto;

import com.ssafy.BaeAndChoi.board.domain.Comment;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String writerName;
    private String writerId;   // <-- 추가
    private LocalDateTime createdAt;

    public static CommentResponseDTO fromEntity(Comment c) {
        return CommentResponseDTO.builder()
                .id(Long.valueOf(c.getId()))
                .content(c.getContent())
                .writerName(c.getWriter().getName())
                .writerId(c.getWriter().getUserId())
                .createdAt(c.getCreatedAt())
                .build();
    }
}

