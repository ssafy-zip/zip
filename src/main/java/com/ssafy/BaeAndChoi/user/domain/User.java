package com.ssafy.BaeAndChoi.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "User")
@Getter
@Setter
public class User {

    @Id
    private String key;

    @Column(length = 63)
    private String field;

    @Column(length = 63)
    private String field2;

    @Column(length = 11)
    private String field3;

    @Column(length = 63)
    private String field4;

    @Column(length = 10)
    private String field5;
}
