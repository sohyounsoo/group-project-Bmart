package com.group.Bmart.global.config;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.service.response.ItemRedisDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ListOperations<String, String> listOperations;

    @BeforeEach
    public void setUp() {
        // listOperations 초기화
        listOperations = redisTemplate.opsForList();
        // Redis 리스트 초기화
        listOperations.trim("testList", 0, 0);
    }

    @Test
    public void testListOperations() {
        // Redis 리스트에 데이터 추가
        listOperations.leftPush("testList", "value1");
        listOperations.leftPush("testList", "value2");
        listOperations.leftPush("testList", "value3");

        // Redis 리스트의 크기 확인
        Long listSize = listOperations.size("testList");
        assertEquals(4, listSize); // 초기화된 요소 + 추가된 요소들
    }

    @Test
    public void testFrom() {
        // given
        String name = "Test Item";
        int price = 1000;
        String description = "Test description";
        int quantity = 10;
        int discount = 100;
        int maxBuyQuantity = 5;
        MainCategory mainCategory = new MainCategory("Test Main Category");
        SubCategory subCategory = new SubCategory(mainCategory, "Test Sub Category");

        // when
        Item item = Item.builder()
                .name(name)
                .price(price)
                .description(description)
                .quantity(quantity)
                .discount(discount)
                .maxBuyQuantity(maxBuyQuantity)
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .build();
        ItemRedisDto itemRedisDto = ItemRedisDto.from(item);

        // then
        assertEquals(name, itemRedisDto.name());
        assertEquals(price, itemRedisDto.price());
        assertEquals(discount, itemRedisDto.discount());
    }
}