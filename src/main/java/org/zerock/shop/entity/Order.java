package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문 상태

    //형재 엔티티가 연관관계의 주인이 아닌 경우 mappedBy 으로 연관관계의 주인을 설정
    //(외래키(order_id)가 order_item 테이블에 있으므로 연관관계의 주인은 OrderItem 엔티티)
    //OrderItem에 있는 order로 관리된다
    //cascade : 영속성 전이 설정
    //orphanRemoval : 고아 객체 제거
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY) //주문상품 엔티티와 매핑
    private List<OrderItem> orderItems = new ArrayList<>();
    //하나의 주문이 여러개의 상품을 가지므로 List 사용

//extends BaseEntity 사용하므로 삭제
//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    //orderItems에 주문 정보를 담는다. orderItem객체를 order객체의 orderItems에 추가
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        //Order orderItem 엔티티가 양방향 객체 이므로
        //orderItem객체에도 order객체 세팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member); //상품을 주문한 회원정보 세팅
        for (OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        } //여러개의 상품을 담을 수 있도록 리스트 처리, 주문 객체에 orderItem객체 추가
        order.setOrderStatus(OrderStatus.ORDER); //주문 상태를 ORDER로 세팅
        order.setOrderDate(LocalDateTime.now()); //현재 시간을 주문 시간으로 세팅
        return order;
    }

    public int getTotalPrice(){ //총 주문 금액을 구하는 메서드
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
