package com.group.Bmart.domain.item.repository;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.ItemSortType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    List<Item> findNewItemsOrderBy(Long lastIdx, Long lastItemId, ItemSortType sortType,
        Pageable pageable);

    List<Item> findHotItemsOrderBy(Long lastIdx, Long lastItemId, ItemSortType sortType,
        Pageable pageable);

    List<Item> findByMainCategoryOrderBy(MainCategory mainCategory, Long lastIdx, Long lastItemId,
        ItemSortType sortType, Pageable pageable);

    List<Item> findBySubCategoryOrderBy(MainCategory mainCategory, SubCategory subCategory,
        Long lastIdx, Long lastItemId, ItemSortType sortType, Pageable pageable);
}
