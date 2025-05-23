package com.ssafy.BaeAndChoi.interest.repogitory;

import com.ssafy.BaeAndChoi.interest.domain.InterestRegion;
import com.ssafy.BaeAndChoi.lwdCd.domain.LwdCd;
import com.ssafy.BaeAndChoi.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRegionRepository extends JpaRepository<InterestRegion, Integer> {
    List<InterestRegion> findAllByUser(User user);
    boolean existsByUserAndLwdCd(User user, LwdCd lwdCd);
    Optional<InterestRegion> findByLwdCdAndUser(LwdCd lwdCd, User user);
}
