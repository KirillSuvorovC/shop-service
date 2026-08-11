package org.web.shop_service.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseError {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;

    public ResponseError(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    public static ResponseEntity<ResponseError> asEntity(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ResponseError(status, message));
    }
}
