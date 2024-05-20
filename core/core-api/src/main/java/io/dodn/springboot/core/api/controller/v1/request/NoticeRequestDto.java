package io.dodn.springboot.core.api.controller.v1.request;

import io.dodn.springboot.core.api.validation.ValidNotice;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@ValidNotice
public record NoticeRequestDto(
        String title,
        String content,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String author,
        List<MultipartFile> attachments
) {
}
