package org.zerock.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDTO;
import org.zerock.shop.dto.ItemSearchDTO;
import org.zerock.shop.entity.Item;
import org.zerock.shop.service.ItemService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDTO", new ItemFormDTO());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDTO itemFormDTO, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        } //상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환

        if(itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        } //상품 등록 시 첫번째 이미지가 없다면 에러 메세지와 함께 상품 등록 페이지로 전환

        try{
            itemService.saveItem(itemFormDTO, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        } //상품 저장 로직을 호출

        return "redirect:/"; //상품이 정상적으로 등록되었다면 메인 페이지로 이동
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try{
            ItemFormDTO itemFormDTO = itemService.getItemDtl(itemId);
            //조회한 상품을 모델에 담아 뷰로 전달
            model.addAttribute("itemFormDTO", itemFormDTO);
        } catch(EntityNotFoundException e){
            //상품 엔티티가 존재하지 않을 경우 에러 메세지를 담아 상품 등록 메세지로 보냄
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDTO", new ItemFormDTO());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDTO itemFormDTO, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        } //상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환

        if(itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        } //상품 등록 시 첫번째 이미지가 없다면 에러 메세지와 함께 상품 등록 페이지로 전환

        try {
            itemService.updateItem(itemFormDTO, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        } //상품 수정 로직을 호출

        return "redirect:/";
    }

    //페이지 번호가 있는 경우롸 없는 경우 둘다 매핑
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManager(ItemSearchDTO itemSearchDTO,
                              @PathVariable("page") Optional<Integer> page,
                              Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        //페이징 처리를 위해 pageable 객체 생성
        //파라미터는 조회할 페이지 번호 / 가지고 올 데이터 수를 넣음
        //url에 페이지 번호가 없을시 0페이지를 조회

        Page<Item> items = itemService.getAdminItemPage(itemSearchDTO, pageable);
        //조회 조건과 페이징 정보를 파라미터로 넘겨 Page<Item> 객체를 반환

        model.addAttribute("items", items);
        //조회한 상품 데이터 및 페이징 정보를 뷰에 전달
        model.addAttribute("itemSearchDTO", itemSearchDTO);
        //페이지 전환 시 기존 검색 조건 유지
        model.addAttribute("maxPage", 5);
        //상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수
        return "item/itemMng";
    }

    //상품 상세 페이지
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model,
                          @PathVariable("itemId") Long itemId){
        ItemFormDTO itemFormDTO = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDTO);

        return "item/itemDtl";
    }

}
