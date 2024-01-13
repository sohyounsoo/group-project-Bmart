package com.group.Bmart.domain.category.repository;

import com.group.Bmart.domain.category.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {

    boolean existsByName(String name);

    Optional<MainCategory> findByName(String name);
}
