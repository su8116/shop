package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.zerock.shop.entity.Item;

import java.util.List;

//QuerydslPredicateExecutor : '이 조건이 맞다'고 판단하는 근거를 함수로 제공
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByItemNm(String itemNm);
    //아이템 이름으로 검색

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    //아이템 이름과 상세 설명으로 검색

    List<Item> findByPriceLessThan(Integer price);
    //price변수보다 값이 작은 데이터를 조회 (가격대 조회?)

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    //가격순 정렬 (상품 가격이 높은 순으로!)

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.itemDetail LIKE %:itemDetail% " +
            "ORDER BY i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
    //@Query를 이용한 가격순 정렬

    @Query(value="SELECT * FROM item i " +
            "WHERE i.item_detail LIKE %:itemDetail% " +
            "ORDER BY i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
    //nativeQuery : 기존의 데이터 베이스에서 사용하던 쿼리를 그대로 사용해야 할 때 사용
}