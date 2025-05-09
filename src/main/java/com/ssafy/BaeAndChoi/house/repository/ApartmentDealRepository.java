package com.ssafy.BaeAndChoi.house.repository;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.ApartmentDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentDealRepository extends JpaRepository<ApartmentDeal, Integer> {
    List<ApartmentDeal> findAllByApartment(Apartment apartment);
}
