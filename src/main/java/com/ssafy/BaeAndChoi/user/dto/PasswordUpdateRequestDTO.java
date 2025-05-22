package com.ssafy.BaeAndChoi.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordUpdateRequestDTO {
    @NotBlank
    private String newPassword;
}
