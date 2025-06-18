package com.nathannolacio.meusaldo.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String error,
        String message,
        LocalDateTime timestamp
) {
}
