package com.ssafy.BaeAndChoi.board.application;

import com.ssafy.BaeAndChoi.board.dto.BoardRequestDTO;
import com.ssafy.BaeAndChoi.board.dto.BoardResponseDTO;
import com.ssafy.BaeAndChoi.board.dto.BoardUpdateDTO;

import java.util.List;

public interface BoardService {
    BoardResponseDTO createBoard(BoardRequestDTO dto);

    BoardResponseDTO getBoardById(Integer id);

    List<BoardResponseDTO> getAllBoards();

    BoardResponseDTO updateBoard(Integer id, BoardUpdateDTO dto);

    void deleteBoard(Integer id);
}
