package com.group.Bmart.global.auth.jwt;


import com.group.Bmart.global.auth.jwt.dto.Claims;
import com.group.Bmart.global.auth.jwt.dto.CreateTokenCommand;

public interface TokenProvider {

    String createToken(final CreateTokenCommand createTokenCommand);

    Claims validateToken(final String accessToken);
}
