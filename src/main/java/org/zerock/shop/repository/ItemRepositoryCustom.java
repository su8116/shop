package org.zerock.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.shop.dto.ItemSearchDTO;
import org.zerock.shop.dto.MainItemDTO;
import org.zerock.shop.entity.Item;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO,
                                Pageable pageable);

    Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO,
                                      Pageable pageable);
}
