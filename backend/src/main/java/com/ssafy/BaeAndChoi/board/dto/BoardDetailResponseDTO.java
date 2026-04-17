package com.ssafy.BaeAndChoi.board.dto;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BoardDetailResponseDTO {
    private Integer id;
    private String title;
    private String content;
    private String writerId;
    private String writerName;
    private PostType category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;
    private Integer likes;
    private List<CommentResponseDTO> comments;

    public static BoardDetailResponseDTO fromEntity(Board entity) {
        List<CommentResponseDTO> commentDTOs = entity.getComments().stream()
                .map(CommentResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return BoardDetailResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writerId(entity.getWriter().getUserId())
                .writerName(entity.getWriter().getName())
                .category(entity.getCategory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .views(entity.getViews())
                .likes(entity.getLikes())
                .comments(commentDTOs)
                .build();
    }
}
