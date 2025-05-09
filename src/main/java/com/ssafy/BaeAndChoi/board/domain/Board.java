package com.ssafy.BaeAndChoi.board.domain;

import com.ssafy.BaeAndChoi.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    /** 게시글 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 게시글 작성자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    /** 게시글 제목 */
    private String title;

    /** 게시글 내용 (길이 제한 없음) */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 게시글 생성일시 */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 게시글 수정일시 */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /** 조회수 (기본값 0) */
    @Column(nullable = false)
    private Integer views = 0;

    /** 좋아요 수 (기본값 0) */
    @Column(nullable = false)
    private Integer likes = 0;

    /** 이 게시글에 달린 댓글 목록 */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
