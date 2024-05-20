package io.dodn.springboot.core.api.domain.file;

public class FileStorageProperties {

    private final String uploadDir;

    public static FileStorageProperties of(String uploadDir) {
        return new FileStorageProperties(uploadDir);
    }

    private FileStorageProperties(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
