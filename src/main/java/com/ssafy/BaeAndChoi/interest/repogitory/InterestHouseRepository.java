package com.ssafy.BaeAndChoi.interest.repogitory;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.interest.domain.InterestHouse;
import com.ssafy.BaeAndChoi.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestHouseRepository extends JpaRepository<InterestHouse, Integer> {
    boolean existsByUserAndApartment(User user, Apartment apartment);
    InterestHouse findByUserAndApartment(User user, Apartment apartment);

    List<Apartment> findAllByUser(User user);

    /**
     * 사용자 관심 아파트를 시/군/구 코드로 필터링해 반환
     * @param userId 사용자 아이디
     * @param si     시 코드 (sggCd 앞 2자리), null일 경우 무시
     * @param gun    군 코드 (sggCd 뒤 3자리), null일 경우 무시
     * @param gu     동(구) 코드 (umdCd), null일 경우 무시
     */
    @Query("SELECT DISTINCT ih FROM InterestHouse ih " +
            "JOIN FETCH ih.apartment apt " +
            "LEFT JOIN FETCH apt.deals d " +
            "JOIN ih.user u " +
            "WHERE u.userId = :userId " +
            "AND (:si IS NULL OR substring(apt.sggCd,1,2)=:si) " +
            "AND (:gun IS NULL OR substring(apt.sggCd,3,3)=:gun) " +
            "AND (:gu IS NULL OR apt.umdCd = :gu)")
    List<InterestHouse> findInterestHousesByUserAndRegion(
            @Param("userId") String userId,
            @Param("si") String si,
            @Param("gun") String gun,
            @Param("gu") String gu
    );
}
