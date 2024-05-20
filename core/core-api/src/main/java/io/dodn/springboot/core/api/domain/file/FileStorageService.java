package io.dodn.springboot.core.api.domain.file;

import io.dodn.springboot.core.api.support.error.CoreApiException;
import io.dodn.springboot.core.api.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new CoreApiException(ErrorType.FILE_UPLOAD_ERROR, "업로드된 파일이 저장될 디렉터리를 만들 수 없습니다.");
        }
    }

    @Async
    public CompletableFuture<String> storeFile(MultipartFile file) {
        String fileName = cleanFileName(file);

        try {
            checkFileName(fileName);
            String filePath = copyFileToTargetLocation(file, fileName);
            return CompletableFuture.completedFuture(filePath);
        } catch (IOException ex) {
            throw new CoreApiException(ErrorType.FILE_UPLOAD_ERROR, "파일을 저장할 수 없습니다." + fileName + ". 다시 시도하십시오!");
        }
    }

    private String cleanFileName(MultipartFile file) {
        return StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    }

    private void checkFileName(String fileName) {
        if (fileName.contains("..")) {
            throw new CoreApiException(ErrorType.FILE_UPLOAD_ERROR, "파일 이름에 잘못된 경로 시퀀스가 포함되어 있습니다" + fileName);
        }
    }

    private String copyFileToTargetLocation(MultipartFile file, String fileName) throws IOException {
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return targetLocation.toString();
    }
}
