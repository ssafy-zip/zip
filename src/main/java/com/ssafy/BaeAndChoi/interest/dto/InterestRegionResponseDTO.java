package com.ssafy.BaeAndChoi.interest.dto;

import com.ssafy.BaeAndChoi.interest.domain.InterestRegion;
import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InterestRegionResponseDTO {
    private String sgg_name;
    private String sido_name;
    private String umd_name;

    public static InterestRegionResponseDTO fromEntity(InterestRegion ir) {
        LwdCd cd = ir.getLwdCd();
        return InterestRegionResponseDTO.builder()
                .sgg_name(cd.getSggName())
                .sido_name(cd.getSidoName())
                .umd_name(cd.getUmdName())
                .build();
    }
}
