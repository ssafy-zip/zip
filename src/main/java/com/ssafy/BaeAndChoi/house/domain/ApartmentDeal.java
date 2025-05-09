package com.ssafy.BaeAndChoi.house.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * 아파트 매매 실거래 내역 엔티티
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 거래 연월 (YYYYMM) */
    @Column(name = "deal_ymd", length = 6, nullable = false)
    private String dealYmd;

    /** 거래일 */
    @Column(name = "deal_day")
    private int dealDay;

    /** 거래 금액 (만원 단위 문자열) */
    @Column(name = "deal_amount", length = 50)
    private String dealAmount;

    /** 전용면적 (㎡) */
    @Column(name = "exclu_use_ar")
    private double excluUseAr;

    /** 층 */
    @Column(name = "floor")
    private int floor;

    /** 매수자 구분 */
    @Column(name = "buyer_gbn", length = 20)
    private String buyerGbn;

    /** 계약일자 구분 */
    @Column(name = "cdeal_day", length = 20)
    private String cdealDay;

    /** 계약 유형 */
    @Column(name = "cdeal_type", length = 20)
    private String cdealType;

    /** 거래 구분 */
    @Column(name = "dealing_gbn", length = 20)
    private String dealingGbn;

    /** 중개업소 시군구명 */
    @Column(name = "estate_agent_sgg_nm", length = 200)
    private String estateAgentSggNm;

    /** 지번 (번지) */
    @Column(name = "jibun", length = 50)
    private String jibun;

    /** 등록일 */
    @Column(name = "rgst_date", length = 50)
    private String rgstDate;

    /** 전세 구분 */
    @Column(name = "sler_gbn", length = 20)
    private String slerGbn;

    /** Apartment 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_seq", nullable = false)
    @JsonBackReference
    private Apartment apartment;
}
