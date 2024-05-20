package io.dodn.springboot.core.api.validation;

import io.dodn.springboot.core.api.controller.v1.request.NoticeRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoticeValidatorTest {

    private NoticeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder violationBuilder;
    private NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    public void setUp() {
        validator = new NoticeValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
        violationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
        nodeBuilder = Mockito.mock(NodeBuilderCustomizableContext.class);

        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(violationBuilder);
        Mockito.when(violationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);
        Mockito.when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    public void testValidNotice() {
        NoticeRequestDto dto = new NoticeRequestDto(
                "Valid Title",
                "Valid Content",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Author",
                List.of(new MockMultipartFile("file1", "file1.txt", "text/plain", new byte[1024]))
        );
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    public void testInvalidNoticeDueToNullStartAt() {
        NoticeRequestDto dto = new NoticeRequestDto(
                "Valid Title",
                "Valid Content",
                null,
                LocalDateTime.now().plusDays(2),
                "Author",
                List.of(new MockMultipartFile("file1", "file1.txt", "text/plain", new byte[1024]))
        );
        assertFalse(validator.isValid(dto, context));
    }

    @Test
    public void testInvalidNoticeDueToNullEndAt() {
        NoticeRequestDto dto = new NoticeRequestDto(
                "Valid Title",
                "Valid Content",
                LocalDateTime.now().plusDays(1),
                null,
                "Author",
                List.of(new MockMultipartFile("file1", "file1.txt", "text/plain", new byte[1024]))
        );
        assertFalse(validator.isValid(dto, context));
    }

    @Test
    public void testInvalidNoticeDueToStartAtAfterEndAt() {
        NoticeRequestDto dto = new NoticeRequestDto(
                "Valid Title",
                "Valid Content",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                "Author",
                List.of(new MockMultipartFile("file1", "file1.txt", "text/plain", new byte[1024]))
        );
        assertFalse(validator.isValid(dto, context));
    }

    @Test
    public void testInvalidNoticeDueToTooManyAttachments() {
        List<MultipartFile> attachments = List.of(
                new MockMultipartFile("file1", "file1.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file2", "file2.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file3", "file3.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file4", "file4.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file5", "file5.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file6", "file6.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file7", "file7.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file8", "file8.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file9", "file9.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file10", "file10.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file11", "file11.txt", "text/plain", new byte[1024])
        );
        NoticeRequestDto dto = new NoticeRequestDto(
                "Valid Title",
                "Valid Content",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Author",
                attachments
        );
        assertFalse(validator.isValid(dto, context));
    }

    @Test
    public void testInvalidNoticeDueToTooLargeAttachment() {
        List<MultipartFile> attachments = List.of(
                new MockMultipartFile("file1", "file1.txt", "text/plain", new byte[1024]),
                new MockMultipartFile("file2", "file2.txt", "text/plain", new byte[1024 * 1024 * 11]) // 11 MB
        );
        NoticeRequestDto dto = new NoticeRequestDto(
                "Valid Title",
                "Valid Content",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Author",
                attachments
        );
        assertFalse(validator.isValid(dto, context));
    }
}
