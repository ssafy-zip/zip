package com.ssafy.BaeAndChoi.house.presentation;

import com.ssafy.BaeAndChoi.house.application.ApartmentService;
import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.ApartmentDeal;
import com.ssafy.BaeAndChoi.house.domain.AptTradeBasicData;
import com.ssafy.BaeAndChoi.house.domain.SearchOption;
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

    @GetMapping("/insertApartmentDeal")
    public ResponseEntity<String> insertHouseDealInfo(@RequestParam(required = false) String code) {
        HashSet<String> dongCodes = apartmentService.requestDongCodes();
        log.info("최종 수집된 동코드 : {}", dongCodes);
        dongCodes.clear();
        dongCodes.add(code != null ? code : "11110");
        ArrayList<AptTradeBasicData> aptTradeBasicDataList = apartmentService.getRealEstateDealInfo(dongCodes);
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