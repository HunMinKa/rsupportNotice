package io.dodn.springboot.core.api.config;

import io.dodn.springboot.core.api.support.error.CoreApiException;
import io.dodn.springboot.core.api.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class AsyncExceptionHandlerTest {

    private AsyncExceptionHandler asyncExceptionHandler;
    private Logger mockLogger;

    @BeforeEach
    public void setUp() throws Exception {
        asyncExceptionHandler = new AsyncExceptionHandler();
        mockLogger = Mockito.mock(Logger.class);

        Field logField = AsyncExceptionHandler.class.getDeclaredField("log");
        logField.setAccessible(true);
        logField.set(asyncExceptionHandler, mockLogger);
    }

    @Test
    public void testHandleCoreApiExceptionWithErrorLogLevel() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("testHandleCoreApiExceptionWithErrorLogLevel");
        CoreApiException exception = new CoreApiException(ErrorType.DEFAULT_ERROR, "Test Error");

        asyncExceptionHandler.handleUncaughtException(exception, method);

        verify(mockLogger).error(eq("CoreApiException in method {} with params {}: {}"), eq(method.getName()), any(), eq("An unexpected error has occurred."), eq(exception));
    }

    @Test
    public void testHandleGenericException() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("testHandleGenericException");
        Exception exception = new Exception("Test Exception");

        asyncExceptionHandler.handleUncaughtException(exception, method);

        verify(mockLogger).error(eq("Exception in method {} with params {}: {}"), eq(method.getName()), any(), eq("Test Exception"), eq(exception));
    }
}