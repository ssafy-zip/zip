package com.ssafy.BaeAndChoi.interest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ApartmentDTO {
    private String aptSeq;
    private String aptNm;
    private String bonbun;
    private String bubun;
    private int buildYear;
    private String umdCd;
    private String umdNm;
    private String roadNm;
    private String roadNmBonbun;
    private String roadNmBubun;
    private String roadNmCd;
    private String roadNmSeq;
    private String roadNmSggCd;
    private String roadNmbCd;
    private String sggCd;
    private String landCd;
    private List<ApartmentDealDTO> deals;
}