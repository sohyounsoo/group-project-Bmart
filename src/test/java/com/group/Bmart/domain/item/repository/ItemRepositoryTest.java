package com.group.Bmart.domain.item.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.group.Bmart.base.TestQueryDslConfig;
import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.category.fixture.CategoryFixture;
import com.group.Bmart.domain.category.repository.MainCategoryRepository;
import com.group.Bmart.domain.category.repository.SubCategoryRepository;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.ItemSortType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@Import(TestQueryDslConfig.class)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MainCategoryRepository mainCategoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    EntityManager entityManager;


    @AfterEach
    public void teardown() {
        itemRepository.deleteAll();
        entityManager
                .createNativeQuery("ALTER TABLE item ALTER COLUMN `item_id` RESTART WITH 1")
                .executeUpdate();
    }

    @Nested
    @DisplayName("대카테고리 별 아이템")
    class FindByMainCategoryByCriteria {

        MainCategory mainCategory = CategoryFixture.mainCategory();
        SubCategory subCategory = CategoryFixture.subCategory(mainCategory);

        @Test
        @DisplayName("최신순으로 조회된다.")
        public void findMyItemIdLessThanAndMainCategoryOrderByItemIdDesc() {
            //given
            List<Item> savedItems = new ArrayList<>();
            mainCategoryRepository.save(mainCategory);
            subCategoryRepository.save(subCategory);
            for (int i = 0; i < 50; i++) { // 50개 저장
                Item item = getSavedItem(i, 1, "description", 1, 1, 1, mainCategory, null);
                savedItems.add(item);
            }

            //when
            PageRequest pageRequest = PageRequest.of(0, 5);
            List<Item> items = itemRepository.findByMainCategoryOrderBy(
                    mainCategory, 30L, Long.MAX_VALUE,
                    ItemSortType.NEW, pageRequest);

            //then
            assertThat(items.size()).isEqualTo(5);

            List<Item> expectedItems = savedItems.subList(24, 29);
            Collections.reverse(expectedItems);
            List<String> expected = expectedItems.stream()
                    .map(Item::getName)
                    .toList();

            List<String> actual = items.stream()
                    .map(Item::getName)
                    .toList();

            assertThat(actual).usingRecursiveComparison()
                    .isEqualTo(expected);

        }

        @Test
        @DisplayName("할인율 높은 순으로 조회된다.")
        public void findByPriceLessThanAndMainCategoryOrderByPriceDescItemIdDesc() {
            //given
            mainCategoryRepository.save(mainCategory);
            subCategoryRepository.save(subCategory);
            for (int i = 0; i < 50; i++) { // 50개 저장
                getSavedItem(i, (int) (Math.random() * 1000), "0", 0, 0, 0,
                        mainCategory, null);
            }

            //when
            PageRequest pageRequest = PageRequest.of(0, 5);
            List<Item> items = itemRepository.findByMainCategoryOrderBy(
                    mainCategory, 100L, Long.MAX_VALUE,
                    ItemSortType.DISCOUNT, pageRequest);

            //then
            assertThat(items.size()).isEqualTo(5);
        }
    }

    private Item getSavedItem(int i, int price, String description, int quantity, int discount,
        int maxBuyQuantity, MainCategory mainCategory, SubCategory subCategory) {

        Item item = new Item(
            "item" + (i + 1), price, description, quantity, discount, maxBuyQuantity,
                mainCategory, subCategory);
        itemRepository.save(item);
        return item;
    }
}