package com.ssafy.BaeAndChoi.house.application;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ssafy.BaeAndChoi.house.domain.*;
import com.ssafy.BaeAndChoi.house.repository.ApartmentDealRepository;
import com.ssafy.BaeAndChoi.house.repository.ApartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApartmentService {
    @Value("${api.key}")
    private String encodedApiKey;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentDealRepository apartmentDealRepository;

    private static final List<String> REGIONS = List.of(
            "부산광역시", "서울특별시", "대구광역시", "인천광역시",
            "광주광역시", "대전광역시", "울산광역시", "세종특별자치시",
            "경기도", "충청북도", "충청남도", "전라남도",
            "경상북도", "경상남도", "강원특별자치도", "전북특별자치도",
            "제주특별자치도"
    );

    public HashSet<String> requestDongCodes() {
        String baseUrl = "https://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList";
        RestTemplate template = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        HashSet<String> result = new HashSet<>();

        for (String regionName : REGIONS) {
            try {
                // 지역명 인코딩
                String encodedLocation = URLEncoder.encode(regionName, StandardCharsets.UTF_8);

                // URI 구성
                URI uri = UriComponentsBuilder
                        .fromHttpUrl(baseUrl)
                        .queryParam("serviceKey", encodedApiKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("type", "json")
                        .queryParam("locatadd_nm", encodedLocation)
                        .build(true)
                        .toUri();

//                log.info("API 호출 [{}]: {}", regionName, uri);
                String jsonResponse = template.getForObject(uri, String.class);
//                log.info("API 응답 [{}]: {}", regionName, jsonResponse);

                // 에러가 XML이면 스킵
                if (jsonResponse != null && jsonResponse.trim().startsWith("<")) {
                    log.error("API 에러 [{}]: {}", regionName, jsonResponse);
                    continue;
                }

                // JSON 파싱
                JsonNode root = mapper.readTree(jsonResponse);
                JsonNode rows = root.path("StanReginCd").findValue("row");
                if (rows != null && rows.isArray()) {
                    for (JsonNode node : rows) {
                        String regionCode = node.path("region_cd").asText();
                        if (regionCode.length() >= 5) {
                            result.add(regionCode.substring(0, 5));
                        }
                    }
                } else {
                    log.warn("[{}] row 항목이 비어있거나 배열이 아님", regionName);
                }

            } catch (Exception e) {
                log.error("API 요청 또는 파싱 실패 [{}]: {}", regionName, e.getMessage());
            }
        }
        return result;
    }

    /**
     * 법정동 코드를 바탕으로 아파트 매매 실거래 자료 조회 (YYYYMM 범위: 202001~202504)
     * @param dongCodes 앞 5자리 동코드 집합
     * @return API 응답문자열 리스트 (XML 또는 JSON raw)
     */
    public ArrayList<AptTradeBasicData> getRealEstateDealInfo(HashSet<String> dongCodes) {
        String baseUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptTradeDev";
        RestTemplate template = new RestTemplate();
        XmlMapper xmlMapper = new XmlMapper();
        ArrayList<AptTradeBasicData> results = new ArrayList<>();

        YearMonth startYm = YearMonth.of(2020, 1);
        YearMonth endYm = YearMonth.of(2020, 2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

        for (YearMonth ym = startYm; !ym.isAfter(endYm); ym = ym.plusMonths(1)) {
            String dealYmd = ym.format(formatter);
            log.info("{}월 데이터 수집중", startYm);
            for (String code : dongCodes) {
                try {
                    URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                            .queryParam("serviceKey", encodedApiKey)
                            .queryParam("LAWD_CD", code)
                            .queryParam("DEAL_YMD", dealYmd)
                            .queryParam("pageNo",1)
                            .queryParam("numOfRows",10000)
                            .build(true)
                            .toUri();

                    String resp = template.getForObject(uri, String.class);
                    if (resp != null && resp.startsWith("<")) {
                        log.info("resp : {}",resp);
                        // XML 파싱 → VO
                        AptTradeResponse response = xmlMapper.readValue(resp, AptTradeResponse.class);
                        if (response.getBody() != null && response.getBody().getItems() != null) {
                            List<AptTradeBasicData> items = response.getBody().getItems().getItem();
                            results.addAll(items);
                            log.info("AptTrade API 요청 성공 [code={}, ymd={}]", code, dealYmd);
                        }
                    } else {
                        log.error("AptTrade API 에러 [code={}, ymd={}]: {}", code, dealYmd, resp);
                    }
                } catch (Exception e) {
                    log.error("AptTrade 요청 실패 [code={}, ymd={}]: {}", code, dealYmd, e.getMessage());
                }
            }
        }
        return results;
    }

    /**
     * 아파트 거래 내역을 받아
     * - 공통 정보(Apartment)에 없으면 저장
     * - 거래 내역(ApartmentDeal)을 생성하여 연관관계 저장
     */
    @Transactional
    public void addApartmentDeal(List<AptTradeBasicData> dealList) {
        for (AptTradeBasicData dto : dealList) {
            // 공통 아파트 정보 조회/생성
            String aptNm = dto.getAptNm();
            Optional<Apartment> opt = apartmentRepository.findApartmentByAptNmAndSggCdAndUmdCd(aptNm, dto.getSggCd(), dto.getUmdCd());
            Apartment apartment = opt.orElseGet(() -> {
                Apartment a = Apartment.builder()
                        .aptSeq(dto.getAptSeq())
                        .aptNm(dto.getAptNm())
                        .bonbun(dto.getBonbun())
                        .bubun(dto.getBubun())
                        .buildYear(dto.getBuildYear() != null ? dto.getBuildYear() : 0)
                        .umdCd(dto.getUmdCd())
                        .umdNm(dto.getUmdNm())
                        .roadNm(dto.getRoadNm())
                        .roadNmBonbun(dto.getRoadNmBonbun())
                        .roadNmBubun(dto.getRoadNmBubun())
                        .roadNmCd(dto.getRoadNmCd())
                        .roadNmSeq(dto.getRoadNmSeq())
                        .roadNmSggCd(dto.getRoadNmSggCd())
                        .roadNmbCd(dto.getRoadNmbCd())
                        .sggCd(dto.getSggCd())
                        .landCd(dto.getLandCd())
                        .build();
                return apartmentRepository.save(a);
            });

            // 거래 내역 생성 및 저장
            ApartmentDeal deal = ApartmentDeal.builder()
                    .dealYmd(String.format("%04d%02d", dto.getDealYear(), dto.getDealMonth()))
                    .dealDay(dto.getDealDay() != null ? dto.getDealDay() : 0)
                    .dealAmount(dto.getDealAmount())
                    .excluUseAr(dto.getExcluUseAr() != null ? dto.getExcluUseAr() : 0)
                    .floor(dto.getFloor() != null ? dto.getFloor() : 0)
                    .buyerGbn(dto.getBuyerGbn())
                    .cdealDay(dto.getCdealDay())
                    .cdealType(dto.getCdealType())
                    .dealingGbn(dto.getDealingGbn())
                    .estateAgentSggNm(dto.getEstateAgentSggNm())
                    .jibun(dto.getJibun())
                    .rgstDate(dto.getRgstDate())
                    .slerGbn(dto.getSlerGbn())
                    .apartment(apartment)
                    .build();
            apartmentDealRepository.save(deal);
        }
    }

    /**
     * 아파트명을 기반으로 해당 거래 내역 리스트 반환
     */
    public List<ApartmentDeal> findDealsByApartmentName(String aptNm) {
        Apartment apartment = apartmentRepository.findApartmentByAptNm(aptNm)
                .orElseThrow(() -> new EntityNotFoundException("Apartment not found: " + aptNm));
        // 거래 내역 조회
        return apartmentDealRepository.findAllByApartment(apartment);
    }

    /**
     * 아파트명을 기반으로 아파트 정보 반환
     */
    public List<Apartment> findAptByApartmentName(String aptNm){
        return apartmentRepository.findApartmentByAptNmContaining(aptNm);
    }

    public List<Apartment> searchApartments(SearchOption searchOption) {
        return apartmentRepository.searchApartments(searchOption);
    }

    public Apartment getApartmentByAptSeq(String aptSeq) {
        return apartmentRepository.findApartmentByAptSeq(aptSeq).orElseThrow(() -> new EntityNotFoundException("Apartment not found: " + aptSeq));
    }
}
