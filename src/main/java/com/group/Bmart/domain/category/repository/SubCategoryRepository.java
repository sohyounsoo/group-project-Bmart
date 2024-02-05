package com.group.Bmart.domain.category.repository;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    boolean existsByMainCategoryAndName(MainCategory mainCategory, String name);

    List<SubCategory> findByMainCategory(MainCategory mainCategory);

    Optional<SubCategory> findByName(String name);
}
