package io.dodn.springboot.storage.db.core.noticeAttachment;


import io.dodn.springboot.storage.db.core.notice.Notice;
import jakarta.persistence.*;

@Entity
public class NoticeAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    protected NoticeAttachment() {
        // JPA requires a default constructor
    }

    public NoticeAttachment(Notice notice, String filePath) {
        this.notice = notice;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public Notice getNotice() {
        return notice;
    }

    public String getFilePath() {
        return filePath;
    }
}