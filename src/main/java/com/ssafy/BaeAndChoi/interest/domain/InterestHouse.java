package com.ssafy.BaeAndChoi.interest.domain;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interest_house")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestHouse {

    /** 관심 매물 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 관심 매물을 등록한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 관심 대상 아파트 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_seq", nullable = false)
    private Apartment apartment;
}
