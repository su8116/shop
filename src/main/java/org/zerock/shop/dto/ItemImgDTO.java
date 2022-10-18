package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.zerock.shop.entity.ItemImg;

@Getter
@Setter
public class ItemImgDTO {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private static ModelMapper modelMapper = new ModelMapper();
    //멤버변수로 ModelMapper 객체 추가

    public static ItemImgDTO of(ItemImg itemImg){
        return modelMapper.map(itemImg, ItemImgDTO.class);
    } //ItemImg 엔티티 객체를 파라미터로 받아서 ItemImg 객체의 자료형과 맴버변수의 이름이 같을 때
    //ItemImgDTO로 값을 복사해서 반환
    //static로 선언해 ItemImgDTO 객체를 생성하지 않아도 호출할 수 있도록 함
}
