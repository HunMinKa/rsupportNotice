package io.dodn.springboot.core.api.domain.notice;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.dodn.springboot.storage.db.core.notice.Notice;

import java.time.LocalDateTime;

public record NoticeResult(
        String title,
        String content,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        int viewCount,
        String author
) {
    public static NoticeResult fromNotice(Notice notice) {
        return new NoticeResult(
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getViewCount(),
                notice.getAuthor()
        );
    }
}