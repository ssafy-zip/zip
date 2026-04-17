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
                        isValidCode(code) ? Expressions.stringTemplate("SUBSTRING(CONCAT({0}, {1}), 1, {2})", apartment.sggCd, apartment.umdCd, code.length())
                                .eq(code)
                                : null
                )
                .fetch();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
    private boolean isValidCode(String code) {
        if(code == null) return false;

        int codeLength = code.length();
        return codeLength == 2 || codeLength == 5 || codeLength == 8 || codeLength == 10;
    }
}