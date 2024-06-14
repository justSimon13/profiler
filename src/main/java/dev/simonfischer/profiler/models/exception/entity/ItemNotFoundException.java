package dev.simonfischer.profiler.models.exception.entity;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ItemNotFoundException(String message) {
        super(message);
    }
    public ItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
