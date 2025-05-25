package com.ssafy.BaeAndChoi.lwdCd.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lwd_cd")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class LwdCd {

    /** 법정동 코드 (시도+시군구+읍면동+리 코드 조합, 10자리) */
    @Id
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    /** 시도 이름 (예: 서울특별시, 경기도 등) */
    @Column(name = "sido_name", length = 30, nullable = false)
    private String sidoName;

    /** 시군구 이름 (예: 강남구, 수원시 등) */
    @Column(name = "sgg_name", length = 30)
    private String sggName;

    /** 읍면동 이름 (예: 역삼동, 장안동 등) */
    @Column(name = "umd_name", length = 30)
    private String umdName;

    /** 리 이름 (예: 송산리, 중리 등) */
    @Column(name = "ri_name", length = 30)
    private String riName;
}
