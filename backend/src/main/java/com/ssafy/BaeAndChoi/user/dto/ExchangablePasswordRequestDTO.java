package com.ssafy.BaeAndChoi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ExchangablePasswordRequestDTO {
    @NotBlank
    private String userId;

    @Email
    @NotBlank
    private String email;
}
