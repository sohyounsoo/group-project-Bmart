package com.group.Bmart.domain.item.controller;

import com.group.Bmart.domain.item.controller.request.RegisterLikeItemRequest;
import com.group.Bmart.domain.item.service.LikeItemService;
import com.group.Bmart.domain.item.service.request.DeleteLikeItemCommand;
import com.group.Bmart.domain.item.service.request.FindLikeItemsCommand;
import com.group.Bmart.domain.item.service.request.RegisterLikeItemCommand;
import com.group.Bmart.domain.item.service.response.FindLikeItemsResponse;
import com.group.Bmart.global.auth.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeItemController {

    private static final String BASE_URI = "/api/v1/likes/";

    private final LikeItemService likeItemService;

    @PostMapping
    public ResponseEntity<Void> registerLikeItem(
        @RequestBody @Valid RegisterLikeItemRequest registerLikeItemRequest,
        @LoginUser final Long userId) {
        RegisterLikeItemCommand registerLikeItemCommand
            = RegisterLikeItemCommand.of(userId, registerLikeItemRequest.itemId());
        Long likeItemId = likeItemService.registerLikeItem(registerLikeItemCommand);
        URI location = URI.create(BASE_URI + likeItemId);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{likeItemId}")
    public ResponseEntity<Void> deleteLikeItem(
        @PathVariable final Long likeItemId,
        @LoginUser final Long userId) {
        DeleteLikeItemCommand deleteLikeItemCommand = DeleteLikeItemCommand.of(userId, likeItemId);
        likeItemService.deleteLikeItem(deleteLikeItemCommand);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<FindLikeItemsResponse> findLikeItems(
        final Pageable pageable,
        @LoginUser final Long userId) {
        FindLikeItemsCommand findLikeItemsCommand = FindLikeItemsCommand.of(userId, pageable);
        FindLikeItemsResponse findLikeItemsResponse
            = likeItemService.findLikeItems(findLikeItemsCommand);
        return ResponseEntity.ok(findLikeItemsResponse);
    }
}
