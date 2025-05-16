package com.ssafy.BaeAndChoi.board.application;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.Comment;
import com.ssafy.BaeAndChoi.board.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {
    BoardResponseDTO createBoard(String userId, BoardRequestDTO dto);

    BoardResponseDTO getBoardById(Integer id);

    List<BoardResponseDTO> getAllBoards();

    BoardResponseDTO updateBoard(Integer id, BoardUpdateDTO dto);

    void deleteBoard(Integer id);

    Page<BoardResponseDTO> getBoards(String category, String sort, int page, int size);

    Board findById(Integer id);

    List<Comment> getComments(Integer boardId);
    CommentResponseDTO addComment(Integer boardId, String userId, CommentRequestDTO dto);

    void deleteComment(String userId, Integer boardId, Integer commentId);
    CommentResponseDTO updateComment(String userId, Integer boardId, Integer commentId, CommentRequestDTO request);

}
