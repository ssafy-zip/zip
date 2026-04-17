package com.ssafy.BaeAndChoi.email.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindIdRequest {
    @NotBlank
    private String email;
}
