package com.ssafy.BaeAndChoi.lwdCd.dto;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LwdCdResponseDTO {
    private String code;
    private String sidoName;
    private String sggName;
    private String umdName;

    public static LwdCdResponseDTO fromEntity(LwdCd lwdCd) {
        return LwdCdResponseDTO.builder()
                .code(lwdCd.getCode())
                .sidoName(lwdCd.getSidoName())
                .sggName(lwdCd.getSggName())
                .umdName(lwdCd.getUmdName())
                .build();
    }

    @Override
    public String toString() {
        return "LwdCdResponseDTO{" +
                "code='" + code + '\'' +
                ", sidoName='" + sidoName + '\'' +
                ", sggName='" + sggName + '\'' +
                ", umdName='" + umdName + '\'' +
                '}';
    }
}
