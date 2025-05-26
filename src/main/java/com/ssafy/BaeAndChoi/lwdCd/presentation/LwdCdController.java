package com.ssafy.BaeAndChoi.lwdCd.presentation;

import com.ssafy.BaeAndChoi.lwdCd.application.LwdCdService;
import com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdResponseDTO;
import com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lwdCd")
@RequiredArgsConstructor
@Validated
public class LwdCdController {
    private final LwdCdService lwdCdService;


    private static <T> ResponseEntity<List<T>> wrapResponse(List<T> list) {
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * 시/도 목록을 반환한다.
     */
    @GetMapping("/sido")
    public ResponseEntity<List<LwdCdResponseDTO>> getSidoList() {
        List<LwdCdResponseDTO> responseList = lwdCdService.findSido()
                .stream().map(LwdCdResponseDTO::fromEntity).collect(Collectors.toList());

        return wrapResponse(responseList);
    }
    @GetMapping("/sido/simple")
    public ResponseEntity<List<LwdCdSimpleResponseDTO>> getSidoListSimple() {
        List<LwdCdSimpleResponseDTO> responseList = lwdCdService.findSidoSimple();

        return wrapResponse(responseList);
    }

    /**
     * 해당 시/도 에 속한 시/군/구 목록을 반환한다.
     * @param code 시/도 코드 (2자리)
     */
    @GetMapping("/sgg/{code}")
    public ResponseEntity<List<LwdCdResponseDTO>> getSggList(
            @PathVariable
            @Pattern(regexp = "\\d{2}", message = "시/도 코드는 2자리 숫자여야 합니다.")
            String code) {
        List<LwdCdResponseDTO> responseList = lwdCdService.findSggBySidoCode(code)
                .stream().map(LwdCdResponseDTO::fromEntity).collect(Collectors.toList());

        return wrapResponse(responseList);
    }
    @GetMapping("/sgg/{code}/simple")
    public ResponseEntity<List<LwdCdSimpleResponseDTO>> getSggListSimple(
            @PathVariable
            @Pattern(regexp = "\\d{2}", message = "시/도 코드는 2자리 숫자여야 합니다.")
            String code) {
        List<LwdCdSimpleResponseDTO> responseList = lwdCdService.findSggBySidoCodeSimple(code);

        System.out.println("responseList = " + responseList);
        return wrapResponse(responseList);
    }

    /**
     * 해당 시/군/구 에 속한 읍/면/동 목록을 반환한다.
     * @param code 시/군/구 코드 (5자리)
     */
    @GetMapping("/umd/{code}")
    public ResponseEntity<List<LwdCdResponseDTO>> getUmdList(
            @PathVariable
            @Pattern(regexp = "\\d{5}", message = "시/군/구 코드는 5자리 숫자여야 합니다.")
            String code) {
        List<LwdCdResponseDTO> responseList = lwdCdService.findUmdBySggCode(code)
                .stream().map(LwdCdResponseDTO::fromEntity).collect(Collectors.toList());

        return wrapResponse(responseList);
    }

    @GetMapping("/umd/{code}/simple")
    public ResponseEntity<List<LwdCdSimpleResponseDTO>> getUmdListSimple(
            @PathVariable
            @Pattern(regexp = "\\d{5}", message = "시/군/구 코드는 5자리 숫자여야 합니다.")
            String code) {
        List<LwdCdSimpleResponseDTO> responseList = lwdCdService.findUmdBySggCodeSimple(code);

        System.out.println("responseList = " + responseList);
        return wrapResponse(responseList);
    }

    /**
     * 해당 읍/면/동 에 속한 리 목록을 반환한다.
     * @param code 읍/면/동 코드 (8자리)
     */
    @GetMapping("/ri/{code}")
    public ResponseEntity<List<LwdCdResponseDTO>> getRiList(
            @PathVariable
            @Pattern(regexp = "\\d{8}", message = "읍/면/동 코드는 8자리 숫자여야 합니다.")
            String code) {
        List<LwdCdResponseDTO> responseList = lwdCdService.findRiByUmdCode(code)
                .stream().map(LwdCdResponseDTO::fromEntity).collect(Collectors.toList());

        return wrapResponse(responseList);
    }

    @GetMapping("/ri/{code}/simple")
    public ResponseEntity<List<LwdCdSimpleResponseDTO>> getRiListSimple(
            @PathVariable
            @Pattern(regexp = "\\d{8}", message = "읍/면/동 코드는 8자리 숫자여야 합니다.")
            String code) {
        List<LwdCdSimpleResponseDTO> responseList = lwdCdService.findRiByUmdCodeSimple(code);

        return wrapResponse(responseList);
    }

    /**
     * 법정동을 반환한다.
     * @param code 법정동 코드 (10자리)
     */
    @GetMapping({"{code}", "/detail/{code}"})
    public ResponseEntity<LwdCdResponseDTO> getLwdCdByCode(
            @PathVariable
            @Pattern(regexp = "\\d{10}", message = "법정동 코드는 10자리 숫자여야 합니다.")
            String code) {
        return lwdCdService.findLwdCdByCode(code)
                .map(entity -> ResponseEntity.ok(LwdCdResponseDTO.fromEntity(entity)))
                .orElse(ResponseEntity.notFound().build());
    }
}