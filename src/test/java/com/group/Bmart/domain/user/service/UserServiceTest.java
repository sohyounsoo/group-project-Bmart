package com.group.Bmart.domain.user.service;

import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.exception.NotFoundUserException;
import com.group.Bmart.domain.user.repository.UserRepository;
import com.group.Bmart.domain.user.service.request.FindUserCommand;
import com.group.Bmart.domain.user.service.request.RegisterUserCommand;
import com.group.Bmart.domain.user.service.response.FindUserDetailResponse;
import com.group.Bmart.domain.user.support.UserFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Nested
    @DisplayName("getOrRegisterUser 메서드 실행 시")
    class GetOrRegisterUserTest {

        RegisterUserCommand registerUserCommand = UserFixture.registerUserCommand();

        @Test
        @DisplayName("성공: User가 존재하면 User 반환")
        void getUserWhenUserExists() {
            //given
            User user = UserFixture.user();
            given(userRepository.findByProviderAndProviderId(any(), any())).willReturn(
                    Optional.ofNullable(user));

            //when
            userService.getOrRegisterUser(registerUserCommand);

            //then
            then(userRepository).should(times(0)).save(any());
        }

        @Test
        @DisplayName("성공: User가 존재하지않으면 생성후 User 반환")
        void getUserWhenUserNotFound() {
            //given
            given(userRepository.findByProviderAndProviderId(any(), any())).willReturn(
                    Optional.empty());

            //when
            userService.getOrRegisterUser(registerUserCommand);

            //then
            then(userRepository).should(times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("getOrRegisterUser 메서드 실행 시")
    class FindUserTest {

        FindUserCommand findUserCommand = UserFixture.findUserCommand();

        @Test
        @DisplayName("성공")
        void success() {
            log.info("findUserCommand= {} " + findUserCommand);

            //given
            User user = UserFixture.user();

            given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

            //when
            FindUserDetailResponse result = userService.findUser(findUserCommand);

            //then
            assertThat(result.nickname()).isEqualTo(user.getNickname());
            assertThat(result.email()).isEqualTo(user.getEmail());
            assertThat(result.provider()).isEqualTo(user.getProvider());
            assertThat(result.providerId()).isEqualTo(user.getProviderId());
            assertThat(result.userRole()).isEqualTo(user.getUserRole());
            assertThat(result.userGrade()).isEqualTo(user.getUserGrade());
        }

        @Test
        @DisplayName("예외: 존재하지 않는 User")
        void throwExceptionWhenUserNotFound() {
            //given
            given(userRepository.findById(any())).willReturn(Optional.empty());

            //when
            //then
            assertThatThrownBy(() -> userService.findUser(findUserCommand))
                    .isInstanceOf(NotFoundUserException.class);

        }
    }

}