package com.group.Bmart.domain.user.controller;

import com.group.Bmart.domain.user.exception.UserException;
import com.group.Bmart.domain.user.service.UserService;
import com.group.Bmart.domain.user.service.request.FindUserCommand;
import com.group.Bmart.domain.user.service.response.FindUserDetailResponse;
import com.group.Bmart.global.auth.LoginUser;
//import com.group.Bmart.global.auth.oauth.client.OAuthRestClient;
import com.group.Bmart.global.auth.oauth.client.OAuthRestClient;
import com.group.Bmart.global.dto.CommonResponse;
import com.group.Bmart.global.message.Message;
import com.group.Bmart.global.util.ErrorTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final OAuthRestClient restClient;

//    @GetMapping("/users/me")
//    public ResponseEntity<FindUserDetailResponse> findUser(@LoginUser Long userId) {
//        FindUserDetailResponse findUserDetailResponse =
//            userService.findUser(FindUserCommand.from(userId));
//        return ResponseEntity.ok(findUserDetailResponse);
//    }

    @GetMapping("/users/me")
    public ResponseEntity<CommonResponse> findUser(@LoginUser Long userId) {
        FindUserDetailResponse findUserDetailResponse =
                userService.findUser(FindUserCommand.from(userId));

        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(findUserDetailResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<CommonResponse> deleteUser(@LoginUser Long userId) {
        FindUserCommand findUserDetailCommand = FindUserCommand.from(userId);
        FindUserDetailResponse findUserDetailResponse = userService.findUser(findUserDetailCommand);
        restClient.callUnlinkOAuthUser(findUserDetailResponse);
        userService.deleteUser(userId);

        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(Message.ACCOUNT_DELETED_SUCCESSFULLY)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorTemplate> userExHandle(UserException ex) {
        return ResponseEntity.badRequest()
            .body(ErrorTemplate.of(ex.getMessage()));
    }
}
