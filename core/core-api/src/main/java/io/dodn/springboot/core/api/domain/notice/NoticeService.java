package io.dodn.springboot.core.api.domain.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dodn.springboot.core.api.support.error.CoreApiException;
import io.dodn.springboot.core.api.support.error.ErrorType;
import io.dodn.springboot.storage.db.core.notice.Notice;
import io.dodn.springboot.storage.db.core.notice.NoticeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeService.class);
    private static final String NOTICE_KEY_PREFIX = "notices::";

    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.noticeRepository = noticeRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @CacheEvict(value = "notices", allEntries = true)
    public NoticeResult createNotice(NoticeData noticeData) {
        Notice notice = new Notice.Builder()
                .withTitle(noticeData.title())
                .withContent(noticeData.content())
                .withStartAt(noticeData.startAt())
                .withEndAt(noticeData.endAt())
                .withAuthor(noticeData.author())
                .withAttachments(noticeData.attachments())
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        return NoticeResult.fromNotice(savedNotice);
    }

    @CacheEvict(value = "notices", key = "#p0")
    public NoticeResult updateNotice(Long id, NoticeData noticeData) {
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new CoreApiException(ErrorType.NOTICE_NOT_FOUND));

        existingNotice.update(
                noticeData.title(),
                noticeData.content(),
                noticeData.startAt(),
                noticeData.endAt(),
                noticeData.author(),
                noticeData.attachments()
        );

        Notice updatedNotice = noticeRepository.save(existingNotice);
        return NoticeResult.fromNotice(updatedNotice);
    }

    @CacheEvict(value = "notices", key = "#p0")
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new CoreApiException(ErrorType.NOTICE_NOT_FOUND);
        }
        noticeRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "notices", key = "#p0", condition = "#p0!=null")
    public NoticeResult getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CoreApiException(ErrorType.NOTICE_NOT_FOUND));
        return NoticeResult.fromNotice(notice);
    }

    public void incrementViewCount(Long id) {
        String noticeJson = (String) redisTemplate.opsForValue().get(NOTICE_KEY_PREFIX + id);
        if (noticeJson != null) {
            Map<String, Object> noticeMap = parseJsonToMap(noticeJson);
            long viewCount = ((Number) noticeMap.getOrDefault("viewCount", 0L)).longValue();
            noticeMap.put("viewCount", viewCount + 1);
            redisTemplate.opsForValue().set(NOTICE_KEY_PREFIX + id, convertMapToJson(noticeMap));
        }
    }

    @Scheduled(fixedRate = 180000)  // 3 minutes
    public void updateViewCounts() {
        Map<String, Long> viewCounts = Objects.requireNonNull(redisTemplate.keys(NOTICE_KEY_PREFIX + "*")).stream()
                .collect(Collectors.toMap(key -> key, key -> {
                    String noticeJson = (String) redisTemplate.opsForValue().get(key);
                    if (noticeJson != null) {
                        Map<String, Object> noticeMap = parseJsonToMap(noticeJson);
                        return ((Number) noticeMap.getOrDefault("viewCount", 0L)).longValue();
                    }
                    return 0L;
                }));

        for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
            Long id = Long.valueOf(entry.getKey().split("::")[1]);
            Long viewCount = entry.getValue();

            if (viewCount > 0) {
                Notice notice = noticeRepository.findById(id)
                        .orElseThrow(() -> new CoreApiException(ErrorType.NOTICE_NOT_FOUND));
                notice.incrementViewCount(viewCount.intValue());
                noticeRepository.save(notice);
            }
        }
    }

    private Map<String, Object> parseJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON to Map", e);
            throw new CoreApiException(ErrorType.DEFAULT_ERROR);
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Error converting Map to JSON", e);
            throw new CoreApiException(ErrorType.DEFAULT_ERROR);
        }
    }
}
