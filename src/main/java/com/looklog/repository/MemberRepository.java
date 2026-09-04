package com.looklog.repository;

import com.looklog.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    Optional<Member> findByEmail(String email);
    Optional<Member> findByUserName(String userName);

}