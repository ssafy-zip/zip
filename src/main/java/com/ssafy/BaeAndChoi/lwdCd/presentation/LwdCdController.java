package com.ssafy.BaeAndChoi.lwdCd.presentation;

import com.ssafy.BaeAndChoi.lwdCd.application.LwdCdService;
import com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lwdCd")
@RequiredArgsConstructor
public class LwdCdController {
    private final LwdCdService lwdCdService;

    @GetMapping("/sido")
    public List<LwdCdResponseDTO> getSidoList() {
        return lwdCdService.findSidoList().stream()
                .map(LwdCdResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/sgg/{code}")
    public List<LwdCdResponseDTO> getSggNames(@PathVariable String code) {
        return lwdCdService.findSggListBySidoCode(code).stream()
                .map(LwdCdResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/umd/{code}")
    public List<LwdCdResponseDTO> getUmdNames(@PathVariable String code) {
        return lwdCdService.findUmdListBySggCode(code).stream()
                .map(LwdCdResponseDTO::fromEntity)
                .toList();
    }
}
