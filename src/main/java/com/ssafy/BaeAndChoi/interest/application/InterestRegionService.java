package com.ssafy.BaeAndChoi.interest.application;

import com.ssafy.BaeAndChoi.exception.BadRequestException;
import com.ssafy.BaeAndChoi.interest.domain.InterestRegion;
import com.ssafy.BaeAndChoi.interest.dto.InterestRegionResponseDTO;
import com.ssafy.BaeAndChoi.interest.repogitory.InterestRegionRepository;
import com.ssafy.BaeAndChoi.lwdCd.application.LwdCdService;
import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import com.ssafy.BaeAndChoi.user.application.UserService;
import com.ssafy.BaeAndChoi.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestRegionService {
    private final LwdCdService lwdCdService;
    private final InterestRegionRepository interestRegionRepository;
    private final UserService userService;

    @Transactional
    public String insertInterestRegion(String userId, String lwdcd) {
        User user = userService.getUser(userId);
        LwdCd lwdCd = lwdCdService.findLwdCdByCode(lwdcd)
                .orElseThrow(() -> new BadRequestException("읍면동 코드가 잘못되었습니다."));

        // 중복 검사
        if (interestRegionRepository.existsByUserAndLwdCd(user, lwdCd)) {
            throw new BadRequestException("이미 관심지역으로 등록되어 있습니다.");
        }

        InterestRegion interestRegion = InterestRegion.builder()
                .user(user)
                .lwdCd(lwdCd)
                .build();

        interestRegionRepository.save(interestRegion);
        return "success";
    }

    /**
     * 관심지역 조회
     */
    public List<InterestRegionResponseDTO> getAllInterestRegion(String userId) {
        User user = userService.getUser(userId);
        List<InterestRegion> regions = interestRegionRepository.findAllByUser(user);
        return regions.stream()
                .map(InterestRegionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public String deleteInterestRegion(String userId, String lwdcd){
        User user = userService.getUser(userId);
        LwdCd lwdCd = lwdCdService.findLwdCdByCode(lwdcd)
                .orElseThrow(() -> new BadRequestException("법정동 코드가 잘못되었습니다."));
        InterestRegion interestRegion = interestRegionRepository.findByLwdCdAndUser(lwdCd, user)
                .orElseThrow(() -> new BadRequestException("이미 삭제된 지역입니다."));

        interestRegionRepository.delete(interestRegion);

        return "success";
    }

    public String isInterestRegionExist(String userId, String code){
        User user = userService.getUser(userId);
        LwdCd lwdCd = lwdCdService.findLwdCdByCode(code)
                .orElseThrow(() -> new BadRequestException("법정동 코드가 잘못되었습니다."));

        return interestRegionRepository.existsByUserAndLwdCd(user,lwdCd) ? "true" : "false";
    }
}
