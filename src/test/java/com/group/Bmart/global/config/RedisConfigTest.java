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

}