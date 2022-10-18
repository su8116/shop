package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //장바구니는 한번에 여러개의 상품을 담을 수 있음
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  //장바구니에 담길 상품의 정보 매핑 //하나의 상품은 여러개의 장바구니에 담을 수 있음
    @JoinColumn(name = "item_id")
    private Item item;

    private int count; //같은 상품을 장바구니에 몇개 담을지 저장
}
