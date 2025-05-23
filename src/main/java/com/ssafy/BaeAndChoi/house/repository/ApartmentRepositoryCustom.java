package com.ssafy.BaeAndChoi.house.repository;

import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.SearchOption;

import java.util.List;

public interface ApartmentRepositoryCustom {
    List<Apartment> searchApartments(SearchOption searchOption);
}
