package com.group.Bmart.global.config;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.service.response.ItemRedisDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(RedisConfig.class)
class RedisConfigTest {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisTemplate<String, Long> stringToLongRedisTemplate;

    @Autowired
    private ListOperations<String, String> listOperations;

    @Autowired
    private RedisTemplate<String, ItemRedisDto> itemRedisDtoRedisTemplate;

    @Test
    public void testRedisConnectionFactory() {
        assertNotNull(redisConnectionFactory);
    }

    @Test
    public void testStringToLongRedisTemplate() {
        assertNotNull(stringToLongRedisTemplate);
    }

    @Test
    public void testListOperations() {
        assertNotNull(listOperations);
    }

    @Test
    public void testItemRedisDtoRedisTemplate() {
        assertNotNull(itemRedisDtoRedisTemplate);
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