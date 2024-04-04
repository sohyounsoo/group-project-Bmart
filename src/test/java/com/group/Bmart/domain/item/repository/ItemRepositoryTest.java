package com.group.Bmart.domain.item.repository;

import static com.group.Bmart.domain.item.support.ItemFixture.item;
import static org.assertj.core.api.Assertions.assertThat;

import com.group.Bmart.base.TestQueryDslConfig;
import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.category.fixture.CategoryFixture;
import com.group.Bmart.domain.category.repository.MainCategoryRepository;
import com.group.Bmart.domain.category.repository.SubCategoryRepository;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.ItemSortType;
import com.group.Bmart.domain.order.OrderItem;
import com.group.Bmart.domain.order.repository.OrderItemRepository;
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
import java.util.Optional;

@DataJpaTest
@Import(TestQueryDslConfig.class)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

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
        public void findByItemIdLessThanAndMainCategoryOrderByItemIdDescDiscountDesc() {
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

        @Test
        @DisplayName("금액 높은 순으로 조회된다.")
        public void findByPriceLessThanAndMainCategoryOrderByPriceDescItemIdDesc() {
            //given
            mainCategoryRepository.save(mainCategory);
            for (int i = 0; i < 50; i++) { // 50개 저장
                getSavedItem(i, (int) (Math.random() * 1000), "0", 0, 0, 0,
                        mainCategory, null);
            }

            //when
            PageRequest pageRequest = PageRequest.of(0, 5);
            List<Item> items = itemRepository.findByMainCategoryOrderBy(
                    mainCategory, 10000L, Long.MAX_VALUE,
                    ItemSortType.HIGHEST_AMOUNT, pageRequest);

            //then
            assertThat(items.size()).isEqualTo(5);
        }

        @Test
        @DisplayName("금액 낮은 순으로 조회된다.")
        public void findByPriceGreaterThanAndMainCategoryOrderByPriceAscItemIdDesc() {
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
                    mainCategory, 0L, Long.MAX_VALUE,
                    ItemSortType.LOWEST_AMOUNT, pageRequest);

            //then
            assertThat(items.size()).isEqualTo(5);
        }

        @Test
        @DisplayName("인기 순으로 조회된다.")
        public void findByOrderCount() {

            //Given
            mainCategoryRepository.save(mainCategory);
            subCategoryRepository.save(subCategory);
            for (int i = 0; i < 50; i++) {
                Item item = getSavedItem(i, (int) (Math.random() * 1000), "0", 0, 0, 0,
                        mainCategory, subCategory);
                OrderItem orderItem = new OrderItem(item, (50 - i));
                orderItemRepository.save(orderItem);
            }
            List<Long> expectedItemIds = List.of(1L, 2L, 3L, 4L, 5L);

            // When
            PageRequest pageRequest = PageRequest.of(0, 5);
            List<Item> items = itemRepository.findByMainCategoryOrderBy(
                    mainCategory, 10000L, Long.MAX_VALUE,
                    ItemSortType.POPULAR, pageRequest);

            // Then
            assertThat(items).map(Item::getItemId)
                    .isEqualTo(expectedItemIds);
        }
    }

    @Test
    @DisplayName("아이템 삭제 시, 아이템 조회가 안된다.")
    public void deleteItem() {
        // Given
        MainCategory mainCategory = new MainCategory("main");
        SubCategory subCategory = new SubCategory(mainCategory, "sub");
        Item item = item(mainCategory, subCategory);
        Item savedItem = itemRepository.save(item);

        // When
        itemRepository.deleteById(savedItem.getItemId());

        // Then
        Optional<Item> findItem = itemRepository.findByItemId(savedItem.getItemId());
        assertThat(findItem.isEmpty()).isEqualTo(true);
    }

    @Nested
    @DisplayName("increaseQuantity 메서드는")
    class IncreaseQuantityTest {

        @Test
        @DisplayName("성공")
        public void success() {
            // Given
            int increaseQuantity = 100;
            Item item = item();
            int originQuantity = item.getQuantity();

            mainCategoryRepository.save(item.getMainCategory());
            subCategoryRepository.save(item.getSubCategory());
            itemRepository.save(item);

            // When
            itemRepository.increaseQuantity(item.getItemId(), increaseQuantity);

            // Then
            Optional<Item> findItem = itemRepository.findById(item.getItemId());
            assertThat(findItem).isNotEmpty();
            assertThat(findItem.get().getQuantity()).isEqualTo(originQuantity + increaseQuantity);
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