package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.ItemImg;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
}
