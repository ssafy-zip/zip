package com.ssafy.BaeAndChoi.interest.domain;

import com.ssafy.BaeAndChoi.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interest_region")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestRegion {

    /** 관심지역 고유 식별자  */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 관심지역을 등록한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 관심있는 법정동 코드 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code", nullable = false)
    private LwdCd lwdCd;
}
