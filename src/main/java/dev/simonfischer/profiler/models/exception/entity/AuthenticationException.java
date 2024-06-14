package dev.simonfischer.profiler.models.exception.entity;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    public AuthenticationException(String message) {
        super(message);
    }
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
