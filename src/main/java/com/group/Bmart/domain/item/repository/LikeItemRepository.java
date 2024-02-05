package com.group.Bmart.domain.item.repository;

import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.LikeItem;
import com.group.Bmart.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeItemRepository extends JpaRepository<LikeItem, Long> {

    boolean existsByUserAndItem(User user, Item item);

    @Query("select li from LikeItem li join fetch li.item where li.user = :user")
    Page<LikeItem> findByUserWithItem(@Param("user") User user, Pageable pageable);

    void deleteByUser(User user);
}
