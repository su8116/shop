package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity { //주문 상품 엔티티

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //하나의 상품은 여러 주문 상품이 들어 갈 수 있음
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY) //한번의 주문에 여러개의 상품을 주문 할 수 있음
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격

    private int count; //주문 수량

//    private LocalDateTime regTime; //삭제
//
//    private LocalDateTime updateTime; //삭제

    //주문할 상품과 주문 수량을 통해 OrderItem 객체 생성 메서드
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); //주문 상품 세팅
        orderItem.setCount(count); //주문 수량 세팅
        orderItem.setOrderPrice(item.getPrice()); //현재 시간 기준으로 주문 가격 세팅

        item.removeStock(count); //주문 수량만큼 재고수량 감소

        return orderItem;
    }

    //총 상품 가격을 계산하는 메서드
    public int getTotalPrice(){
        return orderPrice * count;
    }

}
