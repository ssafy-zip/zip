package com.ssafy.BaeAndChoi.house.presentation;

import com.ssafy.BaeAndChoi.house.application.ApartmentService;
import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.ApartmentDeal;
import com.ssafy.BaeAndChoi.house.domain.AptTradeBasicData;
import com.ssafy.BaeAndChoi.house.domain.SearchOption;
import com.ssafy.BaeAndChoi.lwdCd.application.LwdCdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/apartments")
@RequiredArgsConstructor
public class ApartmentController {
    private final ApartmentService apartmentService;
    private final LwdCdService lwdCdService;
    @GetMapping("/insertApartmentDeal")
    public ResponseEntity<String> insertHouseDealInfo(@RequestParam(required = false) String code) {
        List<String> dongCodes = lwdCdService.findUniqueDongCodes();
        HashSet<String> codes = new HashSet<>();
        codes.addAll(dongCodes);

        ArrayList<AptTradeBasicData> aptTradeBasicDataList = apartmentService.getRealEstateDealInfo(codes);
        apartmentService.addApartmentDeal(aptTradeBasicDataList);
        return new ResponseEntity<>("success", HttpStatus.OK);
//        return ResponseEntity.ok("총 " + dongCodes.size() + "개의 동코드를 가져왔습니다.");
    }

    @GetMapping("/deals")
    public ResponseEntity<List<ApartmentDeal>> selectApartmentDeal(@RequestParam String aptNm){
        List<ApartmentDeal> apartmentDealList = apartmentService.findDealsByApartmentName(aptNm);
        return ResponseEntity.ok(apartmentDealList);
    }

    @GetMapping("/apt")
    public ResponseEntity<List<Apartment>> selectApartment(@ModelAttribute SearchOption searchOption){
        List<Apartment> apartmentList = apartmentService.searchApartments(searchOption);
        return ResponseEntity.ok(apartmentList);
    }
}