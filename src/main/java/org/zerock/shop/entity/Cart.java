package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zerock.shop.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO) //자동번호 생성
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) //회원 엔티티와 일대일 매핑 //지연 로딩
    @JoinColumn(name = "member_id") //매핑할 외래키 지정 (name = 외래키 컬럼명)
    private Member member;
}
