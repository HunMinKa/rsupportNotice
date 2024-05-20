package io.dodn.springboot.core.api.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dodn.springboot.CoreApiApplication;
import io.dodn.springboot.core.api.config.JacksonConfig;
import io.dodn.springboot.core.api.controller.v1.request.NoticeRequestDto;
import io.dodn.springboot.core.api.domain.file.FileStorageService;
import io.dodn.springboot.core.api.domain.notice.NoticeData;
import io.dodn.springboot.core.api.domain.notice.NoticeResult;
import io.dodn.springboot.core.api.domain.notice.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.dodn.springboot.storage.db.core.notice.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {CoreApiApplication.class, JacksonConfig.class})
@AutoConfigureMockMvc
public class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private FileStorageService fileStorageService;

    private Notice notice;

    @BeforeEach
    public void setUp() {
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
    public void testCreateNotice() throws Exception {
        LocalDateTime now = LocalDateTime.of(2024, 5, 20, 12, 0);
        NoticeRequestDto noticeRequestDto = new NoticeRequestDto(
                "Test Title",
                "Test Content",
                now,
                now,
                "Test Author",
                List.of()
        );

        MockMultipartFile noticeFile = new MockMultipartFile("notice", "notice", "application/json", objectMapper.writeValueAsBytes(noticeRequestDto));
        MockMultipartFile attachmentFile = new MockMultipartFile("attachments", "attachment.txt", "text/plain", "some text".getBytes());

        when(fileStorageService.storeFile(any())).thenReturn(CompletableFuture.completedFuture("attachment_path"));
        when(noticeService.createNotice(any(NoticeData.class)))
                .thenAnswer(invocation -> {
                    NoticeData noticeData = invocation.getArgument(0);
                    NoticeResult noticeResult = NoticeResult.fromNotice(notice);
                    return CompletableFuture.completedFuture(noticeResult);
                });

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/notices")
                        .file(noticeFile)
                        .file(attachmentFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateNotice() throws Exception {
        Long id = 1L;
        NoticeRequestDto noticeRequestDto = new NoticeRequestDto(
                "Updated Title",
                "Updated Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "Updated Author",
                List.of()
        );

        MockMultipartFile noticeFile = new MockMultipartFile("notice", "notice", "application/json", objectMapper.writeValueAsBytes(noticeRequestDto));
        MockMultipartFile attachmentFile = new MockMultipartFile("attachments", "attachment.txt", "text/plain", "some text".getBytes());

        when(noticeService.updateNotice(any(Long.class), any(NoticeData.class))).thenReturn(NoticeResult.fromNotice(notice));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/notices/{id}", id)
                        .file(noticeFile)
                        .file(attachmentFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT"); // PUT 메서드를 명시적으로 설정
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteNotice() throws Exception {
        Long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/notices/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetNoticeById() throws Exception {
        Long id = 1L;

        when(noticeService.getNoticeById(id)).thenReturn(NoticeResult.fromNotice(notice));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/notices/{id}", id))
                .andExpect(status().isOk());
    }
}
