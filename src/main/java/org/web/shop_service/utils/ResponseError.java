package org.web.shop_service.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class ResponseError {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ResponseError() {}

    public ResponseError(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    public static ResponseEntity<ResponseError> asEntity(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ResponseError(status, message));
    }
}
