package io.dodn.springboot.core.api.config;

import io.dodn.springboot.core.api.domain.file.FileStorageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Bean
    public FileStorageProperties fileStorageProperties() {
        return FileStorageProperties.of(uploadDir);
    }
}