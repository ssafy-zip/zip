package com.ssafy.BaeAndChoi.house.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SearchOption {
    private String aptNm;   // 아파트 이름 키워드 (LIKE 검색)
    private String code;    // 법정동 코드 (umdCd 매핑 예상)
}
