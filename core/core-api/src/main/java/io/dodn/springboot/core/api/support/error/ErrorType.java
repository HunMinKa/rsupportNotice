package io.dodn.springboot.core.api.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "Notice not found.", LogLevel.WARN),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, "Invalid request.", LogLevel.WARN),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "File upload error.", LogLevel.ERROR);

    private final HttpStatus status;
    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}
