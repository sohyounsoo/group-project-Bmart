package com.group.Bmart.domain.user.service;

import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.repository.UserRepository;
import com.group.Bmart.domain.user.service.request.RegisterUserCommand;
import com.group.Bmart.domain.user.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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

    }

}