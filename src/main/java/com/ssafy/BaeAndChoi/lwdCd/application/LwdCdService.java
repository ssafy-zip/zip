package com.ssafy.BaeAndChoi.lwdCd.application;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO;
import com.ssafy.BaeAndChoi.lwdCd.repogitory.LwdCdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LwdCdService {
    private final LwdCdRepository lwdCdRepository;

    public List<LwdCd> findSido() { return lwdCdRepository.findSido(); }
    public List<LwdCd> findSggBySidoCode(String sidoCode) {
        List<LwdCd> lwdCdList = lwdCdRepository.findSggBySidoCode(sidoCode);
        if("36".equals(sidoCode) && lwdCdList.isEmpty()) { // fallback
            lwdCdList = List.of(new LwdCd("3611000000", "세종특별자치시", null, null, null));
        }
        return lwdCdList;
    }
    public List<LwdCd> findUmdBySggCode(String sggCode) { return lwdCdRepository.findUmdBySggCode(sggCode); }
    public List<LwdCd> findRiByUmdCode(String umdCode) { return lwdCdRepository.findRiByUmdCode(umdCode); }

    public Optional<LwdCd> findLwdCdByCode(String code) { return lwdCdRepository.findLwdCdByCode(code); }

    public List<LwdCdSimpleResponseDTO> findSidoSimple() { return lwdCdRepository.findSidoSimple(); }
    public List<LwdCdSimpleResponseDTO> findSggBySidoCodeSimple(String sidoCode) {
        List<LwdCdSimpleResponseDTO> lwdCdList = lwdCdRepository.findSggBySidoCodeSimple(sidoCode);
        if("36".equals(sidoCode) && lwdCdList.isEmpty()) { // fallback
            lwdCdList = List.of(new LwdCdSimpleResponseDTO("36110", null));
        }
        return lwdCdList;
    }
    public List<LwdCdSimpleResponseDTO> findUmdBySggCodeSimple(String sggCode) { return lwdCdRepository.findUmdBySggCodeSimple(sggCode); }
    public List<LwdCdSimpleResponseDTO> findRiByUmdCodeSimple(String umdCode) { return lwdCdRepository.findRiByUmdCodeSimple(umdCode); }
}
