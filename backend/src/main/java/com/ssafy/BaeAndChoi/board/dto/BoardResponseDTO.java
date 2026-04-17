package com.ssafy.BaeAndChoi.board.dto;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.PostType;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BoardResponseDTO {
    private Integer id;
    private String title;
    private String writerName;
    private String createdAt;      // 포맷된 문자열
    private Integer views;
    private Integer likes;
    private PostType category;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    /**
     * 엔티티를 DTO로 변환하며, 생성일시를 포맷된 문자열로 변환합니다.
     */
    public static BoardResponseDTO fromEntity(Board entity) {
        String formattedDate = entity.getCreatedAt() != null
                ? entity.getCreatedAt().format(FORMATTER)
                : null;

        return BoardResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .writerName(entity.getWriter().getName())
                .category(entity.getCategory())
                .createdAt(formattedDate)
                .views(entity.getViews())
                .likes(entity.getLikes())
                .build();
    }

    @Override
    public String toString() {
        return "BoardResponseDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", writerName='" + writerName + '\'' +
                ", category=" + category +
                ", createdAt='" + createdAt + '\'' +
                ", views=" + views +
                ", likes=" + likes +
                '}';
    }
}