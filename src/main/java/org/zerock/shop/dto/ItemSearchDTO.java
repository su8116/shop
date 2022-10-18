package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.ItemSellStatus;

@Getter
@Setter
public class ItemSearchDTO {

    private String searchDateType;
    //현재 시간과 상품 등록일을 비교해서 상품 데이터를 조회
    //all : 상품 등록일 전체
    //1d : 최근 하루 동안 등록된 상품
    //1w : 최근 일주일 동안 등록된 상품
    //1m : 최근 한달 동안 등록된 상품
    //6m : 최근 6개월 동안 등록된 상품

    private ItemSellStatus searchSellStatus;
    //상품 판매 기준으로 조회 (SELL, SOLD_OUT)

    private String searchBy;
    //상품 조회 유형
    //itemNm : 상품명 / createBy : 상품 등록자 아이디

    private String searchQuery = "";
    //조회할 검색어 저장 변수
    //searchQuery가 itemNm이면 상품명 기준 검색
    //createBy이면 상품 등록자 기준 검색
}
