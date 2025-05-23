package com.ssafy.BaeAndChoi.house.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.BaeAndChoi.house.domain.Apartment;
import com.ssafy.BaeAndChoi.house.domain.QApartment;
import com.ssafy.BaeAndChoi.house.domain.SearchOption;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApartmentRepositoryImpl implements ApartmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ApartmentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Apartment> searchApartments(SearchOption option) {
        QApartment apartment = QApartment.apartment;

        String aptNm = option.getAptNm();
        String code = option.getCode();

        return queryFactory
                .selectFrom(apartment)
                .where(
                        hasText(aptNm) ? apartment.aptNm.contains(aptNm) : null,
                        isValidCode(code) ? apartment.sggCd.eq(code.substring(0, 5)) : null,
                        isValidCode(code) ? apartment.umdCd.eq(code.substring(5)) : null
                )
                .fetch();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
    private boolean isValidCode(String code) {
        return code != null && code.length() >= 10;
    }
}