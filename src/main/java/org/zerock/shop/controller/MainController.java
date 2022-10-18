package org.zerock.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.shop.dto.ItemSearchDTO;
import org.zerock.shop.dto.MainItemDTO;
import org.zerock.shop.service.ItemService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = "/")
    public String main(ItemSearchDTO itemSearchDTO,
                       Optional<Integer> page,
                       Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,6);
        //페이징 처리를 위해 pageable 객체 생성
        //파라미터는 조회할 페이지 번호 / 가지고 올 데이터 수를 넣음
        //url에 페이지 번호가 없을시 0페이지를 조회

        Page<MainItemDTO> items = itemService.getMainItemPage(itemSearchDTO, pageable);
        //조회 조건과 페이징 정보를 파라미터로 넘겨 Page<MainItemDTO> 객체를 반환

        model.addAttribute("items", items);
        //조회한 상품 데이터 및 페이징 정보를 뷰에 전달
        model.addAttribute("itemSearchDTO", itemSearchDTO);
        //페이지 전환 시 기존 검색 조건 유지
        model.addAttribute("maxPage", 5);
        //상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수
        return "main";
    }
}
