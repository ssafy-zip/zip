package com.ssafy.BaeAndChoi.interest.application;

import com.ssafy.BaeAndChoi.exception.BadRequestException;
import com.ssafy.BaeAndChoi.house.application.ApartmentService;
import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.SearchOption;
import com.ssafy.BaeAndChoi.interest.domain.InterestHouse;
import com.ssafy.BaeAndChoi.interest.dto.ApartmentDTO;
import com.ssafy.BaeAndChoi.interest.dto.ApartmentDealDTO;
import com.ssafy.BaeAndChoi.interest.repogitory.InterestHouseRepository;
import com.ssafy.BaeAndChoi.user.application.UserService;
import com.ssafy.BaeAndChoi.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestHouseService {
    private final UserService userService;
    private final InterestHouseRepository interestHouseRepository;
    private final ApartmentService apartmentService;
    /**
     * 관심 아파트 등록
     */
    @Transactional
    public String addInterestHouse(String userId, String aptSeq){
        User user = userService.getUser(userId);

        Apartment apartment = apartmentService.getApartmentByAptSeq(aptSeq);

        if(interestHouseRepository.existsByUserAndApartment(user, apartment)){
            throw new BadRequestException("이미 등록된 아파트입니다.");
        }
        InterestHouse interestHouse = InterestHouse.builder()
                .apartment(apartment)
                .user(user)
                .build();

        interestHouseRepository.save(interestHouse);
        return "success";
    }

    @Transactional
    public String deleteInterestHouse(String userId, String aptSeq){
        User user = userService.getUser(userId);

        Apartment apartment = apartmentService.getApartmentByAptSeq(aptSeq);
        if(!interestHouseRepository.existsByUserAndApartment(user, apartment)){
            throw new BadRequestException("등록되어있지 않은 아파트입니다.");
        }

        InterestHouse interestHouse = interestHouseRepository.findByUserAndApartment(user, apartment);
        interestHouseRepository.delete(interestHouse);

        return "success";
    }

    /**
     * 관심 아파트 목록 반환 (DB JOIN으로 필터링)
     *
     * @param userId 사용자 아이디
     * @param si     시 코드 (sggCd 앞 2자리), null일 경우 전체
     * @param gun    군 코드 (sggCd 뒤 3자리), null일 경우 전체
     * @param gu     동(구) 코드 (umdCd), null일 경우 전체
     * @return 필터된 관심 아파트 목록
     */
    public List<Apartment> getInterestApartments(String userId,String aptName, String si, String gun, String gu) {
        return interestHouseRepository.findInterestHousesByUserAndRegion(userId, aptName, si, gun, gu)
                .stream()
                .map(InterestHouse::getApartment)
                .collect(Collectors.toList());
    }

    public String isInterestApartment(String userId, String aptSeq) {
        User user = userService.getUser(userId);
        Apartment apartment = apartmentService.getApartmentByAptSeq(aptSeq);

        return interestHouseRepository.existsByUserAndApartment(user, apartment) ? "true" : "false";
    }
}
