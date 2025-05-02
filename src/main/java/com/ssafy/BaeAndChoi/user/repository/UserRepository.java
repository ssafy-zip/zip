package com.ssafy.BaeAndChoi.user.repository;

import com.ssafy.BaeAndChoi.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);

    User findByUserId(String userId);
}
