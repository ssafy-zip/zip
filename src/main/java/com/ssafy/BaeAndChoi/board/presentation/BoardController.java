package com.ssafy.BaeAndChoi.board.presentation;

import com.ssafy.BaeAndChoi.board.application.BoardService;
import com.ssafy.BaeAndChoi.board.dto.BoardRequestDTO;
import com.ssafy.BaeAndChoi.board.dto.BoardResponseDTO;
import com.ssafy.BaeAndChoi.board.dto.BoardUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponseDTO> create(@RequestBody BoardRequestDTO dto){
        return ResponseEntity.ok(boardService.createBoard(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getById(@PathVariable Integer id){
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAll(){
        return ResponseEntity.ok(boardService.getAllBoards());
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
}
