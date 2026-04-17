package com.ssafy.BaeAndChoi.house.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 아파트 대표 정보 엔티티
 */
@Entity
@Table(name = "apartment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apartment {

    /** 아파트 고유 식별자 (aptSeq) */
    @Id
    @Column(name = "apt_seq", length = 50)
    private String aptSeq;

    /** 아파트명 */
    @Column(name = "apt_nm", length = 200, nullable = false)
    private String aptNm;

    /** 건축년도 */
    @Column(name = "build_year")
    private int buildYear;

    /** 시군구 코드 (sggCd) */
    @Column(name = "sgg_cd", length = 10)
    private String sggCd;

    /** 읍면동 코드 (umdCd) */
    @Column(name = "umd_cd", length = 10)
    private String umdCd;

    /** 읍면동명 (umdNm) */
    @Column(name = "umd_nm", length = 100)
    private String umdNm;

    /** 본번 (지번) */
    @Column(name = "bonbun", length = 10)
    private String bonbun;

    /** 부번 (지번) */
    @Column(name = "bubun", length = 10)
    private String bubun;

    /** 도로명 일련번호 */
    @Column(name = "road_nm_seq", length = 10)
    private String roadNmSeq;

    /** 도로명 */
    @Column(name = "road_nm", length = 200)
    private String roadNm;

    /** 도로명 본번 */
    @Column(name = "road_nm_bonbun", length = 10)
    private String roadNmBonbun;

    /** 도로명 부번 */
    @Column(name = "road_nm_bubun", length = 10)
    private String roadNmBubun;

    /** 도로명 코드 */
    @Column(name = "road_nm_cd", length = 20)
    private String roadNmCd;

    /** 도로명 시군구 코드 */
    @Column(name = "road_nm_sgg_cd", length = 10)
    private String roadNmSggCd;

    /** 도로번호 코드 */
    @Column(name = "road_nmb_cd", length = 10)
    private String roadNmbCd;

    /** 토지 용도 코드 (landCd) */
    @Column(name = "land_cd", length = 10)
    private String landCd;

    @Column(name="img")
    private String img;

    /** 거래내역 (1:N) */
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ApartmentDeal> deals = new ArrayList<>();
}
