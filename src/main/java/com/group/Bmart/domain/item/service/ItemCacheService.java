package com.group.Bmart.domain.item.service;

import com.group.Bmart.domain.item.service.response.ItemRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCacheService {

    private final RedisTemplate<String, ItemRedisDto> redisTemplate;
    private static final String NEW_PRODUCTS_KEY = "new_products";

    public void saveNewItem(final ItemRedisDto itemRedisDto) {
        redisTemplate.opsForList().rightPush(NEW_PRODUCTS_KEY, itemRedisDto);
    }

    public List<ItemRedisDto> getNewItems() {
        ListOperations<String, ItemRedisDto> listOperations = redisTemplate.opsForList();
        Long itemCount = listOperations.size(NEW_PRODUCTS_KEY);
        if (itemCount == null || itemCount == 0) {
            return null;
        }

        return listOperations.range(NEW_PRODUCTS_KEY, 0, -1);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteOldProducts() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(2, ChronoUnit.WEEKS);

        ListOperations<String, ItemRedisDto> items = redisTemplate.opsForList();
        Long itemCount = items.size(NEW_PRODUCTS_KEY);

        if (itemCount == null || itemCount == 0) {
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            ItemRedisDto item = items.index(NEW_PRODUCTS_KEY, i);
            if (item != null && item.createdAt().isBefore(twoWeeksAgo)) {
                items.remove(NEW_PRODUCTS_KEY, 1, item);
                i--;
            }
        }
    }
}
