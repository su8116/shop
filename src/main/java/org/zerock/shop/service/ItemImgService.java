package org.zerock.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    //application.properties에 등록한 itemImgLocation 값을 itemImgLocation 변수에 등록
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile)
            throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            //사용자가 이미지를 등록했을 때 저장경로, 파일이름, 파일 바이트 배열을 파라미터로 uploadFile를 호출
            //호출 후에는 imgName 변수에 저장
            imgUrl = "/images/item/" + imgName;
            //저장한 상품 이미지를 불러올 경로 설정
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
        //oriImgName : 실제 로컬에 저장된 상품 이미지 파일 이름
        //imgName : 업로드했던 상품 이미지 파일의 원래 이름
        //imgUrl : 업로드 결과 로컬에 저장된 상품 이미지를 불러오는 경로
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){ //상품 이미지를 수정한 경우 상품 이미지를 업데이트
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);
            //상품 이미지 아이디를 조회하여 기존에 저장했던 상품이미지 엔티티를 조회

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            //업데이트한 상품 이미지 파일을 업로드

            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
            //변경된 상품 이미지 정보 세팅 (.save는 사용하지 않음)
            //savedItemImg 엔티티는 현재 영속 상태 이므로 데이터 변경시 변경 감지 기능 작동
            //트랜젝션이 끝날때 update 쿼리 실행됨 (*엔티티가 영속상태여야 함)
        }
    }
}
