package io.dodn.springboot.core.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AsyncConfigTest {

    @Test
    public void testAsyncExecutor() {
        AsyncConfig asyncConfig = new AsyncConfig();
        Executor executor = asyncConfig.getAsyncExecutor();
        assertNotNull(executor);

        // Verify the executor properties
        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(10, threadPoolTaskExecutor.getCorePoolSize());
        assertEquals(10, threadPoolTaskExecutor.getMaxPoolSize());
        assertEquals(10000, threadPoolTaskExecutor.getQueueCapacity());
        assertEquals("AsyncExecutor-", threadPoolTaskExecutor.getThreadNamePrefix());
    }

}