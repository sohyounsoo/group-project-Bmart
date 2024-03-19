package com.group.Bmart.domain.user.repository;

import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.UserGrade;
import com.group.Bmart.domain.user.repository.response.UserOrderCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    @Modifying
    @Query("update User u set u.userGrade = :userGrade"
        + " where u.userId in :userIds")
    void updateUserGrade(
        @Param("userGrade") UserGrade userGrade,
        @Param("userIds") List<Long> userIds);
}
