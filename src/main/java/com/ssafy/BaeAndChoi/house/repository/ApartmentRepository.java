package com.ssafy.BaeAndChoi.house.repository;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer>, ApartmentRepositoryCustom {
    Optional<Apartment> findApartmentByAptNm(String aptName);
    Optional<Apartment> findApartmentByAptNmAndSggCdAndUmdCd(String aptName, String sggCd, String umdCd);
    List<Apartment> findApartmentByAptNmContaining(String aptName);
    Optional<Apartment> findApartmentByAptSeq(String aptSeq);
}
