package org.zerock.shop.exception;

public class OutOfStockException extends RuntimeException{

    //상품의 주문 수량보다 재고가 적을 떄 발생하는 exception 정의
    public OutOfStockException(String message){
        super(message);
    }
}
