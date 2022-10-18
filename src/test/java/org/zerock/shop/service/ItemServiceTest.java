package org.zerock.shop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.ItemFormDTO;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;
import org.zerock.shop.repository.ItemRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws Exception{ //가짜 MultipartFile 리스트를 만들어서 반환
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for(int i=0; i<5; i++){
            String path = "c:/shop/item/";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile =
                    new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(multipartFile);
        }
        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception{ //상품 등록 화면에서 입력 받는 상품 데이터를 세팅
        ItemFormDTO itemFormDTO = new ItemFormDTO();
        itemFormDTO.setItemNm("테스트 상품");
        itemFormDTO.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDTO.setItemDetail("테스트 상품 입니다.");
        itemFormDTO.setPrice(1000);
        itemFormDTO.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(itemFormDTO, multipartFileList);
        //상품 데이터와 이미지 정보를 파라미터로 넘겨서 저장 후
        //저장된 상품의 아이디 값을 반환 값으로 리턴

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        //입력한 상품 데이터와 실제 상품 데이터가 같은지 확인
        assertEquals(itemFormDTO.getItemNm(), item.getItemNm());
        assertEquals(itemFormDTO.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDTO.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDTO.getPrice(), item.getPrice());
        assertEquals(itemFormDTO.getStockNumber(), item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(),
                itemImgList.get(0).getOriImgName());
        //상품 이미지는 첫번째 파일의 원본 이미지 파일 이름만 같은지 확인
    }
}
