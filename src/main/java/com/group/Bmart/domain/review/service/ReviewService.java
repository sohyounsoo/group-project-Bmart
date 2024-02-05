package com.group.Bmart.domain.review.service;


import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.exception.NotFoundItemException;
import com.group.Bmart.domain.item.repository.ItemRepository;
import com.group.Bmart.domain.review.Review;
import com.group.Bmart.domain.review.exception.NotFoundReviewException;
import com.group.Bmart.domain.review.repository.ReviewRepository;
import com.group.Bmart.domain.review.service.request.RegisterReviewCommand;
import com.group.Bmart.domain.review.service.request.UpdateReviewCommand;
import com.group.Bmart.domain.review.service.response.FindReviewsByItemResponse;
import com.group.Bmart.domain.review.service.response.FindReviewsByUserResponse;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.exception.NotFoundUserException;
import com.group.Bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RedisCacheService redisCacheService;

    private static final String REVIEW_COUNT_CACHE_KEY = "reviewCount:Item:";
    private static final String AVERAGE_RATE_CACHE_KEY = "averageRating:Item:";

    @Transactional
    public Long registerReview(
        RegisterReviewCommand registerReviewCommand
    ) {
        User foundUser = userRepository.findById(registerReviewCommand.userId())
            .orElseThrow(() -> new NotFoundUserException("존재하지 않은 사용자입니다."));

        Item foundItem = findItemByItemId(registerReviewCommand.itemId());

        Review review = Review.builder()
            .user(foundUser)
            .item(foundItem)
            .rate(registerReviewCommand.rate())
            .content(registerReviewCommand.content())
            .build();

        Review savedReview = reviewRepository.save(review);

        String cacheKey = REVIEW_COUNT_CACHE_KEY + foundItem.getItemId();

        redisCacheService.plusOneToTotalNumberOfReviewsByItemId(foundItem.getItemId(), cacheKey);
        redisCacheService.updateAverageRatingByItemId(foundItem.getItemId(), cacheKey,
            savedReview.getRate());

        return savedReview.getReviewId();
    }

    @Transactional
    public void deleteReview(
        final Long reviewId
    ) {
        Review foundReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NotFoundReviewException("해당 리뷰를 찾을 수 없습니다."));

        reviewRepository.delete(foundReview);

        String cacheKey = REVIEW_COUNT_CACHE_KEY + foundReview.getItem().getItemId();
        redisCacheService.minusOneToTotalNumberOfReviewsByItemId(foundReview.getItem().getItemId(),
            cacheKey);
    }

    @Transactional
    public void updateReview(
        UpdateReviewCommand updateReviewCommand
    ) {
        Review foundReview = reviewRepository.findById(updateReviewCommand.reviewId())
            .orElseThrow(() -> new NotFoundReviewException("해당 리뷰를 찾을 수 없습니다."));

        foundReview.changeRageAndContent(updateReviewCommand.rate(), updateReviewCommand.content());
    }

    @Transactional(readOnly = true)
    public FindReviewsByUserResponse findReviewsByUser(
        final Long userId
    ) {
        User foundUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException("해당 사용자를 찾을 수 없습니다."));

        List<Review> foundReviews = reviewRepository.findAllByUserOrderByCreatedAt(
            foundUser);

        return FindReviewsByUserResponse.of(
            foundUser, foundReviews);
    }

    @Transactional(readOnly = true)
    public FindReviewsByItemResponse findReviewsByItem(
        final Long itemId
    ) {
        Item foundItem = findItemByItemId(itemId);

        List<Review> foundReviews = reviewRepository.findAllByItemOrderByCreatedAt(
            foundItem);

        return FindReviewsByItemResponse.of(foundItem.getItemId(), foundReviews);
    }

    @Transactional(readOnly = true)
    public Long findTotalReviewsByItem(
        final Long itemId
    ) {
        Item foundItem = findItemByItemId(itemId);

        String cacheKey = REVIEW_COUNT_CACHE_KEY + foundItem.getItemId();
        return redisCacheService.getTotalNumberOfReviewsByItemId(foundItem.getItemId(), cacheKey);
    }

    @Transactional(readOnly = true)
    public Double findAverageRatingByItem(
        final Long itemId
    ) {
        Item foundItem = findItemByItemId(itemId);

        String cacheKey = AVERAGE_RATE_CACHE_KEY + foundItem.getItemId();
        return redisCacheService.getAverageRatingByItemId(itemId, cacheKey);
    }

    private Item findItemByItemId(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundItemException("해당 상품을 찾을 수 없습니다."));
    }
}
