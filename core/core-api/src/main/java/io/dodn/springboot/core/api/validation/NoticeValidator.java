package io.dodn.springboot.core.api.validation;

import io.dodn.springboot.core.api.controller.v1.request.NoticeRequestDto;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoticeValidator implements ConstraintValidator<ValidNotice, NoticeRequestDto> {

    private static final int MAX_ATTACHMENTS = 10;
    private static final long MAX_ATTACHMENT_SIZE = 10 * 1024 * 1024; // 10 MB

    @Override
    public boolean isValid(NoticeRequestDto dto, ConstraintValidatorContext context) {
        boolean isValid = true;
        context.disableDefaultConstraintViolation();
        if (dto.startAt() == null) {
            context.buildConstraintViolationWithTemplate("시작 날짜는 null이 아니어야 합니다")
                    .addPropertyNode("startAt")
                    .addConstraintViolation();
            isValid = false;
        }
        if (dto.endAt() == null) {
            context.buildConstraintViolationWithTemplate("종료 날짜는 null일 수 없습니다.")
                    .addPropertyNode("endAt")
                    .addConstraintViolation();
            isValid = false;
        }
        if (dto.startAt() != null && dto.endAt() != null && dto.startAt().isAfter(dto.endAt())) {
            context.buildConstraintViolationWithTemplate("시작일은 종료일 이전이어야 합니다.")
                    .addPropertyNode("startAt")
                    .addConstraintViolation();
            isValid = false;
        }
        if (dto.attachments() != null && dto.attachments().size() > MAX_ATTACHMENTS) {
            context.buildConstraintViolationWithTemplate("첨부 파일이 너무 많습니다.")
                    .addPropertyNode("attachments")
                    .addConstraintViolation();
            isValid = false;
        }
        if (dto.attachments() != null) {
            for (MultipartFile attachment : dto.attachments()) {
                if (attachment.getSize() > MAX_ATTACHMENT_SIZE) {
                    context.buildConstraintViolationWithTemplate("첨부 파일이 너무 큼: " + attachment.getOriginalFilename())
                            .addPropertyNode("attachments")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        }
        return isValid;
    }
}