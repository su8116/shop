package org.zerock.shop.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.dto.MemberFormDTO;
import org.zerock.shop.repository.CartRepository;
import org.zerock.shop.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Member createMember(){ //회원 엔티티 생성
        MemberFormDTO memberFormDTO = new MemberFormDTO();
        memberFormDTO.setEmail("test@email.com");
        memberFormDTO.setName("홍길동");
        memberFormDTO.setAddress("서울시 마포구 합정동");
        memberFormDTO.setPassword("1234");
        return Member.createMember(memberFormDTO, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush(); //강제로 호출해서 DB에 반영
        em.clear(); //영속성 콘텍스트 비우기
        //jpa는 엔티티 조회시 영속성 컨텍스트에서 먼저 조회 후
        //엔티티가 없다면 DB에서 조회, 테스트를 위해 비워줌

        Cart saveCart = cartRepository.findById(cart.getId()) //장바구니 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(saveCart.getMember().getId(), member.getId());
        //멤버엔티티의 아이디와 세이비카트에 매핑된 멤버엔티티의 아이디 비교
    }

}
