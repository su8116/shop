package org.zerock.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDTO;
import org.zerock.shop.dto.ItemImgDTO;
import org.zerock.shop.dto.ItemSearchDTO;
import org.zerock.shop.dto.MainItemDTO;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;
import org.zerock.shop.repository.ItemRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemImgService itemImgService;

    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDTO itemFormDTO, List<MultipartFile> itemImgFileList)
            throws Exception{

        //상품 등록
        Item item = itemFormDTO.createItem();
        itemRepository.save(item);

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        return  item.getId();
    }

    //상품 데이터를 읽어오는 트랜젝션을 읽기 전용으로 설정
    //JPA가 더티체킹(변경감지)을 수행하지 않아 성능이 향상됨
    @Transactional(readOnly = true)
    public ItemFormDTO getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);//해당 상품의 이미지 조회(등록순으로 갸져옴)
        List<ItemImgDTO> itemImgDTOList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {//조회한 itemImg 엔티티를 itemImgDTO 객체로 만들어 리스트에 추가
            ItemImgDTO itemImgDTO = ItemImgDTO.of(itemImg);
            itemImgDTOList.add(itemImgDTO);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        //상품 아이디로 상품 엔티티조회,
        //상품이 없으면 EntityNotFoundException 발생
        ItemFormDTO itemFormDTO = ItemFormDTO.of(item);
        itemFormDTO.setItemImgDTOList(itemImgDTOList);
        return itemFormDTO;
    }

    public long updateItem(ItemFormDTO itemFormDTO,
                           List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDTO.getId())
                .orElseThrow(EntityNotFoundException::new);
        //상품 등록 화면으로부터 전달받은 상품 아이디를 이용하여 상품 엔티티 조회
        item.updateItem(itemFormDTO);
        //상품 등록 화면으로부터 전달받은 ItemFormDTO를 이용하여 상품 엔티티 업데이트

        List<Long> itemImgIds = itemFormDTO.getItemImgIds();
        //상품 이미지 아이디 리스트 조회

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
            //상품 이미지 아이디와 상품 이미지 파일 정보를 파라미터로 전달
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDTO, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDTO, pageable);
    }
}
