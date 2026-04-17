package com.ssafy.BaeAndChoi.lwdCd.dto;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LwdCdResponseDTO {
    private String code;
    private String sidoName;
    private String sggName;
    private String umdName;
    private String riName;

    public static LwdCdResponseDTO fromEntity(LwdCd lwdCd) {
        return LwdCdResponseDTO.builder()
                .code(lwdCd.getCode())
                .sidoName(lwdCd.getSidoName())
                .sggName(lwdCd.getSggName())
                .umdName(lwdCd.getUmdName())
                .riName(lwdCd.getRiName())
                .build();
    }
}
