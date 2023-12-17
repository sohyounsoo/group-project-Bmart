package com.group.Bmart.global.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
    String code,
    String message,
    int status
) {

}
