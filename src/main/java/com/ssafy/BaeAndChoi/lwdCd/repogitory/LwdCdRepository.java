package com.ssafy.BaeAndChoi.lwdCd.repogitory;

import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdResponseDTO;
import com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LwdCdRepository extends Repository<LwdCd, String> {

    // 시/도 목록 조회
    @Query("SELECT l FROM LwdCd l WHERE l.sggName IS NULL AND l.umdName IS NULL")
    List<LwdCd> findSido();

    // 시/군/구 목록 조회
    @Query("SELECT l FROM LwdCd l WHERE SUBSTRING(l.code, 1, 2) = :sidoCode AND l.sggName IS NOT NULL AND l.umdName IS NULL")
    List<LwdCd> findSggBySidoCode(@Param("sidoCode") String code);

    // 읍/면/동 목록 조회
    @Query("SELECT l FROM LwdCd l WHERE SUBSTRING(l.code, 1, 5) = :sggCode AND l.umdName IS NOT NULL AND l.riName IS NULL")
    List<LwdCd> findUmdBySggCode(@Param("sggCode") String code);

    // 리 목록 조회
    @Query("SELECT l FROM LwdCd l WHERE SUBSTRING(l.code, 1, 8) = :umdCode AND l.riName IS NOT NULL")
    List<LwdCd> findRiByUmdCode(@Param("umdCode") String code);
    
    // 법정동 조회
    Optional<LwdCd> findLwdCdByCode(String code);

    // 시/도 목록 조회 (simple)
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO(SUBSTRING(l.code, 1, 2), l.sidoName)
            FROM LwdCd l
            WHERE l.sggName IS NULL AND l.umdName IS NULL
            """)
    List<LwdCdSimpleResponseDTO> findSidoSimple();

    // 시/군/구 목록 조회 (simple)
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO(SUBSTRING(l.code, 1, 5), l.sggName)
            FROM LwdCd l
            WHERE SUBSTRING(l.code, 1, 2) = :sidoCode AND l.sggName IS NOT NULL AND l.umdName IS NULL
            """)
    List<LwdCdSimpleResponseDTO> findSggBySidoCodeSimple(@Param("sidoCode") String code);

    // 읍/면/동 목록 조회 (simple)
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO(SUBSTRING(l.code, 1, 8), l.umdName)
            FROM LwdCd l
            WHERE SUBSTRING(l.code, 1, 5) = :sggCode AND l.umdName IS NOT NULL AND l.riName IS NULL
            """)
    List<LwdCdSimpleResponseDTO> findUmdBySggCodeSimple(@Param("sggCode") String code);

    // 리 목록 조회 (simple)
    @Query("""
            SELECT new com.ssafy.BaeAndChoi.lwdCd.dto.LwdCdSimpleResponseDTO(l.code, l.riName)
            FROM LwdCd l
            WHERE SUBSTRING(l.code, 1, 8) = :umdCode AND l.riName IS NOT NULL
            """)
    List<LwdCdSimpleResponseDTO> findRiByUmdCodeSimple(@Param("umdCode") String code);

    @Query("""
    SELECT DISTINCT SUBSTRING(l.code, 1, 5)
    FROM LwdCd l
    WHERE SUBSTRING(l.code, 3, 3) <> '000'
""")
    List<String> findUniqueDongCodes();
}
