package io.dodn.springboot.core.api.controller.v1;

import io.dodn.springboot.core.api.controller.v1.request.NoticeRequestDto;
import io.dodn.springboot.core.api.controller.v1.response.NoticeResponseDto;
import io.dodn.springboot.core.api.domain.file.FileStorageService;
import io.dodn.springboot.core.api.domain.notice.NoticeData;
import io.dodn.springboot.core.api.domain.notice.NoticeResult;
import io.dodn.springboot.core.api.domain.notice.NoticeService;
import io.dodn.springboot.core.api.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final NoticeService noticeService;
    private final FileStorageService fileStorageService;

    @Autowired
    public NoticeController(NoticeService noticeService, FileStorageService fileStorageService) {
        this.noticeService = noticeService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public CompletableFuture<ApiResponse<NoticeResult>> createNotice(
            @RequestPart("notice") @Valid NoticeRequestDto noticeRequestDto,
            @RequestPart("attachments") List<MultipartFile> attachments) {

        return CompletableFuture.supplyAsync(() -> processAttachments(attachments))
                .thenApply(attachmentPaths -> {
                    NoticeData noticeData = NoticeData.from(noticeRequestDto, attachmentPaths);
                    NoticeResult noticeResult = noticeService.createNotice(noticeData);
                    return ApiResponse.success(noticeResult);
                });
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public CompletableFuture<ApiResponse<NoticeResult>> updateNotice(
            @PathVariable("id") Long id,
            @RequestPart("notice") @Valid NoticeRequestDto noticeRequestDto,
            @RequestPart("attachments") List<MultipartFile> attachments) {

        return CompletableFuture.supplyAsync(() -> processAttachments(attachments))
                .thenApply(attachmentPaths -> {
                    NoticeData noticeData = NoticeData.from(noticeRequestDto, attachmentPaths);
                    NoticeResult noticeResult = noticeService.updateNotice(id, noticeData);
                    return ApiResponse.success(noticeResult);
                });
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotice(@PathVariable("id") Long id) {
        noticeService.deleteNotice(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<NoticeResponseDto> getNoticeById(@PathVariable("id") Long id) {
        noticeService.incrementViewCount(id);
        NoticeResult noticeResult = noticeService.getNoticeById(id);
        NoticeResponseDto response = NoticeResponseDto.from(noticeResult);
        return ApiResponse.success(response);
    }

    private List<String> processAttachments(List<MultipartFile> attachments) {
        return attachments.stream()
                .map(fileStorageService::storeFile)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}