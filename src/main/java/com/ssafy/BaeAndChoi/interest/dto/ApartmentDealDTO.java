package com.ssafy.BaeAndChoi.interest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApartmentDealDTO {
    private Long id;
    private String dealYmd;
    private int dealDay;
    private String dealAmount;
    private double excluUseAr;
    private int floor;
    private String buyerGbn;
    private String cdealDay;
    private String cdealType;
    private String dealingGbn;
    private String estateAgentSggNm;
    private String jibun;
    private String rgstDate;
    private String slerGbn;
}