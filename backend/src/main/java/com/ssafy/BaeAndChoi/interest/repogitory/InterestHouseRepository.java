package com.ssafy.BaeAndChoi.interest.repogitory;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.interest.domain.InterestHouse;
import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
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
     * 사용자 관심 아파트를 아파트 이름(aptNm), 시/군/구 코드로 필터링해 반환
     */
    @Query("""
        SELECT DISTINCT ih
          FROM InterestHouse ih
          JOIN FETCH ih.apartment apt
          LEFT JOIN FETCH apt.deals d
          JOIN ih.user u
         WHERE u.userId = :userId
           AND (:si     IS NULL OR substring(apt.sggCd,1,2) = :si)
           AND (:gun    IS NULL OR substring(apt.sggCd,3,3) = :gun)
           AND (:gu     IS NULL OR substring(apt.umdCd, 1, 3) = :gu)
           AND (
                 :aptName IS NULL
              OR  :aptName = ''
              OR  LOWER(COALESCE(apt.aptNm, ''))
                   LIKE LOWER(CONCAT('%', :aptName, '%'))
           )
    """)
    List<InterestHouse> findInterestHousesByUserAndRegion(
            @Param("userId")  String userId,
            @Param("aptName") String aptName,
            @Param("si")      String si,
            @Param("gun")     String gun,
            @Param("gu")      String gu
    );
}
