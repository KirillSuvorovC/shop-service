package org.web.shop_service.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@Schema(description = "Ответ об ошибке")
public class ResponseError {
    @Schema(description = "HTTP статус", example = "404")
    private int status;
    @Schema(description = "Сообщение", example = "Not found")
    private String message;
    @Schema(description = "Время", example = "2007-12-03T10:15:30+01:00")
    private ZonedDateTime timestamp;

    public ResponseError() {}

    public ResponseError(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        timestamp = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public static ResponseEntity<ResponseError> asEntity(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ResponseError(status, message));
    }

    public static String asJson(HttpStatus status, String message) {
        final var now = ZonedDateTime.now(ZoneOffset.UTC);
        return """
                {
                    "status": %d,
                    "message": "%s",
                    "timestamp": "%s"
                }
               """.formatted(status.value(), message, now.toString());
    }
}
