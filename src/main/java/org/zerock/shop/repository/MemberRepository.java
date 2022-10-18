package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
    //회원가입시 이메일 중복 검사 쿼리메서드
}
