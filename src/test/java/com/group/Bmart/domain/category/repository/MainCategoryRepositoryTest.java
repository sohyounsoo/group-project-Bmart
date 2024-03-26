package com.group.Bmart.domain.category.repository;

import com.group.Bmart.base.TestQueryDslConfig;
import com.group.Bmart.domain.category.MainCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(TestQueryDslConfig.class)
class MainCategoryRepositoryTest {

    @Autowired
    private MainCategoryRepository mainCategoryRepository;


    @Nested
    @DisplayName("save 메서드 실행")
    class SaveMethod {


        @Test
        @DisplayName("성공")
        public void success() {
            //given
            String categoryName = "test category";
            MainCategory mainCategory = new MainCategory(categoryName);

            //when
            MainCategory save = mainCategoryRepository.save(mainCategory);

            //then
            assertThat(save.getName()).isEqualTo(categoryName);
        }

        @Test
        @DisplayName("예외: 이름이 null")
        public void throwExceptionWhenNameIsBlank() {
            //given
            MainCategory mainCategory = new MainCategory(null);

            //when && then
            assertThatThrownBy(() -> mainCategoryRepository.save(mainCategory))
                    .isInstanceOf(DataIntegrityViolationException.class);

        }

        @Test
        @DisplayName("예외 이미 존재하는 카테고리")
        public void throwExceptionWhenNameIsDuplicated() {
            //given
            String categoryName = "test category";
            MainCategory mainCategory = new MainCategory(categoryName);

            mainCategoryRepository.save(mainCategory);

            // When & Then
            MainCategory duplicateCategory = new MainCategory(categoryName);
            assertThatThrownBy(() -> mainCategoryRepository.save(duplicateCategory))
                    .isInstanceOf(DataIntegrityViolationException.class);

        }


    }
}