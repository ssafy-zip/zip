package com.ssafy.BaeAndChoi.board.application;

import com.ssafy.BaeAndChoi.board.domain.Board;
import com.ssafy.BaeAndChoi.board.dto.BoardRequestDTO;
import com.ssafy.BaeAndChoi.board.dto.BoardResponseDTO;
import com.ssafy.BaeAndChoi.board.dto.BoardUpdateDTO;
import com.ssafy.BaeAndChoi.board.repository.BoardRepository;
import com.ssafy.BaeAndChoi.user.domain.User;
import com.ssafy.BaeAndChoi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public BoardResponseDTO createBoard(BoardRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .views(0)
                .likes(0)
                .build();

        Board svaed = boardRepository.save(board);
        return toDTO(svaed);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO getBoardById(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("게시글 없음"));
        return toDTO(board);
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
        return BoardResponseDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writerName(board.getUser().getName())
                .writerId(board.getUser().getId())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .views(board.getViews())
                .likes(board.getLikes())
                .build();
    }
}
