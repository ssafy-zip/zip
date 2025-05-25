package com.ssafy.BaeAndChoi.lwdCd.dto;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LwdCdSimpleResponseDTO {
    private String code;
    private String name;
}
