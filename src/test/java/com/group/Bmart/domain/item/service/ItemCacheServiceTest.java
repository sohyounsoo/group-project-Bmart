package com.group.Bmart.domain.item.service;

import static org.mockito.Mockito.*;

import com.group.Bmart.domain.item.service.response.ItemRedisDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class ItemCacheServiceTest {

    @Mock
    private RedisTemplate<String, ItemRedisDto> redisTemplateMock;

    @Mock
    private ListOperations<String, ItemRedisDto> listOperationsMock;

    @InjectMocks
    private ItemCacheService itemCacheService;

    @Test
    public void saveNewItemTest() {
        // Given
        ItemRedisDto newItem = new ItemRedisDto(1L,"newItem", 1000, 100, LocalDateTime.now());
        when(redisTemplateMock.opsForList()).thenReturn(listOperationsMock);

        // When
        itemCacheService.saveNewItem(newItem);

        // Then
        verify(listOperationsMock, times(1)).rightPush(eq("new_products"), eq(newItem));
    }

//    @Test
//    public void getNewItemsTest() {
//        // Given
//        when(redisTemplateMock.opsForList()).thenReturn(listOperationsMock);
//        when(listOperationsMock.size("new_products")).thenReturn(2L);
//        List<ItemRedisDto> expectedItems = Arrays.asList(
//                new ItemRedisDto("item1"),
//                new ItemRedisDto("item2")
//        );
//        when(listOperationsMock.range("new_products", 0, -1)).thenReturn(expectedItems);
//
//        // When
//        List<ItemRedisDto> resultItems = itemCacheService.getNewItems();
//
//        // Then
//        assertIterableEquals(expectedItems, resultItems);
//    }
}