package org.web.shop_service.outbox.publishbox.exceptions;

public class PublishBoxException extends RuntimeException {
    public PublishBoxException(String message, Throwable e) {
        super(message, e);
    }
}
