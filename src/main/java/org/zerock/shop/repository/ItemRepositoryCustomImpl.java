package org.zerock.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.ItemSearchDTO;
import org.zerock.shop.dto.MainItemDTO;
import org.zerock.shop.dto.QMainItemDTO;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.QItem;
import org.zerock.shop.entity.QItemImg;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;
    //동적 쿼리 생성을 위해 JPAQueryFactory 사용

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    } //JPAQueryFactory의 생성자로 EntityManager객체를 넣음

    //판매 상태로 조회
    private com.querydsl.core.types.dsl.BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus ==
                null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    } //상품 판매 조건이 전체(null)라면 null 리턴
    //null인 경우 where절 무시함
    //null이 아닌 경우 판매/품절 로만 조회

    //상품 등록일로 조회
    private com.querydsl.core.types.dsl.BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if (StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    } //searchDateType의 값을 dateTime에 넣어 조회
    //해당 되는 기간의 상품 정보만 출력

    //상품명 또는 상품 등록자 아이디로 조회
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    } //searchBy의 갚에 따라 상품명 또는 등록자 아이디에
    //검색어를 포함하고 있는 상품을 조회

    //queryFactory를 이용해 쿼리 생성
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO,
                                       Pageable pageable) {

        //조회한 리스트 및 전체개수를 포함하는 QueryResults 반환 (2번의 쿼리문 실행)

        //1. 상품 데이터 리스트 조회
        List<Item> content = queryFactory
                .selectFrom(QItem.item) //상품 데이터를 조회하기 위해 QItem의 item지정
                .where(regDtsAfter(itemSearchDTO.getSearchDateType()),
                        searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),
                        searchByLike(itemSearchDTO.getSearchBy(),
                                itemSearchDTO.getSearchQuery()))
                //BooleanExpression 반환하는 조건문 "," = and 조건으로 인식
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                //데이터를 가지고 올 시작 인덱스 지정
                .limit(pageable.getPageSize())
                //한번에 가지고 올 최대 개수
                .fetch();

        //2. 상품 데이터 전체 개수 조회
        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDTO.getSearchDateType()),
                        searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),
                        searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery()))
                .fetchOne()
                ;
//.fetchResults(); deprecated 대응 (list 조회 및 count 조회 쿼리 분리)
//        List<Item> content = results.getResults();
//        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ?
                null : QItem.item.itemNm.like("%" + searchQuery + "%");
    } //검색어가 null이 아니면 해당 검색어가 포함되는 상품을 조회하는 조건 반환

    @Override
    public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO,
                                             Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDTO> content = queryFactory
                .select(
                        new QMainItemDTO( //QMainItemDTO의 생성자에 반환할 값 넣어줌
                                //엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있음
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item) //itemImg과 item을 내부조인
                .where(itemImg.repImgYn.eq("Y")) //대표 상품 이미지만 불러옴
                .where(itemNmLike(itemSearchDTO.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDTO.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
