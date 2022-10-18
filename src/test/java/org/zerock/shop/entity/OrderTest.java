package org.zerock.shop.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;
import org.zerock.shop.repository.OrderItemRepository;
import org.zerock.shop.repository.OrderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){

        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = this.createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); //아직 영속성 컨택스트에 저장되지 않은 orderItem를 order에 담는다
        }

        orderRepository.saveAndFlush(order); //order를 저장하면서 강제로 Flush를 호출하여 DB에 담는다
        em.clear(); //영속성 컨택스트 초기화

        Order savedOrder = orderRepository.findById(order.getId()) //DB에서 주문 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size()); //엔티티 3개가 DB에 반영되었는지 확인
    }

    public Order createOrder(){ //주문 데이터 생성

        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);

        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아 객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        //order 엔티티에서 관리하는 orderItem 리스트의 0번째 인덱스 요소를 제거
        em.flush();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder(); //주문 데이터 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        //영속성 컨택스트 상태 초기화 후 order 엔티티에 저장했던 주문 상품 아이디를 이용하여
        //orderItem을 DB에서 조회
        System.out.println("Order class : " + orderItem.getOrder().getClass());
        System.out.println("================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("================================");
    }

}
