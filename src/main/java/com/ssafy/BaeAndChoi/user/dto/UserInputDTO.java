package com.ssafy.BaeAndChoi.user.dto;

import com.ssafy.BaeAndChoi.user.enums.Role;
import lombok.Getter;

@Getter
public class UserInputDTO {
    String userId;
    String password;
    String name;
    String phone;
    String email;
    Role role;
}
