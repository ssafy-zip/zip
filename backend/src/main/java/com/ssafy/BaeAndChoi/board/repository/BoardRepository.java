package com.ssafy.BaeAndChoi.board.repository;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.PostType;
import com.ssafy.BaeAndChoi.user.domain.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByCategory(PostType category, Pageable pageable);
    Page<Board> findByWriter_idAndCategory(int writerId, PostType category, Pageable pageable);

    List<Board> findByWriter_IdOrderByCreatedAtDesc(int id);
}
