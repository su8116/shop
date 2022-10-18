package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.ItemFormDTO;
import org.zerock.shop.exception.OutOfStockException;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{ //상품 클래스

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO) //자동 번호 생성
    private Long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name = "price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob //CLOB, BLOB 타입 매핑
    //CLOB : 사이즈가 큰 데이터를 외부 파일로 저장 (문자열 대용량 파일 등...)
    //BLOB : 바이너리 데이터를 DB외부에 저장 (이미지, 사운드, 비디오 등 멀티 미디어)
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING) //enum타입 매핑
    private ItemSellStatus itemSellStatus; //상품 판매 상태

//    extends BaseEntity 상속하므로 삭제
//    private LocalDateTime regTime; //상품 등록 시간
//
//    private LocalDateTime updateTime; //상품 수정 시간

    //상품 데이터 업데이트
    public void updateItem(ItemFormDTO itemFormDTO){
        this.itemNm = itemFormDTO.getItemNm();
        this.price = itemFormDTO.getPrice();
        this.stockNumber = itemFormDTO.getStockNumber();
        this.itemDetail = itemFormDTO.getItemDetail();
        this.itemSellStatus = itemFormDTO.getItemSellStatus();
    }

    //상품의 주문 수량보다 재고가 적을 때 처리
    public void removeStock(int stockNumber){
        //상품의 재고 수량에서 주문 후 남은 재고 수량을 구함
        int restStock = this.stockNumber - stockNumber;

        //상품의 재고가 주문 수량보다 적을 경우 재고부족 예외 발생
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다. " +
                    "(현재 재고 수량 : " + this.stockNumber + ")");
        }
        //주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당
        this.stockNumber = restStock;
    }
}
