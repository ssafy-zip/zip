package com.ssafy.BaeAndChoi.lwdCd.application;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import com.ssafy.BaeAndChoi.lwdCd.repogitory.LwdCdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LwdCdService {
    private final LwdCdRepository lwdCdRepository;

    public List<LwdCd> findSidoList() { return lwdCdRepository.findSidoList(); }
    public List<LwdCd> findSggListBySidoCode(String sidoCode) { return lwdCdRepository.findSggListBySidoCode(sidoCode); }
    public List<LwdCd> findUmdListBySggCode(String sggCode) { return lwdCdRepository.findUmdListBySggCode(sggCode); }
    public Optional<LwdCd> findLwdCdByCode(String code) { return lwdCdRepository.findLwdCdByCode(code); }
}
