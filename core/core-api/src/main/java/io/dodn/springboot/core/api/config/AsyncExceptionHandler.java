package io.dodn.springboot.core.api.config;

import io.dodn.springboot.core.api.support.error.CoreApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(AsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... params) {
        if (e instanceof CoreApiException coreApiException) {
            switch (coreApiException.getErrorType().getLogLevel()) {
                case ERROR -> log.error("CoreApiException in method {} with params {}: {}", method.getName(), params, e.getMessage(), e);
                case WARN -> log.warn("CoreApiException in method {} with params {}: {}", method.getName(), params, e.getMessage(), e);
                default -> log.info("CoreApiException in method {} with params {}: {}", method.getName(), params, e.getMessage(), e);
            }
        } else {
            log.error("Exception in method {} with params {}: {}", method.getName(), params, e.getMessage(), e);
        }
    }
}