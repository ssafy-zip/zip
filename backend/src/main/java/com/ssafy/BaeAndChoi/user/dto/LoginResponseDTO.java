package com.ssafy.BaeAndChoi.user.dto;

import com.ssafy.BaeAndChoi.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Role role;
    private String userId;
}
