package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.shop.constant.Role;
import org.zerock.shop.dto.MemberFormDTO;

import javax.persistence.*;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true) //동일한 값이 들어올 수 없도록 지정
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING) //enum타입을 entity속성으로 지정
    private Role role;

    public static Member createMember(MemberFormDTO memberFormDTO,
                                      PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDTO.getName());
        member.setEmail(memberFormDTO.getEmail());
        member.setAddress(memberFormDTO.getAddress());
        String password = passwordEncoder.encode(memberFormDTO.getPassword());
        //스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder 빈을
        //파라미터로 넘겨 비밀번호를 암호화
        member.setPassword(password);
        member.setRole(Role.ADMIN); //계정 생성시 권한 설정
        return member;
    }
}
