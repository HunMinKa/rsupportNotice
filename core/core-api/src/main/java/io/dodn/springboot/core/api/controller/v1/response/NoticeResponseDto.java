package io.dodn.springboot.core.api.controller.v1.response;

import io.dodn.springboot.core.api.domain.notice.NoticeResult;

import java.time.LocalDateTime;

public record NoticeResponseDto(
        String title,
        String content,
        LocalDateTime createdAt,
        int viewCount,
        String author
) {
    public static NoticeResponseDto from(NoticeResult result) {
        return new NoticeResponseDto(
                result.title(),
                result.content(),
                result.createdAt(),
                result.viewCount(),
                result.author()
        );
    }
}