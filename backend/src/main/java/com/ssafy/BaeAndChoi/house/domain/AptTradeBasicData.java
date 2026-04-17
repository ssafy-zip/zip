package com.ssafy.BaeAndChoi.house.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * 아파트매매기본자료 한 건의 기록을 표현하는 VO
 * - 빈 문자열을 null로 매핑하기 위해 Wrapper 타입 사용
 * - 알 수 없는 XML 요소 무시
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptTradeBasicData {
    /** 동 코드 */
    @JacksonXmlProperty(localName = "aptDong")
    private String aptDong;

    /** 아파트명 */
    @JacksonXmlProperty(localName = "aptNm")
    private String aptNm;

    /** 아파트 고유 일련번호 */
    @JacksonXmlProperty(localName = "aptSeq")
    private String aptSeq;

    /** 지번 본번 */
    @JacksonXmlProperty(localName = "bonbun")
    private String bonbun;

    /** 지번 부번 */
    @JacksonXmlProperty(localName = "bubun")
    private String bubun;

    /** 건축년도 */
    @JacksonXmlProperty(localName = "buildYear")
    private Integer buildYear;

    /** 매수자 구분 */
    @JacksonXmlProperty(localName = "buyerGbn")
    private String buyerGbn;

    /** 계약일자 구분 */
    @JacksonXmlProperty(localName = "cdealDay")
    private String cdealDay;

    /** 계약 유형 */
    @JacksonXmlProperty(localName = "cdealType")
    private String cdealType;

    /** 거래 금액 (만원) */
    @JacksonXmlProperty(localName = "dealAmount")
    private String dealAmount;

    /** 거래일 */
    @JacksonXmlProperty(localName = "dealDay")
    private Integer dealDay;

    /** 거래 월 */
    @JacksonXmlProperty(localName = "dealMonth")
    private Integer dealMonth;

    /** 거래 연도 */
    @JacksonXmlProperty(localName = "dealYear")
    private Integer dealYear;

    /** 거래 구분 */
    @JacksonXmlProperty(localName = "dealingGbn")
    private String dealingGbn;

    /** 중개업소 시군구명 */
    @JacksonXmlProperty(localName = "estateAgentSggNm")
    private String estateAgentSggNm;

    /** 전용면적 (㎡) */
    @JacksonXmlProperty(localName = "excluUseAr")
    private Double excluUseAr;

    /** 층 */
    @JacksonXmlProperty(localName = "floor")
    private Integer floor;

    /** 지번 (필지 번호) */
    @JacksonXmlProperty(localName = "jibun")
    private String jibun;

    /** 토지 용도 코드 */
    @JacksonXmlProperty(localName = "landCd")
    private String landCd;

    /** 지목 구분 */
    @JacksonXmlProperty(localName = "landLeaseholdGbn")
    private String landLeaseholdGbn;

    /** 등록일 */
    @JacksonXmlProperty(localName = "rgstDate")
    private String rgstDate;

    /** 도로명 */
    @JacksonXmlProperty(localName = "roadNm")
    private String roadNm;

    /** 도로명 본번 */
    @JacksonXmlProperty(localName = "roadNmBonbun")
    private String roadNmBonbun;

    /** 도로명 부번 */
    @JacksonXmlProperty(localName = "roadNmBubun")
    private String roadNmBubun;

    /** 도로명 코드 */
    @JacksonXmlProperty(localName = "roadNmCd")
    private String roadNmCd;

    /** 도로명 일련번호 */
    @JacksonXmlProperty(localName = "roadNmSeq")
    private String roadNmSeq;

    /** 도로명 시군구 코드 */
    @JacksonXmlProperty(localName = "roadNmSggCd")
    private String roadNmSggCd;

    /** 도로번호 코드 */
    @JacksonXmlProperty(localName = "roadNmbCd")
    private String roadNmbCd;

    /** 시군구 코드 */
    @JacksonXmlProperty(localName = "sggCd")
    private String sggCd;

    /** 전세 구분 */
    @JacksonXmlProperty(localName = "slerGbn")
    private String slerGbn;

    /** 행정동 코드 */
    @JacksonXmlProperty(localName = "umdCd")
    private String umdCd;

    /** 읍면동명 */
    @JacksonXmlProperty(localName = "umdNm")
    private String umdNm;
}