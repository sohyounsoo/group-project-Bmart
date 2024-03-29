package com.group.Bmart.domain.item.service;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.category.fixture.CategoryFixture;
import com.group.Bmart.domain.category.repository.MainCategoryRepository;
import com.group.Bmart.domain.category.repository.SubCategoryRepository;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.ItemSortType;
import com.group.Bmart.domain.item.repository.ItemRepository;
import com.group.Bmart.domain.item.service.request.FindItemsByCategoryCommand;
import com.group.Bmart.domain.item.service.request.RegisterItemCommand;
import com.group.Bmart.domain.item.service.response.FindItemsResponse;
import com.group.Bmart.domain.item.service.response.ItemRedisDto;
import com.group.Bmart.domain.item.support.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MainCategoryRepository mainCategoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private ItemCacheService itemCacheService;

    @InjectMocks
    private ItemService itemService;

    @Nested
    @DisplayName("saveItem 메서드 실행 시")
    class SaveItem {

        RegisterItemCommand registerItemCommand = ItemFixture.registerItemCommand();
        Item item = ItemFixture.item();
        MainCategory mainCategory = CategoryFixture.mainCategory();
        SubCategory subCategory = CategoryFixture.subCategory(mainCategory);


        @Test
        @DisplayName("성공")
        public void save() {
            //given
            given(mainCategoryRepository.findById(anyLong())).willReturn(Optional.of(mainCategory));
            given(subCategoryRepository.findById(anyLong())).willReturn(Optional.of(subCategory));
            given(itemRepository.save(any())).willReturn(item);

            //when
            itemService.saveItem(registerItemCommand);

            //then
            verify(mainCategoryRepository, times(1)).findById(anyLong());
            verify(subCategoryRepository, times(1)).findById(anyLong());
            verify(itemRepository, times(1)).save(any());
            verify(itemCacheService, times(1)).saveNewItem(any(ItemRedisDto.class));
        }
    }

    @Nested
    @DisplayName("updateItem 메서드 실행시")
    class UpdateItem {

    }

    @Nested
    @DisplayName("findItemsByMainCategory 메서드 실행 시")
    class FindItemByMainCategory {

        MainCategory mainCategory = CategoryFixture.mainCategory();
        SubCategory subCategory = new SubCategory(mainCategory, "sub1");
        private static final int DEFAULT_PAGE_NUM = 0;
        private static final int DEFAULT_PAGE_SIZE = 3;

        @Test
        @DisplayName("최신 등록 순으로 조회")
        public void orderByLatest() {
            //given
            List<Item> expectedItems = getItems();
            FindItemsByCategoryCommand findItemsByCategoryCommand = getFindItemsByCategoryCommand(
                    ItemSortType.NEW);

            //when
            when(mainCategoryRepository.findByName(any())).thenReturn(Optional.of(mainCategory));
            when(subCategoryRepository.findByName(any())).thenReturn(Optional.of(subCategory));
            when(itemRepository.findBySubCategoryOrderBy(
                    any(MainCategory.class), any(SubCategory.class), anyLong(), anyLong(), any(ItemSortType.class), any(Pageable.class)))
                    .thenReturn(expectedItems);

            FindItemsResponse itemsResponse = itemService.findItemsByCategory(findItemsByCategoryCommand);
            FindItemsResponse expectedResponse = FindItemsResponse.from(expectedItems);

            //then
            assertThat(itemsResponse.items().size()).isEqualTo(DEFAULT_PAGE_SIZE);
            assertThat(itemsResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
        }

        @Test
        @DisplayName("할인율 높은 순으로 조회")
        public void orderByDiscountRateDesc() {
            //given
            List<Item> expectedItems = getItems();
            FindItemsByCategoryCommand findItemsByCategoryCommand = getFindItemsByCategoryCommand(
                    ItemSortType.DISCOUNT);

            //when
            when(mainCategoryRepository.findByName(any())).thenReturn(Optional.of(mainCategory));
            when(subCategoryRepository.findByName(any())).thenReturn(Optional.of(subCategory));
            when(itemRepository.findBySubCategoryOrderBy(
                    any(MainCategory.class), any(SubCategory.class), anyLong(), anyLong(), any(ItemSortType.class), any(Pageable.class)))
                    .thenReturn(expectedItems);

            FindItemsResponse itemsResponse = itemService.findItemsByCategory(findItemsByCategoryCommand);

            //then
            assertThat(itemsResponse.items().size()).isEqualTo(DEFAULT_PAGE_SIZE);
        }

        @Test
        @DisplayName("금액 높은 순으로 조회")
        public void orderByPriceDesc() {
            //given
            List<Item> expectedItems = getItems();
            FindItemsByCategoryCommand findItemsByCategoryCommand = getFindItemsByCategoryCommand(
                    ItemSortType.HIGHEST_AMOUNT);

            //when
            when(mainCategoryRepository.findByName(any())).thenReturn(Optional.of(mainCategory));
            when(subCategoryRepository.findByName(any())).thenReturn(Optional.of(subCategory));
            when(itemRepository.findBySubCategoryOrderBy(
                    any(MainCategory.class), any(SubCategory.class), anyLong(), anyLong(), any(ItemSortType.class), any(Pageable.class)))
                    .thenReturn(expectedItems);

            FindItemsResponse itemsResponse =
                    itemService.findItemsByCategory(findItemsByCategoryCommand);

            //then
            assertThat(itemsResponse.items().size()).isEqualTo(DEFAULT_PAGE_SIZE);
        }

        private FindItemsByCategoryCommand getFindItemsByCategoryCommand(
                ItemSortType itemSortType) {
            return new FindItemsByCategoryCommand(
                    -1L, -1L, mainCategory.getName(), subCategory.getName(),
                    PageRequest.of(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE),
                    itemSortType);
        }

        private List<Item> getItems() {
            Item item1 = Item.builder()
                    .name("name1")
                    .price(10)
                    .quantity(10)
                    .discount(1)
                    .maxBuyQuantity(50)
                    .mainCategory(mainCategory)
                    .subCategory(subCategory)
                    .build();

            Item item2 = Item.builder()
                    .name("name2")
                    .price(100)
                    .quantity(10)
                    .discount(10)
                    .maxBuyQuantity(50)
                    .mainCategory(mainCategory)
                    .subCategory(subCategory)
                    .build();

            Item item3 = Item.builder()
                    .name("name3")
                    .price(1000)
                    .quantity(10)
                    .discount(100)
                    .maxBuyQuantity(50)
                    .mainCategory(mainCategory)
                    .subCategory(subCategory)
                    .build();

            return List.of(item1, item2, item3);
        }
    }

}
