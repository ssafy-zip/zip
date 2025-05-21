package com.ssafy.BaeAndChoi.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponseDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
}
