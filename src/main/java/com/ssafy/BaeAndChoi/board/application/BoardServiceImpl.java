package com.ssafy.BaeAndChoi.board.application;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.domain.Comment;
import com.ssafy.BaeAndChoi.board.domain.PostType;
import com.ssafy.BaeAndChoi.board.dto.*;
import com.ssafy.BaeAndChoi.board.repository.BoardRepository;
import com.ssafy.BaeAndChoi.board.repository.CommentRepository;
import com.ssafy.BaeAndChoi.exception.BadRequestException;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter DTO_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    @Transactional
    public Board findById(Integer id){
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + id));

        board.setViews(board.getViews() + 1);
        return board;
    }

    @Override
    public Page<BoardResponseDTO> getBoards(String category, String sort, int page, int size) {
        // 1) 정렬 컬럼 및 방향 결정
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy;
        switch (sort) {
            case "oldest":
                direction = Sort.Direction.ASC;
                sortBy = "createdAt";
                break;
            case "views":
                sortBy = "views";
                break;
            case "likes":
                sortBy = "likes";
                break;
            case "comments":
                sortBy = "comments";
                break;
            default:
                sortBy = "createdAt";
        }

        // 2) Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 3) 카테고리 필터링 및 String → Enum 변환
        Page<Board> resultPage;
        if (category != null && !category.isBlank()) {
            PostType postType;
            try {
                postType = PostType.valueOf(category.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("유효하지 않은 카테고리: " + category);
            }
            resultPage = boardRepository.findAllByCategory(postType, pageable);
        } else {
            resultPage = boardRepository.findAll(pageable);
        }

        // 4) DTO로 변환 후 반환
        return resultPage.map(BoardResponseDTO::fromEntity);
    }


    @Override
    @Transactional
    public BoardResponseDTO createBoard(String userId, BoardRequestDTO dto) {
        User writer = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        // 2) 엔티티 빌드
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .writer(writer)
                .views(0)
                .likes(0)
                .build();

        // 3) 저장 및 DTO 변환
        Board saved = boardRepository.save(board);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public BoardResponseDTO getBoardById(Integer id) {
        // 1) 엔티티 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + id));

        // 2) 조회수 증가
        board.setViews(board.getViews() + 1);

        // 3) 변경된 board를 DTO로 변환 (Builder 패턴)
        return BoardResponseDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writerName(board.getWriter().getName())
                .views(board.getViews())
                .createdAt(board.getCreatedAt().format(DTO_DATE_FORMATTER))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        List<BoardResponseDTO> dtos = new ArrayList<>();
        for(Board board : boards) {
            dtos.add(toDTO(board));
        }
        return dtos;
    }

    @Override
    @Transactional
    public BoardResponseDTO updateBoard(Integer id, BoardUpdateDTO dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        return toDTO(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Integer id) {
        boardRepository.deleteById(id);
    }

    private BoardResponseDTO toDTO(Board board) {
        String formattedDate = board.getCreatedAt() != null
                ? board.getCreatedAt().format(DTO_DATE_FORMATTER)
                : null;

        return BoardResponseDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writerName(board.getWriter().getName())
                .category(board.getCategory())
                .createdAt(formattedDate)
                .views(board.getViews())
                .likes(board.getLikes())
                .build();
    }

    @Override
    public List<Comment> getComments(Integer boardId) {
        // 게시글 존재 확인 (예외 처리 생략)
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "게시글 없음: " + boardId
                ));
        return commentRepository
                .findAllByBoardIdOrderByCreatedAtAsc(boardId)
                .stream()
                .toList();
    }

    @Override
    @Transactional
    public CommentResponseDTO addComment(Integer boardId, String userId, CommentRequestDTO dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "게시글 없음: " + boardId
                ));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자 없음: " + userId
                ));

        Comment comment = Comment.builder()
                .board(board)
                .writer(user)
                .content(dto.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponseDTO.fromEntity(saved);
    }

    @Override
    public void deleteComment(String userId, Integer boardId, Integer commentId) {
        // 1) 게시글이 존재하는지 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글 없음: " + boardId));

        // 2) 댓글이 존재하는지 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글 없음: " + commentId));

        // 3) 해당 댓글이 이 게시글 소속인지 검증
        if (!comment.getBoard().getId().equals(board.getId())) {
            throw new BadRequestException("댓글이 게시글 소속이 아닙니다.");
        }

        // 4) 본인 댓글인지 검증
        if(!comment.getWriter().getUserId().equals(userId)){
            throw new AccessDeniedException("본인 댓글만 삭제할 수 있습니다.");
        }

        // 5) 삭제
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentResponseDTO updateComment(String userId, Integer boardId, Integer commentId, CommentRequestDTO request) {
        // 1) 게시글 존재 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));

        // 2) 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));

        // 3) 작성자 일치 여부 검증
        if (!comment.getWriter().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인 댓글만 수정할 수 있습니다.");
        }

        // 4) 내용 업데이트
        comment.setContent(request.getContent());

        // 5) 업데이트된 댓글을 DTO로 변환
        return CommentResponseDTO.builder()
                .id(Long.valueOf(comment.getId()))
                .writerId(String.valueOf(comment.getWriter().getId()))
                .writerName(comment.getWriter().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
