package com.ssafy.BaeAndChoi.lwdCd.repogitory;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LwdCdRepository extends Repository<LwdCd, String> {

    // 시도 목록 조회
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd(code, sidoName, null, null)
            FROM LwdCd
            WHERE sggName IS NULL
            """)
    List<LwdCd> findSidoList();

    // 시군구 목록 조회
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd(code, sidoName, sggName, null)
            FROM LwdCd
            WHERE SUBSTRING(code, 1, 2) = :sidoCode
            AND sggName IS NOT NULL
            AND umdName IS NULL
            AND code IN (SELECT MIN(sub.code) FROM LwdCd sub WHERE SUBSTRING(sub.code, 1, 2) = :sidoCode AND sub.umdName IS NULL GROUP BY SUBSTRING(sub.code, 1, 5))
            """)
    List<LwdCd> findSggListBySidoCode(@Param("sidoCode") String sidoCode);

    // 읍면동 목록 조회
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd(code, sidoName, sggName, umdName)
            FROM LwdCd
            WHERE SUBSTRING(code, 1, 5) = :sggCode
            AND umdName IS NOT NULL
            """)
    List<LwdCd> findUmdListBySggCode(@Param("sggCode") String sggCode);

    Optional<LwdCd> findLwdCdByCode(String code);
}
