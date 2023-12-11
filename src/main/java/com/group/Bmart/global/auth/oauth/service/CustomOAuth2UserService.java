package com.group.Bmart.global.auth.oauth.service;

import com.group.Bmart.domain.user.UserGrade;
import com.group.Bmart.domain.user.UserRole;
import com.group.Bmart.domain.user.service.UserService;
import com.group.Bmart.domain.user.service.request.RegisterUserCommand;
import com.group.Bmart.domain.user.service.response.RegisterUserResponse;
import com.group.Bmart.global.auth.oauth.OAuthProvider;
import com.group.Bmart.global.auth.oauth.dto.CustomOAuth2User;
import com.group.Bmart.global.auth.oauth.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(final OAuth2UserRequest userRequest)
        throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthUserInfo oAuthUserInfo = OAuthProvider.getOAuthProvider(registrationId)
            .getOAuthUserInfo(attributes);

        RegisterUserCommand registerUserCommand = RegisterUserCommand.of(
            oAuthUserInfo.nickname(),
            oAuthUserInfo.email(),
            registrationId,
            oAuthUserInfo.oAuthUserId(),
            UserRole.ROLE_USER,
            UserGrade.NORMAL);
        RegisterUserResponse userResponse = userService.getOrRegisterUser(registerUserCommand);
        return new CustomOAuth2User(userResponse, attributes);
    }
}
