package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "item_img")
public class ItemImg extends BaseEntity {

    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 파일명

    private String imgUrl; //이미지 경로 조회

    private String repImgYn; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    } //원본 이미지 파일명, 업로드할 이미지 파일명, 이미지 경로를
    //파라미터로 입력받아서 이미지 정보를 업데이트
}
