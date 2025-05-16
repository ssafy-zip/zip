package com.ssafy.BaeAndChoi.user.repository;

import com.ssafy.BaeAndChoi.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserIdAndPassword(String userId, String password);
    void deleteByUserId(String userId);
}
