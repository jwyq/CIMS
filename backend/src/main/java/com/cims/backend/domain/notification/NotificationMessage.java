package com.cims.backend.domain.notification;

public class NotificationMessage {

    private final Long id;
    private final String recipient;
    private final String content;

    public NotificationMessage(Long id, String recipient, String content) {
        this.id = id;
        this.recipient = recipient;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
}
