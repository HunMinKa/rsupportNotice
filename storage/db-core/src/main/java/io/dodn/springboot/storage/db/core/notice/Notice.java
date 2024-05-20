package io.dodn.springboot.storage.db.core.notice;


import io.dodn.springboot.storage.db.core.BaseEntity;
import io.dodn.springboot.storage.db.core.noticeAttachment.NoticeAttachment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Notice extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int viewCount = 0;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeAttachment> attachments = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    protected Notice() {
        // JPA requires a default constructor
    }

    private Notice(String title, String content, LocalDateTime startAt, LocalDateTime endAt, String author, List<String> attachmentPaths) {
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.author = author;
        if (attachmentPaths != null) {
            for (String path : attachmentPaths) {
                this.attachments.add(new NoticeAttachment(this, path));
            }
        }
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getAuthor() {
        return author;
    }

    public int getViewCount() {
        return viewCount;
    }

    public List<String> getAttachments() {
        List<String> paths = new ArrayList<>();
        for (NoticeAttachment attachment : attachments) {
            paths.add(attachment.getFilePath());
        }
        return Collections.unmodifiableList(paths);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Builder pattern for creating instances
    public static class Builder {
        private String title;
        private String content;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private String author;
        private List<String> attachmentPaths;

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withStartAt(LocalDateTime startAt) {
            this.startAt = startAt;
            return this;
        }

        public Builder withEndAt(LocalDateTime endAt) {
            this.endAt = endAt;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder withAttachments(List<String> attachmentPaths) {
            this.attachmentPaths = attachmentPaths;
            return this;
        }

        public Notice build() {
            return new Notice(title, content, startAt, endAt, author, attachmentPaths);
        }
    }

    public void update(String title, String content, LocalDateTime startAt, LocalDateTime endAt, String author, List<String> attachmentPaths) {
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.author = author;
        this.attachments = new ArrayList<>();
        if (attachmentPaths != null) {
            for (String path : attachmentPaths) {
                this.attachments.add(new NoticeAttachment(this, path));
            }
        }
    }

    public void incrementViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
