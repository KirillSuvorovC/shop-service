package org.web.shop_service.utils.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException(Class<?> entityType) {
        super(entityType.getSimpleName() + " not found.");
    }
}
