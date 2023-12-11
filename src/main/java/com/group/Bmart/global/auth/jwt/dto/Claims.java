package com.group.Bmart.global.auth.jwt.dto;

import java.util.List;

public record Claims(Long userId, List<String> authorities) {

}
