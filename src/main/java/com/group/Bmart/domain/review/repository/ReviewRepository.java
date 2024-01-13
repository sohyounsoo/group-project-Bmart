package com.group.Bmart.domain.review.repository;

import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.review.Review;
import com.group.Bmart.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByUserOrderByCreatedAt(User user);

    List<Review> findAllByItemOrderByCreatedAt(Item item);

    Long countByItem_ItemId(Long itemId);

    void deleteByUser(User findUser);

    @Query("select avg(r.rate) from Review r where r.item.itemId = :itemId")
    Double findAverageRatingByItemId(@Param("itemId") Long itemId);

}
