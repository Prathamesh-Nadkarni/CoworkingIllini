package com.example.coworkingillini;

public class Message {
    private String content;
    private boolean isReceived;
    private boolean isRead;

    public Message(String content, boolean isReceived) {
        this.content = content;
        this.isReceived = isReceived;
        this.isRead = false;
    }

    public String getContent() {
        return content;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}

