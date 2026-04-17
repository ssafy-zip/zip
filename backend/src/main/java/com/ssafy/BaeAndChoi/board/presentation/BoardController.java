package com.ssafy.BaeAndChoi.board.presentation;

import com.ssafy.BaeAndChoi.board.application.BoardService;
import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.Comment;
import com.ssafy.BaeAndChoi.board.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponseDTO> createBoard(
            @RequestBody BoardRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();  // JWT 의 subject
        BoardResponseDTO result = boardService.createBoard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailResponseDTO> getBoardDetail(@PathVariable Integer id) {
        Board board = boardService.findById(id);
        return ResponseEntity.ok(BoardDetailResponseDTO.fromEntity(board));
    }


    @GetMapping
    public ResponseEntity<Page<BoardResponseDTO>> getBoards(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BoardResponseDTO> boards = boardService.getBoards(category, sort, page, size);
        return ResponseEntity.ok(boards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> update(@PathVariable Integer id, @RequestBody BoardUpdateDTO dto){
        return ResponseEntity.ok(boardService.updateBoard(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable Integer boardId
    ) {
        List<CommentResponseDTO> dtoList = boardService.getComments(boardId).stream()
                .map(CommentResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 댓글 등록
     */
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Integer boardId,
            @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        CommentResponseDTO saved = boardService.addComment(boardId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    @DeleteMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer boardId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        boardService.deleteComment(userId, boardId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Integer boardId,
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        CommentResponseDTO updated = boardService.updateComment(userId, boardId, commentId, request);
        return ResponseEntity.ok(updated);
    }
}
