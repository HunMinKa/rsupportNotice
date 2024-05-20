package io.dodn.springboot.core.api.domain.notice;

import io.dodn.springboot.core.api.controller.v1.request.NoticeRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeData(
        String title,
        String content,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String author,
        List<String> attachments
) {
    public static NoticeData from(NoticeRequestDto dto, List<String> attachments) {
        return new NoticeData(
                dto.title(),
                dto.content(),
                dto.startAt(),
                dto.endAt(),
                dto.author(),
                attachments
        );
    }
}