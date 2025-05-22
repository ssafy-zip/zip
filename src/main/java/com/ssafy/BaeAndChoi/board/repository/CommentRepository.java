package com.ssafy.BaeAndChoi.board.repository;

import com.ssafy.BaeAndChoi.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByBoardIdOrderByCreatedAtAsc(Integer boardId);
    List<Comment> findAllByWriter_IdOrderByCreatedAtDesc(Integer writerId);
}
