package com.ssafy.BaeAndChoi.interest.presentation;

import com.ssafy.BaeAndChoi.interest.application.InterestRegionService;
import com.ssafy.BaeAndChoi.interest.domain.InterestRegion;
import com.ssafy.BaeAndChoi.interest.dto.InterestRegionRequestDTO;
import com.ssafy.BaeAndChoi.interest.dto.InterestRegionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/interestRegion")
@RequiredArgsConstructor
public class InterestRegionContoller {
    private final InterestRegionService interestRegionService;

    @PostMapping("")
    public ResponseEntity<String> addInterestRegion(@AuthenticationPrincipal UserDetails userDetails, @RequestBody InterestRegionRequestDTO dto) {
        String userId = userDetails.getUsername();

        return ResponseEntity.ok(interestRegionService.insertInterestRegion(userId,dto.getLwdCd()));
    }

    @GetMapping("/interestRegions")
    public ResponseEntity<List<InterestRegionResponseDTO>> getInterestRegions(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null){
            return ResponseEntity.ok(Collections.emptyList());
        }

        String userId = userDetails.getUsername();
        return ResponseEntity.ok(interestRegionService.getAllInterestRegion(userId));
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteInterestRegion(@AuthenticationPrincipal UserDetails userDetails, String lwdCd) {
        String userId = userDetails.getUsername();

        return ResponseEntity.ok(interestRegionService.deleteInterestRegion(userId,lwdCd));
    }

    @GetMapping("/isInterestRegion")
    public ResponseEntity<String> isInterestRegion(@AuthenticationPrincipal UserDetails userDetails, String lwdCd) {
        if(userDetails == null){
            return ResponseEntity.ok("false");
        }

        String userId = userDetails.getUsername();

        return ResponseEntity.ok(interestRegionService.isInterestRegionExist(userId,lwdCd));
    }
}
