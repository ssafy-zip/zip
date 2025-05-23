package com.ssafy.BaeAndChoi.interest.presentation;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.interest.application.InterestHouseService;
import com.ssafy.BaeAndChoi.interest.domain.InterestHouse;
import com.ssafy.BaeAndChoi.interest.dto.ApartmentDTO;
import com.ssafy.BaeAndChoi.interest.dto.CodeDTO;
import com.ssafy.BaeAndChoi.interest.dto.InterestHouseRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/interestHouse")
@RequiredArgsConstructor
public class InterestHouseController {
    private final InterestHouseService interestHouseService;

    /**
     * 관심 아파트 등록
     */
    @PostMapping("")
    public ResponseEntity<String> addInterestHouse(@AuthenticationPrincipal UserDetails userDetails, @RequestBody InterestHouseRequestDTO dto) {
        String userId = userDetails.getUsername();
        String aptSeq = dto.getAptSeq();

        return ResponseEntity.ok(interestHouseService.addInterestHouse(userId, aptSeq));
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteInterestHouse(@AuthenticationPrincipal UserDetails userDetails, @RequestBody InterestHouseRequestDTO dto) {
        String userId = userDetails.getUsername();
        String aptSeq = dto.getAptSeq();

        return ResponseEntity.ok(interestHouseService.deleteInterestHouse(userId, aptSeq));
    }

    @GetMapping("/interestHouses")
    public ResponseEntity<List<Apartment>> getInterestHouses(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(required = false) String si,
                                                                @RequestParam(required = false) String gun,
                                                                @RequestParam(required = false) String gu) {
        String userId = userDetails.getUsername();

        return ResponseEntity.ok(interestHouseService.getInterestApartments(userId, si, gun, gu));
    }
}
