package io.dodn.springboot.core.api.domain.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dodn.springboot.storage.db.core.notice.Notice;
import io.dodn.springboot.storage.db.core.notice.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NoticeServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private NoticeService noticeService;

    private Notice notice;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        notice = new Notice.Builder()
                .withTitle("Test Title")
                .withContent("Test Content")
                .withStartAt(LocalDateTime.now())
                .withEndAt(LocalDateTime.now().plusDays(1))
                .withAuthor("Test Author")
                .withAttachments(List.of("attachment1", "attachment2"))
                .build();
    }

    @Test
    public void testIncrementViewCount() throws JsonProcessingException {
        Long id = 1L;
        String noticeKey = "notices::" + id;
        String noticeJson = "{\"viewCount\": 5}";

        when(valueOperations.get(noticeKey)).thenReturn(noticeJson);
        Map<String, Object> noticeMap = new HashMap<>();
        noticeMap.put("viewCount", 5);
        when(objectMapper.readValue(noticeJson, Map.class)).thenReturn(noticeMap);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"viewCount\": 6}");

        noticeService.incrementViewCount(id);

        verify(valueOperations, times(1)).set(eq(noticeKey), eq("{\"viewCount\": 6}"));
    }

    @Test
    public void testUpdateNotice() {
        Long id = 1L;
        NoticeData noticeData = new NoticeData(
                "Updated Title",
                "Updated Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "Updated Author",
                List.of("attachment1", "attachment2")
        );

        when(noticeRepository.findById(id)).thenReturn(Optional.of(notice));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        NoticeResult result = noticeService.updateNotice(id, noticeData);

        assertNotNull(result);
        assertEquals("Updated Title", result.title());
        verify(noticeRepository, times(1)).findById(id);
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    public void testDeleteNotice() {
        Long id = 1L;

        when(noticeRepository.existsById(id)).thenReturn(true);
        doNothing().when(noticeRepository).deleteById(id);

        noticeService.deleteNotice(id);

        verify(noticeRepository, times(1)).existsById(id);
        verify(noticeRepository, times(1)).deleteById(id);
    }

    @Test
    public void testGetNoticeById() {
        Long id = 1L;

        when(noticeRepository.findById(id)).thenReturn(Optional.of(notice));

        NoticeResult result = noticeService.getNoticeById(id);

        assertNotNull(result);
        assertEquals("Test Title", result.title());
        verify(noticeRepository, times(1)).findById(id);
    }

    @Test
    public void testUpdateViewCounts() throws JsonProcessingException {
        String noticeKey = "notices::1";
        String noticeJson = "{\"viewCount\": 5}";

        Set<String> keys = Set.of(noticeKey);

        when(redisTemplate.keys("notices::*")).thenReturn(keys);
        when(valueOperations.get(noticeKey)).thenReturn(noticeJson);
        Map<String, Object> noticeMap = new HashMap<>();
        noticeMap.put("viewCount", 5);
        when(objectMapper.readValue(noticeJson, Map.class)).thenReturn(noticeMap);

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        noticeService.updateViewCounts();

        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    public void testCreateNotice() {
        NoticeData noticeData = new NoticeData(
                "Test Title",
                "Test Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "Test Author",
                List.of("attachment1", "attachment2")
        );

        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        NoticeResult result = noticeService.createNotice(noticeData);

        assertNotNull(result);
        assertEquals("Test Title", result.title());
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }
}
