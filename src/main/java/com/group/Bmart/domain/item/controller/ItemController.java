package com.group.Bmart.domain.item.controller;

import com.group.Bmart.domain.item.ItemSortType;
import com.group.Bmart.domain.item.controller.request.RegisterItemRequest;
import com.group.Bmart.domain.item.controller.request.UpdateItemRequest;
import com.group.Bmart.domain.item.service.ItemService;
import com.group.Bmart.domain.item.service.request.*;
import com.group.Bmart.domain.item.service.response.FindItemDetailResponse;
import com.group.Bmart.domain.item.service.response.FindItemsResponse;
import com.group.Bmart.domain.item.service.response.FindNewItemsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;
    private final String DEFAULT_PREVIOUS_ID = "-1";
    private static final String BASE_URI = "/api/v1/items/";

    @GetMapping
    public ResponseEntity<FindItemsResponse> findItemsByCategory(
        @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) Long lastItemId,
        @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) Long lastIdx,
        @RequestParam int size,
        @RequestParam String main,
        @RequestParam(required = false) String sub,
        @RequestParam String sort) {

        FindItemsByCategoryCommand findItemsByCategoryCommand = FindItemsByCategoryCommand.of(
            lastItemId, lastIdx, main, sub, size, sort);
        FindItemsResponse findItemsResponse = itemService.findItemsByCategory(
            findItemsByCategoryCommand);
        return ResponseEntity.ok(findItemsResponse);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<FindItemDetailResponse> findItemDetail(@PathVariable Long itemId) {
        FindItemDetailCommand findItemDetailCommand = FindItemDetailCommand.from(itemId);
        return ResponseEntity.ok(itemService.findItemDetail(findItemDetailCommand));
    }

    @GetMapping("/new")
    public ResponseEntity<FindItemsResponse> findNewItems(
        @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) Long lastIdx,
        @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) Long lastItemId,
        @RequestParam int size,
        @RequestParam(defaultValue = "POPULAR") String sort
    ) {
        FindNewItemsCommand findNewItemsCommand = FindNewItemsCommand.of(lastIdx, lastItemId, size,
            sort);
        return ResponseEntity.ok(itemService.findNewItems(findNewItemsCommand));
    }

    @GetMapping("/new-items")
    public ResponseEntity<FindNewItemsResponse> findNewItemsWithRedis(
        @RequestParam(defaultValue = "NEW") String sort
    ) {
        return ResponseEntity.ok(itemService.findNewItemsWithRedis(ItemSortType.valueOf(sort)));
    }

    @GetMapping("/hot")
    public ResponseEntity<FindItemsResponse> findHotItems(
        @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) Long lastIdx,
        @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) Long lastItemId,
        @RequestParam int size,
        @RequestParam(defaultValue = "POPULAR") String sort
    ) {
        FindHotItemsCommand findHotItemsCommand = FindHotItemsCommand.of(lastIdx, lastItemId, size,
            sort);
        return ResponseEntity.ok(itemService.findHotItems(findHotItemsCommand));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Void> updateItem(
        @PathVariable Long itemId,
        @RequestBody @Valid UpdateItemRequest updateItemRequest
    ) {
        UpdateItemCommand updateItemCommand = UpdateItemCommand.of(itemId, updateItemRequest);
        itemService.updateItem(updateItemCommand);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> saveItem(
        @RequestBody @Valid RegisterItemRequest registerItemRequest
    ) {
        RegisterItemCommand registerItemCommand = RegisterItemCommand.of(
            registerItemRequest.name(),
            registerItemRequest.price(),
            registerItemRequest.description(),
            registerItemRequest.quantity(),
            registerItemRequest.discount(),
            registerItemRequest.maxBuyQuantity(),
            registerItemRequest.mainCategoryId(),
            registerItemRequest.subCategoryId()
        );
        Long savedItemId = itemService.saveItem(registerItemCommand);
        URI location = URI.create(BASE_URI + savedItemId);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }
}
