package com.ssafy.BaeAndChoi.user.domain;

import com.ssafy.BaeAndChoi.user.dto.UserInputDTO;
import com.ssafy.BaeAndChoi.user.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "User")
@NoArgsConstructor
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", length = 63)
    private String userId;

    @Column(length = 63)
    private String password;

    @Column(length = 63)
    private String name;

    @Column(length = 13)
    private String phone;

    @Column(length = 63)
    private String email;

    @Column(length = 10)
    private Role role;

    @Builder
    public User(String userId, String password, String name, String phone, String email, Role role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public void update(UserInputDTO dto) {
        this.password = dto.getPassword();
        this.name = dto.getName();
        this.phone = dto.getPhone();
        this.email = dto.getEmail();
    }

}
