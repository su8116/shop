package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.entity.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDTO {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDTO> itemImgDTOList = new ArrayList<>();
    //상품 저장 후 수정할 때 상품 이미지 정보를 저장

    private List<Long> itemImgIds = new ArrayList<>();
    //상품의 이미지 아이디를 저장하는 리스트
    //수정 시에 이미지 아이디를 담아둘 용도로 사용

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDTO of(Item item){
        return modelMapper.map(item, ItemFormDTO.class);
    }
}
