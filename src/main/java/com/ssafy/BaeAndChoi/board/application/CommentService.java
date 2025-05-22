package com.ssafy.BaeAndChoi.board.application;
import com.ssafy.BaeAndChoi.board.domain.Comment;
import com.ssafy.BaeAndChoi.board.domain.PostType;
import com.ssafy.BaeAndChoi.board.repository.CommentRepository;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.dto.CategoryCommentsDto;
import com.ssafy.BaeAndChoi.user.dto.MyCommentDto;
import com.ssafy.BaeAndChoi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * 카테고리별로 내가 쓴 댓글 반환
     */
    public List<CategoryCommentsDto> findMyCommentsByCategory(String userId) {
        // 1) User 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음: " + userId));

        // 2) 내 댓글 전체 최신순 조회
        List<Comment> allComments = commentRepository
                .findAllByWriter_IdOrderByCreatedAtDesc(user.getId());

        // 3) 엔티티에서 바로 PostType 으로 그룹화하고, DTO 매핑
        Map<PostType, List<MyCommentDto>> grouped = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getBoard().getCategory(),
                        LinkedHashMap::new,
                        Collectors.mapping(MyCommentDto::fromEntity, Collectors.toList())
                ));

        // 4) CategoryCommentsDto 생성
        return grouped.entrySet().stream()
                .map(e -> CategoryCommentsDto.builder()
                        .category(e.getKey())
                        .comments(e.getValue())
                        .build()
                )
                .collect(Collectors.toList());
    }
}